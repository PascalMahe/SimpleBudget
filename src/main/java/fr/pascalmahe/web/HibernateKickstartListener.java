package fr.pascalmahe.web;

import java.io.Serializable;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.services.UserService;

@WebListener
public class HibernateKickstartListener implements Serializable, ServletContextListener {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = -2467732597162573286L;

	private static final Logger logger = LogManager.getLogger();

	/*
	 * Constructeurs
	 */
	public HibernateKickstartListener(){
		logger.debug("constructor");
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		logger.info("Kickstarting Hibernate...");
		UserService.getValidUser("", "");
		
		logger.info("There, Hibernate is up and running!");
		
	}
	
	
}

