/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.protocol.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import wicket.Application;
import wicket.ISessionFactory;
import wicket.Session;
import wicket.WicketRuntimeException;
import wicket.markup.html.link.AutolinkComponentResolver;
import wicket.markup.html.pages.InternalErrorPage;
import wicket.markup.html.pages.PageExpiredErrorPage;

/**
 * A web application is a subclass of Application which associates with an
 * instance of WicketServlet to serve pages over the HTTP protocol. This class
 * is intended to be subclassed by framework clients to define a web
 * application.
 * <p>
 * Application settings are given defaults by the WebApplication() constructor,
 * such as error page classes appropriate for HTML. WebApplication subclasses
 * can override these values and/or modify other application settings in their
 * respective constructors by calling getSettings() to retrieve a mutable
 * ApplicationSettings object.
 * <p>
 * If you want to use servlet specific configuration, e.g. using init parameters
 * from the {@link javax.servlet.ServletConfig}object, you should override the
 * init() method. For example:
 * 
 * <pre>
 *                public void init()
 *                {
 *                  String webXMLParameter = getWicketServlet()
 *                  			.getInitParameter(&quot;myWebXMLParameter&quot;);
 *                  URL schedulersConfig = getWicketServlet().getServletContext()
 *                  			.getResource(&quot;/WEB-INF/schedulers.xml&quot;);
 *                  ...
 * </pre>
 * 
 * @see WicketServlet
 * @see wicket.ApplicationSettings
 * @see wicket.ApplicationPages
 * @author Jonathan Locke
 * @author Chris Turner
 */
public abstract class WebApplication extends Application
{
	/** Serial Version ID. */
	private static final long serialVersionUID = 1152456333052646498L;

	/** Session factory for this web application */
	private ISessionFactory sessionFactory = new ISessionFactory()
	{
		public Session newSession()
		{
			return new WebSession(WebApplication.this);
		}
	};

	/** The WicketServlet that this application is attached to */
	private WicketServlet wicketServlet;

	/**
	 * Constructor.
	 */
	public WebApplication()
	{
		// Set default error pages for HTML markup
		getPages().setPageExpiredErrorPage(PageExpiredErrorPage.class).setInternalErrorPage(
				InternalErrorPage.class);

		// Add resolver for automatically resolving HTML links
		getComponentResolvers().add(new AutolinkComponentResolver());
	}

	/**
	 * @return The Wicket servlet for this application
	 */
	public WicketServlet getWicketServlet()
	{
		return wicketServlet;
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT CALL IT.
	 * 
	 * @param wicketServlet
	 *            The wicket servlet instance for this application
	 * @throws IllegalStateException
	 *             If an attempt is made to call this method once the wicket
	 *             servlet has been set for the application.
	 */
	public void setWicketServlet(WicketServlet wicketServlet)
	{
		if (this.wicketServlet == null)
		{
			this.wicketServlet = wicketServlet;
		}
		else
		{
			throw new IllegalStateException("WicketServlet cannot be changed once it is set");
		}
	}

	/**
	 * @see wicket.Application#getSessionFactory()
	 */
	protected ISessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	/**
	 * Initialize; if you need the wicket servlet for initialization, e.g.
	 * because you want to read an initParameter from web.xml or you want to
	 * read a resource from the servlet's context path, you can override this
	 * method and provide custom initialization. This method is called right
	 * after this application class is constructed, and the wicket servlet is
	 * set.
	 */
	protected void init()
	{
	}

	/**
	 * Creates a new WebRequestCycle object for this web application
	 * 
	 * @param session
	 *            The session
	 * @param request
	 *            The request
	 * @param response
	 *            The response
	 * @return The cycle
	 */
	protected WebRequestCycle newRequestCycle(final WebSession session, final WebRequest request,
			final WebResponse response)
	{
		// Respond to request
		return new WebRequestCycle(this, session, request, response);
	}

	/**
	 * Gets a WebSession object from the HttpServletRequest, creating a new one
	 * if it doesn't already exist.
	 * 
	 * @param request
	 *            The http request object
	 * @return The session object
	 */
	WebSession getSession(final HttpServletRequest request)
	{
		// Get session, creating if it doesn't exist
		final HttpSession httpSession = request.getSession(true);

		// The request session object is unique per web application, but wicket
		// requires it to be unique per servlet. That is, there must be a 1..n
		// relationship between HTTP sessions (JSESSIONID) and Wicket
		// applications.
		final String sessionAttributeName = "session-" + request.getServletPath();

		// Get Session abstraction from httpSession attribute
		WebSession webSession = (WebSession)httpSession.getAttribute(sessionAttributeName);
		if (webSession == null)
		{
			// Create session using session factory
			final Session session = getSessionFactory().newSession();
			if (session instanceof WebSession)
			{
				webSession = (WebSession)session;
				webSession.sessionAttributeName = sessionAttributeName;
			}
			else
			{
				throw new WicketRuntimeException(
						"Session created by a WebApplication session factory must be a subclass of WebSession");
			}

			// Set the client Locale for this session
			webSession.setLocale(request.getLocale());

			// Save this session in the HttpSession using the attribute name
			httpSession.setAttribute(sessionAttributeName, webSession);
		}

		// Attach / reattach http servlet session
		webSession.httpSession = httpSession;

		// Set the current session to the session we just retrieved
		Session.set(webSession);

		return webSession;
	}
}