package fr.pascalmahe.services;

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
		
		StringBuilder beforeMsg = new StringBuilder("updateCategoInLineFromCatChoiceMap - before : [");
		StringBuilder afterMsg = new StringBuilder("updateCategoInLineFromCatChoiceMap - after : [");
		for(Integer categoID : catChoiceMap.keySet()){
			Categorisation catego = line.getCategorisationById(categoID);
			
			// logging before
			if(beforeMsg.length() > 48){
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
			if(newAmount != null && newAmount != catego.getAmount()){
				catego.setAmount(newAmount);
			}
			
			// logging after
			if(afterMsg.length() > 46){
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
	
	
}
