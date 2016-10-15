package fr.pascalmahe.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.testUtil.AbstractTest;

public class TestCategoryService extends AbstractTest {

	private static final Logger logger = LogManager.getLogger();
	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		preTestDatabaseCheckup();
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		cleanUpDatabase();
		
		logger.info("afterClass finished.");
	}
	
	@Test
	public void testFetchFirstRankCategoryList() {
		logger.info("Starting testFetchFirstRankCategoryList...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		List<Category> listAllCat = catDao.fetchAll();
		
		List<Category> listFirstRankCatExpected = new ArrayList<>();
		
		for(Category cat : listAllCat){
			if(cat.getFatherCategory() == null){
				listFirstRankCatExpected.add(cat);
			}
		}
		
		List<Category> listFirstRankCatActual = 
				CategoryService.fetchFirstRankCategoryList();
		
		assertThat(listFirstRankCatActual.size(), 
				is(listFirstRankCatExpected.size()));
		
		for(Category cat : listFirstRankCatExpected){
			assertThat(listFirstRankCatActual, hasItem(cat));
		}
		
//		logger.debug("testFetchFirstRankCategoryList - listExpected : " + listFirstRankCatExpected);
//		
//		logger.debug("testFetchFirstRankCategoryList - listActual : " + listFirstRankCatActual);
		
		logger.info("testFetchFirstRankCategoryList finished.");
	}

	@Test
	public void testFetchSecondRankCategoryList() {
		logger.info("Starting testFetchSecondRankCategoryList...");
		
		List<Category> listAllCat = null;
		try {
			GenericDao<Category> catDao = new GenericDao<>(Category.class);
			listAllCat = catDao.fetchAll();
		} catch (Exception e){
			logger.error("", e);
		}
		
		
		List<Category> listSecondRankCatExpected = new ArrayList<>();
		
		for(Category cat : listAllCat){
			if(cat.getFatherCategory() != null){
				listSecondRankCatExpected.add(cat);
			}
		}
		
		List<Category> listSecondRankCatActual = 
				CategoryService.fetchSecondRankCategoryList();
		
		assertThat(listSecondRankCatActual.size(), 
				is(listSecondRankCatExpected.size()));
		
		for(Category cat : listSecondRankCatExpected){
			assertThat(listSecondRankCatActual, hasItem(cat));
			assertThat(cat.getFatherCategory(), notNullValue());
			logger.debug("testFetchSecondRankCategoryList - category " + cat.getName() + 
					" has for fatherCategory : " + cat.getFatherCategory().getName() + 
					" (cat#" + cat.getId() + ")");
		}

//		logger.debug("testFetchSecondRankCategoryList - listExpected : " + listSecondRankCatExpected);
//		
//		logger.debug("testFetchSecondRankCategoryList - listActual : " + listSecondRankCatActual);

		logger.info("testFetchSecondRankCategoryList finished.");
	}

}