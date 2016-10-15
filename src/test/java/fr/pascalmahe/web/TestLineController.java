package fr.pascalmahe.web;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.services.CategoryService;
import fr.pascalmahe.testUtil.AbstractTest;

public class TestLineController extends AbstractTest {

	private static final Logger logger = LogManager.getLogger();

	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		preTestDatabaseCheckup();
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		int nbCat = catDao.count();
		
		if(nbCat <= 5){
			logger.info("Creating Categorys for tests...");
			Category cat0 = new Category();
			cat0.setName("Armement");
			Category cat1 = new Category();
			cat1.setName("Véhicules");
			Category cat2 = new Category();
			cat2.setName("Nourriture");
			Category cat3 = new Category();
			cat3.setName("Equipement");
			Category cat4 = new Category();
			cat4.setName("Transmission");
			
			Category cat5 = new Category();
			cat5.setName("Sabre lasers");
			cat5.setFatherCategory(cat0);
			Category cat6 = new Category();
			cat6.setName("ZFI-0");
			cat6.setFatherCategory(cat0);
			Category cat7 = new Category();
			cat7.setName("Héliporteur");
			cat7.setFatherCategory(cat1);
			Category cat8 = new Category();
			cat8.setName("TIE-Fighter");
			cat8.setFatherCategory(cat1);
			Category cat9 = new Category();
			cat9.setName("Lembas");
			cat9.setFatherCategory(cat2);
			Category cat10 = new Category();
			cat10.setName("Ramen");
			cat10.setFatherCategory(cat2);
			Category cat11 = new Category();
			cat11.setName("Masque");
			cat11.setFatherCategory(cat3);
			Category cat12 = new Category();
			cat12.setName("Cape");
			cat12.setFatherCategory(cat3);
			Category cat13 = new Category();
			cat13.setName("Antenne");
			cat13.setFatherCategory(cat4);
			Category cat14 = new Category();
			cat14.setName("Fil & pot de yaourt");
			cat14.setFatherCategory(cat4);
			
			catDao.saveOrUpdate(cat0);
			catDao.saveOrUpdate(cat1);
			catDao.saveOrUpdate(cat2);
			catDao.saveOrUpdate(cat3);
			catDao.saveOrUpdate(cat4);
			catDao.saveOrUpdate(cat5);
			catDao.saveOrUpdate(cat6);
			catDao.saveOrUpdate(cat7);
			catDao.saveOrUpdate(cat8);
			catDao.saveOrUpdate(cat9);
			catDao.saveOrUpdate(cat10);
			catDao.saveOrUpdate(cat11);
			catDao.saveOrUpdate(cat12);
			catDao.saveOrUpdate(cat13);
			catDao.saveOrUpdate(cat14);
			
			listToDelete.add(cat0);
			listToDelete.add(cat1);
			listToDelete.add(cat2);
			listToDelete.add(cat3);
			listToDelete.add(cat4);
			listToDelete.add(cat5);
			listToDelete.add(cat6);
			listToDelete.add(cat7);
			listToDelete.add(cat8);
			listToDelete.add(cat9);
			listToDelete.add(cat10);
			listToDelete.add(cat11);
			listToDelete.add(cat12);
			listToDelete.add(cat13);
			listToDelete.add(cat14);
			logger.info("Test Categorys created.");
		}
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		
		cleanUpDatabase();
		
		logger.info("afterClass finished.");
	}
	
	@Test
	public void testPopulateSonCatMap() {
		logger.info("Starting testPopulateSonCatMap...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		List<Category> secondRankCategoryList = CategoryService.fetchSecondRankCategoryList();
		Map<Integer, List<Category>> sonCatMap = LineController.populateSonCatMap(secondRankCategoryList);
		logger.debug("testPopulateSonCatMap - sonCatMap.size : " + sonCatMap.size());
		
		List<Category> firstRankCategoryList = CategoryService.fetchFirstRankCategoryList();
		
		assertThat(sonCatMap.size(), is(firstRankCategoryList.size()));
		
		for(Integer fatherCatId : sonCatMap.keySet()){
			List<Category> listSonCategoriesActual = sonCatMap.get(fatherCatId);
			
			logger.debug("testPopulateSonCatMap - sons of cat #" + fatherCatId);
			logger.debug("testPopulateSonCatMap - # found in sonCatmap : " + listSonCategoriesActual.size());
			Map<String, Object> searchCriteriaMap = new HashMap<>();
			searchCriteriaMap.put("fatherCategory.id", fatherCatId);
			
			List<Category> listSonCategoriesExpected = catDao.search(searchCriteriaMap);
			logger.debug("testPopulateSonCatMap - # found in listSonCategoriesExpected : " + 
					listSonCategoriesExpected.size());
			assertThat(listSonCategoriesActual.size(), is(listSonCategoriesExpected.size()));
			for(Category cat : listSonCategoriesExpected){
				assertThat(listSonCategoriesActual, hasItem(cat));
			}
		}
		
		logger.info("testPopulateSonCatMap finished.");
	}

}