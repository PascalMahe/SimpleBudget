package fr.pascalmahe.services;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	
}
