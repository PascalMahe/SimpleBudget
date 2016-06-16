package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	/*
	 * Constructeurs
	 */
	public LoginController(){
		logger.debug("LoginController - constructor");
	}
	
	/*
	 * Méthodes
	 */
	public String loginAction(){
		logger.debug("loginAction - start");
		String redirect = "";
		
		logger.debug("loginAction - login: " + login);
		logger.debug("loginAction - password: " + password);
		
		boolean isUserValid = UserService.isUserValid(login, password);
		if(isUserValid){
			redirect = "dashboard.xhtml?faces-redirect=true";
		} else {
			FacesMessage fmWrongPwd = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
											"Mauvais login/mot de passe.", 
											"Aucune correspondence n'a été "
											+ "trouvée pour cette combinaison "
											+ "de login et de mot de passe.");
			FacesContext.getCurrentInstance().addMessage("wrong_login_pwd", fmWrongPwd);
		}
		
		logger.debug("loginAction - redirecting to: " + redirect);
		return redirect;
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

	
}

