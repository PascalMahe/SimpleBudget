package fr.pascalmahe.persistence;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Balance;
import fr.pascalmahe.business.Budget;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.User;

public class TestGenericDao {

	private static final Logger logger = LogManager.getLogger();

	private static List<Object> listToDelete = new ArrayList<>();
	
	private static int nbBalancesBeforeTests;
	
	private static int nbBudgetsBeforeTests;
	
	private static int nbCategoriesBeforeTests;
	
	private static int nbLinesBeforeTests;
	
	private static int nbUsersBeforeTests;
	
	
	@BeforeClass
	public static void loadUpDatabase() {
		
		logger.info("loadUpDatabase - Inserting test data...");

		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		GenericDao<Category> categoryDao = new GenericDao<>(Category.class);
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		nbBalancesBeforeTests = balanceDao.count();
		nbCategoriesBeforeTests = budgetDao.count();
		nbUsersBeforeTests = categoryDao.count();
		nbBudgetsBeforeTests = lineDao.count();
		nbLinesBeforeTests = userDao.count();
		
		logger.debug("loadUpDatabase - nbBalancesBeforeTests: " + nbBalancesBeforeTests);
		logger.debug("loadUpDatabase - nbCategoriesBeforeTests: " + nbCategoriesBeforeTests);
		logger.debug("loadUpDatabase - nbUsersBeforeTests: " + nbUsersBeforeTests);
		logger.debug("loadUpDatabase - nbBudgetsBeforeTests: " + nbBudgetsBeforeTests);
		logger.debug("loadUpDatabase - nbLinesBeforeTests: " + nbLinesBeforeTests);
		
		// Standalone beans : Balance, Category, User
		
		// inserting Balances
		
		Balance bal1 = new Balance(LocalDate.of(2014, Month.MAY, 28), 456.23f);
		Balance bal2 = new Balance(LocalDate.of(2015, Month.MARCH, 29), -456.23f);
		Balance bal3 = new Balance(LocalDate.of(2016, Month.MAY, 30), 0.01f);
		
		balanceDao.saveOrUpdate(bal1);
		balanceDao.saveOrUpdate(bal2);
		balanceDao.saveOrUpdate(bal3);

		listToDelete.add(0, bal1); // insertion toujours en premier pour permettre 
		listToDelete.add(0, bal2); // de supprimer dans l'ordre inverse
		listToDelete.add(0, bal3);
		
		// inserting Categories
		
		Category cat1 = new Category("TestStandaloneCategory", null);
		Category cat2 = new Category("TestFatherCategory", null);
		Category cat3 = new Category("TestSonCategory", cat2);
		
		categoryDao.saveOrUpdate(cat1);
		categoryDao.saveOrUpdate(cat2);
		categoryDao.saveOrUpdate(cat3);

		listToDelete.add(0, cat1);
		listToDelete.add(0, cat2);
		listToDelete.add(0, cat3);
		
		// inserting Users
		
		User use1 = new User("TestUser 01", "Safe password", true);
		User use2 = new User("TestUser 02", "Unsafe password", false);
		User use3 = new User("TestUser 03", "Unsafe password", true);
		
		userDao.saveOrUpdate(use1);
		userDao.saveOrUpdate(use2);
		userDao.saveOrUpdate(use3);

		listToDelete.add(0, use1);
		listToDelete.add(0, use2);
		listToDelete.add(0, use3);
		
		
		// Beans depending on Category : Line, Budget

		// inserting Budgets
		Map<Integer, Category> listWithOneCategory = new HashMap<>();
		listWithOneCategory.put(cat2.getId(), cat2);
		
		Budget bud1 = new Budget("TestBudget", 
								listWithOneCategory, 
								800.0f, 
								LocalDate.of(2016, Month.FEBRUARY, 1),
								LocalDate.of(2016, Month.FEBRUARY, 28));
		
		
		Map<Integer, Category> listWithTwoCategories = new HashMap<>();
		listWithOneCategory.put(cat1.getId(), cat1);
		listWithOneCategory.put(cat2.getId(), cat2);
		
		Budget bud2 = new Budget("TestBudget", 
								listWithTwoCategories, 
								1000.0f, 
								LocalDate.of(2016, Month.MARCH, 1),
								LocalDate.of(2016, Month.MARCH, 31));

		Map<Integer, Category> listWithTwoOtherCategories = new HashMap<>();
		listWithOneCategory.put(cat1.getId(), cat1);
		listWithOneCategory.put(cat3.getId(), cat3);
		
		Budget bud3 = new Budget("TestBudget01", 
								listWithTwoOtherCategories, 
								40.0f, 
								LocalDate.of(2015, Month.JUNE, 1),
								LocalDate.of(2015, Month.JUNE, 30));
		
		budgetDao.saveOrUpdate(bud1);
		budgetDao.saveOrUpdate(bud2);
		budgetDao.saveOrUpdate(bud3);
		
		listToDelete.add(0, bud1);
		listToDelete.add(0, bud2);
		listToDelete.add(0, bud3);
		
		// inserting Lines
		
		Line lin1 = new Line(	LocalDate.of(2016, Month.MARCH, 01), 
								"TestLine 01", 
								"category: TestStandaloneCategory", 
								123.45f, 
								false, 
								cat1);

		Line lin2 = new Line(	LocalDate.of(2016, Month.APRIL, 02), 
								"TestLine 02", 
								"category: TestFatherCategory",  
								-123.45f, 
								false, 
								cat2);

		Line lin3 = new Line(	LocalDate.of(2016, Month.MAY, 05), 
								"TestLine 03", 
								"category: TestFatherCategory", 
								612024.45f, 
								false, 
								cat2);
		
		lineDao.saveOrUpdate(lin1);
		lineDao.saveOrUpdate(lin2);
		lineDao.saveOrUpdate(lin3);

		listToDelete.add(0, lin1);
		listToDelete.add(0, lin2);
		listToDelete.add(0, lin3);

		int nbBalancesAfterInsertions = balanceDao.count();
		int nbBudgetsAfterInsertions = budgetDao.count();
		int nbCategoriesAfterInsertions = categoryDao.count();
		int nbLinesAfterInsertions = lineDao.count();
		int nbUsersAfterInsertions = userDao.count();
		
		logger.debug("loadUpDatabase - nbBalancesAfterInsertions: " + nbBalancesAfterInsertions);
		logger.debug("loadUpDatabase - nbBudgetsAfterInsertions: " + nbBudgetsAfterInsertions);
		logger.debug("loadUpDatabase - nbCategoriesAfterInsertions: " + nbCategoriesAfterInsertions);
		logger.debug("loadUpDatabase - nbLinesAfterInsertions: " + nbLinesAfterInsertions);
		logger.debug("loadUpDatabase - nbUsersAfterInsertions: " + nbUsersAfterInsertions);

		logger.info("loadUpDatabase - Test data inserted.");
	}

	@AfterClass
	public static void cleanUpDatabase() {

		logger.info("cleanUpDatabase - Deleting test data...");
		
		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		GenericDao<Category> categoryDao = new GenericDao<>(Category.class);
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		int nbBalancesAfterTests = balanceDao.count();
		int nbBudgetsAfterTests = budgetDao.count();
		int nbCategoriesAfterTests = categoryDao.count();
		int nbLinesAfterTests = lineDao.count();
		int nbUsersAfterTests = userDao.count();
		
		logger.debug("cleanUpDatabase - nbBalancesAfterTests: " + nbBalancesAfterTests);
		logger.debug("cleanUpDatabase - nbBudgetsAfterTests: " + nbBudgetsAfterTests);
		logger.debug("cleanUpDatabase - nbCategoriesAfterTests: " + nbCategoriesAfterTests);
		logger.debug("cleanUpDatabase - nbLinesAfterTests: " + nbLinesAfterTests);
		logger.debug("cleanUpDatabase - nbUsersAfterTests: " + nbUsersAfterTests);

		for(Object currObject : listToDelete){
			if(currObject instanceof Balance){
				Balance balTodelete = (Balance) currObject;
				balanceDao.delete(balTodelete);
			} else if(currObject instanceof Budget){
				Budget balTodelete = (Budget) currObject;
				budgetDao.delete(balTodelete);
			} else if(currObject instanceof Category){
				Category catTodelete = (Category) currObject;
				categoryDao.delete(catTodelete);
			} else if(currObject instanceof Line){
				Line linTodelete = (Line) currObject;
				lineDao.delete(linTodelete);
			} else if(currObject instanceof User){
				User userTodelete = (User) currObject;
				userDao.delete(userTodelete);
			}
		}
		
		int nbBalancesAfterDeletions = balanceDao.count();
		int nbBudgetsAfterDeletions = budgetDao.count();
		int nbCategoriesAfterDeletions = categoryDao.count();
		int nbLinesAfterDeletions = lineDao.count();
		int nbUsersAfterDeletions = userDao.count();
		
		logger.debug("cleanUpDatabase - nbBalancesAfterDeletions: " + nbBalancesAfterDeletions);
		logger.debug("cleanUpDatabase - nbBudgetsAfterDeletions: " + nbBudgetsAfterDeletions);
		logger.debug("cleanUpDatabase - nbCategoriesAfterDeletions: " + nbCategoriesAfterDeletions);
		logger.debug("cleanUpDatabase - nbLinesAfterDeletions: " + nbLinesAfterDeletions);
		logger.debug("cleanUpDatabase - nbUsersAfterDeletions: " + nbUsersAfterDeletions);

		logger.info("cleanUpDatabase - Test data deleted.");
	}

	@Test
	public void testSearch() {
		logger.info("Starting testSearch...");
		// Balance
		// Test : 1 criteria, 1 result
		Map<String, Object> critList = new HashMap<>();
		critList.put("amount", -456.23f);
		
		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		List<Balance> oneResultBalance = balanceDao.search(critList);
		
		assertEquals("Wrong number of Balances "
						+ "found when searching with 1 criteria.", 
					1, 
					oneResultBalance.size());
		
		// Budget

		// Test : 1 criteria, 2 results
		critList = new HashMap<>();
		critList.put("name", "TestBudget");
		
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		List<Budget> twoResultsBudget = budgetDao.search(critList);
		
		assertEquals("Wrong number of Budget "
						+ "found when searching with 1 criteria.", 
					2, 
					twoResultsBudget.size());
		
		// Test : 2 criteria, 1 result
		critList = new HashMap<>();
		critList.put("name", "TestBudget");
		critList.put("maxAmount", 800.0f);
		
		budgetDao = new GenericDao<>(Budget.class);
		List<Budget> oneResultBudget = budgetDao.search(critList);

		assertEquals("Wrong number of Budget "
						+ "found when searching with 2 criteria.", 
					1, 
					oneResultBudget.size());
		
		// Category
		// Test : 1 criteria, 1 result
		critList = new HashMap<>();
		critList.put("name", "TestSonCategory");
		
		GenericDao<Category> categoryDao = new GenericDao<>(Category.class);
		List<Category> oneResultCategory = categoryDao.search(critList);
		
		assertEquals("Wrong number of Category "
						+ "found when searching with 1 criteria.", 
					1, 
					oneResultCategory.size());

		// Line
		// Test : 1 criteria, 2 results
		critList = new HashMap<>();
		critList.put("secondaryLabel", "category: TestFatherCategory");
		
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		List<Line> twoResultsLine = lineDao.search(critList);
		
		assertEquals("Wrong number of Line "
						+ "found when searching with 1 criteria.", 
					2, 
					twoResultsLine.size());
		
		// Test : 2 criteria, 1 result
		critList = new HashMap<>();
		critList.put("secondaryLabel", "category: TestFatherCategory");
		critList.put("amount", 612024.45f);
		
		List<Line> oneResultLine = lineDao.search(critList);
		
		assertEquals("Wrong number of Line "
						+ "found when searching with 2 criteria.", 
					1, 
					oneResultLine.size());

		// User
		// Test : 1 criteria, 2 results
		critList = new HashMap<>();
		critList.put("password", "Unsafe password");
		// "TestUser 03", "Unsafe password"
		
		GenericDao<User> userDao = new GenericDao<>(User.class);
		List<User> twoResultsUser = userDao.search(critList);
		
		assertEquals("Wrong number of Users "
						+ "found when searching with 1 criteria.", 
					2, 
					twoResultsUser.size());
		
		// Test : 2 criteria, 1 result
		critList = new HashMap<>();
		critList.put("password", "Unsafe password");
		critList.put("dailyNotification", true);
		// "TestUser 03", "Unsafe password"
		
		userDao = new GenericDao<>(User.class);
		List<User> oneResultUser = userDao.search(critList);
		
		assertEquals("Wrong number of Users "
						+ "found when searching with 1 criteria.", 
					1, 
					oneResultUser.size());
		logger.info("testSearch done.");
	}
	
	@Test
	public void testInsert(){
		logger.info("Starting testInsert...");
		// Balance
		// Counting before
		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		int nbBalancesBeforeInsertion = balanceDao.count();
		
		// Creating value
		Balance balanceToInsert = new Balance(LocalDate.now(), -0.05f);
		balanceDao.saveOrUpdate(balanceToInsert);
		
		// Checking by counting
		int nbBalancesAfterInsertion = balanceDao.count();
		assertEquals("Wrong number of Balances counted after insertion test", 
						nbBalancesBeforeInsertion + 1, 
						nbBalancesAfterInsertion);
		
		// Checking by fetching
		Balance balanceForVerification = balanceDao.fetch(balanceToInsert.getId());
		assertEquals("Wrong value fetched after insertion of Balance.", balanceToInsert, balanceForVerification);
		
		// Category (simple)
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		int nbCategorysBeforeInsertion = catDao.count();
		
		// Creating value
		Category catToInsert = new Category("Category for insertion test", null);
		catDao.saveOrUpdate(catToInsert);
		
		// Checking by counting
		int nbCategorysAfterInsertion = catDao.count();
		assertEquals("Wrong number of Categorys counted after insertion test", 
						nbCategorysBeforeInsertion + 1, 
						nbCategorysAfterInsertion);
		
		// Checking by fetching
		Category catForVerification = catDao.fetch(balanceToInsert.getId());
		assertEquals("Wrong value fetched after insertion of Categorys.", 
						balanceToInsert, 
						balanceForVerification);

		// Category (with father)
		nbCategorysBeforeInsertion = catDao.count();
		
		// Creating value
		Category catToInsertWithFather = new Category("Category for insertion test (with father)", catToInsert);
		catDao.saveOrUpdate(catToInsertWithFather);
		
		// Checking by counting
		nbCategorysAfterInsertion = catDao.count();
		assertEquals("Wrong number of Categorys counted after insertion test (with father)", 
						nbCategorysBeforeInsertion + 1, 
						nbCategorysAfterInsertion);
		
		// Checking by fetching
		catForVerification = catDao.fetch(balanceToInsert.getId());
		assertEquals("Wrong value fetched after insertion of Categorys (with father).", 
						balanceToInsert, 
						balanceForVerification);
		
		// Budget
		// Counting before
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		int nbBudgetsBeforeInsertion = budgetDao.count();
		
		// Creating value
		LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
		boolean isCurrentYearLeap = monthStart.isLeapYear();
		int currentMonthLength = monthStart.getMonth().length(isCurrentYearLeap);
		LocalDate monthEnd = LocalDate.now().withDayOfMonth(currentMonthLength);
		Map<Integer, Category> categoryMap = new HashMap<>();
		categoryMap.put(catToInsert.getId(), catToInsert);
		categoryMap.put(catToInsert.getId(), catToInsertWithFather);
		Budget budgetToInsert = new Budget("Testbudget", categoryMap, -1245.69f, monthStart, monthEnd);
		budgetDao.saveOrUpdate(budgetToInsert);
		
		// Checking by counting
		int nbBudgetsAfterInsertion = budgetDao.count();
		assertEquals("Wrong number of Budgets counted after insertion test", 
						nbBudgetsBeforeInsertion + 1, 
						nbBudgetsAfterInsertion);
		
		// Checking by fetching
		Budget budgetForVerification = budgetDao.fetch(budgetToInsert.getId());
		assertEquals("Wrong value fetched after insertion of Budget.", budgetToInsert, budgetForVerification);
		
		// Line (simple)
		// Counting before
		GenericDao<Line> linDao = new GenericDao<>(Line.class);
		int nbLinesBeforeInsertion = linDao.count();
		
		// Creating value
		Line linToInsert = new Line(LocalDate.now(), 
									"Line for insertion test", 
									"Line for insertion test (sec label)", 
									0.123f, 
									false, 
									null);
		linDao.saveOrUpdate(linToInsert);
		
		// Checking by counting
		int nbLinesAfterInsertion = linDao.count();
		assertEquals("Wrong number of Lines counted after insertion test", 
						nbLinesBeforeInsertion + 1, 
						nbLinesAfterInsertion);
		
		// Checking by fetching
		Line lineForVerification = linDao.fetch(linToInsert.getId());
		assertEquals("Wrong value fetched after insertion of Line.", linToInsert, lineForVerification);
		
		// Line (with category)
		// Counting before
		nbLinesBeforeInsertion = linDao.count();
		
		// Creating value
		Line linToInsertWithCategory = new Line(LocalDate.now(), 
											"Line for insertion test (with category)", 
											"Line for insertion test (sec label) (with category)", 
											-15150.123f, 
											false, 
											catToInsert);
		linDao.saveOrUpdate(linToInsertWithCategory);
		
		// Checking by counting
		nbLinesAfterInsertion = linDao.count();
		assertEquals("Wrong number of Lines counted after insertion test (with category)", 
						nbLinesBeforeInsertion + 1, 
						nbLinesAfterInsertion);
		
		// Checking by fetching
		lineForVerification = linDao.fetch(linToInsertWithCategory.getId());
		assertEquals("Wrong value fetched after insertion of Line (with category).", linToInsertWithCategory, lineForVerification);

		// User
		// Counting before
		GenericDao<User> userDao = new GenericDao<>(User.class);
		int nbUsersBeforeInsertion = userDao.count();
		
		// Creating value
		User userToInsert = new User("User for insertion test", "pwd", true);
		userDao.saveOrUpdate(userToInsert);
		
		// Checking by counting
		int nbUsersAfterInsertion = userDao.count();
		assertEquals("Wrong number of Lines counted after insertion test", 
						nbUsersBeforeInsertion + 1, 
						nbUsersAfterInsertion);
		
		// Checking by fetching
		User userForVerification = userDao.fetch(userToInsert.getId());
		assertEquals("Wrong value fetched after insertion of User.", userToInsert, userForVerification);
		
		listToDelete.add(balanceToInsert);
		listToDelete.add(budgetToInsert);
		listToDelete.add(catToInsert);
		listToDelete.add(catToInsertWithFather);
		listToDelete.add(linToInsert);
		listToDelete.add(linToInsertWithCategory);
		listToDelete.add(userToInsert);
		
		logger.info("testInsert done.");
	}

}