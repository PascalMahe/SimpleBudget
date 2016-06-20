package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.ex.LoginAlreadyExistsException;
import fr.pascalmahe.services.UserService;


@ManagedBean
public class AddUserController implements Serializable {

	/*
	 * Variables statiques
	 */
	
	private static final long serialVersionUID = 397384593975443027L;
	
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
	public AddUserController(){
		logger.debug("AddUserController - constructor");
	}
	
	/*
	 * Méthodes
	 */
	public String addUserAction(){
		logger.debug("addUserAction - start");
		String redirect = "";
		
		logger.debug("addUserAction - login: " + login);
		logger.debug("addUserAction - password: " + password);
		
		try {
			UserService.addUser(login, password);
		} catch(LoginAlreadyExistsException laee){
			logger.info("Login already exists. Error message to user.");
			FacesMessage fmWrongPwd = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Login existe déjà.", 
					"Ce login est déjà employé par un utilisateur en base de données, choisissez-en un autre.");
			FacesContext.getCurrentInstance().addMessage("login_already_exists", fmWrongPwd);
		}
		
		logger.debug("addUserAction - redirecting to: " + redirect);
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

