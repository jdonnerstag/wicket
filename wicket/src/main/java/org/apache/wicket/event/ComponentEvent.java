package org.apache.wicket.event;

class ComponentEvent implements IEvent
{
	private final IEventSink sink;
	private final IEventSource source;
	private final BroadcastType type;
	private final Object payload;

	private boolean stop;
	private boolean shallow;

	public ComponentEvent(IEventSink sink, IEventSource source, BroadcastType type, Object payload)
	{
		this.sink = sink;
		this.source = source;
		this.type = type;
		this.payload = payload;
	}

	public IEventSink getSink()
	{
		return sink;
	}

	public IEventSource getSource()
	{
		return source;
	}

	public BroadcastType getType()
	{
		return type;
	}

	public Object getPayload()
	{
		return payload;
	}

	public void dontBroadcastDeeper()
	{
		shallow = true;
	}

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