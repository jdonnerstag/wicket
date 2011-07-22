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
package org.apache.wicket.markup.html.panel;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;

/**
 * A panel is a reusable component that holds markup and other components.
 * <p>
 * Whereas WebMarkupContainer is an inline container like
 * 
 * <pre>
 *  ...
 *  &lt;span wicket:id=&quot;xxx&quot;&gt;
 *    &lt;span wicket:id=&quot;mylabel&quot;&gt;My label&lt;/span&gt;
 *    ....
 *  &lt;/span&gt;
 *  ...
 * </pre>
 * 
 * a Panel has its own associated markup file and the container content is taken from that file,
 * like:
 * 
 * <pre>
 *  &lt;span wicket:id=&quot;mypanel&quot;/&gt;
 * 
 *  TestPanel.html
 *  &lt;wicket:panel&gt;
 *    &lt;span wicket:id=&quot;mylabel&quot;&gt;My label&lt;/span&gt;
 *    ....
 *  &lt;/wicket:panel&gt;
 * </pre>
 * 
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public abstract class Panel extends WebMarkupContainer
{
	private static final long serialVersionUID = 1L;

	/** */
	public static final String PANEL = "panel";

	static
	{
		// register "wicket:panel"
		WicketTagIdentifier.registerWellKnownTagName(PANEL);
	}

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public Panel(final String id)
	{
		super(id);
	}

	/**
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public Panel(final String id, final IModel<?> model)
	{
		super(id, model);
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy()
	{
		return new PanelMarkupSourcingStrategy(false);
	}

	// TODO This code should be moved into PanelMSS
	// TODO Border require the same code => copy
	// TODO Fragment requires the same code => copy
	@Override
	protected void enqueueAutoComponents()
	{
		super.enqueueAutoComponents();

		// <wicket:panel> ..
		IMarkupFragment markup = getMarkup(null);
		MarkupStream stream = new MarkupStream(markup);
		stream.next();
		enqueueAutoComponents(stream);

		// <wicket:head> ..
		markup = getAssociatedMarkup();
		stream = new MarkupStream(markup);
		while (stream.hasMore())
		{
			MarkupElement elem = stream.get();
			if (elem instanceof ComponentTag)
			{
				ComponentTag tag = (ComponentTag)elem;
				if (elem instanceof WicketTag)
				{
					WicketTag wtag = (WicketTag)elem;
					if (wtag.isOpen() && wtag.isHeadTag())
					{
						IMarkupFragment m = stream.getMarkupFragment();
						MarkupStream s = new MarkupStream(m);
						s.next();
						enqueueAutoComponents(s);
					}
				}

				if (tag.isOpen())
				{
					stream.skipToMatchingCloseTag(tag);
				}
			}
			stream.next();
		}
	}
}
