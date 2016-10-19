package fr.pascalmahe.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.CatChoice;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.persistence.GenericDao;

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
		
		// NB: 'updateCategoInLineFromCatChoiceMap -> 37 char
		StringBuilder beforeMsg = new StringBuilder("updateCategoInLineFromCatChoiceMap - catChoiceMap in: [");
		StringBuilder afterMsg = new StringBuilder("updateCategoInLineFromCatChoiceMap - categoList out: [");
		
		for(Integer categoID : catChoiceMap.keySet()){
			Categorisation catego = line.getCategorisationById(categoID);
			
			// logging before
			if(beforeMsg.length() > 37 + 18){
				beforeMsg.append(", ");
			}
			String initialCatName = "null";
			if(catego.getCategory() != null){
				initialCatName = catego.getCategory().getCompleteName();
			}
			beforeMsg.append("catego #" + categoID + " -> cat : " + initialCatName);
			
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
//			if(newAmount != null && newAmount != catego.getAmount()){
				catego.setAmount(newAmount);
//			}
			
			// logging after
			if(afterMsg.length() > 37 + 17){
				afterMsg.append(", ");
			}
			String finalCatName = "null";
			if(finalCat != null){
				finalCatName = finalCat.getCompleteName();
			}
			afterMsg.append("catego #" + categoID + " -> cat : " + finalCatName);
		}
		beforeMsg.append("]");
		afterMsg.append("]");
		logger.debug(beforeMsg);
		logger.debug(afterMsg);
		
		logger.info("Updating catego list from line #" + line.getId() + " done.");
	}
	

	public static void computeCatChoiceAmounts(Float lineAmount, Map<Integer, CatChoice> catChoiceMap) {
		logger.debug("computeCatChoiceAmounts - start with " + catChoiceMap.size() + " CatChoices...");
		List<CatChoice> catChoiceWithEmptyAmountList = new ArrayList<>();
		float totalCategoAmount = 0.0f;
		for(Integer key : catChoiceMap.keySet()){
			CatChoice currentCatChoice = catChoiceMap.get(key);
			logger.debug("computeCatChoiceAmounts - current amount: " + currentCatChoice.getAmount());
			if(currentCatChoice.getAmount() == null || 
					(currentCatChoice.getAmount() != null && currentCatChoice.getAmount() == 0.0f)){
				catChoiceWithEmptyAmountList.add(currentCatChoice);
				logger.debug("computeCatChoiceAmounts - adding to list of empty catego");
			} else {
				totalCategoAmount += currentCatChoice.getAmount();
				logger.debug("computeCatChoiceAmounts - adding to total amount");
			}
		}
		logger.debug("computeCatChoiceAmounts - # of CatChoices with no amount: " + catChoiceWithEmptyAmountList.size());
		if(catChoiceWithEmptyAmountList.size() == 1){
			
			CatChoice catChoice = catChoiceWithEmptyAmountList.get(0);
			logger.debug("computeCatChoiceAmounts - only one detected: #" + catChoice.getCategoId());
			catChoice.setAmount(lineAmount - totalCategoAmount);
			logger.debug("computeCatChoiceAmounts - totalCategoAmount: " + totalCategoAmount);
			logger.debug("computeCatChoiceAmounts - lineAmount: " + lineAmount);
			logger.debug("computeCatChoiceAmounts - final amount is: " + catChoice.getAmount() + " (should be: " + (lineAmount - totalCategoAmount) + ").");
		}
	}

}
