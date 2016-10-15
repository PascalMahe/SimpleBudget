package fr.pascalmahe.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.persistence.GenericDao;

public class CategoryService {

	private static final Logger logger = LogManager.getLogger();

	public static List<Category> fetchFirstRankCategoryList() {

		logger.info("Fetching first rank categories (with no parents)...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		Map<String, Object> crita = new HashMap<>();
		crita.put("fatherCategory", null);
		
		List<Category> returnList = catDao.search(crita);
		Collections.sort(returnList);
		
		logger.info("Fetched " + returnList.size() + " first rank categories (with no parents).");

		return returnList;
	}

	public static List<Category> fetchSecondRankCategoryList() {
		logger.info("Fetching second rank categories (only with parents)...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		Map<String, Object> crita = new HashMap<>();
		crita.put("fatherCategory", null);
		
		List<Category> returnList = catDao.searchNegative(crita);
		
//		for(Category cat : returnList){
//			logger.debug("fetchSecondRankCategoryList - category " + cat.getName() + 
//					" has for fatherCategory : " + cat.getFatherCategory().getName() + 
//					" (cat#" + cat.getId() + ")");
//		}
		Collections.sort(returnList);
		
		logger.info("Fetched " + returnList.size() + " second rank categories (only with parents).");
		
		return returnList;
	}

	public static Category fetchOrCreateFromName(String catName) {
		logger.info("Fetching Category with name: " + catName + "...");
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
 		Category existingCat = catDao.fetchByName(catName);
 		Category returnedOne;
 		if(existingCat == null){
 			returnedOne = new Category(catName);
 			logger.info("No Category with name: " + catName + ". Creating it...");
 			catDao.saveOrUpdate(returnedOne );
 			logger.info("Category created.");
 		} else {
 			returnedOne = existingCat;
 		}
 		logger.info("Returning category #" + returnedOne.getId() + ".");
		return returnedOne;
	}

	public static Category fetchOrCreateFromName(String catName, Integer fatCatId) {
		
		logger.info("Fetching Category with name: " + catName + " and fatherId: #" + fatCatId + "...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
 		Category existingCat = catDao.fetchByName(catName);
 		Category returnedOne;
 		if(existingCat == null){
 			returnedOne = new Category(catName);
 			Category fatCat = catDao.fetch(fatCatId);
 			if(fatCat == null){
 				logger.warn("Received ID #" + fatCatId + " as father "
 						+ "for new Category (named " + catName + ") "
						+ "but the father category does not exist. "
						+ "Creating without a father...");
 				returnedOne = new Category(catName);
 			} else {
 				
 				returnedOne = new Category(catName, fatCat);
 			}
 			logger.info("No Category with name: " + catName + ". Creating it...");
 			catDao.saveOrUpdate(returnedOne);
 			logger.info("Category created.");
 		} else {
 			returnedOne = existingCat;
 		}
 		
 		logger.info("Returning category #" + returnedOne.getId() + ".");
		return returnedOne;
	}

	public static Category fetchById(Integer catId) {
		logger.info("Fetching Category #" + catId + "...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		Category cat = catDao.fetch(catId);
		
		logger.info("Fetched Category #" + catId + ": '" + cat.getName() + "'.");
		return cat;
	}

}
