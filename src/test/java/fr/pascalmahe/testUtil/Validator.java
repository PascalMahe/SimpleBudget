package fr.pascalmahe.testUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import fr.pascalmahe.business.Balance;
import fr.pascalmahe.business.Budget;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.Type;
import fr.pascalmahe.business.User;

public class Validator {
	
//	private static Logger logger = LogManager.getLogger();
	
	public static void validateBalance(String message, Balance expected, Balance actual){
		assertEquals("Wrong id in Balance " + message, expected.getId(), actual.getId());
		
		assertEquals("Wrong amount in Balance " + message, expected.getAmount(), actual.getAmount());
		assertEquals("Wrong fetchDate in Balance " + message, expected.getFetchDate(), actual.getFetchDate());
	}

	public static void validateBudget(String message, Budget expected, Budget actual){
		assertEquals("Wrong id in Budget " + message, expected.getId(), actual.getId());
		
		
		assertEquals("Wrong endDate in Budget " + message, expected.getEndDate(), actual.getEndDate());
		assertEquals("Wrong maxAmount in Budget " + message, expected.getMaxAmount(), actual.getMaxAmount());
		assertEquals("Wrong name in Budget " + message, expected.getName(), actual.getName());
		assertEquals("Wrong startDate in Budget " + message, expected.getStartDate(), actual.getStartDate());
		
//		assertEquals("Wrong categoryList in Budget " + message, expected.getCategoryList(), actual.getCategoryList());
		List<Category> expectedCatList = expected.getCategoryList();
		List<Category> actualCatList = expected.getCategoryList();
		assertEquals("Wrong categoryList size in Budget " + message, 
						expectedCatList.size(), 
						actualCatList.size());
		for(int i = 0; i < expectedCatList.size(); i++){
			Category expectedCategory = expectedCatList.get(i);
			Category actualCategory = actualCatList.get(i);
			validateCategory("at index " + i + " in Budget " + message, expectedCategory, actualCategory);
		}
	}

	public static void validateCategorisation(String message, Categorisation expected, Categorisation actual){
		assertEquals("Wrong id in Categorisation " + message, expected.getId(), actual.getId());
		
		assertEquals("Wrong amount in Categorisation " + message, expected.getAmount(), actual.getAmount());
		
		validateCategory("from Categorisation from " + message, expected.getCategory(), actual.getCategory());
	}

	public static void validateCategory(String message, Category expected, Category actual){
		assertEquals("Wrong id in Category " + message, expected.getId(), actual.getId());
		
		assertEquals("Wrong name in Category " + message, expected.getName(), actual.getName());
		
		if(expected.getFatherCategory() != null && 
				actual.getFatherCategory() != null){
			validateCategory("(fatherCategory) from " + message, 
								expected.getFatherCategory(), 
								actual.getFatherCategory());
		} else {
			if(expected.getFatherCategory() == null){
				assertNull("Wrong fatherCategory in Category from " + message + " (should be null).", 
							actual.getFatherCategory());
			} else {
				// expected.fatherCategory is not null but actual.fatherCategory is
				fail("Wrong fatherCategory in Category from " + message + " (should not be null).");
			}
		}
	}

	public static void validateLine(String message, Line expected, Line actual){
		assertEquals("Wrong id in Line " + message, expected.getId(), actual.getId());
		
		assertEquals("Wrong amount in Line " + message, expected.getAmount(), actual.getAmount());
		
		assertEquals("Wrong date in Line " + message, expected.getDate(), actual.getDate());
		assertEquals("Wrong cCardDate in Line " + message, expected.getCCardDate(), actual.getCCardDate());
		assertEquals("Wrong detailedLabel in Line " + message, expected.getDetailedLabel(), actual.getDetailedLabel());
		assertEquals("Wrong note in Line " + message, expected.getNote(), actual.getNote());
		assertEquals("Wrong recurring in Line " + message, expected.getRecurring(), actual.getRecurring());
		assertEquals("Wrong shortLabel in Line " + message, expected.getShortLabel(), actual.getShortLabel());
		
		validateType("from Line from " + message, expected.getType(), actual.getType());
		
//		assertEquals("Wrong categorisationList in Line " + message, 
//				expected.getCategorisationList(), 
//				actual.getCategorisationList());
		List<Categorisation> expectedCategoList = expected.getCategorisationList();
		List<Categorisation> actualCategoList = expected.getCategorisationList();
		assertEquals("Wrong categorisationList size in Line " + message, 
						expectedCategoList.size(), 
						actualCategoList.size());
		for(int i = 0; i < expectedCategoList.size(); i++){
			Categorisation expectedCatego = expectedCategoList.get(i);
			Categorisation actualCatego = actualCategoList.get(i);
			validateCategorisation("at index " + i + " in Line " + message, expectedCatego, actualCatego);
		}
		
	}

	public static void validateUser(String message, User expected, User actual){
		assertEquals("Wrong id in User " + message, expected.getId(), actual.getId());
		
		assertEquals("Wrong dailyNotification in User " + message, 
						expected.getDailyNotification(), 
						actual.getDailyNotification());
		assertEquals("Wrong login in User " + message, expected.getLogin(), actual.getLogin());
		assertEquals("Wrong password in User " + message, expected.getPassword(), actual.getPassword());
		assertEquals("Wrong UUID in User " + message, expected.getUuid(), actual.getUuid());
	}

	public static void validateType(String message, Type expected, Type actual) {
		assertEquals("Wrong id in Type " + message, expected.getId(), actual.getId());
		assertEquals("Wrong name in Type " + message, expected.getName(), actual.getName());
	}
	
}
