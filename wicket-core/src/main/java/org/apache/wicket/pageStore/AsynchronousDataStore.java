/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.pageStore;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for {@link IDataStore} that does the actual saving in worker thread.
 * <p>
 * Creates an {@link Entry} for each triple (sessionId, pageId, data) and puts it in
 * {@link #entries} queue if there is room. Acts as producer.<br/>
 * Later {@link PageSavingRunnable} reads in blocking manner from {@link #entries} and saves each
 * entry. Acts as consumer.
 * </p>
 * It starts only one instance of {@link PageSavingRunnable} because all we need is to make the page
 * storing asynchronous. We don't want to write concurrently in the wrapped {@link IDataStore},
 * though it may happen in the extreme case when the queue is full. These cases should be avoided.
 * 
 * @author Matej Knopp
 */
public class AsynchronousDataStore implements IDataStore
{
	/** Log for reporting. */
	private static final Logger log = LoggerFactory.getLogger(AsynchronousDataStore.class);

	/**
	 * The time to wait when adding an {@link Entry} into the entries. In millis.
	 */
	private static final long OFFER_WAIT = 30L;

	/**
	 * The time to wait for an entry to save with the wrapped {@link IDataStore}. In millis.
	 */
	private static final long POLL_WAIT = 1000L;

	/**
	 * A flag indicating that this {@link IDataStore} should stop
	 */
	private final AtomicBoolean destroy;

	/**
	 * The wrapped {@link IDataStore} that actually stores that pages
	 */
	private final IDataStore dataStore;

	/**
	 * The queue where the entries which have to be saved are temporary stored
	 */
	private final BlockingQueue<Entry> entries;

	/**
	 * A map 'sessionId:::pageId' -> {@link Entry}. Used for fast retrieval of {@link Entry}s which
	 * are not yet stored by the wrapped {@link IDataStore}
	 */
	private final ConcurrentMap<String, Entry> entryMap;

	/**
	 * Construct.
	 * 
	 * @param dataStore
	 *            the wrapped {@link IDataStore} that actually saved the data
	 * @param capacity
	 *            the capacity of the queue that delays the saving
	 */
	public AsynchronousDataStore(final IDataStore dataStore, final int capacity)
	{
		this.dataStore = dataStore;
		destroy = new AtomicBoolean(false);
		entries = new LinkedBlockingQueue<Entry>(capacity);
		entryMap = new ConcurrentHashMap<String, Entry>();

		PageSavingRunnable savingRunnable = new PageSavingRunnable(dataStore, entries, entryMap,
			destroy);
		Thread thread = new Thread(savingRunnable, "Wicket-PageSavingThread");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#destroy()
	 */
	public void destroy()
	{
		destroy.set(true);

		try
		{
			synchronized (destroy)
			{
				destroy.wait();
			}
		}
		catch (InterruptedException e)
		{
			log.error(e.getMessage(), e);
		}

		dataStore.destroy();
	}

	/**
	 * Little helper
	 * 
	 * @param sessionId
	 * @param id
	 * @return Entry
	 */
	private Entry getEntry(final String sessionId, final int id)
	{
		return entryMap.get(getKey(sessionId, id));
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#getData(java.lang.String, int)
	 */
	public byte[] getData(final String sessionId, final int id)
	{
		Entry entry = getEntry(sessionId, id);
		if (entry != null)
		{
			log.debug(
				"Returning the data of a non-stored entry with sessionId '{}' and pageId '{}'",
				sessionId, id);
			return entry.data;
		}
		byte[] data = dataStore.getData(sessionId, id);

		log.debug("Returning the data of a stored entry with sessionId '{}' and pageId '{}'",
			sessionId, id);

		return data;
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#isReplicated()
	 */
	public boolean isReplicated()
	{
		return dataStore.isReplicated();
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#removeData(java.lang.String, int)
	 */
	public void removeData(final String sessionId, final int id)
	{
		String key = getKey(sessionId, id);
		if (key != null)
		{
			Entry entry = entryMap.remove(key);
			if (entry != null)
			{
				entries.remove(entry);
			}
		}

		dataStore.removeData(sessionId, id);
	}

	/**
	 * @see org.apache.wicket.pageStore.IDataStore#removeData(java.lang.String)
	 */
	public void removeData(final String sessionId)
	{
		for (Iterator<Entry> itor = entries.iterator(); itor.hasNext();)
		{
			Entry entry = itor.next();
			if (entry != null) // this check is not needed in JDK6
			{
				String entrySessionId = entry.sessionId;

				if (sessionId.equals(entrySessionId))
				{
					entryMap.remove(getKey(entry));
					itor.remove();
				}
			}
		}

		dataStore.removeData(sessionId);
	}

	/**
	 * Save the entry in the queue if there is a room or directly pass it to the wrapped
	 * {@link IDataStore} if there is no such
	 * 
	 * @see org.apache.wicket.pageStore.IDataStore#storeData(java.lang.String, int, byte[])
	 */
	public void storeData(final String sessionId, final int id, final byte[] data)
	{
		Entry entry = new Entry(sessionId, id, data);
		try
		{
			boolean added = entries.offer(entry, OFFER_WAIT, TimeUnit.MILLISECONDS);

			if (added == false)
			{
				log.debug("Storing synchronously page with id '{}' in session '{}'", id, sessionId);
				dataStore.storeData(sessionId, id, data);
			}
			else
			{
				entryMap.put(getKey(entry), entry);
			}
		}
		catch (InterruptedException e)
		{
			log.error(e.getMessage(), e);
			dataStore.storeData(sessionId, id, data);
		}
	}

	/**
	 * 
	 * @param pageId
	 * @param sessionId
	 * @return generated key
	 */
	private static String getKey(final String sessionId, final int pageId)
	{
		return pageId + ":::" + sessionId;
	}

	/**
	 * 
	 * @param entry
	 * @return generated key
	 */
	private static String getKey(final Entry entry)
	{
		return getKey(entry.sessionId, entry.pageId);
	}

	/**
	 * The structure used for an entry in the queue
	 */
	private static class Entry
	{
		private final String sessionId;
		private final int pageId;
		private final byte data[];

		public Entry(final String sessionId, final int pageId, final byte data[])
		{
			this.sessionId = Args.notNull(sessionId, "sessionId");
			this.pageId = pageId;
			this.data = Args.notNull(data, "data");
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + pageId;
			result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry)obj;
			if (pageId != other.pageId)
				return false;
			if (sessionId == null)
			{
				if (other.sessionId != null)
					return false;
			}
			else if (!sessionId.equals(other.sessionId))
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			return "Entry [sessionId=" + sessionId + ", pageId=" + pageId + "]";
		}

	}

	/**
	 * The thread that acts as consumer of {@link Entry}ies
	 */
	private static class PageSavingRunnable implements Runnable
	{
		private static final Logger log = LoggerFactory.getLogger(PageSavingRunnable.class);

		private final AtomicBoolean destroy;

		private final BlockingQueue<Entry> entries;

		private final ConcurrentMap<String, Entry> entryMap;

		private final IDataStore dataStore;

		private PageSavingRunnable(IDataStore dataStore, BlockingQueue<Entry> entries,
			ConcurrentMap<String, Entry> entryMap, AtomicBoolean destroy)
		{
			this.dataStore = dataStore;
			this.entries = entries;
			this.entryMap = entryMap;
			this.destroy = destroy;
		}

		public void run()
		{
			while (destroy.get() == false)
			{
				Entry entry = null;
				try
				{
					entry = entries.poll(POLL_WAIT, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException e)
				{
					log.error(e.getMessage(), e);
				}

				if (entry != null)
				{
					log.debug("Saving asynchronously: {}...", entry);
					dataStore.storeData(entry.sessionId, entry.pageId, entry.data);
					entryMap.remove(getKey(entry));
				}
			}

			synchronized (destroy)
			{
				destroy.notify();
			}
		}
	}
}