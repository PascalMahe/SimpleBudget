package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	
	private List<MonthCell> sumRow;
	
	private List<MonthCell> sumExceptInternalMovmtRow;

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
		monthList = rowList.get(0).getListOfMonthNames();
		logger.debug("init - monthList.size: " + monthList.size());
		
		logger.debug("init - Computing sum and sumExceptInternalMovements...");
		
		HashMap<String, MonthCell> sumPerMonthMap = new HashMap<>();
		HashMap<String, MonthCell> sumExceptInternalMovmtPerMonthMap = new HashMap<>();
		
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
		
		this.sumRow = hashMapToMonthList(sumPerMonthMap);

		this.sumExceptInternalMovmtRow = hashMapToMonthList(sumExceptInternalMovmtPerMonthMap);
		logger.debug("init - Sum and sumExceptInternalMovements computed.");
		
		logger.debug("init - end");
	}
	
	private void addRowToSums(HashMap<MonthInYear, MonthCell> monthList, 
			String categoryName,
			HashMap<String, MonthCell> sumPerMonthMap, 
			HashMap<String, MonthCell> sumExceptInternalMovmtPerMonthMap){
		for(MonthCell month : monthList.values()){
			if(!sumPerMonthMap.containsKey(month.getName())){
				logger.debug("init - month '" + month.getName() + "' not in list, adding it...");
				MonthCell newMonth = new MonthCell(month.getMonthValue(), 
													month.getPosAmount(), 
													month.getNegAmount());
				sumPerMonthMap.put(month.getName(), newMonth);
				logger.debug("init - month '" + month.getName() + "' added.");
			} else {
				logger.debug("init - month '" + month.getName() + "' already in list, adding amounts to it...");
				MonthCell existingCell = sumPerMonthMap.get(month.getName());
				existingCell.addPosAmount(month.getPosAmount());
				existingCell.addNegAmount(month.getNegAmount());
				logger.debug("init - amounts added to month '" + month.getName() + "'.");
			}
			
			if(!sumExceptInternalMovmtPerMonthMap.containsKey(month.getName())){
				float negAmount = 0.00f;
				float posAmount = 0.00f;
				if(!categoryName.equalsIgnoreCase(NAME_INTERNAL_MOVEMENT)){
					negAmount += month.getNegAmount();
					posAmount += month.getPosAmount();
				}
				MonthCell newMonth = new MonthCell(month.getMonthValue(), 
													posAmount, 
													negAmount);
				sumExceptInternalMovmtPerMonthMap.put(month.getName(), newMonth);
			} else {
				
				if(!categoryName.equalsIgnoreCase(NAME_INTERNAL_MOVEMENT)){
					MonthCell existingCell = sumExceptInternalMovmtPerMonthMap.get(month.getName());
					existingCell.addPosAmount(month.getPosAmount());
					existingCell.addNegAmount(month.getNegAmount());
				}
				
			}
		}
	}

	private List<MonthCell> hashMapToMonthList(HashMap<String, MonthCell> monthMap) {
		List<MonthCell> result = new ArrayList<>();
		
		for(String key : monthMap.keySet()){
			MonthCell currentMonth = monthMap.get(key);
			result.add(new MonthCell(currentMonth.getMonthValue(), 
									currentMonth.getPosAmount(), 
									currentMonth.getNegAmount()));
		}
		
		Collections.sort(result);
		
		return result;
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

	public List<MonthCell> getSumRow() {
		return sumRow;
	}

	public void setSumRow(List<MonthCell> sumRow) {
		this.sumRow = sumRow;
	}

	public List<MonthCell> getSumExceptInternalMovmtRow() {
		return sumExceptInternalMovmtRow;
	}

	public void setSumExceptInternalMovmtRow(List<MonthCell> sumExceptInternalMovmtRow) {
		this.sumExceptInternalMovmtRow = sumExceptInternalMovmtRow;
	}
	
}

