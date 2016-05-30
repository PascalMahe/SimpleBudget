package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
		logger.error("Constructeur de LoginController");
		logger.fatal("Constructeur de LoginController");
		logger.warn("Constructeur de LoginController");
		logger.info("Constructeur de LoginController");
		logger.debug("Constructeur de LoginController");
		logger.trace("Constructeur de LoginController");
	}
	
	/*
	 * Méthodes
	 */
	public String loginAction(ActionEvent event){
		logger.debug("loginAction - start");
		String redirect = "";
		
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

