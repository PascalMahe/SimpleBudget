package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Line;
import fr.pascalmahe.services.LineService;


@ManagedBean
public class ListLineController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = 2180789495593868542L;

	private static final Logger logger = LogManager.getLogger();

	private List<Line> lineList;

	private String categoryName;
	
	public ListLineController(){
		logger.debug("constructor");
		categoryName = "new category";
	}
	
	/*
	 * Méthodes
	 */
	@PostConstruct
	public void init(){
		logger.debug("init - start");
		
		lineList = LineService.fetchLinesReverseChronologically();
		
		logger.debug("init - end");
	}

	/*
	 * Getters et Setters
	 */
	
	public List<Line> getLineList() {
		return lineList;
	}

	public void setLineList(List<Line> lineList) {
		this.lineList = lineList;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	


}

