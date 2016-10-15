package fr.pascalmahe.services;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CookieService {

	private static final Logger logger = LogManager.getLogger();
	
	public static void addCookie(HttpServletResponse response, 
								String cookieName, 
								String value,
								Integer cookieAge) {
		logger.debug("Adding cookie named : " + cookieName + "...");
		
		Cookie cookie = new Cookie(cookieName, value);
		cookie.setPath("/");
		cookie.setMaxAge(cookieAge);
		response.addCookie(cookie);
		
		logger.debug("Adding cookie named : " + cookieName + " done.");
	}

	public static void removeCookie(HttpServletResponse response, String cookieName) {
		logger.debug("Removing cookie named : " + cookieName + "...");
		logger.debug("(ie. adding it with null value, so message that it's being"
				+ "added can be ignored if right below this one.)");
		
		addCookie(response, cookieName, null, 0);
		
		logger.debug("Removing cookie named : " + cookieName + " done.");
	}

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		logger.debug("Getting value of cookie named : " + cookieName + "...");
		
		Cookie[] cookieArray = request.getCookies();
		
		// snooping loop...
//		if(cookieArray != null){
//			StringBuilder snoop = new StringBuilder(" Found : " + cookieArray.length + " cookies:\n");
//			for(Cookie currCookie : cookieArray){
//				snoop.append("\t" + currCookie.getName() + "\n");
//			}
//			logger.debug(snoop.toString());
//		}
		
		if(cookieArray != null){
			for(Cookie currCookie : cookieArray){
				if(currCookie.getName().equalsIgnoreCase(cookieName)){
					logger.debug("Found cookie named : " + cookieName + ", returning value.");
					return currCookie.getValue();
				}
			}
		}
		
		logger.debug("Found no cookie named : " + cookieName + ", returning null.");
		return null;
	}

}
