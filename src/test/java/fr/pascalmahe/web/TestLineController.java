package fr.pascalmahe.web;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
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
import fr.pascalmahe.web.beans.CatChoice;

public class TestLineController extends AbstractTest {

	private static final Logger logger = LogManager.getLogger();

	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		preTestDatabaseCheckup();
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		logger.info("Creating Categorys for tests...");
		// fathers with sons
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
		
		// sons (... with fathers)
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
		
		// sonless fathers
		Category cat15 = new Category();
		cat15.setName("Munitions");
		Category cat16 = new Category();
		cat16.setName("Moral");
		
		
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
		catDao.saveOrUpdate(cat15);
		catDao.saveOrUpdate(cat16);
		
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
		listToDelete.add(cat15);
		listToDelete.add(cat16);
		logger.info("Test Categorys created.");
		
		
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
		List<Category> firstRankCategoryWithSonsList = new ArrayList<Category>();
		List<Category> firstRankCategoryWithoutSonsList = new ArrayList<Category>();
		firstRankCategoryWithSonsList.addAll(firstRankCategoryList);
		firstRankCategoryWithoutSonsList.addAll(firstRankCategoryList);
		
		// removing categorys with sons
		for(Category son : secondRankCategoryList){
			Category fatCat = son.getFatherCategory();
			firstRankCategoryWithoutSonsList.remove(fatCat);
		}
		
		// substracting listWithoutSons from totalList to get listWithSons
		firstRankCategoryWithSonsList.removeAll(firstRankCategoryWithoutSonsList);
		
		assertThat(sonCatMap.size(), is(firstRankCategoryWithSonsList.size()));
		
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
	
	@Test
	public void testValidateCatChoiceList(){
		logger.info("Starting testPopulateSonCatMap...");
		
		Map<Integer, CatChoice> catChoiceMapToValidate = new HashMap<>();

		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		Category cat0 = catDao.fetchByName("Moral");
		Category cat1 = catDao.fetchByName("Héliporteur");
		Category cat2 = catDao.fetchByName("ZFI-0");
		
		
		// no pairs
		
		CatChoice catChoice0 = new CatChoice(0, cat0, null, 15.0f, null);
		CatChoice catChoice1 = new CatChoice(1, cat1.getFatherCategory(), cat1, 15.0f, null);
		CatChoice catChoice2 = new CatChoice(2, cat2.getFatherCategory(), cat2, 15.0f, null);
		
		catChoiceMapToValidate.put(catChoice0.getCategoId(), catChoice0);
		catChoiceMapToValidate.put(catChoice1.getCategoId(), catChoice1);
		catChoiceMapToValidate.put(catChoice2.getCategoId(), catChoice2);
		
		int nbInvalidPairs = LineController.validateMapCatChoice(catChoiceMapToValidate);
		assertThat(nbInvalidPairs, is(0));
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);
			assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
			assertThat(catChoice.getValidSon(), is(CatChoice.VALID_CLASS));
		}
		
		// son pair
		catChoiceMapToValidate = new HashMap<>();
		
		catChoice0 = new CatChoice(0, cat0, null, 15.0f, null);
		catChoice1 = new CatChoice(1, cat1.getFatherCategory(), cat1, 15.0f, null);
		catChoice2 = new CatChoice(2, cat1.getFatherCategory(), cat1, 15.0f, null);
		
		catChoiceMapToValidate.put(catChoice0.getCategoId(), catChoice0);
		catChoiceMapToValidate.put(catChoice1.getCategoId(), catChoice1);
		catChoiceMapToValidate.put(catChoice2.getCategoId(), catChoice2);
		
		nbInvalidPairs = LineController.validateMapCatChoice(catChoiceMapToValidate);
		assertThat(nbInvalidPairs, is(1));
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);
			if(catChoice.getFullCategory().getName().equalsIgnoreCase("Moral")){
				assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.VALID_CLASS));
			} else {
				assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.INVALID_CLASS));
			}
			
		}
		
		// father pair
		catChoiceMapToValidate = new HashMap<>();
		
		catChoice0 = new CatChoice(0, cat0, null, 15.0f, null);
		catChoice1 = new CatChoice(1, cat1.getFatherCategory(), cat1, 15.0f, null);
		catChoice2 = new CatChoice(2, cat0, null, 15.0f, null);
		
		catChoiceMapToValidate.put(catChoice0.getCategoId(), catChoice0);
		catChoiceMapToValidate.put(catChoice1.getCategoId(), catChoice1);
		catChoiceMapToValidate.put(catChoice2.getCategoId(), catChoice2);
		
		nbInvalidPairs = LineController.validateMapCatChoice(catChoiceMapToValidate);
		assertThat(nbInvalidPairs, is(1));
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);
			if(catChoice.getFullCategory().getName().equalsIgnoreCase("Moral")){
				assertThat(catChoice.getValidFather(), is(CatChoice.INVALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.VALID_CLASS));
			} else {
				assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.VALID_CLASS));
			}
		}
		
		
		// son pair and father pair
		catChoiceMapToValidate = new HashMap<>();
		
		catChoice0 = new CatChoice(0, cat0, null, 15.0f, null);
		catChoice1 = new CatChoice(1, cat1.getFatherCategory(), cat1, 15.0f, null);
		catChoice2 = new CatChoice(2, cat2.getFatherCategory(), cat2, 15.0f, null);
		CatChoice catChoice3 = new CatChoice(3, cat0, null, 15.0f, null);
		CatChoice catChoice4 = new CatChoice(4, cat2.getFatherCategory(), cat2, 15.0f, null);
		
		catChoiceMapToValidate.put(catChoice0.getCategoId(), catChoice0);
		catChoiceMapToValidate.put(catChoice1.getCategoId(), catChoice1);
		catChoiceMapToValidate.put(catChoice2.getCategoId(), catChoice2);
		catChoiceMapToValidate.put(catChoice3.getCategoId(), catChoice3);
		catChoiceMapToValidate.put(catChoice4.getCategoId(), catChoice4);
		
		nbInvalidPairs = LineController.validateMapCatChoice(catChoiceMapToValidate);
		assertThat(nbInvalidPairs, is(2));
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);
			if(catChoice.getFullCategory().getName().equalsIgnoreCase("Moral")){
				assertThat(catChoice.getValidFather(), is(CatChoice.INVALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.VALID_CLASS));
				
			} else if(catChoice.getFullCategory().getName().equalsIgnoreCase("ZFI-0")){
				assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.INVALID_CLASS));
			} else {
				assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.VALID_CLASS));
			}
		}
		
		// specific case returned from manual test
		catChoiceMapToValidate = new HashMap<>();
		
		Category catQuotidiennes = CategoryService.fetchById(20351);
		Category catCourses = CategoryService.fetchById(20350);
		if(catQuotidiennes != null && catCourses != null){
			CatChoice catCh0 = new CatChoice(20526, catCourses, catQuotidiennes, 45.0f, null);
			CatChoice catCh1 = new CatChoice(20527, catCourses, catQuotidiennes, 40.0f, null);
			
			catChoiceMapToValidate.put(catCh0.getCategoId(), catCh0);
			catChoiceMapToValidate.put(catCh1.getCategoId(), catCh1);
			
			nbInvalidPairs = LineController.validateMapCatChoice(catChoiceMapToValidate);
			assertThat(nbInvalidPairs, is(1));
			for(Integer categoID : catChoiceMapToValidate.keySet()){
				CatChoice catChoice = catChoiceMapToValidate.get(categoID);
				
				assertThat(catChoice.getValidFather(), is(CatChoice.VALID_CLASS));
				assertThat(catChoice.getValidSon(), is(CatChoice.INVALID_CLASS));
				
			}
		}
		
		logger.info("testPopulateSonCatMap finished.");
	}

}