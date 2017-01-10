package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.MonthInYear;
import fr.pascalmahe.services.CategoryService;
import fr.pascalmahe.web.beans.CatRow;
import fr.pascalmahe.web.beans.MonthCell;


@ManagedBean
public class CategoryListController implements Serializable {

	private static final String NAME_INTERNAL_MOVEMENT = "Virements internes";
	
	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = 2706069152258036613L;

	private static final Logger logger = LogManager.getLogger();

	private List<CatRow> rowList;
	
	private Map<MonthInYear, MonthCell> sumRow;
	
	private Map<MonthInYear, MonthCell> sumExceptInternalMovmtRow;

	private List<String> monthList;
	
	public CategoryListController(){
		logger.debug("constructor");
	}
	
	/*
	 * Méthodes
	 */
	@PostConstruct
	public void init(){
		logger.debug("init - start");
		
		rowList = CategoryService.fetchCategoryTable();
		
		String debugMsg = "init - monthList.size: ";
		if(rowList.size() > 0){
			debugMsg += rowList.get(0).getListOfMonthNames().size();
		} else {
			debugMsg += "0";
		}
		logger.debug(debugMsg);
		
		logger.debug("init - Filling monthList...");
		
		monthList = rowList.get(0).getListOfMonthNames();
		
		logger.debug("init - MonthList filled.");
		
		logger.debug("init - Computing sum and sumExceptInternalMovements...");
		
		HashMap<MonthInYear, MonthCell> sumPerMonthMap = new HashMap<>();
		HashMap<MonthInYear, MonthCell> sumExceptInternalMovmtPerMonthMap = new HashMap<>();
		
		for(CatRow row : rowList){
			logger.debug("init - CatRow: " + row.getCategory().getName());
			
			
			if(row.getMonthList() != null){
				// loop on monthList 
				addRowToSums(row.getMonthList(), 
							row.getCategory().getCompleteName(), 
							sumPerMonthMap, 
							sumExceptInternalMovmtPerMonthMap);
			} else {
				// loop on sons if monthList null
				for(CatRow sonRow : row.getSonsCatRowList()){
					addRowToSums(sonRow.getMonthList(), 
							row.getCategory().getCompleteName(), 
							sumPerMonthMap, 
							sumExceptInternalMovmtPerMonthMap);
				}
			}
			
		}
		
		this.sumRow = sumPerMonthMap;

		this.sumExceptInternalMovmtRow = sumExceptInternalMovmtPerMonthMap;
		logger.debug("init - Sum and sumExceptInternalMovements computed.");
		
		logger.debug("init - end");
	}
	

	private void addRowToSums(Map<MonthInYear, MonthCell> monthList, 
			String categoryName,
			Map<MonthInYear, MonthCell> sumPerMonthMap, 
			Map<MonthInYear, MonthCell> sumExceptInternalMovmtPerMonthMap){
		for(MonthCell month : monthList.values()){
			if(!sumPerMonthMap.containsKey(month.getMonthValue())){

				sumPerMonthMap.put(month.getMonthValue(), month);

			} else {
				
				MonthCell existingCell = sumPerMonthMap.get(month.getMonthValue());
				
				existingCell.addPosAmount(month.getPosAmount());
				existingCell.addNegAmount(month.getNegAmount());
				sumPerMonthMap.put(month.getMonthValue(), existingCell);
			}
			
			if(!sumExceptInternalMovmtPerMonthMap.containsKey(month.getMonthValue())){
				float negAmount = 0.00f;
				float posAmount = 0.00f;
				if(!categoryName.equalsIgnoreCase(NAME_INTERNAL_MOVEMENT)){
					negAmount += month.getNegAmount();
					posAmount += month.getPosAmount();
				}
				MonthCell newMonth = new MonthCell(month.getMonthValue(), 
													posAmount, 
													negAmount);
				sumExceptInternalMovmtPerMonthMap.put(month.getMonthValue(), newMonth);
			} else {
				
				if(!categoryName.equalsIgnoreCase(NAME_INTERNAL_MOVEMENT)){
					MonthCell existingCell = sumExceptInternalMovmtPerMonthMap.get(month.getMonthValue());
					existingCell.addPosAmount(month.getPosAmount());
					existingCell.addNegAmount(month.getNegAmount());
					sumExceptInternalMovmtPerMonthMap.put(month.getMonthValue(), existingCell);
				}
				
			}
		}
	}

	/*
	 * Getters et Setters
	 */
	public List<CatRow> getRowList() {
		return rowList;
	}

	public void setRowList(List<CatRow> rowList) {
		this.rowList = rowList;
	}

	public List<String> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

	public Map<MonthInYear, MonthCell> getSumRow() {
		return sumRow;
	}

	public void setSumRow(Map<MonthInYear, MonthCell> sumRow) {
		this.sumRow = sumRow;
	}

	public Map<MonthInYear, MonthCell> getSumExceptInternalMovmtRow() {
		return sumExceptInternalMovmtRow;
	}

	public void setSumExceptInternalMovmtRow(Map<MonthInYear, MonthCell> sumExceptInternalMovmtRow) {
		this.sumExceptInternalMovmtRow = sumExceptInternalMovmtRow;
	}

	
}

