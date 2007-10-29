/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.parser.filter;


import wicket.markup.html.WebPage;


/**
 * Mock page for testing.
 * 
 * @author Chris Turner
 */
public class HeaderSectionPage_6 extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public HeaderSectionPage_6()
	{
		add(new HeaderSectionPanel("panel1"));
		add(new HeaderSectionPanel("panel2"));
		add(new HeaderSectionPanel("panel3"));

		add(new HeaderSectionBorder("border1"));
		add(new HeaderSectionBorder("border2"));
		add(new HeaderSectionBorder("border3"));
	}
}