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
	
	public static boolean isUserValid(String login, String password) {
		
		logger.debug("isUserValid - Fetching user with login : '" + login + "'...");
		User userFetched = fetchUserOnLogin(login);
		
		boolean result = false;
		if(userFetched != null){
			logger.debug("isUserValid - User fetched.");
			
			logger.debug("fetched hash : '" + userFetched.getPassword() + "'");
			
			logger.debug("isUserValid - Checking password...");
			result = BCrypt.checkpw(password, userFetched.getPassword());
			
			String strLogResult = "differed";
			if(result) { strLogResult = "matched"; }
			logger.debug("isUserValid - Passwords " + strLogResult + ".");
		} else {
			logger.debug("isUserValid - No user found.");
		}
		
		return result;
	}


	public static int addUser(String login, String password) {
		
		logger.debug("addUser - Checking no other user named : '" + login + "'...");
		User userFetched = fetchUserOnLogin(login);
		
		if(userFetched != null){
			throw new LoginAlreadyExistsException("Login '" + login + "' already exist in database.");
		} else {
			logger.debug("addUser - Hashing password...");
			String hashedPwd = BCrypt.hashpw(password, BCrypt.gensalt());
			logger.debug("addUser - Password hashed.");
			
			User userToInsert = new User(login, hashedPwd, false);
			GenericDao<User> userDao = new GenericDao<>(User.class);
			userDao.saveOrUpdate(userToInsert);
			
			return userToInsert.getId();
		}
	}

	private static User fetchUserOnLogin(String login) {
		GenericDao<User> userDao = new GenericDao<>(User.class);
		Map<String, Object> criteriaMap = new HashMap<>();
		criteriaMap.put("login", login);
		
		
		List<User> userList = userDao.search(criteriaMap);
		User userToReturn = null;
		if(userList.size() > 0){
			userToReturn = userList.get(0);
		}
		
		return userToReturn;
	}
	
}
