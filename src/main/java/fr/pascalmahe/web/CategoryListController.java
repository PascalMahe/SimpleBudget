package fr.pascalmahe.web;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Account;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.MonthInYear;
import fr.pascalmahe.business.Type;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.services.CategoryService;
import fr.pascalmahe.services.TypeService;
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
		

		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		Category armesCategory = catDao.fetchByName("Armes");
		if(armesCategory == null){

			logger.debug("init - inserting data...");
			
			Type ccard = TypeService.fromDetailedLabel(Type.CCARD_PAYMENT);
			
			Category cat1 = new Category("Armure");
			Category cat2 = new Category("Armes");
			Category cat3 = new Category("Cheval");
			Category cat4 = new Category("Hommes d'armes");
			Category cat5 = new Category("Château");

			Category cat6 = new Category("Sièges", cat2);
			Category cat7 = new Category("Mêlée", cat2);
			Category cat8 = new Category("A distance", cat2);
			
			Account acc = Account.fromName(Account.NAME_LBP);
			
			LocalDate date1 = LocalDate.now().minusMonths(5);
			List<Categorisation> categoList1 = new ArrayList<>();
			Categorisation catego10 = new Categorisation(-100f, cat1);
			categoList1.add(catego10);
			Line l1 = new Line(date1, date1.plusDays(3), "Category Table test 1", "Category Table test 1", "", -100f, false, ccard, acc, categoList1);

			LocalDate date2 = LocalDate.now().minusMonths(4);
			List<Categorisation> categoList2 = new ArrayList<>();
			Categorisation catego20 = new Categorisation(-100f, cat1);
			categoList2.add(catego20);
			Categorisation catego21 = new Categorisation(-25f, cat6);
			categoList2.add(catego21);
			Categorisation catego22 = new Categorisation(-25f, cat7);
			categoList2.add(catego22);
			Categorisation catego23 = new Categorisation(-50f, cat8);
			categoList2.add(catego23);
			Line l2 = new Line(date2, date2.plusDays(3), "Category Table test 2", "Category Table test 2", "", -200f, false, ccard, acc, categoList2);
			
			LocalDate date3 = LocalDate.now().minusMonths(3);
			List<Categorisation> categoList3 = new ArrayList<>();
			Categorisation catego30 = new Categorisation(-100f, cat1);
			categoList3.add(catego30);
			Categorisation catego31 = new Categorisation(-25f, cat6);
			categoList3.add(catego31);
			Categorisation catego32 = new Categorisation(-25f, cat7);
			categoList3.add(catego32);
			Categorisation catego33 = new Categorisation(-50f, cat8);
			categoList3.add(catego33);
			Categorisation catego34 = new Categorisation(-100f, cat3);
			categoList3.add(catego34);
			Line l3 = new Line(date3, date3.plusDays(3), "Category Table test 3", "Category Table test 3", "", -300f, false, ccard, acc, categoList3);

			LocalDate date4 = LocalDate.now().minusMonths(3);
			List<Categorisation> categoList4 = new ArrayList<>();
			Categorisation catego40 = new Categorisation(100f, cat1);
			categoList4.add(catego40);
			Categorisation catego41 = new Categorisation(-25f, cat6);
			categoList4.add(catego41);
			Categorisation catego42 = new Categorisation(-25f, cat7);
			categoList4.add(catego42);
			Categorisation catego43 = new Categorisation(-50f, cat8);
			categoList4.add(catego43);
			Categorisation catego44 = new Categorisation(100f, cat3);
			categoList4.add(catego44);
			Categorisation catego45 = new Categorisation(100f, cat4);
			categoList4.add(catego45);
			Line l4 = new Line(date4, date4.plusDays(3), "Category Table test 4", "Category Table test 4", "", 400f, false, ccard, acc, categoList4);

			LocalDate date5 = LocalDate.now().minusMonths(3);
			List<Categorisation> categoList5 = new ArrayList<>();
			Categorisation catego50 = new Categorisation(-100f, cat1);
			categoList5.add(catego50);
			Categorisation catego51 = new Categorisation(-25f, cat6);
			categoList5.add(catego51);
			Categorisation catego52 = new Categorisation(-25f, cat7);
			categoList5.add(catego52);
			Categorisation catego53 = new Categorisation(-50f, cat8);
			categoList5.add(catego53);
			Categorisation catego54 = new Categorisation(-100f, cat3);
			categoList5.add(catego54);
			Categorisation catego55 = new Categorisation(-100f, cat4);
			categoList5.add(catego55);
			Categorisation catego56 = new Categorisation(-100f, cat5);
			categoList5.add(catego56);
			Line l5 = new Line(date5, date5.plusDays(3), "Category Table test 5", "Category Table test 5", "", -500f, false, ccard, acc, categoList5);
			
			catDao.saveOrUpdate(cat1);
			catDao.saveOrUpdate(cat2);
			catDao.saveOrUpdate(cat3);
			catDao.saveOrUpdate(cat4);
			catDao.saveOrUpdate(cat5);
			catDao.saveOrUpdate(cat6);
			catDao.saveOrUpdate(cat7);
			catDao.saveOrUpdate(cat8);
			
			GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
			categoDao.saveOrUpdate(catego10);
			categoDao.saveOrUpdate(catego20);
			categoDao.saveOrUpdate(catego21);
			categoDao.saveOrUpdate(catego22);
			categoDao.saveOrUpdate(catego23);
			categoDao.saveOrUpdate(catego30);
			categoDao.saveOrUpdate(catego30);
			categoDao.saveOrUpdate(catego31);
			categoDao.saveOrUpdate(catego32);
			categoDao.saveOrUpdate(catego33);
			categoDao.saveOrUpdate(catego34);
			categoDao.saveOrUpdate(catego40);
			categoDao.saveOrUpdate(catego41);
			categoDao.saveOrUpdate(catego42);
			categoDao.saveOrUpdate(catego43);
			categoDao.saveOrUpdate(catego44);
			categoDao.saveOrUpdate(catego45);
			categoDao.saveOrUpdate(catego50);
			categoDao.saveOrUpdate(catego51);
			categoDao.saveOrUpdate(catego52);
			categoDao.saveOrUpdate(catego53);
			categoDao.saveOrUpdate(catego54);
			categoDao.saveOrUpdate(catego55);
			categoDao.saveOrUpdate(catego56);
			
			GenericDao<Line> linDao = new GenericDao<>(Line.class);
			linDao.saveOrUpdate(l1);
			linDao.saveOrUpdate(l2);
			linDao.saveOrUpdate(l3);
			linDao.saveOrUpdate(l4);
			linDao.saveOrUpdate(l5);
			
			logger.debug("init - data inserted.");
		}
		
		
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
		
//		StringBuilder strB = new StringBuilder("init - expected table: \n");
//		for(CatRow row : rowList){
//			if(row.isChildFree()){
//				strB = complementStringBuilderWithCatRow(row, strB);
//			} else {
//				strB.append(row.getCategory().getName());
//				for(CatRow son : row.getSonsCatRowList()){
//					strB.append(" \t");
//					strB = complementStringBuilderWithCatRow(son, strB);
//				}
//			}
//		}
//		logger.debug(strB);
		
		logger.debug("init - end");
	}
	
	private StringBuilder complementStringBuilderWithCatRow(CatRow row, StringBuilder strB) {

		strB.append(row.getCategory().getName());
		if(row.getCategory().getName().length() < 8){
			strB.append("\t");
		}
		strB.append("\t");
		strB.append(" -> ");
		
		for(MonthInYear moiy : row.getMonthList().keySet()){
			MonthCell moCe = row.getMonthList().get(moiy);
			strB.append(moiy)
				.append(" : ")
				.append(moCe)
				.append(";\t ");
		}
		strB.append("\n");
		
		return strB;
	}

	private void addRowToSums(Map<MonthInYear, MonthCell> monthList, 
			String categoryName,
			Map<MonthInYear, MonthCell> sumPerMonthMap, 
			Map<MonthInYear, MonthCell> sumExceptInternalMovmtPerMonthMap){
		for(MonthCell month : monthList.values()){
			if(!sumPerMonthMap.containsKey(month.getMonthValue())){
//				logger.debug("addRowToSums - month '" + month.getMonthValue() + "' not in list, "
//						+ "creating it with amounts (" + 
//						month.getPosAmount() + ", " + 
//						month.getNegAmount() + ")...");
				sumPerMonthMap.put(month.getMonthValue(), month);
//				logger.debug("addRowToSums - month '" + month.getMonthValue() + "' created with amounts (" + 
//						month.getPosAmount() + ", " + 
//						month.getNegAmount() + ").");
			} else {
//				logger.debug("addRowToSums - month '" + month.getMonthValue() + "' already in list, adding amounts to it...");
				
				MonthCell existingCell = sumPerMonthMap.get(month.getMonthValue());
				
//				logger.debug("addRowToSums - before : (" + existingCell.getPosAmount() + ", " + existingCell.getNegAmount() + ")");
//				logger.debug("addRowToSums - amounts : (" + month.getPosAmount() + ", " + month.getNegAmount() + ")");
				
				
				existingCell.addPosAmount(month.getPosAmount());
				existingCell.addNegAmount(month.getNegAmount());
				sumPerMonthMap.put(month.getMonthValue(), existingCell);
				

				MonthCell newExistingCell = sumPerMonthMap.get(month.getMonthValue());
//				logger.debug("addRowToSums - after : (" + newExistingCell.getPosAmount() + ", " + newExistingCell.getNegAmount() + ")");
//				
//				logger.debug("addRowToSums - amounts added to month '" + month.getMonthValue() + "'.");
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

	private List<MonthCell> hashMapToMonthList(HashMap<MonthInYear, MonthCell> monthMap) {
		List<MonthCell> result = new ArrayList<>();
		
		for(MonthInYear key : monthMap.keySet()){
			MonthCell currentMonth = monthMap.get(key);
			result.add(currentMonth);
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

