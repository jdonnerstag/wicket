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
package org.apache.wicket.markup;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.util.iterator.IteratorFilter;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Generics;

/**
 * 
 * @author Juergen Donnerstag
 */
public class MarkupIterator implements Iterator<ComponentTag>, Iterable<ComponentTag>
{
	private final MarkupStream stream;

	private List<IteratorFilter<ComponentTag>> filters = Generics.newArrayList();

	private boolean dontGoDeeper;
	private boolean found;

	/**
	 * Construct.
	 * 
	 * @param stream
	 */
	public MarkupIterator(final MarkupStream stream)
	{
		this.stream = Args.notNull(stream, "stream");
	}

	public final Iterator<ComponentTag> iterator()
	{
		return this;
	}

	public final boolean hasNext()
	{
		if (found && dontGoDeeper)
		{
			MarkupElement elem = stream.get();
			if (elem instanceof ComponentTag)
			{
				ComponentTag tag = stream.getTag();
				if ((tag.hasNoCloseTag() == false) && tag.isOpen())
				{
					stream.skipToMatchingCloseTag(tag);
				}
			}

			stream.next();
		}

		found = false;
		while (stream.hasMore())
		{
			MarkupElement elem = stream.get();
			if (elem instanceof ComponentTag)
			{
				found = true;
				ComponentTag tag = stream.getTag();
				for (IteratorFilter<ComponentTag> filter : filters)
				{
					if (filter.onFilter(tag) == false)
					{
						found = false;
						break;
					}
				}

				if (found == true)
				{
					return true;
				}

				if (dontGoDeeper && (tag.hasNoCloseTag() == false) && tag.isOpen())
				{
					stream.skipToMatchingCloseTag(tag);
				}
			}

			stream.next();
		}

		return false;
	}

	public final ComponentTag next()
	{
		return stream.getTag();
	}

	/**
	 * 
	 * @param dontGoDeeper
	 * @return this
	 */
	public MarkupIterator setDontGoDeeper(boolean dontGoDeeper)
	{
		this.dontGoDeeper = dontGoDeeper;
		return this;
	}

	/**
	 * @param filter
	 * @return this
	 */
	public MarkupIterator addFilter(final IteratorFilter<ComponentTag> filter)
	{
		filters.add(filter);
		return this;
	}

	/**
	 * @return this
	 */
	public MarkupIterator skipComponentTags()
	{
		addFilter(new IteratorFilter<ComponentTag>()
		{
			@Override
			public boolean onFilter(final ComponentTag tag)
			{
				return (tag instanceof WicketTag);
			}
		});

		return this;
	}

	/**
	 * @return this
	 */
	public MarkupIterator skipWicketTags()
	{
		addFilter(new IteratorFilter<ComponentTag>()
		{
			@Override
			public boolean onFilter(final ComponentTag tag)
			{
				return !(tag instanceof WicketTag);
			}
		});

		return this;
	}

	/**
	 * @return this
	 */
	public MarkupIterator openTagsOnly()
	{
		addFilter(new IteratorFilter<ComponentTag>()
		{
			@Override
			public boolean onFilter(final ComponentTag tag)
			{
				return !tag.isClose();
			}
		});

		return this;
	}

	/**
	 * 
	 * @return MarkupStream
	 */
	public final MarkupStream getMarkupStream()
	{
		return stream;
	}

	/**
	 * Not supported. Markup is immutable.
	 */
	public final void remove()
	{
		throw new UnsupportedOperationException(
			"Markup is immutable. You can not remove tags or raw markup.");
	}
}
