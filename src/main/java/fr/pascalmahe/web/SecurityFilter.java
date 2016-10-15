package fr.pascalmahe.web;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.User;
import fr.pascalmahe.services.CookieService;
import fr.pascalmahe.services.UserService;


@WebFilter(filterName="securityFilter", urlPatterns = {"/secured/*"})
public class SecurityFilter implements Serializable, Filter {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = -1576144532794743551L;

	private static final Logger logger = LogManager.getLogger();

	/*
	 * Constructeurs
	 */
	public SecurityFilter(){
		logger.debug("constructor");
	}

	@Override
	public void destroy() {
		// nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// if a user is in session -> nothing else to do
		// else
		//	if not going to login.xhtml
		//		redirect to login.xhtml
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		logger.debug("doFilter - Asked for : " + httpRequest.getRequestURL());
		
		// fetching user from session
		User userInSession = (User) httpRequest.getSession().getAttribute(WebConstants.USER_ATTRIBUTE);
		
		// if no user in session, checking cookie
		// (and extending cookie/adding user to session/deleting cookie
		// as needed)
		if(userInSession == null){
			logger.debug("doFilter - No user in session, checking cookie...");
			// checking if the user has a cookie to connect
			String uuid = CookieService.getCookieValue(httpRequest, WebConstants.REMEMBERME_COOKIE_NAME);
			if(uuid != null){
				userInSession = UserService.getUserOnUUID(uuid);
				if(userInSession != null){
					logger.debug("doFilter - Cookie with user found. Loging in...");
					// add user in session
					httpRequest.getSession().setAttribute(WebConstants.USER_ATTRIBUTE, userInSession);
					// extend cookie age
					CookieService.addCookie(
							(HttpServletResponse) response, 
							WebConstants.REMEMBERME_COOKIE_NAME, 
							uuid, 
							WebConstants.REMEMBERME_COOKIE_AGE);
				} else {
					logger.debug("doFilter - Cookie found but with invalid user. Deleting cookie...");
					// remove cookie
					CookieService.removeCookie(
							(HttpServletResponse) response, 
							WebConstants.REMEMBERME_COOKIE_NAME);
				}
			} else {
				logger.debug("doFilter - No cookie found.");
			}
		}
		String url = httpRequest.getRequestURL().toString();
		if(userInSession != null){

			logger.debug("doFilter - User " + userInSession.getLogin() + " in session. Moving on...");
			chain.doFilter(request, response);
		} else {
			if(url.contains(WebConstants.PAGE_SUFFIX) && 
					!url.endsWith(WebConstants.LOGIN_PAGE)){
				logger.debug("doFilter - No user in session. Redirecting to login page...");
				
				// message to the user
				httpRequest.getSession().setAttribute(WebConstants.ERROR_MESSAGE_ATTR, "Veuillez vous connecter.");

				// redirect to login.xhtml
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.sendRedirect(WebConstants.WEB_SUBDOMAIN + WebConstants.LOGIN_PAGE);
			} else {
				logger.debug("doFilter - Going to login page...");
				chain.doFilter(request, response);
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// nothing to do
	}
	
}

