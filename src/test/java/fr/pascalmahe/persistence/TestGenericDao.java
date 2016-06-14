package fr.pascalmahe.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Balance;
import fr.pascalmahe.business.Budget;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.User;

public class TestGenericDao {

	private static final Logger logger = LogManager.getLogger();

	private static List<Object> listToDelete = new ArrayList<>();
	private static List<Object> listAlreadyDeleted = new ArrayList<>();
	
	private static int nbBalancesBeforeTests;
	
	private static int nbBudgetsBeforeTests;
	
	private static int nbCategoriesBeforeTests;
	
	private static int nbCategoBeforeTests;
	
	private static int nbLinesBeforeTests;
	
	private static int nbUsersBeforeTests;
	
	private static Integer idBalanceForUpdate;
	private static Integer idBudgetForUpdate;
	private static Integer idCategoryForUpdate;
	private static Integer idCategorisationForUpdate;
	private static Integer idLineForUpdate;
	private static Integer idUserForUpdate;

	private static Integer idBalanceForDeletion;
	private static Integer idBudgetForDeletion;
	private static Integer idCategoryForDeletion;
	private static Integer idCategorisationForDeletion;
	private static Integer idLineForDeletion;
	private static Integer idUserForDeletion;
	
	@BeforeClass
	public static void loadUpDatabase() {
		
		logger.info("loadUpDatabase - Inserting test data...");

		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		GenericDao<Category> categoryDao = new GenericDao<>(Category.class);
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		nbBalancesBeforeTests = balanceDao.count();
		nbBudgetsBeforeTests = budgetDao.count();
		nbCategoriesBeforeTests = categoryDao.count();
		nbCategoBeforeTests = categoDao.count();
		nbLinesBeforeTests = lineDao.count();
		nbUsersBeforeTests = userDao.count();
		
		logger.debug("loadUpDatabase - nbBalancesBeforeTests: " + nbBalancesBeforeTests);
		logger.debug("loadUpDatabase - nbBudgetsBeforeTests: " + nbBudgetsBeforeTests);
		logger.debug("loadUpDatabase - nbCategoriesBeforeTests: " + nbCategoriesBeforeTests);
		logger.debug("loadUpDatabase - nbCategoBeforeTests: " + nbCategoBeforeTests);
		logger.debug("loadUpDatabase - nbLinesBeforeTests: " + nbLinesBeforeTests);
		logger.debug("loadUpDatabase - nbUsersBeforeTests: " + nbUsersBeforeTests);
		
		// Standalone beans : Balance, Category, User
		
		// inserting Balances
		
		Balance bal1 = new Balance(LocalDate.of(2014, Month.MAY, 28), 456.23f);
		Balance bal2 = new Balance(LocalDate.of(2015, Month.MARCH, 29), -456.23f);
		Balance bal3 = new Balance(LocalDate.of(2016, Month.MAY, 30), 0.01f);
		Balance balToDelete = new Balance(LocalDate.of(2016, Month.JUNE, 10), 6.16f);
		
		balanceDao.saveOrUpdate(bal1);
		balanceDao.saveOrUpdate(bal2);
		balanceDao.saveOrUpdate(bal3);
		balanceDao.saveOrUpdate(balToDelete);

		idBalanceForUpdate = bal1.getId();
		idBalanceForDeletion = balToDelete.getId();
		
		listToDelete.add(0, bal1); 
		listToDelete.add(0, bal2);
		listToDelete.add(0, bal3);
		
		
		// inserting Categories
		
		Category cat1 = new Category("TestStandaloneCategory", null);
		Category cat2 = new Category("TestFatherCategory", null);
		Category cat3 = new Category("TestSonCategory", cat2);
		Category catToDelete = new Category("TestCategoryToDelete", cat2);
		
		categoryDao.saveOrUpdate(cat1);
		categoryDao.saveOrUpdate(cat2);
		categoryDao.saveOrUpdate(cat3);
		categoryDao.saveOrUpdate(catToDelete);

		listToDelete.add(0, cat1);
		listToDelete.add(0, cat2);
		listToDelete.add(0, cat3);
		
		idCategoryForUpdate = cat1.getId();
		idCategoryForDeletion = catToDelete.getId();
		
		// inserting Users
		
		User use1 = new User("TestUser 01", "Safe password", true);
		User use2 = new User("TestUser 02", "Unsafe password", false);
		User use3 = new User("TestUser 03", "Unsafe password", true);
		User useToDelete = new User("TestUser to delete", "no password", true);
		
		userDao.saveOrUpdate(use1);
		userDao.saveOrUpdate(use2);
		userDao.saveOrUpdate(use3);
		userDao.saveOrUpdate(useToDelete);

		listToDelete.add(0, use1);
		listToDelete.add(0, use2);
		listToDelete.add(0, use3);

		idUserForUpdate = use1.getId();
		idUserForDeletion = useToDelete.getId();
		
		// Beans depending on Category : Line, Budget

		// inserting Budgets
		List<Category> listWithOneCategory = new ArrayList<>();
		listWithOneCategory.add(cat2);
		
		Budget bud1 = new Budget("TestBudget", 
								listWithOneCategory, 
								800.0f, 
								LocalDate.of(2016, Month.FEBRUARY, 1),
								LocalDate.of(2016, Month.FEBRUARY, 28));
		
		
		List<Category> listWithTwoCategories = new ArrayList<>();
		listWithTwoCategories.add(cat1);
		listWithTwoCategories.add(cat2);
		
		Budget bud2 = new Budget("TestBudget", 
								listWithTwoCategories, 
								1000.0f, 
								LocalDate.of(2016, Month.MARCH, 1),
								LocalDate.of(2016, Month.MARCH, 31));

		List<Category> listWithTwoOtherCategories = new ArrayList<>();
		listWithTwoOtherCategories.add(cat1);
		listWithTwoOtherCategories.add(cat3);
		
		Budget bud3 = new Budget("TestBudget01", 
								listWithTwoOtherCategories, 
								40.0f, 
								LocalDate.of(2015, Month.JUNE, 1),
								LocalDate.of(2015, Month.JUNE, 30));

		Budget budToDelete = new Budget("TestBudget to delete", 
								listWithTwoOtherCategories, 
								616.0f, 
								LocalDate.of(2016, Month.JUNE, 1),
								LocalDate.of(2016, Month.JUNE, 30));
		
		budgetDao.saveOrUpdate(bud1);
		budgetDao.saveOrUpdate(bud2);
		budgetDao.saveOrUpdate(bud3);
		budgetDao.saveOrUpdate(budToDelete);
		
		listToDelete.add(0, bud1);
		listToDelete.add(0, bud2);
		listToDelete.add(0, bud3);

		idBudgetForUpdate = bud3.getId();
		idBudgetForDeletion = budToDelete.getId();
		
		// inserting Lines
		Categorisation catego1 = new Categorisation(23.01f, cat2);
		List<Categorisation> emptyListCatego = new ArrayList<>();
		List<Categorisation> oneListCatego = new ArrayList<>();
		oneListCatego.add(catego1);
		
		List<Categorisation> twoListCatego = new ArrayList<>();
		Categorisation catego2 = new Categorisation(-00.05f, cat1);
		Categorisation catego3 = new Categorisation(481.01f, cat3);
		twoListCatego.add(catego2);
		twoListCatego.add(catego3);
		
		List<Categorisation> twoOtherListCatego = new ArrayList<>();
		Categorisation categoToDelete1 = new Categorisation(61.6f, cat1);
		Categorisation categoToDelete2 = new Categorisation(6.16f, cat2);
		Categorisation categoToDelete3 = new Categorisation(6.16f, cat3);
		twoOtherListCatego.add(categoToDelete1);
		twoOtherListCatego.add(categoToDelete2);
		
		categoDao.saveOrUpdate(catego1);
		categoDao.saveOrUpdate(catego2);
		categoDao.saveOrUpdate(catego3);
		categoDao.saveOrUpdate(categoToDelete1);
		categoDao.saveOrUpdate(categoToDelete2);
		categoDao.saveOrUpdate(categoToDelete3);

		listToDelete.add(0, catego1);
		listToDelete.add(0, catego2);
		listToDelete.add(0, catego3);

		idCategorisationForUpdate = catego2.getId();
		idCategorisationForDeletion = categoToDelete3.getId();
		
		Line lin1 = new Line(	LocalDate.of(2016, Month.MARCH, 01), 
								"TestLine 01", 
								"category: empty list", 
								123.45f, 
								false, 
								emptyListCatego);

		Line lin2 = new Line(	LocalDate.of(2016, Month.APRIL, 02), 
								"TestLine 02", 
								"category: one category",  
								-123.45f, 
								false, 
								oneListCatego);

		Line lin3 = new Line(	LocalDate.of(2016, Month.MAY, 05), 
								"TestLine 03", 
								"category: 2 categorisations", 
								612024.45f, 
								false, 
								twoListCatego);

		Line lin4 = new Line(	LocalDate.of(2016, Month.MAY, 05), 
								"TestLine 03", 
								"category: 2 categorisations", 
								-24.45f, 
								true, 
								new ArrayList<Categorisation>());

		Line linToDelete = new Line(LocalDate.of(2016, Month.JUNE, 01), 
								"TestLine To delete", 
								"category: 2 categorisations", 
								-616.0f, 
								true, 
								twoOtherListCatego);
		
		lineDao.saveOrUpdate(lin1);
		lineDao.saveOrUpdate(lin2);
		lineDao.saveOrUpdate(lin3);
		lineDao.saveOrUpdate(lin4);
		lineDao.saveOrUpdate(linToDelete);

		listToDelete.add(0, lin1);
		listToDelete.add(0, lin2);
		listToDelete.add(0, lin3);
		listToDelete.add(0, lin4);

		idLineForUpdate = lin1.getId();
		idLineForDeletion = linToDelete.getId();

		int nbBalancesAfterInsertions = balanceDao.count();
		int nbBudgetsAfterInsertions = budgetDao.count();
		int nbCategoriesAfterInsertions = categoryDao.count();
		int nbCategoAfterInsertions = categoryDao.count();
		int nbLinesAfterInsertions = lineDao.count();
		int nbUsersAfterInsertions = userDao.count();
		
		logger.debug("loadUpDatabase - nbBalancesAfterInsertions: " + nbBalancesAfterInsertions);
		logger.debug("loadUpDatabase - nbBudgetsAfterInsertions: " + nbBudgetsAfterInsertions);
		logger.debug("loadUpDatabase - nbCategoriesAfterInsertions: " + nbCategoriesAfterInsertions);
		logger.debug("loadUpDatabase - nbCategoAfterInsertions: " + nbCategoAfterInsertions);
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
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		int nbBalancesAfterTests = balanceDao.count();
		int nbBudgetsAfterTests = budgetDao.count();
		int nbCategoriesAfterTests = categoryDao.count();
		int nbCategorisationsAfterTests = categoDao.count();
		int nbLinesAfterTests = lineDao.count();
		int nbUsersAfterTests = userDao.count();
		
		
		logger.debug("cleanUpDatabase - nbBalancesAfterTests: " + nbBalancesAfterTests);
		logger.debug("cleanUpDatabase - nbBudgetsAfterTests: " + nbBudgetsAfterTests);
		logger.debug("cleanUpDatabase - nbCategoriesAfterTests: " + nbCategoriesAfterTests);
		logger.debug("cleanUpDatabase - nbCategoAfterTests: " + nbCategorisationsAfterTests);
		logger.debug("cleanUpDatabase - nbLinesAfterTests: " + nbLinesAfterTests);
		logger.debug("cleanUpDatabase - nbUsersAfterTests: " + nbUsersAfterTests);

		for(Object currObject : listToDelete){
			
			if(listAlreadyDeleted.contains(currObject)){
				logger.debug("Skipping " + currObject + 
						" because it's already been deleted.");
			} else {
				if(currObject instanceof Balance){
					Balance balTodelete = (Balance) currObject;
					balanceDao.delete(balTodelete);
				} else if(currObject instanceof Budget){
					Budget balTodelete = (Budget) currObject;
					budgetDao.delete(balTodelete);
				} else if(currObject instanceof Category){
					Category catTodelete = (Category) currObject;
					try{
						categoryDao.delete(catTodelete);
					}  catch(ConstraintViolationException cve){
						logger.warn("'Seems like there are still Budgets needing "
								+ "to be deleted before this Category (#" + catTodelete.getId() 
								+ ") can be. Deleting them now...");
						
						List<Budget> budgetListToDelete = 
								budgetDao.searchBudgetContainingCategory(catTodelete);
						
						for(Budget budgetToDelete : budgetListToDelete){
							// setting categoryList to null to avoid a HibernateException
							// (illegal attempt to associate a Collection with 2 open sessions)
							// but has to be after the adding in the listAlreayDeleted so that
							// budget.equals still works
							listAlreadyDeleted.add(budgetToDelete);
							budgetToDelete.setCategoryList(null);
							budgetDao.delete(budgetToDelete);
						}
						logger.warn("Budget(s) deleted. Re-trying to delete Category...");
						categoryDao.delete(catTodelete);
						logger.warn("Category deleted.");
					}
				} else if(currObject instanceof Categorisation){
					Categorisation categoTodelete = (Categorisation) currObject;
					categoDao.delete(categoTodelete);				
				} else if(currObject instanceof Line){
					Line linToDelete = (Line) currObject;
					try {
						lineDao.delete(linToDelete);
					} catch(ConstraintViolationException cve){
						logger.warn("'Seems like this Line (#" + linToDelete.getId() 
								+ ") has Categorisations in the database but not the Java bean. "
								+ "Fetching them now to delete properly...");
						linToDelete = lineDao.fetch(linToDelete.getId());
//						for(Categorisation categoToDeleteBefore : 
//							linToDelete.getCategorisationList()){
//							categoDao.delete(categoToDeleteBefore);
//						}
						logger.warn(linToDelete.getCategorisationList().size() 
										+ " Categorisation(s) fetched. Re-trying to delete Line...");
						lineDao.delete(linToDelete);
						logger.warn("Line deleted.");
					}
					for(Categorisation categoDeleted : linToDelete.getCategorisationList()){
						listAlreadyDeleted.add(categoDeleted);
						logger.debug("Added Categorisation (#" + 
								categoDeleted.getId() + 
								") to list of already deleted objects.");
					}
					
				} else if(currObject instanceof User){
					User userTodelete = (User) currObject;
					userDao.delete(userTodelete);
				} else {
					throw new IllegalArgumentException("An object had no corresponding DAO! It's a : " 
							+ currObject.getClass().getSimpleName() + ".");
				}
			}
		}
		
		int nbBalancesAfterDeletions = balanceDao.count();
		int nbBudgetsAfterDeletions = budgetDao.count();
		int nbCategoriesAfterDeletions = categoryDao.count();
		int nbCategorisationsAfterDeletions = categoDao.count();
		int nbLinesAfterDeletions = lineDao.count();
		int nbUsersAfterDeletions = userDao.count();
		
		logger.debug("cleanUpDatabase - nbBalancesAfterDeletions: " + nbBalancesAfterDeletions);
		logger.debug("cleanUpDatabase - nbBudgetsAfterDeletions: " + nbBudgetsAfterDeletions);
		logger.debug("cleanUpDatabase - nbCategoriesAfterDeletions: " + nbCategoriesAfterDeletions);
		logger.debug("cleanUpDatabase - nbCategoAfterDeletions: " + nbCategorisationsAfterDeletions);
		logger.debug("cleanUpDatabase - nbLinesAfterDeletions: " + nbLinesAfterDeletions);
		logger.debug("cleanUpDatabase - nbUsersAfterDeletions: " + nbUsersAfterDeletions);

		boolean throwException = false;
		String errorMessageSuffix = "";
		if(nbBalancesAfterDeletions != nbBalancesBeforeTests){
			throwException = true;
			errorMessageSuffix += "Balances (found " + nbBalancesAfterDeletions + 
									", should be " + nbBalancesBeforeTests + ")"; 
		}
		if(nbBudgetsAfterDeletions != nbBudgetsBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Budget (found " + nbBudgetsAfterDeletions + 
									", should be " + nbBudgetsBeforeTests + ")"; 
		}
		if(nbCategoriesAfterDeletions != nbCategoriesBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Categories (found " + nbCategoriesAfterDeletions + 
									", should be " + nbCategoriesBeforeTests + ")"; 
		}
		if(nbCategorisationsAfterDeletions != nbCategoBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Categorisations (found " + nbCategorisationsAfterDeletions + 
									", should be " + nbCategoBeforeTests + ")"; 
		}
		if(nbLinesAfterDeletions != nbLinesBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Lines (found " + nbLinesAfterDeletions + 
									", should be " + nbLinesBeforeTests + ")"; 
		}
		if(nbUsersAfterDeletions != nbUsersBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Users(found " + nbUsersAfterDeletions + 
									", should be " + nbUsersBeforeTests + ")"; 
		}
		if(throwException){
			throw new TestException("Found values in DB after tests from the following classes : " + errorMessageSuffix);
		}
		
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
		critList.put("secondaryLabel", "category: 2 categorisations");
		
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		List<Line> twoResultsLine = lineDao.search(critList);
		
		assertEquals("Wrong number of Line "
						+ "found when searching with 1 criteria.", 
					2, 
					twoResultsLine.size());
		
		// Test : 2 criteria, 1 result
		critList = new HashMap<>();
		critList.put("secondaryLabel", "category: 2 categorisations");
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

		listToDelete.add(balanceToInsert);
		
		// Checking by counting
		int nbBalancesAfterInsertion = balanceDao.count();
		assertEquals("Wrong number of Balances counted after insertion test", 
						nbBalancesBeforeInsertion + 1, 
						nbBalancesAfterInsertion);
		
		// Checking by fetching
		Balance balanceForVerification = balanceDao.fetch(balanceToInsert.getId());
		Validator.validateBalance("value checking after insertion of Balance.", 
									balanceToInsert, 
									balanceForVerification);
		
		// Category (simple)
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		int nbCategorysBeforeInsertion = catDao.count();
		
		// Creating value
		Category catToInsert = new Category("Category for insertion test", null);
		catDao.saveOrUpdate(catToInsert);


		listToDelete.add(catToInsert);
		
		// Checking by counting
		int nbCategorysAfterInsertion = catDao.count();
		assertEquals("Wrong number of Categorys counted after insertion test", 
						nbCategorysBeforeInsertion + 1, 
						nbCategorysAfterInsertion);
		
		// Checking by fetching
		Category catForVerification = catDao.fetch(catToInsert.getId());
		Validator.validateCategory("value checking after insertion of Categorys.", 
						catToInsert, 
						catForVerification);

		
		
		// Category (with father)
		nbCategorysBeforeInsertion = catDao.count();
		
		// Creating value
		Category catToInsertWithFather = new Category("Category for insertion test (with father)", catToInsert);
		catDao.saveOrUpdate(catToInsertWithFather);

		listToDelete.add(catToInsertWithFather);
		
		// Checking by counting
		nbCategorysAfterInsertion = catDao.count();
		assertEquals("Wrong number of Categorys counted after insertion test (with father)", 
						nbCategorysBeforeInsertion + 1, 
						nbCategorysAfterInsertion);
		
		// Checking by fetching
		catForVerification = catDao.fetch(catToInsertWithFather.getId());
		Validator.validateCategory("value checking after insertion of Categorys (with father).", 
						catToInsertWithFather, 
						catForVerification);
		
		// Budget
		// Counting before
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		int nbBudgetsBeforeInsertion = budgetDao.count();
		
		// Creating value
		LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
		boolean isCurrentYearLeap = monthStart.isLeapYear();
		int currentMonthLength = monthStart.getMonth().length(isCurrentYearLeap);
		LocalDate monthEnd = LocalDate.now().withDayOfMonth(currentMonthLength);
		List<Category> categoryList = new ArrayList<>();
		categoryList.add(catToInsert);
		categoryList.add(catToInsertWithFather);
		Budget budgetToInsert = new Budget("Testbudget to insert", categoryList, -1245.69f, monthStart, monthEnd);
		budgetDao.saveOrUpdate(budgetToInsert);

		listToDelete.add(budgetToInsert);
		
		// Checking by counting
		int nbBudgetsAfterInsertion = budgetDao.count();
		assertEquals("Wrong number of Budgets counted after insertion test", 
						nbBudgetsBeforeInsertion + 1, 
						nbBudgetsAfterInsertion);
		
		// Checking by fetching
		Budget budgetForVerification = budgetDao.fetch(budgetToInsert.getId());
		Validator.validateBudget("value checking after insertion of Budget.", budgetToInsert, budgetForVerification);
		
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
									new ArrayList<>());
		linDao.saveOrUpdate(linToInsert);

		listToDelete.add(linToInsert);
		
		// Checking by counting
		int nbLinesAfterInsertion = linDao.count();
		assertEquals("Wrong number of Lines counted after insertion test", 
						nbLinesBeforeInsertion + 1, 
						nbLinesAfterInsertion);
		
		// Checking by fetching
		Line lineForVerification = linDao.fetch(linToInsert.getId());
		Validator.validateLine("value checking after insertion of Line.", linToInsert, lineForVerification);
		
		// Line (with category)
		// Counting before
		nbLinesBeforeInsertion = linDao.count();
		
		List<Categorisation> listeCatego = new ArrayList<>();
		listeCatego.add(new Categorisation(456f, catToInsert));
		
		// Creating value
		Line linToInsertWithCategory = new Line(LocalDate.now(), 
											"Line for insertion test (with category)", 
											"Line for insertion test (sec label) (with category)", 
											-15150.123f, 
											false, 
											listeCatego);
		linDao.saveOrUpdate(linToInsertWithCategory);

		listToDelete.add(linToInsertWithCategory);
		
		// Checking by counting
		nbLinesAfterInsertion = linDao.count();
		assertEquals("Wrong number of Lines counted after insertion test (with category)", 
						nbLinesBeforeInsertion + 1, 
						nbLinesAfterInsertion);
		
		// Checking by fetching
		lineForVerification = linDao.fetch(linToInsertWithCategory.getId());
		Validator.validateLine("value checking after insertion of Line (with category).", 
								linToInsertWithCategory, 
								lineForVerification);

		// User
		// Counting before
		GenericDao<User> userDao = new GenericDao<>(User.class);
		int nbUsersBeforeInsertion = userDao.count();
		
		// Creating value
		User userToInsert = new User("User for insertion test", "pwd", true);
		userDao.saveOrUpdate(userToInsert);

		listToDelete.add(userToInsert);
		
		// Checking by counting
		int nbUsersAfterInsertion = userDao.count();
		assertEquals("Wrong number of Lines counted after insertion test", 
						nbUsersBeforeInsertion + 1, 
						nbUsersAfterInsertion);
		
		// Checking by fetching
		User userForVerification = userDao.fetch(userToInsert.getId());
		Validator.validateUser("value checking after insertion of User.", userToInsert, userForVerification);
		
		
		logger.info("testInsert done.");
	}
	
	@Test
	public void testUpdate(){
		logger.info("Starting testUpdate...");
		
		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budDao = new GenericDao<>(Budget.class);
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		GenericDao<Line> linDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		
		// Balance
		// Fetching value to update
		Balance balanceToUpdate = balanceDao.fetch(idBalanceForUpdate);
		
		// Checking we're not overwriting the same data 
		// (which would invalidate the test)
		LocalDate updatingLocalDateTo = LocalDate.now();
		Float updatingFloatTo = new Float(7894.25f);
		assertNotEquals("Wrong amount on fetch for update", updatingFloatTo, balanceToUpdate.getAmount());
		assertNotEquals("Wrong date on fetch for update", updatingLocalDateTo, balanceToUpdate.getFetchDate());
		
		// Changing the value
		balanceToUpdate.setAmount(updatingFloatTo);
		balanceToUpdate.setFetchDate(updatingLocalDateTo);
		
		// Saving
		balanceDao.saveOrUpdate(balanceToUpdate);
		
		// Fetching value for verification
		Balance balanceToCheck = balanceDao.fetch(idBalanceForUpdate);
		Validator.validateBalance("update test", balanceToUpdate, balanceToCheck);

		
		// Budget
		// Fetching value to update
		Budget budToUpdate = budDao.fetch(idBudgetForUpdate);
		
		// Checking we're not overwriting the same data 
		// (which would invalidate the test)
		String updatingBudgetNameTo = "test update of budget";
		LocalDate updatingStartDateTo = LocalDate.now().withDayOfMonth(2);
		LocalDate updatingEndDateTo = LocalDate.now().withDayOfMonth(16);
		Float updatingMaxAmountTo = new Float(769.74f);
		assertNotEquals("Wrong name on fetch for update", updatingBudgetNameTo, budToUpdate.getName());
		assertNotEquals("Wrong startDate on fetch for update", updatingStartDateTo, budToUpdate.getStartDate());
		assertNotEquals("Wrong endDate on fetch for update", updatingEndDateTo, budToUpdate.getEndDate());
		assertNotEquals("Wrong maxAmount on fetch for update", updatingMaxAmountTo, budToUpdate.getMaxAmount());
		assertNotEquals("Wrong categoryMap (size) on fetch for update", 4, budToUpdate.getCategoryList().size());
		
		// Changing the value
		budToUpdate.setName(updatingBudgetNameTo);
		budToUpdate.setStartDate(updatingStartDateTo);
		budToUpdate.setEndDate(updatingEndDateTo);
		budToUpdate.setMaxAmount(updatingMaxAmountTo);
		Category catToAdd1 = new Category("Category for budget update test 1", null);
		Category catToAdd2 = new Category("Category for budget update test 2", null);
		catDao.saveOrUpdate(catToAdd1);
		catDao.saveOrUpdate(catToAdd2);
		budToUpdate.getCategoryList().add(catToAdd1);
		budToUpdate.getCategoryList().add(catToAdd2);
		
		listToDelete.add(catToAdd1);
		listToDelete.add(catToAdd2);
		
		// Saving
		budDao.saveOrUpdate(budToUpdate);
		
		// Fetching value for verification
		Budget budToCheck = budDao.fetch(idBudgetForUpdate);
		Validator.validateBudget("update test", budToUpdate, budToCheck);
		
		
		// Category
		// Fetching value to update
		Category catToUpdate = catDao.fetch(idCategoryForUpdate);
		
		// Checking we're not overwriting the same data 
		// (which would invalidate the test)
		String updatingCategoryNameTo = "test update of categroy";
		String updatingFatherCategoryNameTo = "test update of categroy (father category)";
		assertNotEquals("Wrong name on fetch for update", updatingCategoryNameTo, catToUpdate.getName());
		assertNull("Wrong fatherCategory on fetch for update", catToUpdate.getFatherCategory());
		
		// Changing the value
		catToUpdate.setName(updatingCategoryNameTo);
		catToUpdate.setFatherCategory(new Category(updatingFatherCategoryNameTo, null));
		
		// Saving
		catDao.saveOrUpdate(catToUpdate);
		
		// Fetching value for verification
		Category catToCheck = catDao.fetch(idCategoryForUpdate);
		Validator.validateCategory("update test", catToUpdate, catToCheck);
		
		// Adding father category to list to delete
		listToDelete.add(catToUpdate.getFatherCategory());
		
		
		// Categorisation
		// fetching value to update
		Categorisation categoToUpdate = categoDao.fetch(idCategorisationForUpdate);

		// Checking we're not overwriting the same data 
		// (which would invalidate the test)
		Category updatingCategoryTo = catToAdd1;
		Float updatingAmountTo = 32.6f;
		assertNotEquals("Wrong Category on fetch for update", updatingCategoryTo, categoToUpdate.getCategory());
		assertNotEquals("Wrong amount on fetch for update", updatingAmountTo, categoToUpdate.getAmount());
		
		// Changing the value
		categoToUpdate.setCategory(updatingCategoryTo);
		categoToUpdate.setAmount(updatingAmountTo);

		// Saving
		categoDao.saveOrUpdate(categoToUpdate);
		
		// Fetching value for verification
		Categorisation categoToCheck = categoDao.fetch(idCategorisationForUpdate);
		Validator.validateCategorisation("update test", categoToUpdate, categoToCheck);
		
		
		// Line
		// Fetching value to update
		Line linToUpdate = linDao.fetch(idLineForUpdate);
		
		// Checking we're not overwriting the same data 
		// (which would invalidate the test)
		Float updatingLineAmountTo = 14785.23f;
		LocalDate updatingLineDateTo = LocalDate.now().withDayOfMonth(2);
		String updatingMainLabelTo = "test update of line";
		String updatingSecondaryLabelTo = "test update of line (secondary label)";
		Boolean updatingRecurringTo = true;
		assertNotEquals("Wrong amount on fetch for update", updatingLineAmountTo, linToUpdate.getAmount());
		assertNotEquals("Wrong date on fetch for update", updatingLineDateTo, linToUpdate.getAmount());
		assertNotEquals("Wrong mainLabel on fetch for update", updatingMainLabelTo, linToUpdate.getAmount());
		assertNotEquals("Wrong secondaryLabel on fetch for update", updatingSecondaryLabelTo, linToUpdate.getAmount());
		assertNotEquals("Wrong recurring on fetch for update", updatingRecurringTo, linToUpdate.getAmount());
		assertNotEquals("Wrong size of categorisationLise on fetch for update", 
						1, 
						linToUpdate.getCategorisationList().size());
		
		// Changing the value
		linToUpdate.setAmount(updatingLineAmountTo);
		linToUpdate.setDate(updatingLineDateTo);
		linToUpdate.setMainLabel(updatingMainLabelTo);
		linToUpdate.setSecondaryLabel(updatingSecondaryLabelTo);
		linToUpdate.setRecurring(updatingRecurringTo);
		linToUpdate.getCategorisationList().add(new Categorisation(45f, catToUpdate));
		
		// Saving
		linDao.saveOrUpdate(linToUpdate);
		
		// Fetching value for verification
		Line linToCheck = linDao.fetch(idLineForUpdate);
		Validator.validateLine("update test", linToUpdate, linToCheck);

		
		// User
		// Fetching value to update
		User userToUpdate = userDao.fetch(idUserForUpdate);
		
		// Checking we're not overwriting the same data 
		// (which would invalidate the test)
		String updatingLoginTo = "test update of line";
		String updatingPasswordTo = "test update of line (secondary label)";
		Boolean updatingDailyNotificationTo = false;
		assertNotEquals("Wrong login on fetch for update", updatingLoginTo, userToUpdate.getLogin());
		assertNotEquals("Wrong password on fetch for update", updatingPasswordTo, userToUpdate.getPassword());
		assertNotEquals("Wrong daily notification on fetch for update", 
						updatingDailyNotificationTo, 
						userToUpdate.getDailyNotification());
		
		// Changing the value
		userToUpdate.setLogin(updatingLoginTo);
		userToUpdate.setPassword(updatingPasswordTo);
		userToUpdate.setDailyNotification(updatingDailyNotificationTo);
		
		// Saving
		userDao.saveOrUpdate(userToUpdate);
		
		// Fetching value for verification
		User userToCheck = userDao.fetch(idUserForUpdate);
		Validator.validateUser("update test", userToUpdate, userToCheck);
		
		logger.info("testUpdate done.");
	}

	@Test
	public void testDelete(){
		logger.info("Starting testDelete...");

		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budDao = new GenericDao<>(Budget.class);
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		GenericDao<Line> linDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		
		// Balance
		int nbBalanceBeforeDeletion = balanceDao.count();
		
		// Fetching value to delete
		Balance balanceToDelete = balanceDao.fetch(idBalanceForDeletion);
		
		balanceDao.delete(balanceToDelete);
		
		// Check by counting
		int nbBalanceAfterDeletion = balanceDao.count();
		assertEquals("Wrong number of Balances after deletion test.", nbBalanceBeforeDeletion - 1, nbBalanceAfterDeletion);
		
		// Check by fetching
		Balance balanceToCheck = balanceDao.fetch(idBalanceForDeletion);
		assertNull("Wrong Balance found after deletion test (should be null)", balanceToCheck);

		
		// Budget
		int nbBudgetBeforeDeletion = budDao.count();
		
		// Fetching value to delete
		Budget budgetToDelete = budDao.fetch(idBudgetForDeletion);
		
		budDao.delete(budgetToDelete);
		
		// Check by counting
		int nbBudgetAfterDeletion = balanceDao.count();
		assertEquals("Wrong number of Budgets after deletion test.", nbBudgetBeforeDeletion - 1, nbBudgetAfterDeletion);
		
		// Check by fetching
		Budget budgetToCheck = budDao.fetch(idBudgetForDeletion);
		assertNull("Wrong Budget found after deletion test (should be null)", budgetToCheck);
		

		// Category
		int nbCategoryBeforeDeletion = catDao.count();
		
		// Fetching value to delete
		Category catToDelete = catDao.fetch(idCategoryForDeletion);
		
		catDao.delete(catToDelete);
		
		// Check by counting
		int nbCategoryAfterDeletion = catDao.count();
		assertEquals("Wrong number of Categories after deletion test.", 
				nbCategoryBeforeDeletion - 1, 
				nbCategoryAfterDeletion);
		
		// Check by fetching
		Category catToCheck = catDao.fetch(idCategoryForDeletion);
		assertNull("Wrong Category found after deletion test (should be null)", catToCheck);


		// Categorisation
		int nbCategorisationBeforeDeletion = categoDao.count();
		
		// Fetching value to delete
		Categorisation categoToDelete = categoDao.fetch(idCategorisationForDeletion);
		
		categoDao.delete(categoToDelete);
		
		// Check by counting
		int nbCategorisationAfterDeletion = categoDao.count();
		assertEquals("Wrong number of Categorisations after deletion test.", 
				nbCategorisationBeforeDeletion - 1, 
				nbCategorisationAfterDeletion);
		
		// Check by fetching
		Categorisation catgegoToCheck = categoDao.fetch(idCategorisationForDeletion);
		assertNull("Wrong Categorisation found after deletion test (should be null)", catgegoToCheck);
		

		// Line
		int nbLineBeforeDeletion = linDao.count();
		
		// Fetching value to delete
		Line linToDelete = linDao.fetch(idLineForDeletion);
		
		linDao.delete(linToDelete);
		
		// Check by counting
		int nbLineAfterDeletion = linDao.count();
		assertEquals("Wrong number of Lines after deletion test.", nbLineBeforeDeletion - 1, nbLineAfterDeletion);
		
		// Check by fetching
		Line linToCheck = linDao.fetch(idLineForDeletion);
		assertNull("Wrong Line found after deletion test (should be null)", linToCheck);


		// User
		int nbUserBeforeDeletion = userDao.count();
		
		// Fetching value to delete
		User userToDelete = userDao.fetch(idUserForDeletion);
		
		userDao.delete(userToDelete);
		
		// Check by counting
		int nbUserAfterDeletion = userDao.count();
		assertEquals("Wrong number of Lines after deletion test.", nbUserBeforeDeletion - 1, nbUserAfterDeletion);
		
		// Check by fetching
		User userToCheck = userDao.fetch(idUserForDeletion);
		assertNull("Wrong User found after deletion test (should be null)", userToCheck);

		logger.info("testDelete done.");
	}
}