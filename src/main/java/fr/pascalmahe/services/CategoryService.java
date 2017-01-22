package fr.pascalmahe.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.MonthInYear;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.web.beans.CatRow;
import fr.pascalmahe.web.beans.MonthCell;

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
		
		String infoMsg = "Fetched Category #" + catId + ": ";
		if(cat != null){
			infoMsg += "'" + cat.getName() + "'";
		} else {
			infoMsg += "null";
		}
		infoMsg += ".";
		logger.info(infoMsg);
		return cat;
	}

	public static List<CatRow> fetchCategoryTable() {
		logger.info("Fetching fetchCategoryTable...");
		
		GenericDao<Line> dao = new GenericDao<>(Line.class);
		
		List<Line> lineList = dao.fetchLinesLast6Months();
		List<CatRow> catRowList = lineListToCatRowList(lineList);
		
		String infoMsg = "Fetched " + catRowList.size() + " rows on ";
		if(catRowList.size() > 0){
			infoMsg += catRowList.get(0).getNumberOfMonths();
		} else {
			infoMsg += 0;
		}
		infoMsg += " months.";
		
		logger.info(infoMsg);
		return catRowList;
	}

	private static List<CatRow> lineListToCatRowList(List<Line> lineList) {
		
		logger.debug("lineListToCatRowList - Creating list of CatRows with " + lineList.size() + " Lines...");
		
		List<CatRow> listToReturn = new ArrayList<>();
		MonthInYear oldestMonthYet = null;
		
		HashMap<Category, CatRow> catRowTempMap = new HashMap<>();
		for(Line currentLine : lineList){
			List<Categorisation> listCatego = currentLine.getCategorisationList();
			MonthInYear currentMonth = new MonthInYear(currentLine.getDate().getMonth(), currentLine.getDate().getYear());
			
			if(oldestMonthYet == null || oldestMonthYet.compareTo(currentMonth) > 0){
				oldestMonthYet = currentMonth;
			}
			
			for(Categorisation catego : listCatego){
				Category currentCat = catego.getCategory();
				
				// if ID is null, the cat is loaded from the DB
				// otherwise the maps swallows the categories with
				// null IDs silently (as is expected since the Category.equals
				// and category.hashCode are based on ID only).
				if(currentCat.getId() == null){
					GenericDao<Category> catDao = new GenericDao<>(Category.class);
					logger.debug("lineListToCatRowList - looking for Cat w/ name: " + currentCat.getName());
					currentCat = catDao.fetchByName(currentCat.getName());
				}
				
				CatRow currentCatRow = null;
				if(catRowTempMap.containsKey(currentCat)){
					currentCatRow = catRowTempMap.get(currentCat);
					MonthCell mo = currentCatRow.getMonth(currentMonth);
					if(mo == null){
						mo = new MonthCell(currentMonth, 0.0f, 0.0f);
					}

				} else if(currentCat.getFatherCategory() != null) {
					CatRow fatherCatRow;
					Category fatCat = currentCat.getFatherCategory();
					if(catRowTempMap.containsKey(fatCat)){
						fatherCatRow = catRowTempMap.get(fatCat);
					} else {
						fatherCatRow = new CatRow(fatCat);
						catRowTempMap.put(fatCat, fatherCatRow);
					}
					
					// if it doesn't exist, create catRow and add it to fatherCatRow
					currentCatRow = fatherCatRow.createOrReturnSonCatRow(currentCat);
 
				} else {
					currentCatRow = new CatRow(currentCat);
					catRowTempMap.put(currentCat, currentCatRow);
				}
				
				currentCatRow.addAmountToMonth(currentMonth, catego.getAmount());
			}
		}
		logger.debug("lineListToCatRowList - Adding catRow to list (" + catRowTempMap.size() + " in the map)...");
		
		for(CatRow catRow : catRowTempMap.values()){
			catRow.fillMonthsUpTo(oldestMonthYet);
			listToReturn.add(catRow);
		}
		
		logger.debug("lineListToCatRowList - Added all catRows (" + listToReturn.size() + " of 'em). Sorting...");
		
		Collections.sort(listToReturn);
		
		logger.debug("lineListToCatRowList - Sorted.");
		
		return listToReturn;
	}

	public static Category fetchCategory(String idToFetch) {

		logger.debug("fetchCategory - start");

		Category returnLine = null;
		
		try {
			Integer id = Integer.parseInt(idToFetch);
			returnLine = fetchCategory(id);
			
		} catch (NumberFormatException nfe){
			logger.error("Error while parsing Category id to fetch: " + idToFetch + ".", nfe);
		}
		
		logger.debug("fetchCategory - end");
		return returnLine;
	}
	

	public static Category fetchCategory(Integer id) {
		Category returnCat = null;
		logger.info("Fetching cat #" + id + "...");
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		returnCat = catDao.fetch(id);
		logger.info("Fetched cat #" + id + ".");
		return returnCat;
	}

	public static List<Category> fetchAll() {

		logger.info("Fetching all categories...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		List<Category> returnList = catDao.fetchAll();
		
		Comparator<Category> completeNameComparator = new Comparator<Category>(){
			public int compare(Category cat1, Category cat2){
				return cat1.getCompleteName().compareTo(cat2.getCompleteName());
			}
		};
		Collections.sort(returnList, completeNameComparator);
		
		logger.info("Fetched " + returnList.size() + " categories (all of 'em).");

		return returnList;
	}

	public static List<Category> fetchSonsOf(Category cat) {
		logger.info("Fetching son categories of cat #" + cat.getId() + " (" + cat.getCompleteName() + ")...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		Map<String, Object> crita = new HashMap<>();
		crita.put("fatherCategory", cat);
		
		List<Category> returnList = catDao.search(crita);
		
		Collections.sort(returnList);
		
		logger.info("Fetched " + returnList.size() + " son categories of cat #" + cat.getId() + " (" + cat.getCompleteName() + ").");
		
		return returnList;
	}

	public static void saveCategoryAndSons(Category cat, List<Category> sonsList) {
		logger.info("Saving category #" + cat.getId() + " (" + cat.getCompleteName() + ") and sons...");

		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		catDao.saveOrUpdate(cat);
		
		for(Category sonCat: sonsList){
			sonCat.setFatherCategory(cat);
			catDao.saveOrUpdate(sonCat);
		}
		
		logger.info("Saved cat #" + cat.getId() + " (" + cat.getCompleteName() + ") and sons.");		
	}

}
