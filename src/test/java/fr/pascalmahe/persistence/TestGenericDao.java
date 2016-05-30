package fr.pascalmahe.persistence;

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
		
		nbBalancesBeforeTests = GenericDao.count(Balance.class);
		nbCategoriesBeforeTests = GenericDao.count(Category.class);
		nbUsersBeforeTests = GenericDao.count(User.class);
		nbBudgetsBeforeTests = GenericDao.count(Budget.class);
		nbLinesBeforeTests = GenericDao.count(Line.class);
		
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
		
		GenericDao.saveOrUpdate(bal1);
		GenericDao.saveOrUpdate(bal2);
		GenericDao.saveOrUpdate(bal3);

		listToDelete.add(0, bal1); // insertion toujours en premier pour permettre 
		listToDelete.add(0, bal2); // de supprimer dans l'ordre inverse
		listToDelete.add(0, bal3);
		
		// inserting Categories
		
		Category cat1 = new Category("TestStandaloneCategory", null);
		Category cat2 = new Category("TestFatherCategory", null);
		Category cat3 = new Category("TestSonCategory", cat2);
		
		GenericDao.saveOrUpdate(cat1);
		GenericDao.saveOrUpdate(cat2);
		GenericDao.saveOrUpdate(cat3);

		listToDelete.add(0, cat1);
		listToDelete.add(0, cat2);
		listToDelete.add(0, cat3);
		
		// inserting Users
		
		User use1 = new User("TestUser 01", "Unsafe password", false);
		User use2 = new User("TestUser 02", "Unsafe password", true);
		User use3 = new User("TestUser 03", "Unsafe password", true);
		
		GenericDao.saveOrUpdate(use1);
		GenericDao.saveOrUpdate(use2);
		GenericDao.saveOrUpdate(use3);

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
		
		Budget bud3 = new Budget("TestBudget", 
								listWithTwoOtherCategories, 
								40.0f, 
								LocalDate.of(2015, Month.JUNE, 1),
								LocalDate.of(2015, Month.JUNE, 30));
		
		GenericDao.saveOrUpdate(bud1);
		GenericDao.saveOrUpdate(bud2);
		GenericDao.saveOrUpdate(bud3);
		
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
								"category: TestSonCategory", 
								612024.45f, 
								false, 
								cat3);
		
		GenericDao.saveOrUpdate(lin1);
		GenericDao.saveOrUpdate(lin2);
		GenericDao.saveOrUpdate(lin3);

		listToDelete.add(0, lin1);
		listToDelete.add(0, lin2);
		listToDelete.add(0, lin3);


		int nbBalancesAfterInsertions = GenericDao.count(Balance.class);
		int nbBudgetsAfterInsertions = GenericDao.count(Budget.class);
		int nbCategoriesAfterInsertions = GenericDao.count(Category.class);
		int nbLinesAfterInsertions = GenericDao.count(Line.class);
		int nbUsersAfterInsertions = GenericDao.count(User.class);
		
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

		int nbBalancesAfterTests = GenericDao.count(Balance.class);
		int nbBudgetsAfterTests = GenericDao.count(Budget.class);
		int nbCategoriesAfterTests = GenericDao.count(Category.class);
		int nbLinesAfterTests = GenericDao.count(Line.class);
		int nbUsersAfterTests = GenericDao.count(User.class);
		
		logger.debug("cleanUpDatabase - nbBalancesAfterTests: " + nbBalancesAfterTests);
		logger.debug("cleanUpDatabase - nbBudgetsAfterTests: " + nbBudgetsAfterTests);
		logger.debug("cleanUpDatabase - nbCategoriesAfterTests: " + nbCategoriesAfterTests);
		logger.debug("cleanUpDatabase - nbLinesAfterTests: " + nbLinesAfterTests);
		logger.debug("cleanUpDatabase - nbUsersAfterTests: " + nbUsersAfterTests);

		for(Object currObject : listToDelete){
			GenericDao.delete(currObject);
		}
		int nbBalancesAfterDeletions = GenericDao.count(Balance.class);
		int nbBudgetsAfterDeletions = GenericDao.count(Budget.class);
		int nbCategoriesAfterDeletions = GenericDao.count(Category.class);
		int nbLinesAfterDeletions = GenericDao.count(Line.class);
		int nbUsersAfterDeletions = GenericDao.count(User.class);
		
		logger.debug("cleanUpDatabase - nbBalancesAfterDeletions: " + nbBalancesAfterDeletions);
		logger.debug("cleanUpDatabase - nbBudgetsAfterDeletions: " + nbBudgetsAfterDeletions);
		logger.debug("cleanUpDatabase - nbCategoriesAfterDeletions: " + nbCategoriesAfterDeletions);
		logger.debug("cleanUpDatabase - nbLinesAfterDeletions: " + nbLinesAfterDeletions);
		logger.debug("cleanUpDatabase - nbUsersAfterDeletions: " + nbUsersAfterDeletions);

		logger.info("cleanUpDatabase - Test data deleted.");
	}

	@Test
	public void testSearch() {
		logger.debug("ICI");
	}

}