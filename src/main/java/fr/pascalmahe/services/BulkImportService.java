package fr.pascalmahe.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.ex.MalformedLineException;
import fr.pascalmahe.ex.MalformedTextException;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.web.beans.BulkImportResult;

public class BulkImportService {

	private static final Logger logger = LogManager.getLogger();

	// find first line: (cf. http://www.stackoverflow.com/a/4194343/2112089)
	// regex : 	^?		-> 1 or 0 start of line
	//			(?=)	-> lookahead
	//			2\d\d\d -> year between 2000 and 2999
	//			-		-> month/year separator
	//			[0-3]\d -> day of month between 00 and 39
	//			-		-> day/month separator
	//			[0-1]\d -> month between 00 and 19

	// => we're looking for a line starting with a date BUT the regex must not match
	// on the date or it'll be eaten by split
	protected static final String datePatternRegex = "^?(?=2\\d\\d\\d-[0-1]\\d-[0-3]\\d)";
	
	protected static final Pattern datePattern = Pattern.compile(datePatternRegex);
	
	// Regex explanation: ,(?!\d\d€)
	// , 		-> comma
	// (?! ) 	-> negative lookahead
	// \d\d 	-> double digit
	// \s? 		-> zero or one space CSV 
	// € 		-> euro sign
	// => we're looking for a comma not followed by (that's a 
	// negative lookahead) two digits, possibly a space and a (euro or percent) sign 
	// '016,"Cofir Amboi' 		-> matches once
	// 'rte","72,50€",,Loisirs' -> matches thrice
	// ' 1,204270201' 			-> matches once
	protected static final String strCommaNotFollowedRegex = ",(?!\\d\\d(\\s){0,1}(€|%))";

	public static final String CR = "\n";
	
	public static final String EN = "\r";

	public static final String ENCR = EN + CR;
	
	public static BulkImportResult consume(String text) throws MalformedTextException {
		
		logger.info("Importing...");
		
		BulkImportResult result = new BulkImportResult();
		
		int startOfFirstDataLine;
		try {
			startOfFirstDataLine = findFirstDataLineStart(text);
		} catch (MalformedTextException e) {
			logger.error("Error while computing the start of the first line: " + e.getLocalizedMessage());
			throw e;
		}
		
		
		text = text.substring(startOfFirstDataLine);
		
		String[] lineArray = text.split(datePatternRegex);
		
		result = arrayOfLineAsStrToLineList(lineArray, result);
		
		logger.debug("consume - valuesArrayToLineList returned list with " + result.getNbLinesDetected() + " lines.");
		
		result = insertLineList(result.getLineList(), result);
		
		logger.info("Import done: " + result.getNbLinesDetected() + " lines detected, " + 
										result.getNbLinesCreated() + " were inserted, " + 
										result.getNbLinesSkipped() + " skipped, " + 
										result.getNbLinesMalformed() + " malformed and " + 
										result.getNbCategorysCreated()+ " categories were created (and " + 
										result.getNbCategorysSkipped() + " skipped).");
		
		return result;
	}
	

	protected static BulkImportResult arrayOfLineAsStrToLineList(String[] lineArray, BulkImportResult result) {
		
		List<Line> lineList = new ArrayList<>();
		
		for(int lineIndex = 0; lineIndex < lineArray.length; lineIndex++){
			// split with negative limit -> extracts even empty strings between matches but doesn't
			// limit the number of results in the returned array 
			// cf. http://stackoverflow.com/a/13939902/2112089
			// & http://docs.oracle.com/javase/6/docs/api/java/lang/String.html#split%28java.lang.String,%20int%29
			String[] valuesArray = lineArray[lineIndex].split(strCommaNotFollowedRegex, -1);
			
//			StringBuilder dbgMsg = new StringBuilder("[");
//			for(int i = 0; i < valuesArray.length; i++){
//				if(i > 0){
//					dbgMsg.append(", ");
//				}
//				
//				dbgMsg.append(valuesArray[i]);
//			}
//			dbgMsg.append("]");
//			logger.debug("arrayOfLineAsStrToLineList - line #" + lineIndex + " as string: " + lineArray[lineIndex]);
//			logger.debug("arrayOfLineAsStrToLineList - line #" + lineIndex + "'s length: " + valuesArray.length);
//			logger.debug("arrayOfLineAsStrToLineList - line #" + lineIndex + ": " + dbgMsg);
			try {
				Line currentLine = new Line(valuesArray);
				
				lineList.add(currentLine);
			} catch (MalformedLineException e) {
				String errorMessage = "Error on line n°" + lineIndex + ": " + e.getLocalizedMessage();
				logger.error("valuesArrayToLineList - " + errorMessage, e);
				result.addLineMalformed();
			}
			result.addLineDetected();
		}
		result.setLineList(lineList);
		
		logger.debug("arrayOfLineAsStrToLineList - Got " + result.getNbLinesDetected() + " lines "
				+ "(including " + result.getNbLinesMalformed() + " bad ones).");
		
		return result;
	}

	protected static int findFirstDataLineStart(String text) throws MalformedTextException {

		Matcher firstDateMatcher = datePattern.matcher(text);
		
		// /!\ Calling find() moves the matcher through the string
		boolean foundFirstDate = firstDateMatcher.find(); 
		
		logger.debug("findFirstDataLine - foundFirstDate: " + foundFirstDate);
		int firstDateStart;
		if(foundFirstDate){ 	
			
			firstDateStart = firstDateMatcher.start();
			logger.debug("findFirstDataLine - firstDateStart: " + firstDateStart);
			
		} else {
			throw new MalformedTextException("First date (start of first line) not found in : " + text);
		}
		
		return firstDateStart;
	}

	/**
	 * Inserts a List of Lines in the DB.
	 * @param lineList
	 * @param result 
	 * @return first int: # of lines inserted <br/>
	 * 			second int: # lines skipped because they already existed
	 * 			third int: # categories created
	 */
	protected static BulkImportResult insertLineList(List<Line> lineList, BulkImportResult result) {
		
		GenericDao<Line> lineDao = new GenericDao<>(Line.class);
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		
		for(Line currentLine : lineList){
			List<Categorisation> categoList = currentLine.getCategorisationList();
			
			// checking if the line exists
			Map<String, Object> critList = new HashMap<>();
			critList.put("amount", currentLine.getAmount());
			critList.put("date", currentLine.getDate());
			critList.put("detailedLabel", currentLine.getDetailedLabel());
			critList.put("note", currentLine.getNote());
			
//			logger.debug("insertLineList - Searching line with criteria: ");
//			for(String key: critList.keySet()){
//				Object value = critList.get(key);
//				logger.debug(key + ": " + value + " (" + value.getClass().getSimpleName() + ")");
//			}
			
			List<Line> lineInDatabase = lineDao.search(critList);
			if(lineInDatabase.size() > 0){
				result.addLineSkipped();
			} else {
				
				// if the line isn't already present, 
				// the categories are added
				for(Categorisation currentCatego : categoList){
					Category cat = currentCatego.getCategory();
					
					// NB: saving father Category first, otherwise
					// Hibernate throws an error
					Category fatCat = cat.getFatherCategory();
					if(fatCat != null && fatCat.getId() == null){
						GenericDao<Category> catDao = new GenericDao<>(Category.class);
						Category fatCatAlreadyInDb = catDao.fetchByName(fatCat.getName());
						if(fatCatAlreadyInDb != null){
							cat.setFatherCategory(fatCatAlreadyInDb);
						} else {
							catDao.saveOrUpdate(fatCat);
							result.addCategoryCreated(fatCat.getName());
							
						}
					}
					
					result = saveIfNotExists(cat, result);
					
					logger.debug("insertLineList - saving categorisation: "
							+ "'" + currentLine.getShortLabel() + "' "
							+ "-> [" + currentCatego.getCategory() + "]");
					categoDao.saveOrUpdate(currentCatego);
				}
				lineDao.saveOrUpdate(currentLine);
				result.addLineCreated();
			}
			
		}
		
		return result;
	}

	protected static BulkImportResult saveIfNotExists(Category cat, BulkImportResult result) {

		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		// checking if it already exists
		
		Category catAlreadySaved = catDao.fetchByName(cat.getName());
		
		if(cat.getId() == null && catAlreadySaved == null){
			
			catDao.saveOrUpdate(cat);
			result.addCategoryCreated(cat.getName());
			logger.debug("saveIfNotExists - saved cat " + cat.getName() + 
					" (#" + cat.getId() + ")");
		} else {
			result.addCategorySkipped(cat.getName());
			logger.debug("saveIfNotExists - cat " + cat.getName() + 
					" (#" + cat.getId() + ") already exists, not saving again.");
		}
		
		return result;
	}


	protected static List<Line> valuesArrayToLineList(String[] valuesArray, int nbCommaPerLine) {
		
		List<Line> lineList = new ArrayList<>();
		
		// Date,Libellé,Débit €,Crédit €,Catégorie,Sous-catégorie,Note,Catégorie,Sous-catégorie,Montant
		int nbOfValues = 10; 
		int nbOfLines = valuesArray.length / nbOfValues;
		int nbOfFailedLines = 0;

		logger.debug("valuesArrayToLineList - Received array with " + 
					valuesArray.length + " elements. nbOfValues : " + 
					nbOfValues + ". Expecting " + 
					nbOfLines + " lines.");
		
		// loop on lines
		for(int lineIndex = 0; lineIndex < nbOfLines; lineIndex++){
			String[] currentLineValues = new String[nbOfValues];
			
			StringBuilder debugMsg = new StringBuilder("[");
			// loop on values for a line
			for(int i = 0; i < nbOfValues; i++){
				int currentValueIndex = lineIndex * nbOfValues + i;
				String currValue = valuesArray[currentValueIndex];
				currentLineValues[i] = currValue;
				
				if(i > 0){
					debugMsg.append(", ");
				}
				debugMsg.append(currValue);
			}
			debugMsg.append("]");
			
			logger.debug("valuesArrayToLineList - line #" + lineIndex + " has " + 
							currentLineValues.length + " elements " + 
							"(should be " + nbOfValues + ").");
			
			Line currentLine;
			try {
				logger.debug("valuesArrayToLineList - currentLineValues : " + debugMsg.toString());
				currentLine = new Line(currentLineValues);

				lineList.add(currentLine);
			} catch (MalformedLineException e) {
				String errorMessage = "Error on line n°" + lineIndex + ": " + e.getLocalizedMessage();
				logger.error("valuesArrayToLineList - " + errorMessage, e);
				nbOfFailedLines++;
			}
		}
		
		logger.debug("valuesArrayToLineList - lineList.size : " + lineList.size() + ", nbOfFailedLines : " + nbOfFailedLines);
		
		return lineList;
	}

}
