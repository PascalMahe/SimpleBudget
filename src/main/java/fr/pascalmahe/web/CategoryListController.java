package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.services.CategoryService;
import fr.pascalmahe.web.beans.CatRow;


@ManagedBean
public class CategoryListController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = 2706069152258036613L;

	private static final Logger logger = LogManager.getLogger();

	private List<CatRow> rowList;

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
		
		logger.debug("init - end");
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

}

