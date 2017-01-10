package fr.pascalmahe.web.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.MonthInYear;

/**
 * Class for manipulating a row of values in the categoryList page
 * One row has a category and EITHER a list of other CatRows (sons
 * of this' category) OR a list of individual values (ie. Months).
 * @author Pascal
 */
public class CatRow implements Serializable, Comparable<CatRow> {

	private static final Logger logger = LogManager.getLogger();
	
	private static final long serialVersionUID = 7369100279387330019L;
	
	private static final Comparator<MonthInYear> reverseMonthInYearComparator = new Comparator<MonthInYear>(){
		public int compare(MonthInYear moy1, MonthInYear moy2){
			return moy2.compareTo(moy1);
		}
	};

	private Category category;
	
	private List<CatRow> sonsCatRowList;
	
	private Map<MonthInYear, MonthCell> monthList;

	public CatRow(Category currentCat) {
		this.category = currentCat;
	}

	public List<String> getListOfMonthNames() {
		if(monthList == null){
			return sonsCatRowList.get(0).getListOfMonthNames();
		}
		List<String> listOfNames = new ArrayList<>();
		
		for(MonthInYear month : monthList.keySet()){
			listOfNames.add(month.toString());
		}
		
		return listOfNames;
	}

	public int getNumberOfMonths() {
		if(monthList == null){
			return sonsCatRowList.get(0).getNumberOfMonths();
		}
		return monthList.size();
	}

	public void addMonthCell(MonthInYear month, float posAmount, float negAmount) {
		MonthCell monthCell = new MonthCell(month, posAmount, negAmount);
		if(this.monthList == null){
			
			monthList = new TreeMap<>(reverseMonthInYearComparator);
		}
		monthList.put(month, monthCell);
	}

	public void addSonCatRow(CatRow sonCatRow) {
		if(sonsCatRowList == null){
			sonsCatRowList = new ArrayList<>();
		}
		sonsCatRowList.add(sonCatRow);
	}

	public void addAmountToMonth(MonthInYear currentMonth, Float amount) {
//		logger.debug("addAmountToMonth - CatRow #" + category.getId() + "(" + category.getName() + ")"
//				+ ", adding " + amount + " to month " + currentMonth + "...");
		
		MonthCell currentMoce = null;
		if(monthList == null){
			monthList = new TreeMap<>(reverseMonthInYearComparator);
//			logger.debug("addAmountToMonth - monthList created.");
		}
		if(monthList.keySet().contains(currentMonth)){
			
//			logger.debug("addAmountToMonth - monthList "
//					+ "(current size: " + monthList.size() + ") contains " + currentMonth + ".");
			currentMoce = monthList.get(currentMonth);

//			logger.debug("addAmountToMonth - found it in list. "
//					+ "Adding to its current value: "
//					+ "(" + currentMoce.getNegAmount() + "; "
//					+ "" + currentMoce.getPosAmount() + ")...");
			currentMoce.addAmount(amount);
			
		} else {
//			logger.debug("addAmountToMonth - monthList does not contain " + currentMonth + " "
//					+ "(current size: " + monthList.size() + "). "
//					+ "Adding it...");
			
			float posAmount = 0.0f;
			float negAmount = 0.0f;
			if(amount > 0){
				posAmount = amount;
			} else {
				negAmount = amount;
			}
			currentMoce = new MonthCell(currentMonth, posAmount, negAmount);
			monthList.put(currentMonth, currentMoce);
			
//			logger.debug("addAmountToMonth - monthList "
//					+ "(current size: " + monthList.size() + ") "
//					+ "contains " + currentMonth + "? : " 
//					+ (monthList.keySet().contains(currentMonth)));
		}
		
//		logger.debug("addAmountToMonth - value before exiting: "
//				+ "(" + currentMoce.getNegAmount() + "; "
//				+ "" + currentMoce.getPosAmount() + ")...");
	}

	public void fillMonthsUpTo(MonthInYear oldestMonthYet) {
		
		// filling up sons
		if(sonsCatRowList != null && !sonsCatRowList.isEmpty()){

//			logger.debug("fillMonthsUpTo - CatRow: " + category.getName() + 
//					", filling up sons...");
			
			for(CatRow son : sonsCatRowList){
				son.fillMonthsUpTo(oldestMonthYet);
			}

//			logger.debug("fillMonthsUpTo - CatRow: " + category.getName() + 
//					", sons filled up.");
		} else {

//			logger.debug("fillMonthsUpTo - CatRow: " + category.getName() + 
//					" (with currently " + monthList.size() + " months), filling up months down to " + oldestMonthYet + "...");
			
			// actual filling up
			LocalDate now = LocalDate.now();
			MonthInYear thisMonth = new MonthInYear(now.getMonth(), now.getYear());
			
//			logger.debug("fillMonthsUpTo - CatRow: " + category.getName() + 
//					", filling up from " + thisMonth + "...");
			
			List<MonthInYear> monthRange = oldestMonthYet.rangeTo(thisMonth);
			for(MonthInYear miy : monthRange){
				this.addAmountToMonth(miy, 0.0f);
			}

//			logger.debug("fillMonthsUpTo - CatRow: " + category.getName() + 
//					", done. Now with: " + this.getMonthList().size() + " months: " + this.getMonthList());
			
		}
		
	}

	public CatRow createOrReturnSonCatRow(Category currentCat) {
		
		CatRow toReturn = null;
		if(sonsCatRowList == null){
			sonsCatRowList = new ArrayList<>();
		}
		
		for(CatRow catRow: sonsCatRowList){
//			logger.debug("createOrReturnSonCatRow - is cat #" + catRow.category.getId() + 
//					"(" + catRow.category.getName() + ") "
//					+ "equal to cat #" + currentCat.getId() + 
//					"(" + currentCat.getName() + ") ? " + (catRow.category.equals(currentCat)));
			if(catRow.category.equals(currentCat)){
				toReturn = catRow;
				break;
			}
		}
		
		if(toReturn == null){
			toReturn = new CatRow(currentCat);
			sonsCatRowList.add(toReturn);
			Collections.sort(sonsCatRowList);
			logger.debug("createOrReturnSonCatRow - adding cat " + currentCat.getName() + " to " + this.category.getName());
		}
		
		return toReturn;
	}
	
	public MonthCell getMonth(MonthInYear moy){
		return monthList.get(moy);
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

	public Map<MonthInYear, MonthCell> getMonthList() {
		return monthList;
	}

	public void setMonthList(Map<MonthInYear, MonthCell> monthMap) {
		this.monthList = monthMap;
	}

	@Override
	public int compareTo(CatRow o) {
		return category.compareTo(o.category);
	}


}
