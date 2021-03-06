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
package org.apache.wicket.request.mapper.info;

import junit.framework.TestCase;

/**
 * 
 * @author Matej Knopp
 */
public class ComponentInfoTest extends TestCase
{

	/**
	 * 
	 * Construct.
	 */
	public ComponentInfoTest()
	{
	}

	/**
	 * 
	 */
	public void test1()
	{
		String s = "listener-component-path";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals("listener", info.getListenerInterface());
		assertEquals("component:path", info.getComponentPath());
		assertNull(info.getBehaviorId());

		assertEquals(s, info.toString());
	}

	/**
	 * 
	 */
	public void test2()
	{
		String s = "-component-path";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals(null, info);
	}

	/**
	 * 
	 */
	public void test3()
	{
		String s = "listener-";
		ComponentInfo info = ComponentInfo.parse(s);
		// empty component path is allowed - listener invoked on page
		assertEquals("listener", info.getListenerInterface());
		assertEquals("", info.getComponentPath());
	}

	/**
	 * 
	 */
	public void test4()
	{
		String s = "-";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals(null, info);
	}

	/**
	 * 
	 */
	public void test5()
	{
		String s = "abcd";
		assertEquals(null, ComponentInfo.parse(s));
	}

	/**
	 * 
	 */
	public void test6()
	{
		String s = "listener-compo--nent-path";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals("listener", info.getListenerInterface());
		assertEquals("compo-nent:path", info.getComponentPath());
		assertNull(info.getBehaviorId());

		assertEquals(s, info.toString());
	}

	/**
	 * 
	 */
	public void test7()
	{
		String s = "listener-co--mpo----nent-path";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals("listener", info.getListenerInterface());
		assertEquals("co-mpo--nent:path", info.getComponentPath());
		assertNull(info.getBehaviorId());

		assertEquals(s, info.toString());
	}

	/**
	 * 
	 */
	public void test8()
	{
		String s = "listener.12-component-path";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals("listener", info.getListenerInterface());
		assertEquals("component:path", info.getComponentPath());
		assertEquals((Object)12, info.getBehaviorId());

		assertEquals(s, info.toString());
	}

	/**
	 * 
	 */
	public void test9()
	{
		String s = "4.listener-a-b";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals("listener", info.getListenerInterface());
		assertEquals((Integer)4, info.getRenderCount());

		assertEquals(s, info.toString());
	}

	/**
	 * 
	 */
	public void test10()
	{
		String s = "4.listener.5-a-b";
		ComponentInfo info = ComponentInfo.parse(s);
		assertEquals("listener", info.getListenerInterface());
		assertEquals((Integer)4, info.getRenderCount());
		assertEquals((Integer)5, info.getBehaviorId());

		assertEquals(s, info.toString());
	}

}
