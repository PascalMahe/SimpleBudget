package fr.pascalmahe.persistence;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.JoinType;

import fr.pascalmahe.business.Balance;
import fr.pascalmahe.business.Budget;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.User;

public class GenericDao<T> {

	private static SessionFactory sessionFactory;
	
	private static final Logger logger = LogManager.getLogger();
	
	private static final String CRITERIA_STRING_NAKED_START = " following criterias: ";
	
	private Class<T> classToUse;
	
	public GenericDao(Class<T> classUse){
		this.classToUse = classUse;
		setFactoryUp();
	}
	
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
		configuration.addAnnotatedClass(Categorisation.class);
		configuration.addAnnotatedClass(Line.class);
		configuration.addAnnotatedClass(User.class);
		
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
		
		sessionFactory = configuration
                .buildSessionFactory(serviceRegistry);
		
		logger.debug("Hibernate configured and connected");
	}

	public void saveOrUpdate(T objectToSave) {
		Session currSession = sessionFactory.openSession();
		
		currSession.beginTransaction();
		
		logger.debug("Saving:  " + objectToSave + "...");
		try {
			currSession.saveOrUpdate(objectToSave);
			currSession.getTransaction().commit();
		}
		finally {
			currSession.close();
		}
		
		logger.debug(objectToSave.getClass().getSimpleName() + " saved.");
	}

	public int count() {
		logger.debug("Counting " + classToUse.getSimpleName() + "s...");

		Session currSession = sessionFactory.openSession();
		currSession.beginTransaction();
		Number result = (Number) currSession.createCriteria(classToUse)
									.setProjection(Projections.rowCount())
									.uniqueResult();
		currSession.close();
		
		logger.debug("Counted " + result.intValue() + " " + classToUse.getSimpleName() + "s.");
		return result.intValue();
	}

	public void delete(T objectToDelete) {
		logger.debug("Deleting " + objectToDelete + ".");

		Session currSession = sessionFactory.openSession();
		currSession.beginTransaction();
		try {
			currSession.delete(objectToDelete);
			currSession.getTransaction().commit();
		} finally {
			currSession.close();
		}
		logger.debug(objectToDelete + " deleted.");
	}
	
	public T fetch(Integer id){
		logger.debug("Fetching " + classToUse.getSimpleName() + " with #" + id +"...");

		Session currSession = sessionFactory.openSession();
		T fetched = currSession.get(classToUse, id);
		
		currSession.close();
		logger.debug("Found : " + fetched);
		return fetched;
	}
	
	public List<T> search(Map<String, Object> searchCriteriaMap){

		String criteriaString = " no criterias";
		if(searchCriteriaMap.size() > 0){
			criteriaString = CRITERIA_STRING_NAKED_START;
		}
		Session currSession = sessionFactory.openSession();
		
		Criteria crita = currSession.createCriteria(classToUse);
		for(String property : searchCriteriaMap.keySet()){
			Object value = searchCriteriaMap.get(property);
			
			// adding to the debug message
			if(criteriaString.length() != CRITERIA_STRING_NAKED_START.length()){
				criteriaString += ", ";
			}
			criteriaString += property + " = '" + value + "'";
			
			// adding to the criteria
			crita.add(Restrictions.eqOrIsNull(property, value));
		}
		
		logger.debug("Searching " + classToUse.getSimpleName() + "s with" + criteriaString + ".");
		
		// To avoid duplicates in list returned
		// cf. http://stackoverflow.com/a/4645549
		crita.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		@SuppressWarnings("unchecked")
		List<T> listToReturn = crita.list();
		
		logger.debug("Returning " + listToReturn.size() + " " + classToUse.getSimpleName() + "s.");
		return listToReturn;
	}
	
	public List<T> searchBudgetContainingCategory(Category cat){

		Session currSession = sessionFactory.openSession();
		
		Criteria crita = currSession.createCriteria(classToUse);
		crita.createAlias("categoryList", "cat", JoinType.INNER_JOIN);
		
//		crita.add(Restrictions.in("categoryList", new ArrayList<Category>().add(cat)));
		crita.add(Restrictions.eqOrIsNull("cat.id", cat.getId()));
		
		logger.debug("Searching " + classToUse.getSimpleName() + "s containing Category#" + cat.getId() + ".");
		
		@SuppressWarnings("unchecked")
		List<T> listToReturn = crita.list();
		
		logger.debug("Returning " + listToReturn.size() + " " + classToUse.getSimpleName() + "s.");
		return listToReturn;
	}
	
}
