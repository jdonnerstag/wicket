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
package org.apache.wicket.event;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Session;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Checks;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.util.visit.Visits;

public class EventSender implements IEventSource
{
	private final Component source;

	public EventSender(Component source)
	{
		this.source = source;
	}

	public void send(IEventSink sink, BroadcastType type, Object payload)
	{
		ComponentEvent event = new ComponentEvent(sink, source, type, payload);
		Checks.argumentNotNull(type, "type");
		switch (type)
		{
			case BUBBLE :
				bubble(event);
				break;
			case BREADTH :
				breadth(event);
				break;
			case DEAPTH :
				deapth(event);
				break;
		}
	}

	private void breadth(final ComponentEvent e)
	{
		IEventSink sink = e.getSink();

		boolean app = sink instanceof Application;
		boolean ses = app || sink instanceof Session;
		boolean rc = ses || sink instanceof RequestCycle;

		if (!(sink instanceof Component) && !rc)
		{
			sink.onEvent(e);
			return;
		}

		Component cursor = null;

		if (app)
		{
			source.getApplication().onEvent(e);
		}
		if (e.isStop())
		{
			return;
		}
		if (ses)
		{
			source.getSession().onEvent(e);
		}
		if (e.isStop())
		{
			return;
		}

		if (rc)
		{
			source.getRequestCycle().onEvent(e);
			cursor = source.getPage();
		}
		else
		{
			cursor = (Component)sink;
		}

		if (e.isStop())
		{
			return;
		}

		cursor.onEvent(e);

		if (e.isStop())
		{
			return;
		}

		e.reset(); // reset shallow flag

		if (cursor instanceof MarkupContainer)
		{
			((MarkupContainer)cursor).visitChildren(new IVisitor<Component, Void>()
			{

				public void component(Component object, IVisit<Void> visit)
				{
					object.onEvent(e);
					if (e.isShallow())
					{
						visit.dontGoDeeper();
						e.reset();
					}
					else if (e.isStop())
					{
						visit.stop();
						e.reset();
					}
				}
			});
		}
	}

	private void deapth(final ComponentEvent e)
	{
		IEventSink sink = e.getSink();

		boolean app = sink instanceof Application;
		boolean ses = app || sink instanceof Session;
		boolean rc = ses || sink instanceof RequestCycle;

		if (!(sink instanceof Component) && !rc)
		{
			sink.onEvent(e);
			return;
		}

		Component cursor = null;

		if (rc)
		{
			cursor = source.getPage();
		}
		else
		{
			cursor = (Component)sink;
		}

		if (cursor instanceof MarkupContainer)
		{
			Visits.visitComponentsPostOrder(cursor, new ComponentEventVisitor(e));
		}


		if (app)
		{
			source.getApplication().onEvent(e);
		}
		if (e.isStop())
		{
			return;
		}
		if (ses)
		{
			source.getSession().onEvent(e);
		}
		if (e.isStop())
		{
			return;
		}

		if (rc)
		{
			source.getRequestCycle().onEvent(e);
			cursor = source.getPage();
		}
		else
		{
			cursor = (Component)sink;
		}

		if (e.isStop())
		{
			return;
		}

		cursor.onEvent(e);

		if (e.isStop())
		{
			return;
		}

	}


	private void bubble(ComponentEvent e)
	{
		IEventSink sink = e.getSink();

		boolean app = sink instanceof Application;
		boolean ses = app || sink instanceof Session;
		boolean rc = ses || sink instanceof RequestCycle;
		boolean c = sink instanceof Component;
		if (c)
		{
			Component cursor = (Component)sink;
			cursor.onEvent(e);
			cursor.visitParents(Component.class, new ComponentEventVisitor(e));
		}

		if (!e.isStop() && (rc || c))
		{
			source.getRequestCycle().onEvent(e);
		}
		if (!e.isStop() && (ses || c))
		{
			source.getSession().onEvent(e);
		}
		if (!e.isStop() && (app || c))
		{
			source.getApplication().onEvent(e);

		}
	}

	private static class ComponentEventVisitor implements IVisitor<Component, Void>
	{
		private final ComponentEvent e;

		private ComponentEventVisitor(ComponentEvent e)
		{
			this.e = e;
		}

		public void component(Component object, IVisit<Void> visit)
		{
			object.onEvent(e);
			if (e.isShallow())
			{
				visit.dontGoDeeper();
			}
			else if (e.isStop())
			{
				visit.stop();
			}
			e.reset();
		}
	}

}
