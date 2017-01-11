package fr.pascalmahe.web;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.services.CategoryService;

@SessionScoped
@ManagedBean
public class CategoryController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = -7429215837503847892L;

	private static final Logger logger = LogManager.getLogger();

	private Category cat;

	private String idToFetch;
	
	public CategoryController(){
		logger.debug("constructor");
		cat = new Category(); // necessary so that JSF finds the line when updating
		
	}
	
	/*
	 * Méthodes
	 */
	public void fetch(){
		
		logger.debug("fetch - idToFetch : " + idToFetch);
		
		cat = CategoryService.fetchCategory(idToFetch);
		if(cat == null){
			FacesMessage fmWrongPwd = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Pas de catégorie trouvée pour l'id : " + idToFetch, "");
			FacesContext.getCurrentInstance().addMessage("wrong_login_pwd", fmWrongPwd);
		}
		logger.debug("fetch - end.");
	}

	public Category getCat() {
		return cat;
	}

	public void setCat(Category cat) {
		this.cat = cat;
	}

	public String getIdToFetch() {
		return idToFetch;
	}

	public void setIdToFetch(String idToFetch) {
		this.idToFetch = idToFetch;
	}
	
	/*
	 * Getters et Setters
	 */
	
}

