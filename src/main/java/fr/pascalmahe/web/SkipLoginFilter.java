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


@WebFilter(filterName="skipLoginFilter", urlPatterns = {"/login.xhtml", "/"})
public class SkipLoginFilter implements Serializable, Filter {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = -1576144532794743551L;

	private static final Logger logger = LogManager.getLogger();

	/*
	 * Constructeurs
	 */
	public SkipLoginFilter(){
		logger.debug("constructor");
	}

	@Override
	public void destroy() {
		// nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// if a user is in session -> go to dashboard.xhtml
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURL().toString();

		String logoutParameter = (String) httpRequest.getParameter(WebConstants.LOGOUT_PARAM_NAME);
		
		boolean logoutFlagPresent = Boolean.parseBoolean(logoutParameter);
		
		String debugLogin = "doFilter - Asked for : " + url;
		debugLogin += ", ";
		if(logoutFlagPresent){
			debugLogin += "with";
		} else {
			debugLogin += "no";
		}
		debugLogin += " logout parameter.";
		
		logger.debug(debugLogin);
		
		// fetching user from session
		User userInSession = (User) httpRequest.getSession().getAttribute(WebConstants.USER_ATTRIBUTE);
		
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
		
		if(userInSession != null){

			logger.debug("doFilter - User " + userInSession.getLogin() + " in session.");
			
			// if user asked for main page (http://localhost:5000/ or 
			// https://immense-headland-25588.herokuapp.com/), redirect to dashboard
			
			// naive implementation: CONSTANTS ! \o/ 
			if(!logoutFlagPresent && 
					(url.equalsIgnoreCase(WebConstants.LOCAL_HEROKU_ROOT_URL) 
					|| url.equalsIgnoreCase(WebConstants.LOCAL_HEROKU_LOGIN_URL)
					|| url.equalsIgnoreCase(WebConstants.LOCAL_TOMCAT_ROOT_URL)
					|| url.equalsIgnoreCase(WebConstants.LOCAL_TOMCAT_LOGIN_URL)
					|| url.equalsIgnoreCase(WebConstants.HEROKUAPP_ROOT_URL)
					|| url.equalsIgnoreCase(WebConstants.HEROKUAPP_LOGIN_URL))){
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				logger.debug("doFilter - Wants to go to main or login page -> skip that step and go to DASHBOARD!");
				String dashboardURL = WebConstants.DASHBOARD_PAGE;
				if(url.equalsIgnoreCase(WebConstants.LOCAL_TOMCAT_ROOT_URL)
						|| url.equalsIgnoreCase(WebConstants.LOCAL_TOMCAT_LOGIN_URL)){
					dashboardURL = WebConstants.WEB_SUBDOMAIN + WebConstants.DASHBOARD_PAGE;
				}
				httpResponse.sendRedirect(dashboardURL);
			} else {
				logger.debug("doFilter - Moving on...");
				chain.doFilter(request, response);
			}
		} else {
			logger.debug("doFilter - No user in session, moving on...");
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// nothing to do
	}
	
}

