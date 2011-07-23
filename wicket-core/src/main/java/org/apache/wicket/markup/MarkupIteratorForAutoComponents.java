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

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.border.Border.BorderBodyContainer;
import org.apache.wicket.markup.resolver.ComponentResolvers;
import org.apache.wicket.markup.resolver.HasEqualMarkupResolver;
import org.apache.wicket.util.iterator.IteratorFilter;

/**
 * 
 * @author Juergen Donnerstag
 */
public class MarkupIteratorForAutoComponents extends MarkupIterator
{
	/**
	 * Construct.
	 * 
	 * @param stream
	 */
	public MarkupIteratorForAutoComponents(final MarkupStream stream)
	{
		super(stream);

		openTagsOnly();
		setDontGoDeeper(true);
	}

	/**
	 * Construct.
	 * 
	 * @param markup
	 */
	public MarkupIteratorForAutoComponents(final IMarkupFragment markup)
	{
		this(new MarkupStream(markup));
	}

	/**
	 * 
	 * @param container
	 */
	public void onTagFound(final MarkupContainer container)
	{
		MarkupStream stream = getMarkupStream();
		ComponentTag tag = stream.getTag();
		Component child = new HasEqualMarkupResolver().resolve(container, stream, tag);
		if (child == null)
		{
			child = ComponentResolvers.resolve(container, stream, tag, null);
			if (child != null)
			{
				if (container.contains(child, false) == false)
				{
					child.setAuto(true);
					if (!(child instanceof BorderBodyContainer))
					{
						container.add(child);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param dontGoDeeper
	 * @return this
	 */
	@Override
	public MarkupIteratorForAutoComponents setDontGoDeeper(boolean dontGoDeeper)
	{
		super.setDontGoDeeper(dontGoDeeper);
		return this;
	}

	/**
	 * @param filter
	 * @return this
	 */
	@Override
	public MarkupIteratorForAutoComponents addFilter(final IteratorFilter<ComponentTag> filter)
	{
		super.addFilter(filter);
		return this;
	}

	/**
	 * @return this
	 */
	@Override
	public MarkupIteratorForAutoComponents skipComponentTags()
	{
		super.skipComponentTags();
		return this;
	}

	/**
	 * @return this
	 */
	@Override
	public MarkupIteratorForAutoComponents skipWicketTags()
	{
		super.skipWicketTags();
		return this;
	}

	/**
	 * @return this
	 */
	@Override
	public MarkupIteratorForAutoComponents openTagsOnly()
	{
		super.openTagsOnly();
		return this;
	}
}
