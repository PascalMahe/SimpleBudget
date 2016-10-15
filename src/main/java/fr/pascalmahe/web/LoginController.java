package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.User;
import fr.pascalmahe.services.CookieService;
import fr.pascalmahe.services.UserService;


@ManagedBean
public class LoginController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = 3973801993975443027L;
	
	private static final Logger logger = LogManager.getLogger();

	/*
	 * Variables d'instance
	 */	
	private Locale locale = Locale.getDefault();
	
	private String login;

	private String password;
	
	private Boolean rememberMe;
	
	/*
	 * Constructeurs
	 */
	public LoginController(){
		logger.debug("constructor");
	}
	
	/*
	 * Méthodes
	 */
	public String loginAction(){
		logger.debug("loginAction - start");
		String redirect = "";
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext()
				.getRequest();
		HttpSession sess = request.getSession();
		if(sess.getAttribute(WebConstants.ERROR_MESSAGE_ATTR) != null){
			logger.debug("loginAction - Cleaning up session.");
			sess.setAttribute(WebConstants.ERROR_MESSAGE_ATTR, null);
		}
		
		logger.debug("loginAction - login: " + login);
		logger.debug("loginAction - password: " + password);
		
		User validUser = UserService.getValidUser(login, password);
		if(validUser != null){
			
			// if remember is true, we add a cookie
			// (consumed by the LoginFilter)
			HttpServletResponse response = (HttpServletResponse) FacesContext
										.getCurrentInstance()
										.getExternalContext()
										.getResponse();
								
			if(rememberMe){
				String uuid = UUID.randomUUID().toString();
				UserService.saveUUID(validUser, uuid);
				CookieService.addCookie(response, 
										WebConstants.REMEMBERME_COOKIE_NAME, 
										uuid, 
										WebConstants.REMEMBERME_COOKIE_AGE);
			} else {
				// erase cookie
				UserService.deleteUUID(validUser);
				CookieService.removeCookie(response, 
						WebConstants.REMEMBERME_COOKIE_NAME);
			}
			
			request.getSession().setAttribute(WebConstants.USER_ATTRIBUTE, validUser);
			redirect = WebConstants.DASHBOARD_PAGE + WebConstants.REDIRECT_FLAG;
		} else {
			FacesMessage fmWrongPwd = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
											"Mauvais login/mot de passe.", "");
			FacesContext.getCurrentInstance().addMessage("wrong_login_pwd", fmWrongPwd);
		}
		
		logger.debug("loginAction - redirecting to: " + redirect);
		return redirect;
	}
	
	public String logoutAction(){
		logger.debug("logoutAction - start");
		String redirect = "";
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
										.getExternalContext()
										.getRequest();
		request.getSession().setAttribute(WebConstants.USER_ATTRIBUTE, null);
		redirect = WebConstants.LOGIN_PAGE + WebConstants.REDIRECT_FLAG + WebConstants.LOGOUT_FLAG;
		
		logger.debug("logoutAction - redirecting to: " + redirect);
		return redirect;
	}
	
	public void fetchErrorMessagesAction(){

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext()
				.getRequest();
		HttpSession sess = request.getSession();
		if(sess.getAttribute(WebConstants.ERROR_MESSAGE_ATTR) != null){
			logger.debug("fetchErrorMessagesAction - Fetching error message in session...");
			String errorMessage = (String) sess.getAttribute(WebConstants.ERROR_MESSAGE_ATTR);
			sess.setAttribute(WebConstants.ERROR_MESSAGE_ATTR, null);
			FacesMessage fmLoginError = 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
										errorMessage, 
										errorMessage);
			FacesContext.getCurrentInstance()
						.addMessage("login_error_message", fmLoginError);
			List<FacesMessage> messageList = FacesContext.getCurrentInstance().getMessageList();
			StringBuilder strDebug = new StringBuilder("fetchErrorMessagesAction - Messages : \n");
			for(FacesMessage currMess : messageList){
				strDebug.append("\t" + currMess.getSeverity() + " - " + currMess.getSummary()+ "\n");
			}
			logger.debug(strDebug.toString());
		}
	}
	/*
	 * Getters et Setters
	 */
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(Boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	
}

