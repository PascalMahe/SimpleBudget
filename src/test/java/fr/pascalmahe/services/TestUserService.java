package fr.pascalmahe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import fr.pascalmahe.business.User;
import fr.pascalmahe.ex.LoginAlreadyExistsException;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.persistence.TestException;
import fr.pascalmahe.services.UserService;

public class TestUserService {

	private static final Logger logger = LogManager.getLogger();

	private static final String LOGIN_WITH_PWD = "aa";

	private static final String PWD = "aa";

	private static final String LOGIN_WITHOUT_PWD = "az";

	private static final String LOGIN_INSERTION = "gfpsdksqdpsqkldp147";

	private static List<Object> listToDelete = new ArrayList<>();
	
	private static int nbUsersBeforeTests;
	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		GenericDao<User> userDao = new GenericDao<>(User.class);
		nbUsersBeforeTests = userDao.count();
		
		logger.debug("Inserting user with password...");
		int idUserWithPwd = UserService.addUser(LOGIN_WITH_PWD, PWD);
		logger.debug("User with password inserted.");
		
		logger.debug("Inserting user with empty password...");
		int idUserWithoutPwd = UserService.addUser(LOGIN_WITHOUT_PWD, "");
		logger.debug("User with empty password inserted.");
		
		User userWithPwd = userDao.fetch(idUserWithPwd);
		listToDelete.add(userWithPwd);

		User userWithoutPwd = userDao.fetch(idUserWithoutPwd);
		listToDelete.add(userWithoutPwd);
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		
		GenericDao<User> userDao = new GenericDao<>(User.class);
		for(Object currObj : listToDelete){
			User userToDelete = (User) currObj;
			userDao.delete(userToDelete);
		}
		
		int nbUsersAfterDeletions = userDao.count();
		if(nbUsersAfterDeletions != nbUsersBeforeTests){
			String errorMessageSuffix = "Users(found " + nbUsersAfterDeletions + 
									", should be " + nbUsersBeforeTests + ")";
			throw new TestException("Found values in DB after tests from "
									+ "the following classes: " 
									+ errorMessageSuffix);
		}
		
		logger.info("afterClass finished.");
	}
	
	@Test
	public void testIsUserValid() {
		logger.info("Starting testIsUserValid...");
		
		logger.debug("Testing with good password: ");
		boolean resultWithGoodPwd = UserService.isUserValid(LOGIN_WITH_PWD, PWD);
		assertTrue("Wrong result after test of isUserValid with good pwd: ", resultWithGoodPwd);

		logger.debug("Testing with wrong password: ");
		boolean resultWithWrongPwd = UserService.isUserValid(LOGIN_WITH_PWD, "a");
		assertFalse("Wrong result after test of isUserValid with wrong pwd: ", resultWithWrongPwd);

		logger.debug("Testing with wrong password (empty): ");
		boolean resultWithWrongPwdEmpty = UserService.isUserValid(LOGIN_WITH_PWD, "");
		assertFalse("Wrong result after test of isUserValid with wrong pwd: ", resultWithWrongPwdEmpty);

		logger.debug("Testing with good password (empty): ");
		boolean resultWithGoodPwdEmpty = UserService.isUserValid(LOGIN_WITHOUT_PWD, "");
		assertTrue("Wrong result after test of isUserValid with wrong pwd: ", resultWithGoodPwdEmpty);

		logger.debug("Testing with wrong password (not empty): ");
		boolean resultWithWrongPwdNotEmpty = UserService.isUserValid(LOGIN_WITHOUT_PWD, PWD);
		assertFalse("Wrong result after test of isUserValid with wrong pwd: ", resultWithWrongPwdNotEmpty);
		
		logger.info("testIsUserValid finished.");
	}

	@Test
	public void testAddUser() {
		logger.info("Starting testAddUser...");
		
		GenericDao<User> uDao = new GenericDao<User>(User.class);
		
		int nbUserBeforeInsertion = uDao.count();
		
		logger.debug("Testing first insertion: ");
		Integer idUserInserted = UserService.addUser(LOGIN_INSERTION, PWD);
		assertNotNull("Wrong id after addUser test (should not be null)", idUserInserted);

		int nbUserAfterInsertion = uDao.count();
		assertEquals("Wrong number of Users counted after insertion test: ", 
				nbUserBeforeInsertion + 1,
				nbUserAfterInsertion);
		
		logger.debug("Testing second insertion (should throw LoginAlreadyExistsException): ");
		try {
			UserService.addUser(LOGIN_INSERTION, PWD);
			fail("addUser has not thrown a LoginAlreadyExistsException.");
		} catch (LoginAlreadyExistsException e){
			
		}

		int nbUserAfterFailedInsertion = uDao.count();
		assertEquals("Wrong number of Users counted after failed insertion test: ", 
				nbUserBeforeInsertion + 1,
				nbUserAfterFailedInsertion);
		
		listToDelete.add(uDao.fetch(idUserInserted));
		
		logger.info("testAddUser finished.");
	}
}