package fr.pascalmahe.services;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.web.beans.CatChoice;

public class TestLineService extends LineService {

	private static final Logger logger = LogManager.getLogger();

	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		
		logger.info("afterClass finished.");
	}
	
	@Test
	public void testComputeCategoAmounts() {
		logger.info("Starting testComputeCategoAmounts...");
		
		// 1 CatChoice, 1 to fill
		Map<Integer, CatChoice> mapCatChoice = new HashMap<>();
		CatChoice catChoice0 = new CatChoice(0, new Category("whoCares0"), null, null, null);
		
		mapCatChoice.put(0, catChoice0);
		
		computeCatChoiceAmounts(100.0f, mapCatChoice);
		
		for(Integer categoID : mapCatChoice.keySet()){
			CatChoice currentCatChoice = mapCatChoice.get(categoID);
			assertThat("Wrong amount with one CatChoice to fill alone", 
						currentCatChoice.getAmount(), 
						is(100.0f));
		}
		
		// 2 CatChoices, 1 to fill
		mapCatChoice = new HashMap<>();
		catChoice0 = new CatChoice(0, new Category("whoCares0"), null, 50.0f, null);
		CatChoice catChoice1 = new CatChoice(1, new Category("whoCares1"), null, null, null);
		
		mapCatChoice.put(0, catChoice0);
		mapCatChoice.put(1, catChoice1);
		
		computeCatChoiceAmounts(100.0f, mapCatChoice);
		
		for(Integer categoID : mapCatChoice.keySet()){
			CatChoice currentCatChoice = mapCatChoice.get(categoID);
			assertThat("Wrong amount with one CatChoice to fill w/ respect to 1 other", 
					currentCatChoice.getAmount(), 
					is(50.0f));
		}
		
		// 3 CatChoices, 1 to fill
		
		mapCatChoice = new HashMap<>();
		catChoice0 = new CatChoice(0, new Category("whoCares0"), null, 50.0f, null);
		catChoice1 = new CatChoice(1, new Category("whoCares1"), null, 30.0f, null);
		CatChoice catChoice2 = new CatChoice(2, new Category("whoCares2"), null, null, null);

		mapCatChoice.put(0, catChoice0);
		mapCatChoice.put(1, catChoice1);
		mapCatChoice.put(2, catChoice2);
		
		computeCatChoiceAmounts(100.0f, mapCatChoice);
		
		for(Integer categoID : mapCatChoice.keySet()){
			CatChoice currentCatChoice = mapCatChoice.get(categoID);
			
			String name = currentCatChoice.getFatherCategory().getName();
			float expectedAmount = 0.0f;
			switch(name){
				case "whoCares0":
					expectedAmount = 50.0f;
					break;
				case "whoCares1":
					expectedAmount = 30.0f;
					break;
				case "whoCares2":
					expectedAmount = 20.0f;
					break;
				default:
					logger.error("Unexpected CatChoice: " + name);
					fail();
					break;
			}
			
			assertThat("Wrong amount with one Categorisation to fill w/ respect to 2 other", 
					currentCatChoice.getAmount(), 
					is(expectedAmount));
		}
		
		// 3 catego, 2 to leave alone
		
		mapCatChoice = new HashMap<>();
		catChoice0 = new CatChoice(0, new Category("whoCares0"), null, 50.0f, null);
		catChoice1 = new CatChoice(1, new Category("whoCares1"), null, null, null);
		catChoice2 = new CatChoice(1, new Category("whoCares2"), null, null, null);

		mapCatChoice.put(0, catChoice0);
		mapCatChoice.put(1, catChoice1);
		mapCatChoice.put(2, catChoice2);
		
		computeCatChoiceAmounts(100.0f, mapCatChoice);
		
		for(Integer categoID : mapCatChoice.keySet()){
			CatChoice currentCatChoice = mapCatChoice.get(categoID);
			
			String name = currentCatChoice.getFatherCategory().getName();
			Float expectedAmount = null;
			if(name.equalsIgnoreCase("whoCares0")){
				expectedAmount = 50.0f;
			}
			
			assertThat("Wrong amount with 3 CatChoices w/ 2 to leave alone", 
					currentCatChoice.getAmount(), 
					is(expectedAmount));
		}
		
		logger.info("testComputeCategoAmounts finished.");
	}

}