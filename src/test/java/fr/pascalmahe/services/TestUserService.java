package fr.pascalmahe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import fr.pascalmahe.business.User;
import fr.pascalmahe.ex.LoginAlreadyExistsException;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.services.UserService;
import fr.pascalmahe.testUtil.AbstractTest;
import fr.pascalmahe.testUtil.Validator;

public class TestUserService extends AbstractTest {

	private static final Logger logger = LogManager.getLogger();

	private static final String LOGIN_WITH_PWD = "aa";

	private static final String PWD = "aa";

	private static final String LOGIN_WITHOUT_PWD = "az";

	private static final String LOGIN_INSERTION = "gfpsdksqdpsqkldp147";
	
	private static final String PWD_WITH_UUID = "gqbssqddnl";

	private static final String LOGIN_WITHOUT_UUID = "54651";

	private static final String PWD_WITHOUT_UUID = "178";

	private static final String LOGIN_WITH_UUID_1 = "nqmldkl";

	private static final String LOGIN_WITH_UUID_2 = "1654584sdz";
	
	private static String uuidForSearchingUUID;
	
	private static int idUserWithoutUUID;
	
	private static int idUserWithUUIDForSearching;
	
	private static int idUserWithUUIDForDeleting;
	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		preTestDatabaseCheckup();
		
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		
		logger.debug("Inserting user with password...");
		int idUserWithPwd = 0;
		try {
			idUserWithPwd = UserService.addUser(LOGIN_WITH_PWD, PWD);
		} catch (LoginAlreadyExistsException e) {
			logger.error("Exception while inserting user for tests: " + e.getLocalizedMessage(), e);
		}
		logger.debug("User with password inserted.");
		
		logger.debug("Inserting user with empty password...");
		int idUserWithoutPwd = 0;
		try {
			idUserWithoutPwd = UserService.addUser(LOGIN_WITHOUT_PWD, "");
		} catch (LoginAlreadyExistsException e) {
			logger.error("Exception while inserting user for tests: " + e.getLocalizedMessage(), e);
		}
		logger.debug("User with empty password inserted.");
		
		User userWithPwd = userDao.fetch(idUserWithPwd);
		listToDelete.add(userWithPwd);

		User userWithoutPwd = userDao.fetch(idUserWithoutPwd);
		listToDelete.add(userWithoutPwd);
		
		logger.debug("Inserting users with UUID...");
		User userWithUUIDForSearching = new User();
		userWithUUIDForSearching.setLogin(LOGIN_WITH_UUID_1);
		userWithUUIDForSearching.setPassword(PWD_WITH_UUID);
		uuidForSearchingUUID = UUID.randomUUID().toString();
		userWithUUIDForSearching.setUuid(uuidForSearchingUUID);
		
		User userWithUUIDForDeleting = new User();
		userWithUUIDForDeleting.setLogin(LOGIN_WITH_UUID_2);
		userWithUUIDForDeleting.setPassword(PWD_WITH_UUID);
		userWithUUIDForDeleting.setUuid(UUID.randomUUID().toString());
		
		userDao.saveOrUpdate(userWithUUIDForSearching);
		userDao.saveOrUpdate(userWithUUIDForDeleting);
		idUserWithUUIDForDeleting = userWithUUIDForDeleting.getId();
		idUserWithUUIDForSearching = userWithUUIDForSearching.getId();
		
		listToDelete.add(userWithUUIDForSearching);
		listToDelete.add(userWithUUIDForDeleting);
		
		logger.debug("Users with UUID inserted.");
		

		logger.debug("Inserting user without UUID...");
		try {
			idUserWithoutUUID = UserService.addUser(LOGIN_WITHOUT_UUID, PWD_WITHOUT_UUID);
			User userWithoutUUID = userDao.fetch(idUserWithoutUUID);
			listToDelete.add(userWithoutUUID);
		} catch (LoginAlreadyExistsException e) {
			logger.error("Exception while inserting user for tests: " + e.getLocalizedMessage(), e);
		}
		
		logger.debug("User with UUID inserted.");
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		cleanUpDatabase();
		
		logger.info("afterClass finished.");
	}
	
	@Test
	public void testIsUserValid() {
		logger.info("Starting testIsUserValid...");
		
		logger.debug("Testing with good password: ");
		User validUserWithGoodPwd = UserService.getValidUser(LOGIN_WITH_PWD, PWD);
		assertNotNull("Wrong result after test of isUserValid with good pwd: ", validUserWithGoodPwd);

		logger.debug("Testing with wrong password: ");
		User resultWithWrongPwd = UserService.getValidUser(LOGIN_WITH_PWD, "a");
		assertNull("Wrong result after test of isUserValid with wrong pwd: ", resultWithWrongPwd);

		logger.debug("Testing with wrong password (empty): ");
		User resultWithWrongPwdEmpty = UserService.getValidUser(LOGIN_WITH_PWD, "");
		assertNull("Wrong result after test of isUserValid with wrong pwd: ", resultWithWrongPwdEmpty);

		logger.debug("Testing with good password (empty): ");
		User resultWithGoodPwdEmpty = UserService.getValidUser(LOGIN_WITHOUT_PWD, "");
		assertNotNull("Wrong result after test of isUserValid with wrong pwd: ", resultWithGoodPwdEmpty);

		logger.debug("Testing with wrong password (not empty): ");
		User resultWithWrongPwdNotEmpty = UserService.getValidUser(LOGIN_WITHOUT_PWD, PWD);
		assertNull("Wrong result after test of isUserValid with wrong pwd: ", resultWithWrongPwdNotEmpty);
		
		logger.info("testIsUserValid finished.");
	}

	@Test
	public void testAddUser() {
		logger.info("Starting testAddUser...");
		
		GenericDao<User> uDao = new GenericDao<User>(User.class);
		
		int nbUserBeforeInsertion = uDao.count();
		
		logger.debug("Testing first insertion: ");
		Integer idUserInserted = null;
		try {
			idUserInserted = UserService.addUser(LOGIN_INSERTION, PWD);
		} catch (LoginAlreadyExistsException e) {
			logger.error("Exception while inserting user for tests: " + e.getLocalizedMessage(), e);
			fail("Exception while inserting user for tests: " + e.getLocalizedMessage());
		}
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
	

	@Test
	public void testDeleteUUID() {
		logger.info("Starting testDeleteUUID...");
		
		GenericDao<User> uDao = new GenericDao<User>(User.class);
		User userWithUUID = uDao.fetch(idUserWithUUIDForDeleting);
		
		assertNotNull("Wrong UUID in userWithUUID: it's null, it shouldn't be.", userWithUUID.getUuid());
		
		UserService.deleteUUID(userWithUUID);
		
		User userNowWithoutUUID = uDao.fetch(idUserWithUUIDForDeleting);
		assertNull("Wrong UUID in userWithUUID: it's not null, it should be.", userNowWithoutUUID.getUuid());
		
		logger.info("testDeleteUUID finished.");
	}


	@Test
	public void testGetUserOnUUID() {
		logger.info("Starting testGetUserOnUUID...");
		
		User userFoundOnUUID = UserService.getUserOnUUID(uuidForSearchingUUID);
		
		User userVerification = new User();
		userVerification.setId(idUserWithUUIDForSearching);
		userVerification.setLogin(LOGIN_WITH_UUID_1);
		userVerification.setPassword(PWD_WITH_UUID);
		userVerification.setUuid(uuidForSearchingUUID);
		
		Validator.validateUser("test to fetch User on UUID", userVerification, userFoundOnUUID);
		
		logger.info("testGetUserOnUUID finished.");
	}
	


	@Test
	public void testSaveUUID() {
		logger.info("Starting testSaveUUID...");
		
		GenericDao<User> uDao = new GenericDao<User>(User.class);
		
		User userWithoutUUID = uDao.fetch(idUserWithoutUUID);
		
		assertNull("Wrong UUID in userWithUUID: it's not null, it should be.", userWithoutUUID.getUuid());
		
		String updatingUUIDTo = UUID.randomUUID().toString();
		
		UserService.saveUUID(userWithoutUUID, updatingUUIDTo);
		
		User userNowWithUUID = uDao.fetch(idUserWithoutUUID);

		assertNotNull("Wrong UUID in userWithUUID: it's null, it shouldn't be.", userNowWithUUID.getUuid());
		
		logger.info("testSaveUUID finished.");
	}
	
	@Test
	public void testCreateRealUsers(){
		logger.info("Starting testCreateRealUsers...");
		
		String user1 = System.getenv("USER1");
		String pwd1 = System.getenv("USER_PWD1");
		
		String user2 = System.getenv("USER2");
		String pwd2 = System.getenv("USER_PWD2");
		
		try {
			UserService.addUser(user1, pwd1);
		} catch (LoginAlreadyExistsException laee){
			// do nothing, user already exists
		}

		try {
			UserService.addUser(user2, pwd2);
		} catch (LoginAlreadyExistsException laee){
			// do nothing, user already exists
		}
		
		
		logger.info("testCreateRealUsers finished.");
	}
}