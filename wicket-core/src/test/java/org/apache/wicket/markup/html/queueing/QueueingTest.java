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
package org.apache.wicket.markup.html.queueing;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Juergen Donnerstag
 */
public class QueueingTest extends WicketTestCase
{
	private static final Logger log = LoggerFactory.getLogger(QueueingTest.class);

	/** */
	@Test
	public void page1()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='label1'>mein Label</span>" //
			+ "</body></html>");

		page.queue(new Label("label1", "test"));

		executeMyTest(page,
			"<html xmlns:wicket><body><span wicket:id=\"label1\">test</span></body></html>");
	}

	/** */
	@Test
	public void page2()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='label1'>mein Label</span>" //
			+ "<span wicket:id='label2'>mein Label</span>" //
			+ "</body></html>");

		page.queue(new Label("label2", "22"));
		page.queue(new Label("label1", "11"));

		executeMyTest(
			page,
			"<html xmlns:wicket><body><span wicket:id=\"label1\">11</span><span wicket:id=\"label2\">22</span></body></html>");
	}

	/** */
	@Test
	public void page2a()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='label1'>mein Label</span>" //
			+ "<span wicket:id='label2'>mein Label</span>" //
			+ "</body></html>");

		page.queue(new Label("label1", "11"));
		page.queue(new Label("label2", "22"));

		executeMyTest(
			page,
			"<html xmlns:wicket><body><span wicket:id=\"label1\">11</span><span wicket:id=\"label2\">22</span></body></html>");
	}

	/** */
	@Test
	public void page3()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='c-1'>" //
			+ "<span wicket:id='l-1'>mein Label</span>" //
			+ "</span>" //
			+ "<span wicket:id='l-2'>mein Label</span>" //
			+ "</body></html>");

		page.queue(new WebMarkupContainer("c-1"));
		page.queue(new Label("l-1", "11"));
		page.queue(new Label("l-2", "22"));

		executeMyTest(
			page,
			"<html xmlns:wicket><body><span wicket:id=\"c-1\"><span wicket:id=\"l-1\">11</span></span><span wicket:id=\"l-2\">22</span></body></html>");
	}

	/** */
	@Test
	public void page4()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='c-1'>" //
			+ "<span wicket:id='l-1'>mein Label</span>" //
			+ "<span wicket:id='l-2'>mein Label</span>" //
			+ "</span>" //
			+ "</body></html>");

		page.queue(new WebMarkupContainer("c-1"));
		page.queue(new Label("l-1", "11"));
		page.queue(new Label("l-2", "22"));

		executeMyTest(
			page,
			"<html xmlns:wicket><body><span wicket:id=\"c-1\"><span wicket:id=\"l-1\">11</span><span wicket:id=\"l-2\">22</span></span></body></html>");
	}

	/** */
	@Test
	public void page5()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='l-1'>mein Label</span>" //
			+ "<span wicket:id='c-1'>" //
			+ "<span wicket:id='l-2'>mein Label</span>" //
			+ "</span>" //
			+ "</body></html>");

		page.queue(new WebMarkupContainer("c-1"));
		page.queue(new Label("l-1", "11"));
		page.queue(new Label("l-2", "22"));

		executeMyTest(
			page,
			"<html xmlns:wicket><body><span wicket:id=\"l-1\">11</span><span wicket:id=\"c-1\"><span wicket:id=\"l-2\">22</span></span></body></html>");
	}

	/** */
	@Test(expected = IllegalStateException.class)
	public void page6()
	{
		Page page = new MyPage("");

		page.queue(new Label("l-1", "11"));
		page.queue(new Label("l-1", "22"));
	}

	/** */
	@Test
	public void page7()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='l-1'>mein Label</span>" //
			+ "<span wicket:id='c-1'>" //
			+ "<span wicket:id='l-1'>mein Label</span>" //
			+ "</span>" //
			+ "</body></html>");

		MarkupContainer c;
		page.queue(c = new WebMarkupContainer("c-1"));
		c.queue(new Label("l-1", "11"));
		page.queue(new Label("l-1", "22"));

		executeMyTest(
			page,
			"<html xmlns:wicket><body><span wicket:id=\"l-1\">22</span><span wicket:id=\"c-1\"><span wicket:id=\"l-1\">11</span></span></body></html>");
	}

	/** */
	@Test(expected = IllegalStateException.class)
	public void page8()
	{
		Page page = new MyPage("" //
			+ "<html xmlns:wicket><body>" //
			+ "<span wicket:id='l-1'>mein Label</span>" //
			+ "</body></html>");

		page.queue(new Label("aaa", "22"));

		executeMyTest(page, null);
	}

	/** */
	public class MyPage extends WebPage
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @param markup
		 */
		public MyPage(final String markup)
		{
			setMarkup(Markup.of(markup));
		}
	}

	/**
	 * 
	 * @param page
	 * @param result
	 */
	public void executeMyTest(final Page page, final String result)
	{
		tester.startPage(page);
		tester.assertRenderedPage(page.getClass());
		String document = tester.getLastResponseAsString();
		assertEquals(result, document);
	}
}