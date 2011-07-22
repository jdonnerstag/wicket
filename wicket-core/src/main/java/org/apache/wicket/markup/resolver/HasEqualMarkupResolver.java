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
package org.apache.wicket.markup.resolver;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.MarkupStream;

/**
 * Check if any of the container's children (not grand children) has the very same markup attached,
 * we are at right now.
 * 
 * @author Juergen Donnerstag
 */
public class HasEqualMarkupResolver implements IComponentResolver
{
	private static final long serialVersionUID = 1L;

	public Component resolve(final MarkupContainer container, final MarkupStream markupStream,
		final ComponentTag tag)
	{
		for (Component child : container)
		{
			IMarkupFragment markup = child.getMarkup();
			if (markup != null)
			{
				if (tag == markup.get(0))
				{
					return child;
				}
			}
		}

		// We were not able to handle the componentId
		return null;
	}
}