/*
 * $Id$ $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.extensions.ajax.markup.html.repeater.data.table;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.IAjaxCallDecorator;
import wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByBorder;
import wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import wicket.extensions.markup.html.repeater.data.table.DataTable;
import wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import wicket.markup.html.WebMarkupContainer;

/**
 * Ajaxified {@link HeadersToolbar}
 * 
 * @see HeadersToolbar
 * 
 * @author ivaynberg
 * 
 */
public class AjaxFallbackHeadersToolbar extends HeadersToolbar
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param table
	 * @param stateLocator
	 */
	public AjaxFallbackHeadersToolbar(DataTable table, ISortStateLocator stateLocator)
	{
		super(table, stateLocator);
		table.setOutputMarkupId(true);
	}


	/**
	 * @see wicket.extensions.markup.html.repeater.data.table.HeadersToolbar#newSortableHeader(java.lang.String,
	 *      java.lang.String,
	 *      wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator)
	 */
	protected WebMarkupContainer newSortableHeader(String borderId, String property,
			ISortStateLocator locator)
	{
		return new AjaxFallbackOrderByBorder(borderId, property, locator, getAjaxCallDecorator())
		{
			private static final long serialVersionUID = 1L;

			protected void onAjaxClick(AjaxRequestTarget target)
			{
				target.addComponent(getTable());

			}

		};
	}

	/**
	 * Returns a decorator that will be used to decorate ajax links used in
	 * sortable headers
	 * 
	 * @return decorator or null for none
	 */
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return null;
	}


}