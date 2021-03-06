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
package org.apache.wicket.request.resource.caching.version;

import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Time;

/**
 * Uses the last modified timestamp of a {@link PackageResourceReference} 
 * converted to milliseconds as a version string.
 *
 * @author Peter Ertl
 *
 * @since 1.5
 */
public class LastModifiedResourceVersion implements IResourceVersion
{
	public String getVersion(PackageResourceReference resourceReference)
	{
		// get last modified timestamp of resource
		final Time lastModified = resourceReference.getLastModified();

		// if no timestamp is available we can not provide a version
		if (lastModified == null)
		{
			return null;
		}
		// version string = last modified timestamp converted to milliseconds
		return String.valueOf(lastModified.getMilliseconds());
	}
}
