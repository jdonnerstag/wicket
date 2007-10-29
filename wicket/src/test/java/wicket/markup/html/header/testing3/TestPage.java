/*
 * $Id: ContainerWithAssociatedMarkupHelper.java,v 1.1 2006/03/10 22:20:42
 * jdonnerstag Exp $ $Revision$ $Date: 2006/03/10 22:20:42 $
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
package wicket.markup.html.header.testing3;

import wicket.Page;
import wicket.markup.html.WebPage;
import wicket.markup.html.link.IPageLink;
import wicket.markup.html.link.PageLink;
import wicket.markup.html.panel.Panel;

/**
 * 
 * @author Juergen Donnerstag
 */
public class TestPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	private final Panel panel1 = new Panel1("panel");
	private final Panel panel2 = new Panel2("panel");
	private Panel current;
	
	/**
	 * Construct.
	 */
	public TestPage()
	{
		this.current = panel1;
		add(current);
		
		add(new PageLink("link", new IPageLink()
				{
					private static final long serialVersionUID = 1L;

					public Page getPage()
					{
						replacePanel();
						return TestPage.this;
					}

					public Class getPageIdentity()
					{
						return TestPage.this.getClass();
					}
				}));
	}
	
	/**
	 * 
	 */
	public void replacePanel()
	{
		this.current = (this.current == this.panel1 ? this.panel2 : this.panel1);
		replace(this.current);
	}
}