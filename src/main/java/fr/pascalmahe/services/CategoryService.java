package fr.pascalmahe.services;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.web.beans.CatRow;

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

	public static List<CatRow> fetchCategoryTable() {
		logger.info("Fetching fetchCategoryTable...");
		List<CatRow> catRowList = new ArrayList<>();
		
		CatRow catRo0 = new CatRow();
		CatRow catRo1 = new CatRow();
		CatRow catRo10 = new CatRow();
		CatRow catRo11 = new CatRow();
		CatRow catRo12 = new CatRow();
		CatRow catRo2 = new CatRow();
		CatRow catRo3 = new CatRow();
		CatRow catRo30 = new CatRow();
		CatRow catRo31 = new CatRow();
		CatRow catRo32 = new CatRow();
		
		Month latestMonth = LocalDate.now().getMonth();
		Month penultimateMonth = latestMonth.minus(1l);
		Month antePenultimateMonth = penultimateMonth.minus(1l);
		Month minus3Month = antePenultimateMonth.minus(1l);
		Month minus4Month = minus3Month.minus(1l);
		
		catRo0.setCategory(new Category("Emprunts"));
		catRo0.addMonthCell(latestMonth, 0.00f, -1185.00f);
		catRo0.addMonthCell(penultimateMonth, 0.00f, -1185.00f);
		catRo0.addMonthCell(antePenultimateMonth, 0.00f, -1185.00f);
		catRo0.addMonthCell(minus3Month, 0.00f, -1185.00f);
		catRo0.addMonthCell(minus4Month, 0.00f, -1185.00f);
		
		catRo10.setCategory(new Category("Mobiles"));
		catRo10.addMonthCell(latestMonth, 0.00f, -24.99f);
		catRo10.addMonthCell(penultimateMonth, 0.00f, -24.99f);
		catRo10.addMonthCell(antePenultimateMonth, 0.00f, -24.99f);
		catRo10.addMonthCell(minus3Month, 0.00f, -31.06f);
		catRo10.addMonthCell(minus4Month, 0.00f, -28.50f);
		
		catRo11.setCategory(new Category("Web"));
		catRo11.addMonthCell(latestMonth, 0.00f, -24.99f);
		catRo11.addMonthCell(penultimateMonth, 0.00f, -24.99f);
		catRo11.addMonthCell(antePenultimateMonth, 0.00f, -24.99f);
		catRo11.addMonthCell(minus3Month, 0.00f, -31.06f);
		catRo11.addMonthCell(minus4Month, 0.00f, -28.50f);

		catRo12.setCategory(new Category("Electricité"));
		catRo12.addMonthCell(latestMonth, 0.00f, -187.06f);
		catRo12.addMonthCell(penultimateMonth, 0.00f, -187.06f);
		catRo12.addMonthCell(antePenultimateMonth, 0.00f, -187.06f);
		catRo12.addMonthCell(minus3Month, 0.00f, -187.06f);
		catRo12.addMonthCell(minus4Month, 150.00f, -187.06f);
		
		catRo1.setCategory(new Category("Factures"));
		catRo1.addSonCatRow(catRo10);
		catRo1.addSonCatRow(catRo11);
		catRo1.addSonCatRow(catRo12);

		catRo2.setCategory(new Category("Cadeaux"));
		catRo2.addMonthCell(latestMonth, 0.00f, -55.00f);
		catRo2.addMonthCell(penultimateMonth, 0.00f, -12.50f);
		catRo2.addMonthCell(antePenultimateMonth, 0.00f, -33.66f);
		catRo2.addMonthCell(minus3Month, 0.00f, -0.00f);
		catRo2.addMonthCell(minus4Month, 0.00f, -0.00f);

		catRo30.setCategory(new Category("Nourrice"));
		catRo30.addMonthCell(latestMonth, 0.00f, -424.99f);
		catRo30.addMonthCell(penultimateMonth, 0.00f, -124.99f);
		catRo30.addMonthCell(antePenultimateMonth, 0.00f, -124.99f);
		catRo30.addMonthCell(minus3Month, 0.00f, -142.06f);
		catRo30.addMonthCell(minus4Month, 0.00f, -400.50f);

		catRo31.setCategory(new Category("Cantine"));
		catRo31.addMonthCell(latestMonth, 0.00f, -64.99f);
		catRo31.addMonthCell(penultimateMonth, 0.00f, -128.99f);
		catRo31.addMonthCell(antePenultimateMonth, 0.00f, -120.99f);
		catRo31.addMonthCell(minus3Month, 0.00f, -135.06f);
		catRo31.addMonthCell(minus4Month, 0.00f, -60.50f);

		catRo32.setCategory(new Category("Epargne"));
		catRo32.addMonthCell(latestMonth, 0.00f, -200.00f);
		catRo32.addMonthCell(penultimateMonth, 0.00f, -200.00f);
		catRo32.addMonthCell(antePenultimateMonth, 0.00f, -200.00f);
		catRo32.addMonthCell(minus3Month, 0.00f, -200.00f);
		catRo32.addMonthCell(minus4Month, 0.00f, -200.00f);

		catRo3.setCategory(new Category("Enfants"));
		catRo3.addSonCatRow(catRo30);
		catRo3.addSonCatRow(catRo31);
		catRo3.addSonCatRow(catRo32);
		
		catRowList.add(catRo0);
		catRowList.add(catRo1);
		catRowList.add(catRo2);
		catRowList.add(catRo3);

		logger.info("Fetched " + catRowList.size() + " rows "
				+ "on " + catRowList.get(0).getNumberOfMonths() + " months.");
		return catRowList;
	}

}
