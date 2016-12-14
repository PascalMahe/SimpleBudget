package fr.pascalmahe.testUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import fr.pascalmahe.business.Balance;
import fr.pascalmahe.business.Budget;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.Type;
import fr.pascalmahe.business.User;
import fr.pascalmahe.persistence.GenericDao;

public class AbstractTest {

	private static Logger logger = LogManager.getLogger();
	
	protected static List<Object> listToDelete = new ArrayList<>();
	protected static List<Object> listAlreadyDeleted = new ArrayList<>();
	
	protected static int nbBalancesBeforeTests;
	
	protected static int nbBudgetsBeforeTests;
	
	protected static int nbCategoriesBeforeTests;
	
	protected static int nbCategoBeforeTests;
	
	protected static int nbLinesBeforeTests;
	
	protected static int nbUsersBeforeTests;

	protected static int nbTypesBeforeTests;
	
	public static void preTestDatabaseCheckup(){

		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		GenericDao<Category> categoryDao = new GenericDao<>(Category.class);
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		GenericDao<Type> typeDao = new GenericDao<>(Type.class);
		
		nbBalancesBeforeTests = balanceDao.count();
		nbBudgetsBeforeTests = budgetDao.count();
		nbCategoriesBeforeTests = categoryDao.count();
		nbCategoBeforeTests = categoDao.count();
		nbLinesBeforeTests = lineDao.count();
		nbUsersBeforeTests = userDao.count();
		nbTypesBeforeTests = typeDao.count();
		
		logger.debug("loadUpDatabase - nbBalancesBeforeTests: " + nbBalancesBeforeTests);
		logger.debug("loadUpDatabase - nbBudgetsBeforeTests: " + nbBudgetsBeforeTests);
		logger.debug("loadUpDatabase - nbCategoriesBeforeTests: " + nbCategoriesBeforeTests);
		logger.debug("loadUpDatabase - nbCategoBeforeTests: " + nbCategoBeforeTests);
		logger.debug("loadUpDatabase - nbLinesBeforeTests: " + nbLinesBeforeTests);
		logger.debug("loadUpDatabase - nbUsersBeforeTests: " + nbUsersBeforeTests);
		logger.debug("loadUpDatabase - nbTypesBeforeTests: " + nbTypesBeforeTests);
	}
	
	public static void cleanUpDatabase() {

		int nbDeletedObjects = 0;
		logger.info("cleanUpDatabase - Deleting test data (" + listToDelete.size() + " objects to delete)...");
		
		GenericDao<Balance> balanceDao = new GenericDao<>(Balance.class);
		GenericDao<Budget> budgetDao = new GenericDao<>(Budget.class);
		GenericDao<Category> categoryDao = new GenericDao<>(Category.class);
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<User> userDao = new GenericDao<>(User.class);
		GenericDao<Type> typeDao = new GenericDao<>(Type.class);
		
		int nbBalancesAfterTests = balanceDao.count();
		int nbBudgetsAfterTests = budgetDao.count();
		int nbCategoriesAfterTests = categoryDao.count();
		int nbCategorisationsAfterTests = categoDao.count();
		int nbLinesAfterTests = lineDao.count();
		int nbUsersAfterTests = userDao.count();
		int nbTypesAfterTests = typeDao.count();
		
		logger.debug("cleanUpDatabase - nbBalancesAfterTests: " + nbBalancesAfterTests);
		logger.debug("cleanUpDatabase - nbBudgetsAfterTests: " + nbBudgetsAfterTests);
		logger.debug("cleanUpDatabase - nbCategoriesAfterTests: " + nbCategoriesAfterTests);
		logger.debug("cleanUpDatabase - nbCategoAfterTests: " + nbCategorisationsAfterTests);
		logger.debug("cleanUpDatabase - nbLinesAfterTests: " + nbLinesAfterTests);
		logger.debug("cleanUpDatabase - nbUsersAfterTests: " + nbUsersAfterTests);
		logger.debug("cleanUpDatabase - nbTypesAfterTests: " + nbTypesAfterTests);

		for(Object currObject : listToDelete){
			
			if(listAlreadyDeleted.contains(currObject)){
				logger.debug("Skipping " + currObject + 
						" because it's already been deleted.");
			} else {
				if(currObject instanceof Balance){
					
					Balance balTodelete = (Balance) currObject;
					logger.debug("Deleting Balance #" + balTodelete.getId() + "...");
					
					balanceDao.delete(balTodelete);
					nbDeletedObjects++;
					logger.debug("Balance #" + balTodelete.getId() + " deleted.");
					
				} else if(currObject instanceof Budget){
					Budget budgetTodelete = (Budget) currObject;
					logger.debug("Deleting Budget #" + budgetTodelete.getId() + "...");
					budgetDao.delete(budgetTodelete);
					nbDeletedObjects++;
					logger.debug("Budget #" + budgetTodelete.getId() + " deleted.");
					
				} else if(currObject instanceof Category){
					Category catTodelete = (Category) currObject;
					logger.debug("Deleting Category #" + catTodelete.getId() + "...");
					try{
						categoryDao.delete(catTodelete);
						nbDeletedObjects++;
					}  catch(ConstraintViolationException cve){
						logger.warn("'Seems like there are still Budgets, Categorys or Categorisations (or Lines through the Categorisations) needing "
								+ "to be deleted before this Category (#" + catTodelete.getId() 
								+ ": " + catTodelete + ") can be. Deleting them now...");
						
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
							nbDeletedObjects++;
						}
						logger.warn("Budget(s) deleted. Deleting Categorys...");
						
						Map<String, Object> searchCriteria = new HashMap<>();
						searchCriteria.put("fatherCategory.id", catTodelete.getId());
						
						List<Category> catListToDelete =
								categoryDao.search(searchCriteria);
						
						for(Category sonCatToDelete : catListToDelete){
							listAlreadyDeleted.add(sonCatToDelete);
							categoryDao.delete(sonCatToDelete);
							nbDeletedObjects++;
						}
						
						logger.warn("Category(s) deleted. Re-trying to delete Category...");
						categoryDao.delete(catTodelete);
						nbDeletedObjects++;
						logger.warn("Category deleted.");
					}
					logger.debug("Category #" + catTodelete.getId() + " deleted.");
					
				} else if(currObject instanceof Categorisation){
					Categorisation categoTodelete = (Categorisation) currObject;
					logger.debug("Deleting Categorisation #" + categoTodelete.getId() + "...");
					
					try{
						categoDao.delete(categoTodelete);
						nbDeletedObjects++;
					}  catch(ConstraintViolationException cve){
						logger.warn("'Seems like there is still a Line needing "
								+ "to be deleted before this Categorisation (#" + categoTodelete.getId() 
								+ ") can be. Deleting it (and all its Categorisations) now...");
						

						Line lineToDelete = 
								lineDao.searchLineContainingCatego(categoTodelete);
						
						listAlreadyDeleted.add(lineToDelete);
						listAlreadyDeleted.addAll(lineToDelete.getCategorisationList());
						lineDao.delete(lineToDelete);
						nbDeletedObjects = nbDeletedObjects + 1 + lineToDelete.getCategorisationList().size();

						logger.warn("Line deleted. The Categorisations have been deleted with it.");
					}
					
					logger.debug("Categorisation #" + categoTodelete.getId() + " deleted.");
					
				} else if(currObject instanceof Line){
					Line linToDelete = (Line) currObject;
					logger.debug("Deleting Line #" + linToDelete.getId() + "...");
					try {
						lineDao.delete(linToDelete);
						nbDeletedObjects++;
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
						nbDeletedObjects++;
						logger.warn("Line deleted.");
					}
					for(Categorisation categoDeleted : linToDelete.getCategorisationList()){
						listAlreadyDeleted.add(categoDeleted);
						nbDeletedObjects++;
						logger.debug("Added Categorisation (#" + 
								categoDeleted.getId() + 
								") to list of already deleted objects.");
					}

					logger.debug("Line #" + linToDelete.getId() + " deleted.");
					
				} else if(currObject instanceof User){
					User userToDelete = (User) currObject;
					logger.debug("Deleting User #" + userToDelete.getId() + "...");
					userDao.delete(userToDelete);
					nbDeletedObjects++;
					logger.debug("User #" + userToDelete.getId() + " deleted.");
					
				} else if(currObject instanceof Type){
					Type typeToDelete = (Type) currObject;
					logger.debug("Deleting Type #" + typeToDelete.getId() + "...");
					typeDao.delete(typeToDelete);
					nbDeletedObjects++;
					logger.debug("Type #" + typeToDelete.getId() + " deleted.");
					
				} else {
					throw new IllegalArgumentException("An object had no corresponding DAO! It's a: " 
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
		int nbTypesAfterDeletions = typeDao.count();
		
		logger.debug("cleanUpDatabase - nbBalancesAfterDeletions: " + nbBalancesAfterDeletions);
		logger.debug("cleanUpDatabase - nbBudgetsAfterDeletions: " + nbBudgetsAfterDeletions);
		logger.debug("cleanUpDatabase - nbCategoriesAfterDeletions: " + nbCategoriesAfterDeletions);
		logger.debug("cleanUpDatabase - nbCategoAfterDeletions: " + nbCategorisationsAfterDeletions);
		logger.debug("cleanUpDatabase - nbLinesAfterDeletions: " + nbLinesAfterDeletions);
		logger.debug("cleanUpDatabase - nbUsersAfterDeletions: " + nbUsersAfterDeletions);
		logger.debug("cleanUpDatabase - nbTypesAfterDeletions: " + nbTypesAfterDeletions);

		logger.info("cleanUpDatabase - deleted: " + nbDeletedObjects + " objects.");
		
		boolean throwException = false;
		String errorMessageSuffix = "";
		if(nbBalancesAfterDeletions != nbBalancesBeforeTests){
			throwException = true;
			errorMessageSuffix += "Balances (found " + nbBalancesAfterDeletions + 
									", should be " + nbBalancesBeforeTests + ") "; 
		}
		if(nbBudgetsAfterDeletions != nbBudgetsBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Budgets (found " + nbBudgetsAfterDeletions + 
									", should be " + nbBudgetsBeforeTests + ") "; 
		}
		if(nbCategoriesAfterDeletions != nbCategoriesBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Categories (found " + nbCategoriesAfterDeletions + 
									", should be " + nbCategoriesBeforeTests + ") "; 
		}
		if(nbCategorisationsAfterDeletions != nbCategoBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Categorisations (found " + nbCategorisationsAfterDeletions + 
									", should be " + nbCategoBeforeTests + ") "; 
		}
		if(nbLinesAfterDeletions != nbLinesBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Lines (found " + nbLinesAfterDeletions + 
									", should be " + nbLinesBeforeTests + ") "; 
		}
		if(nbUsersAfterDeletions != nbUsersBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Users(found " + nbUsersAfterDeletions + 
									", should be " + nbUsersBeforeTests + ") "; 
		}
		if(nbTypesAfterDeletions != nbTypesBeforeTests){
			throwException = true;
			if(errorMessageSuffix.length() > 0){
				errorMessageSuffix += ", ";
			}
			errorMessageSuffix += "Types(found " + nbTypesAfterDeletions + 
									", should be " + nbTypesBeforeTests + ") "; 
		}
		if(throwException){
			throw new TestException("Found values in DB after tests from the following classes: " + errorMessageSuffix);
		}
		
		logger.info("cleanUpDatabase - Test data deleted.");
	}

}
