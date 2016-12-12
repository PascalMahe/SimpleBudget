package fr.pascalmahe.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.web.beans.CatChoice;

public class LineService {

	private static final Logger logger = LogManager.getLogger();

	public static void updateLine(Line lineToSave){
		logger.info("Updating line#" + lineToSave.getId());
		
		GenericDao<Line> linDao = new GenericDao<>(Line.class);
		linDao.saveOrUpdate(lineToSave);
	}

	public static List<Line> fetchLinesReverseChronologically() {

		logger.debug("fetchLines - start");

		logger.info("Fetching lines reverse chronologically (most recent first)...");
		
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		
		List<Line> returnedList = lineDao.fetchReverseChronologically();
		logger.debug("fetchLines - fetched " + returnedList.size() + " lines. Sorting...");
		Collections.sort(returnedList, Collections.reverseOrder());
		logger.debug("fetchLines - sorted.");
		
		logger.info("Fetched lines reverse chronologically: " + returnedList.size() + " lines.");
		logger.debug("fetchLines - end");
		return returnedList;
	}

	public static Line fetchLine(String idToFetch) {

		logger.debug("fetchLines - start");

		Line returnLine = null;
		
		try {
			Integer id = Integer.parseInt(idToFetch);
			returnLine = fetchLine(id);
			
		} catch (NumberFormatException nfe){
			logger.error("Error while parsing Line id to fetch: " + idToFetch + ".", nfe);
		}
		
		logger.debug("fetchLines - end");
		return returnLine;
	}

	public static Line fetchLine(Integer id) {
		Line returnLine = null;
		logger.info("Fetching line #" + id + "...");
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		returnLine = lineDao.fetch(id);
		logger.info("Fetched line #" + id + ".");
		return returnLine;
	}

	public static void updateCategoInLineFromCatChoiceMap(Line line, Map<Integer, CatChoice> catChoiceMap) {
		logger.info("Updating catego list from line #" + line.getId() + "...");
		
		// computing value if only one has null or 0.00 as value
		// -> it's set to the rest of the line's amount
		logger.debug("updateCategoInLineFromCatChoiceMap - "
				+ "autoCompleting the last CatChoice's amount");
		computeCatChoiceAmounts(line.getAmount(), catChoiceMap);
		
		// deleting values not in catChoiceMap
		List<Categorisation> listOfCategoToDelete = new ArrayList<>();
		for(Categorisation catego : line.getCategorisationList()){
			if(!catChoiceMap.containsKey(catego.getId())){
				listOfCategoToDelete.add(catego);
			}
		}
		logger.debug("updateCategoInLineFromCatChoiceMap - "
				+ "removing " + listOfCategoToDelete.size() + " "
				+ "categorisations that have been deleted...");
		line.getCategorisationList().removeAll(listOfCategoToDelete);
		
		
		for(Integer categoID : catChoiceMap.keySet()){
			Categorisation catego = line.getCategorisationById(categoID);
			
			CatChoice catChoice = catChoiceMap.get(categoID);
			Category finalCat;
			if(catChoice.getSonCategory() == null){
				finalCat = catChoice.getFatherCategory();
			} else {
				finalCat = catChoice.getSonCategory();
				finalCat.setFatherCategory(catChoice.getFatherCategory());
			}
			catego.setCategory(finalCat);
			
			Float newAmount = catChoice.getAmount();
			catego.setAmount(newAmount);
		}
		
		logger.info("Updating catego list from line #" + line.getId() + " done.");
	}
	

	public static void computeCatChoiceAmounts(Float lineAmount, Map<Integer, CatChoice> catChoiceMap) {
		List<CatChoice> catChoiceWithEmptyAmountList = new ArrayList<>();
		float totalCategoAmount = 0.0f;
		for(Integer key : catChoiceMap.keySet()){
			CatChoice currentCatChoice = catChoiceMap.get(key);
			if(currentCatChoice.getAmount() == null || 
					(currentCatChoice.getAmount() != null && currentCatChoice.getAmount() == 0.0f)){
				catChoiceWithEmptyAmountList.add(currentCatChoice);
			} else {
				totalCategoAmount += currentCatChoice.getAmount();
			}
		}
		
		if(catChoiceWithEmptyAmountList.size() == 1){
			CatChoice catChoice = catChoiceWithEmptyAmountList.get(0);
			catChoice.setAmount(lineAmount - totalCategoAmount);
		}
	}

	public static int countLines() {

		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		
		return lineDao.count();
	}

}
