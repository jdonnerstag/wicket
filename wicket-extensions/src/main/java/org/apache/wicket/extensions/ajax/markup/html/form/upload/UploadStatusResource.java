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
package org.apache.wicket.extensions.ajax.markup.html.form.upload;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequestImpl;
import org.apache.wicket.protocol.http.servlet.UploadInfo;
import org.apache.wicket.request.resource.AbstractResource;

/**
 * A resource that prints out basic statistics about the current upload. This resource is used to
 * feed the progress bar information by the progress bar javascript which requests this resource
 * through ajax.
 * 
 * @author Andrew Lombardi
 * @author Igor Vaynberg (ivaynberg)
 */
class UploadStatusResource extends AbstractResource
{

	private static final long serialVersionUID = 1L;

	@Override
	protected ResourceResponse newResourceResponse(final Attributes attributes)
	{
		// Determine encoding
		final String encoding = Application.get()
			.getRequestCycleSettings()
			.getResponseRequestEncoding();

		ResourceResponse response = new ResourceResponse();
		response.setContentType("text/html; charset=" + encoding);

		final String status = getStatus(attributes);
		response.setWriteCallback(new WriteCallback()
		{
			@Override
			public void writeData(final Attributes attributes)
			{
				attributes.getResponse().write("<html><body>|");
				attributes.getResponse().write(status);
				attributes.getResponse().write("|</body></html>");
			}
		});

		return response;
	}

	/**
	 * @param attributes
	 * @return Status string with progress data that will feed the progressbar.js variables on
	 *         browser to update the progress bar
	 */
	private String getStatus(final Attributes attributes)
	{
		HttpServletRequest req = (HttpServletRequest)attributes.getRequest().getContainerRequest();
		UploadInfo info = MultipartServletWebRequestImpl.getUploadInfo(req);

		String status = null;
		if ((info == null) || (info.getTotalBytes() < 1))
		{
			status = "100|";
		}
		else
		{
			Locale locale = Session.get().getLocale();

			String pattern = getStatus("statusUpdate", locale);

			status = info.getPercentageComplete() +
				"|" +
				MessageFormat.format(pattern, info.getPercentageComplete(),
					info.getBytesUploadedString(locale), info.getTotalBytesString(locale),
					info.getTransferRateString(locale), info.getRemainingTimeString(locale));
		}
		return status;
	}

	/**
	 * Get a status message for the given key.
	 * 
	 * @param key
	 *            message key
	 * @param locale
	 *            locale for message
	 * @return status message
	 */
	static String getStatus(String key, Locale locale)
	{
		return ResourceBundle.getBundle(UploadStatusResource.class.getName(), locale)
			.getString(key);
	}
}
