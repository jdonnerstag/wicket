package org.apache.wicket;

import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.event.IEventSource;

/**
 * Implementation of {@link IEvent} raised by a component
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
final class ComponentEvent implements IEvent
{
	private final IEventSink sink;
	private final IEventSource source;
	private final Broadcast type;
	private final Object payload;

	private boolean stop;
	private boolean shallow;

	/**
	 * Constructor
	 * 
	 * @param sink
	 *            sink
	 * @param source
	 *            source
	 * @param broadcast
	 *            broadcast
	 * @param payload
	 *            payload
	 */
	public ComponentEvent(IEventSink sink, IEventSource source, Broadcast broadcast, Object payload)
	{
		this.sink = sink;
		this.source = source;
		type = broadcast;
		this.payload = payload;
	}

	/**
	 * @return event sink
	 */
	public IEventSink getSink()
	{
		return sink;
	}

	/** {@inheritDoc} */
	public IEventSource getSource()
	{
		return source;
	}

	/** {@inheritDoc} */
	public Broadcast getType()
	{
		return type;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public <T> T getPayload()
	{
		return (T)payload;
	}

	/** {@inheritDoc} */
	public void dontBroadcastDeeper()
	{
		shallow = true;
	}

	/** {@inheritDoc} */
	public void stop()
	{
		stop = true;
	}

	boolean isStop()
	{
		return stop;
	}

	boolean isShallow()
	{
		return shallow;
	}

	void reset()
	{
		stop = false;
		shallow = false;
	}
}