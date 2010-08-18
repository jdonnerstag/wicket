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
package org.apache.wicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests component events
 * 
 * @author igor
 */
public class ComponentEventsTest
{

	private WicketTester tester;
	private TestPage page;
	private TestContainer c1;
	private TestContainer c12;
	private TestContainer c13;
	private TestContainer c134;
	private TestComponent c135;
	private TestComponent c6;
	private TestApplication application;
	private TestSession session;
	private TestRequestCycle cycle;
	private Testable[] all;

	@Before
	public void setup()
	{
		tester = new WicketTester(new TestApplication());

		application = (TestApplication)tester.getApplication();

		session = (TestSession)tester.getSession();
		cycle = (TestRequestCycle)tester.getRequestCycle();

		page = new TestPage();
		c1 = new TestContainer("c1");
		c12 = new TestContainer("c12");
		c13 = new TestContainer("c13");
		c134 = new TestContainer("c134");
		c135 = new TestComponent("c135");
		c6 = new TestComponent("c6");

		page.add(c1);
		c1.add(c12);
		c1.add(c13);
		c13.add(c134);
		c13.add(c135);
		page.add(c6);

		all = new Testable[] { page, c1, c12, c13, c134, c135, c6, application, session, cycle };
	}

	@After
	public void destroy()
	{
		tester.destroy();
	}

	@Test
	public void testBreadth()
	{
		page.send(tester.getApplication(), Broadcast.BREADTH, new Payload());
		assertPath(application, session, cycle, page, c1, c12, c13, c134, c135, c6);
	}

	@Test
	public void testDepth()
	{
		page.send(tester.getApplication(), Broadcast.DEPTH, new Payload());
		assertPath(c12, c134, c135, c13, c1, c6, page, cycle, session, application);
	}

	@Test
	public void testBubble_Component()
	{
		c6.send(c6, Broadcast.BUBBLE, new Payload());
		assertPath(c6, page, cycle, session, application);
	}

	@Test
	public void testBubble_Page()
	{
		c6.send(page, Broadcast.BUBBLE, new Payload());
		assertPath(page, cycle, session, application);
	}

	@Test
	public void testBubble_Cycle()
	{
		c6.send(cycle, Broadcast.BUBBLE, new Payload());
		assertPath(cycle, session, application);
	}

	@Test
	public void testBubble_Session()
	{
		c6.send(session, Broadcast.BUBBLE, new Payload());
		assertPath(session, application);
	}


	@Test
	public void testBubble_Application()
	{
		c6.send(application, Broadcast.BUBBLE, new Payload());
		assertPath(application);
	}


	private void assertPath(Testable... testables)
	{
		List<Testable> remaining = new ArrayList<Testable>(Arrays.asList(all));

		for (int i = 0; i < testables.length; i++)
		{
			Assert.assertEquals("checking path element " + i, i, testables[i].getSequence());
			remaining.remove(testables[i]);
		}

		for (Testable testable : remaining)
		{
			Assert.assertEquals(-1, testable.getSequence());
		}
	}

	private static interface Testable
	{
		int getSequence();
	}


	private static class TestApplication extends MockApplication implements Testable
	{
		int sequence = -1;

		@Override
		public void onEvent(IEvent event)
		{
			super.onEvent(event);
			Payload payload = event.getPayload();
			sequence = payload.next();
		}

		@Override
		public Session newSession(Request request, Response response)
		{
			return new TestSession(request);
		}

		@Override
		protected void init()
		{
			super.init();
			setRequestCycleProvider(new IRequestCycleProvider()
			{
				public RequestCycle get(RequestCycleContext context)
				{
					return new TestRequestCycle(context);
				}
			});
		}

		public int getSequence()
		{
			return sequence;
		}
	}

	private static class TestSession extends WebSession implements Testable
	{
		int sequence = -1;

		public TestSession(Request request)
		{
			super(request);
		}


		public int getSequence()
		{
			return sequence;
		}

		@Override
		public void onEvent(IEvent event)
		{
			super.onEvent(event);
			Payload payload = event.getPayload();
			sequence = payload.next();
		}
	}

	private static class TestRequestCycle extends RequestCycle implements Testable
	{
		int sequence = -1;

		public TestRequestCycle(RequestCycleContext context)
		{
			super(context);
		}


		public int getSequence()
		{
			return sequence;
		}

		@Override
		public void onEvent(IEvent event)
		{
			super.onEvent(event);
			Payload payload = event.getPayload();
			sequence = payload.next();
		}
	}


	private static class TestPage extends WebPage implements Testable
	{
		int sequence = -1;

		public TestPage()
		{
		}


		public int getSequence()
		{
			return sequence;
		}

		@Override
		public void onEvent(IEvent event)
		{
			super.onEvent(event);
			Payload payload = event.getPayload();
			sequence = payload.next();
			System.out.println(getId());
		}

	}


	private static class TestContainer extends WebMarkupContainer implements Testable
	{
		int sequence = -1;

		public TestContainer(String id)
		{
			super(id);
		}

		@Override
		public void onEvent(IEvent event)
		{
			super.onEvent(event);
			Payload payload = event.getPayload();
			sequence = payload.next();
			System.out.println(getId());
		}


		public int getSequence()
		{
			return sequence;
		}
	}

	private static class TestComponent extends WebComponent implements Testable
	{
		int sequence = -1;

		public TestComponent(String id)
		{
			super(id);
		}

		@Override
		public void onEvent(IEvent event)
		{
			super.onEvent(event);
			Payload payload = event.getPayload();
			sequence = payload.next();
			System.out.println(getId());
		}


		public int getSequence()
		{
			return sequence;
		}
	}

	public static class Payload
	{
		private int counter;

		public int next()
		{
			return counter++;
		}
	}

}
