package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.services.CategoryService;
import fr.pascalmahe.services.LineService;

@SessionScoped
@ManagedBean
public class CategoryController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = -7429215837503847892L;

	private static final Logger logger = LogManager.getLogger();

	private Category cat;

	private Category catReplace;
	
	private String idToFetch;

	private List<Category> otherCatsList;
	
	private List<Category> fatherPotentialsList;
	
	private List<Category> sonsList;
	
	public CategoryController(){
		logger.debug("constructor");
		cat = new Category(); // necessary so that JSF finds the line when updating
		fatherPotentialsList = CategoryService.fetchFirstRankCategoryList();
		otherCatsList = CategoryService.fetchAll();
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
		} else {
			if(otherCatsList == null || otherCatsList.isEmpty()){
				otherCatsList = CategoryService.fetchAll();
			}
			if(fatherPotentialsList == null || fatherPotentialsList.isEmpty()){
				fatherPotentialsList = CategoryService.fetchFirstRankCategoryList();
			}
			otherCatsList.remove(cat);
			sonsList = CategoryService.fetchSonsOf(cat);
		}
		logger.debug("fetch - end.");
	}
	
	public void saveAction(){
		logger.debug("saveAction - start...");

		FacesMessage fMess = null;
		try {
			CategoryService.saveCategoryAndSons(cat, sonsList);

			String userMsg = "Catégorie sauvegardée.";
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, 
					userMsg, 
					"");
		} catch (Exception e){
			logger.error("Exception while updating Category: " + e.getLocalizedMessage(), e);
			
			String userMsg = "Erreur lors de la sauvegarde de la catégorie : " + e.getLocalizedMessage() + ".";
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							userMsg, 
							"");
		}

		FacesContext.getCurrentInstance().addMessage("saved_a_cat", fMess);
		
		logger.debug("saveAction - end.");
	}

	public void addCatChoice(){
		logger.debug("addCatChoice - start...");
		
		sonsList.add(new Category("", this.cat));
		
		logger.debug("addCatChoice - end.");
	}
	
	public void removeCatChoice(Integer catID){
		logger.debug("removeCatChoice - removing cat #" + catID + "from list of sons...");
		
		Category catToRemove = CategoryService.fetchById(catID);
		if(sonsList.contains(catToRemove)){
			sonsList.remove(catToRemove);
		}
		
		logger.debug("removeCatChoice - end.");
	}
	
	/*
	 * Getters et Setters
	 */
	
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

	public List<Category> getFatherPotentialsList() {
		return fatherPotentialsList;
	}

	public void setFatherPotentialsList(List<Category> fatherPotentialsList) {
		this.fatherPotentialsList = fatherPotentialsList;
	}

	public Category getCatReplace() {
		return catReplace;
	}

	public void setCatReplace(Category catReplace) {
		this.catReplace = catReplace;
	}

	public List<Category> getOtherCatsList() {
		return otherCatsList;
	}

	public void setOtherCatsList(List<Category> otherCatsList) {
		this.otherCatsList = otherCatsList;
	}

	public List<Category> getSonsList() {
		return sonsList;
	}

	public void setSonsList(List<Category> sonsList) {
		this.sonsList = sonsList;
	}
	
}

