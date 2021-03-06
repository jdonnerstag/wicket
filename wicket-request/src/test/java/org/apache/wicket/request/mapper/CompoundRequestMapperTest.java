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
package org.apache.wicket.request.mapper;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.handler.EmptyRequestHandler;
import org.apache.wicket.request.mapper.mount.MountMapper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link CompoundRequestMapper}
 */
public class CompoundRequestMapperTest extends Assert
{

	private static final String MOUNT_PATH_3 = "mount/path/3";
	private static final String MOUNT_PATH_2 = "mount/path/2";
	private static final String MOUNT_PATH_1 = "mount/path/1";

	/**
	 * 
	 */
	@Test
	public void unmount()
	{
		CompoundRequestMapper compound = new CompoundRequestMapper();

		compound.add(new MountMapper(MOUNT_PATH_1, new EmptyRequestHandler()));
		compound.add(new MountMapper(MOUNT_PATH_2, new EmptyRequestHandler()));
		compound.add(new MountMapper(MOUNT_PATH_3, new EmptyRequestHandler()));

		assertEquals(3, compound.size());

		compound.unmount(MOUNT_PATH_2);
		assertEquals(2, compound.size());

		assertTrue(
			"Mount path 1 should match",
			compound.mapRequest(compound.createRequest(Url.parse(MOUNT_PATH_1))) instanceof EmptyRequestHandler);
		assertNull("Mount path 2 should not match",
			compound.mapRequest(compound.createRequest(Url.parse(MOUNT_PATH_2))));
		assertTrue(
			"Mount path 3 should match",
			compound.mapRequest(compound.createRequest(Url.parse(MOUNT_PATH_3))) instanceof EmptyRequestHandler);
	}

}
