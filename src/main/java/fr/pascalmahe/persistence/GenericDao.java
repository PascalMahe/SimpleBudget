package fr.pascalmahe.persistence;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import fr.pascalmahe.business.Balance;
import fr.pascalmahe.business.Budget;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.User;

public class GenericDao {

	private static SessionFactory sessionFactory;
	
	private static final Logger logger = LogManager.getLogger();
	
	private static void setFactoryUp(){
		if(sessionFactory == null){
			 try {
			        // Create the SessionFactory from hibernate.cfg.xml
			        Configuration configuration = new Configuration();
			        configuration.configure("hibernate.cfg.xml");
			        
			        String databaseUrlEnvProperty = System.getenv("DATABASE_URL");
			        
			        if(databaseUrlEnvProperty != null){
			        	logger.debug("Detection de la propriété système DATABASE_URL");
			        	configuration.setProperty("hibernate.connection.url",
			        			databaseUrlEnvProperty);
			        	
			        	URI dbUri = new URI(databaseUrlEnvProperty);

				        String username = dbUri.getUserInfo().split(":")[0];
				        String password = dbUri.getUserInfo().split(":")[1];
				        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
				                + dbUri.getPort() + dbUri.getPath();
				        configuration
				                .setProperty("hibernate.connection.username", username);
				        configuration
				                .setProperty("hibernate.connection.password", password);
				        configuration.setProperty("hibernate.connection.url", dbUrl);
			        }
			        
			        logger.info("Hibernate configuration loaded");
			        
			        String dbUrl = configuration.getProperty("hibernate.connection.url");
			        String password = configuration.getProperty("hibernate.connection.password");
			        String user = configuration.getProperty("hibernate.connection.username");
			        
			        logger.debug("Hibernate username: " + user);
			        logger.debug("Hibernate password: " + password);
			        logger.debug("Hibernate dbUrl: " + dbUrl);

			        
			        try {
			        	createSessionFactory(configuration);
			        } catch(HibernateException he){
			        	logger.error("An error occurred while connecting to the database: " 
			        			+ he.getLocalizedMessage(), he);
			        	
			        	// Error while connecting to the database
			        	// Maybe it doesn't exist ? 
			        	// -> Trying to create it...
			        	
			        	int lastIndexOfSlash = dbUrl.lastIndexOf("/");
			        	String databaseName = dbUrl.substring(lastIndexOfSlash + 1);
			        	logger.warn("Hibernate can't connect to database. Trying to create schema: " + databaseName);
			        	
			        	String shortDbUrl = dbUrl.substring(0, lastIndexOfSlash + 1);
			        	try {
			        		Connection conn = DriverManager.getConnection(shortDbUrl, user, password);
			        		PreparedStatement stat = conn.prepareStatement(" CREATE DATABASE " + databaseName);
			        		stat.execute();
			        		
			        		if(!conn.getAutoCommit()){
			        			conn.commit();
			        		}
			        		conn.close();
			        		logger.info("Schema: " + databaseName + " created.");
				        	
			        		
			        		// Retry to connect via hibernate
			        		createSessionFactory(configuration);
			        		
						} catch (SQLException sqle) {
							logger.error("Error with database: " + shortDbUrl +
									" (with user: " + user + " and password: " + password + ")", sqle);
							throw new ExceptionInInitializerError(sqle);
						}
			        	
			        }
			     
			    } catch (URISyntaxException ex) {
			        // Make sure you log the exception, as it might be swallowed
			        logger.error("Initial SessionFactory creation failed.", ex);
			        throw new ExceptionInInitializerError(ex);
			    }
		}
	}

	private static void createSessionFactory(Configuration configuration) {
		// Add annotated classes 
		configuration.addAnnotatedClass(Balance.class);
		configuration.addAnnotatedClass(Budget.class);
		configuration.addAnnotatedClass(Category.class);
		configuration.addAnnotatedClass(Line.class);
		configuration.addAnnotatedClass(User.class);
		
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
		
		sessionFactory = configuration
                .buildSessionFactory(serviceRegistry);
		
		logger.debug("Hibernate configured and connected");
	}

	public static boolean saveOrUpdate(Object objectToSave) {
		
		setFactoryUp();
		
		Session currSession = sessionFactory.openSession();
		
		currSession.beginTransaction();
		
		logger.debug("Saving:  " + objectToSave);
		currSession.saveOrUpdate(objectToSave);
		
		currSession.getTransaction().commit();
		
		currSession.flush();
		currSession.close();
		logger.debug(objectToSave.getClass().getSimpleName() + " saved.");
		
		return true;
	}

	public static List<Line> searchBySiteDesi(String site, String desi) {
		
		setFactoryUp();
		logger.debug("searchBySiteDesi - searching on site: '" + site + "', desi: '" + desi + "'.");
		
		Session currSession = sessionFactory.openSession();
		currSession.beginTransaction();
		Criteria crita = currSession.createCriteria(Line.class);
		
		if(!StringUtils.isBlank(site)){
			crita.add(Restrictions.eq("mainLabel", desi));
		}

		if(!StringUtils.isBlank(site)){
			crita.add(Restrictions.eq("category", desi));
		}
		
		List<Line> returnList = crita.list();
		currSession.flush();
		currSession.close();
		
		logger.debug("searchBySiteDesi - returning " + returnList.size() + " result(s).");
		
		return returnList;
	}

	public static int count(Class classeToCount) {
		
		logger.debug("Counting " + classeToCount.getSimpleName() + "s.");

		setFactoryUp();
		
		Session currSession = sessionFactory.openSession();
		currSession.beginTransaction();
		Number result = (Number) currSession.createCriteria(classeToCount)
									.setProjection(Projections.rowCount())
									.uniqueResult();
		currSession.flush();
		currSession.close();
		
		logger.debug("Counted " + result.intValue() + " " + classeToCount.getSimpleName() + "s.");
		return result.intValue();
	}

	public static void delete(Object objectToDelete) {
		
		logger.debug("Deleting " + objectToDelete + ".");

		Session currSession = sessionFactory.openSession();
		currSession.beginTransaction();
		
		currSession.delete(objectToDelete);
		
		currSession.getTransaction().commit();
		currSession.flush();
		currSession.close();
		
		logger.debug(objectToDelete + " deleted.");
	}
}
