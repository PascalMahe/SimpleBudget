package fr.pascalmahe.web.beans;

import java.io.Serializable;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Category;

/**
 * Class for manipulating a row of values in the categoryList page
 * One row has a category and EITHER a list of other CatRows (sons
 * of this' category) OR a list of individual values (ie. Months).
 * @author Pascal
 */
public class CatRow implements Serializable {

	private static final Logger logger = LogManager.getLogger();
	
	private static final long serialVersionUID = 7369100279387330019L;

	private Category category;
	
	private List<CatRow> sonsCatRowList;
	
	private List<MonthCell> monthList;


	public List<String> getListOfMonthNames() {
		if(monthList == null){
			return sonsCatRowList.get(0).getListOfMonthNames();
		}
		List<String> listOfNames = new ArrayList<>();
		
		for(MonthCell month : monthList){
			listOfNames.add(month.getName());
		}
		
		return listOfNames;
	}

	public int getNumberOfMonths() {
		if(monthList == null){
			return sonsCatRowList.get(0).getNumberOfMonths();
		}
		return monthList.size();
	}

	public void addMonthCell(Month month, float posAmount, float negAmount) {
		MonthCell monthCell = new MonthCell(month, posAmount, negAmount);
		if(this.monthList == null){
			monthList = new ArrayList<>();
		}
		monthList.add(monthCell);
	}

	public void addSonCatRow(CatRow sonCatRow) {
		if(sonsCatRowList == null){
			sonsCatRowList = new ArrayList<>();
		}
		sonsCatRowList.add(sonCatRow);
	}

	public int getSizeOfSonsCatRowList(){
		return sonsCatRowList.size();
	}
	
	public boolean isChildFree(){
		return sonsCatRowList == null;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<CatRow> getSonsCatRowList() {
		return sonsCatRowList;
	}

	public void setSonsCatRowList(List<CatRow> sonsCatRowList) {
		this.sonsCatRowList = sonsCatRowList;
	}

	public List<MonthCell> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<MonthCell> monthList) {
		this.monthList = monthList;
	}

}
