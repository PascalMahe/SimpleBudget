package fr.pascalmahe.web;

public class WebConstants {
	
	public static final String USER_ATTRIBUTE = "user";
	
	public static final String PAGE_SUFFIX = ".xhtml";
	
	public static final String WEB_SUBDOMAIN = "/simplebudget";
	
	public static final String SECURITY_PREFIX = "/secured";
	
	public static final String LOGIN_PAGE = "/login" + PAGE_SUFFIX;
	
	public static final String ERROR_MESSAGE_ATTR = "error_message";

	public static final String REMEMBERME_COOKIE_NAME = "remember_me_cookie";
	
	// cookie age in seconds: 90 days * 24 hrs * 60 min * 60 secs
	public static final Integer REMEMBERME_COOKIE_AGE = 90 * 24 * 60 * 60;

	public static final String TEST_HOST = "http://localhost:5000";
	
	public static final String TEST_LOGIN_URL = TEST_HOST + LOGIN_PAGE;
	
	public static final String TEST_ROOT_URL = TEST_HOST + "/";

	public static final String HEROKUAPP_HOST = "https://immense-headland-25588.herokuapp.com";
	
	public static final String HEROKUAPP_ROOT_URL = HEROKUAPP_HOST + "/";
	
	public static final String HEROKUAPP_LOGIN_URL = HEROKUAPP_HOST + LOGIN_PAGE;

	public static final String DASHBOARD_PAGE = SECURITY_PREFIX + "/dashboard" + PAGE_SUFFIX;

	public static final String LOGOUT_PARAM_NAME = "logout";
	
	public static final String LOGOUT_FLAG = "&" + LOGOUT_PARAM_NAME + "=true";

	public static final String REDIRECT_FLAG = "?faces-redirect=true";;
	
}
