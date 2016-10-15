package fr.pascalmahe.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import fr.pascalmahe.business.User;
import fr.pascalmahe.ex.LoginAlreadyExistsException;
import fr.pascalmahe.persistence.GenericDao;

public class UserService {

	private static final Logger logger = LogManager.getLogger();
	
	public static User getValidUser(String login, String password) {
		
		logger.info("getValidUser - Fetching user with login : '" + login + "'...");
		User userFetched = fetchUserOnLogin(login);
		User validUser = null;
		if(userFetched != null){
			logger.debug("getValidUser - User fetched. Checking password...");
			boolean pwdsMatched = BCrypt.checkpw(password, userFetched.getPassword());
			
			String strLogResult = "differed";
			if(pwdsMatched) { 
				strLogResult = "matched";
				validUser = userFetched;
			}
			logger.debug("getValidUser - Passwords " + strLogResult + ".");
		} else {
			logger.debug("getValidUser - No user found.");
		}
		
		return validUser;
	}


	public static int addUser(String login, String password) throws LoginAlreadyExistsException {
		logger.info("Trying to add user w/ login : " + login);
		
		logger.debug("addUser - Checking no other user named : '" + login + "'...");
		User userFetched = fetchUserOnLogin(login);
		
		if(userFetched != null){
			logger.info("User " + login + " already exists.");
			throw new LoginAlreadyExistsException("Login '" + login + "' already exist in database.");
		} else {
			logger.debug("addUser - Hashing password...");
			String hashedPwd = BCrypt.hashpw(password, BCrypt.gensalt());
			logger.debug("addUser - Password hashed.");
			
			User userToInsert = new User(login, hashedPwd, false);
			GenericDao<User> userDao = new GenericDao<>(User.class);
			userDao.saveOrUpdate(userToInsert);
			int idInsertedUser = userToInsert.getId();
			
			logger.info("User " + login + " #" + idInsertedUser + " created.");
			return idInsertedUser;
		}
	}

	private static User fetchUserOnLogin(String login) {
		logger.debug("fetchUserOnLogin - Fetching user with login : '" + login + "'...");
		
		GenericDao<User> userDao = new GenericDao<>(User.class);
		Map<String, Object> criteriaMap = new HashMap<>();
		criteriaMap.put("login", login);
		
		
		List<User> userList = userDao.search(criteriaMap);
		User userToReturn = null;
		if(userList.size() > 0){
			userToReturn = userList.get(0);
		}
		
		logger.debug("fetchUserOnLogin - Returning " + userList.size() + " user(s).");
		
		return userToReturn;
	}


	public static void saveUUID(User user, String uuid) {
		logger.info("Saving UUID for user " + user.getLogin() + " (#" + user.getId() + ")");
		
		user.setUuid(uuid);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		userDao.saveOrUpdate(user);
		
		logger.info("Saving UUID done.");
	}


	public static void deleteUUID(User user) {
		logger.info("Deleting UUID for user " + user.getLogin() + " (#" + user.getId() + ")");
		
		user.setUuid(null);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		userDao.saveOrUpdate(user);
		
		logger.info("Deleting UUID done.");
	}


	public static User getUserOnUUID(String uuid) {
		logger.info("Fetching user on UUID...");
		
		GenericDao<User> userDao = new GenericDao<>(User.class);
		Map<String, Object> criteriaMap = new HashMap<>();
		criteriaMap.put("uuid", uuid);
		
		List<User> userList = userDao.search(criteriaMap);
		User userToReturn = null;
		if(userList.size() > 0){
			userToReturn = userList.get(0);
		}
		
		logger.info("Returning " + userList.size() + " user(s) on UUID.");
		return userToReturn;
	}
	
}
