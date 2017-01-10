package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.Type;
import fr.pascalmahe.services.CategoryService;
import fr.pascalmahe.services.LineService;
import fr.pascalmahe.services.TypeService;
import fr.pascalmahe.web.beans.CatChoice;

@SessionScoped
@ManagedBean
public class LineController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = 2180789495593868542L;

	private static final Logger logger = LogManager.getLogger();

	private Line line;

	private String idToFetch;
	
	private List<Type> typeList;
	
	private List<Category> firstRankCategoryList;
	
	private static Map<Integer, List<Category>> sonCatMap;
	
	private Map<Integer, CatChoice> catChoiceMap;
	
	public LineController(){
		logger.debug("constructor");
		line = new Line(); // necessary so that JSF finds the line when updating
		typeList = TypeService.getTypeList();
		firstRankCategoryList = CategoryService.fetchFirstRankCategoryList();
		
		populateSonCatMap();
		
		// populating catChoiceMap
		catChoiceMap = populateCatChoiceMap(line.getCategorisationList());
	}
	
	/*
	 * Méthodes
	 */
	private static void populateSonCatMap() {
		
		if(sonCatMap == null){
//			logger.debug("populateSonCatMap - Populating...");
			List<Category> rawSecondRankCategoryList = CategoryService.fetchSecondRankCategoryList();
			sonCatMap = populateSonCatMap(rawSecondRankCategoryList);
//			logger.debug("populateSonCatMap - Populated.");
		} else {
//			logger.debug("populateSonCatMap - Already populated.");
		}
	}

	protected static Map<Integer, CatChoice> populateCatChoiceMap(List<Categorisation> categorisationList) {
		
		logger.debug("populateCatChoiceMap - in list.size: " + categorisationList.size());
		Map<Integer, CatChoice> returnedMap = new HashMap<>();
		
		for(Categorisation catego : categorisationList){
			
			Category cat = catego.getCategory();
			
			Category sonCat = null;
			Category fatCat = null;
			List<Category> secondRankCatList = new ArrayList<>();
			if(cat != null){
				if(cat.getFatherCategory() != null){
					sonCat = cat;
					fatCat = cat.getFatherCategory();
				} else {
					fatCat = cat;
				}
				
//				logger.debug("populateCatChoiceMap - fatCat: " + fatCat);
//				logger.debug("populateCatChoiceMap - sonCat: " + sonCat);
				secondRankCatList = sonCatMap.get(fatCat.getId());
			}
			
			CatChoice currCatChoice = new CatChoice(catego.getId(), fatCat, sonCat, catego.getAmount(), secondRankCatList);
			returnedMap.put(catego.getId(), currCatChoice);
//			logger.debug("populateCatChoiceMap - currCatChoice: " + currCatChoice);
			
		}
		logger.debug("populateCatChoiceMap - out map.size: " + returnedMap.size());
		return returnedMap;
	}

	
	protected static Map<Integer, List<Category>> populateSonCatMap(List<Category> secondRankCategoryList) {
		Map<Integer, List<Category>>  hashMapBeingBuilt = new HashMap<>();
		for(Category cat : secondRankCategoryList){
			
			int key = cat.getFatherCategory().getId();
			List<Category> brotherCategories = hashMapBeingBuilt.get(key);
			// noting wether to push back the list of just fill it
			boolean entryWasNull = false;
			if(brotherCategories == null){
				entryWasNull = true;
				brotherCategories = new ArrayList<>();
//				logger.debug("populateSonCatMap - Adding list for "+ 
//						"fatherCategory " + cat.getFatherCategory().getName() + 
//						" (#" + key + ").");
			}
			
			brotherCategories.add(cat);
//			logger.debug("populateSonCatMap - Adding category " + cat.getName() +  
//						" (#" + cat.getId() + ") to " + 
//						"fatherCategory " + cat.getFatherCategory().getName() + ".");
			
			if(entryWasNull){
				hashMapBeingBuilt.put(key, brotherCategories);
			}
			
		}
		return hashMapBeingBuilt;
	}

	public void fetch(){
		
		logger.debug("fetch - idToFetch : " + idToFetch);
		
		populateSonCatMap();
		
		line = LineService.fetchLine(idToFetch);
		if(line == null){
			FacesMessage fmWrongPwd = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Pas de ligne trouvée pour l'id : " + idToFetch, "");
			FacesContext.getCurrentInstance().addMessage("wrong_login_pwd", fmWrongPwd);
		} else {
			catChoiceMap = populateCatChoiceMap(line.getCategorisationList());
		}
		
		if(typeList == null || typeList.size() == 0){
			typeList = TypeService.getTypeList();
		}
		
		if(firstRankCategoryList == null || firstRankCategoryList.size() == 0){
			firstRankCategoryList = CategoryService.fetchFirstRankCategoryList();
		}
		logger.debug("fetch - end.");
	}
	
	private String catChoiceMapToString(String header){

		StringBuilder dbgMsg = new StringBuilder(header + " - catChoiceMap : [");
		for(Integer key : catChoiceMap.keySet()){
			CatChoice catCh = catChoiceMap.get(key);
			if(dbgMsg.length() > 19 + header.length()){
				dbgMsg.append(", ");
			}
			Category fatCat = catCh.getFatherCategory();
			if(fatCat != null){
				dbgMsg.append(fatCat.getName());
			} else {
				dbgMsg.append("(NOCAT)");
			}
			
			Category sonCat = catCh.getSonCategory();
			if(sonCat != null){
				dbgMsg.append(":" + sonCat.getName());
			}
		}
		dbgMsg.append("]");
		
		return dbgMsg.toString();
	}
	

	private String categoListToString(String header){

		StringBuilder dbgMsg = new StringBuilder(header + " - categoList : [");
		for(Categorisation catego : line.getCategorisationList()){
			
			if(dbgMsg.length() > 17 + header.length()){
				dbgMsg.append(", ");
			}
			Category sonCat = catego.getCategory();
			
			if(sonCat != null){
				Category fatCat = sonCat.getFatherCategory();
				if(fatCat != null){
					dbgMsg.append(fatCat.getName())
							.append(":");
				} else {
					dbgMsg.append("(NOCAT):");
				}
				dbgMsg.append(sonCat.getName());
			}
		}
		dbgMsg.append("]");
		
		return dbgMsg.toString();
	}
	
	public void addCatChoice(){
		logger.debug("addCatChoice - start...");
		
		
		logger.debug(categoListToString("addCatChoice"));
		LineService.updateCategoInLineFromCatChoiceMap(line, catChoiceMap);
//		LineService.updateLine(line);
		this.line.addCategorisation(0, null);
		logger.debug(catChoiceMapToString("addCatChoice"));
		logger.debug(categoListToString("addCatChoice"));
		this.catChoiceMap = populateCatChoiceMap(this.line.getCategorisationList());
		
		logger.debug(categoListToString("addCatChoice"));
		logger.debug(catChoiceMapToString("addCatChoice"));
		
		RequestContext.getCurrentInstance().execute("validateCatChoices()");
		
		logger.debug("addCatChoice - end.");
	}
	
	public void removeCatChoice(Integer categoID){
		logger.debug("removeCatChoice - start...");
		
		logger.debug("removeCatChoice - categoID: #" + categoID);
		this.catChoiceMap.remove(categoID);
		logger.debug("removeCatChoice - end.");
	}
	
	public void saveAction(){
		logger.debug("saveAction - start...");
		
		FacesMessage fMess = null;
		try {
			// validation of father/son choices
			int invalidPairsNumber = validateMapCatChoice(catChoiceMap);
			fMess = getInvalidMessage(invalidPairsNumber);
				
			if(invalidPairsNumber == 0){
				// continue update

				// replace Categories in Categorizations
				LineService.updateCategoInLineFromCatChoiceMap(line, catChoiceMap);
				
				LineService.updateLine(line);
				this.catChoiceMap = populateCatChoiceMap(line.getCategorisationList());

				String userMsg = "Ligne sauvegardée.";
				fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, 
						userMsg, 
						"");
			}
			

		} catch (Exception e){
			logger.error("Exception while updating Line: " + e.getLocalizedMessage(), e);
			
			String userMsg = "Erreur lors de la sauvegarde de la ligne : " + e.getLocalizedMessage() + ".";
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							userMsg, 
							"");
		}

		FacesContext.getCurrentInstance().addMessage("saved_a_line", fMess);
		
		logger.debug("saveAction - end.");
	}
	
	private FacesMessage getInvalidMessage(int invalidPairsNumber){
		FacesMessage fMess = null;
		
		if(invalidPairsNumber > 0){
			// show error message
			String userMsg = null;
			Severity msgSeverity = FacesMessage.SEVERITY_ERROR;
			if(invalidPairsNumber == 1){
				// singular
				userMsg = "Impossible d'avoir plusieurs fois la même catégorie.";
			} else {
				// plural
				userMsg = "Impossible d'avoir plusieurs fois les mêmes catégories.";
			}
			fMess = new FacesMessage(msgSeverity, 
									userMsg, 
									"");
		}
		
		return fMess;
	}
	
	static protected int validateMapCatChoice(Map<Integer, CatChoice> catChoiceMapToValidate) {

		Map<Category, CatChoice> alreadyValidatedCategories = new HashMap<>();
		int invalidPairsNumber = 0;
		
		StringBuilder dbgBefore = new StringBuilder("");
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);

			Category currentCategory = catChoice.getFullCategory();
			dbgBefore.append("\n\tcatChoice #")
					.append(categoID)
					.append(" Cat#")
					.append(currentCategory.getId())
					.append(" ")
					.append(currentCategory.getCompleteName())
					.append(" is FatherValid? ")
					.append(catChoice.isFatherValid())
					.append(", is SonValid? ")
					.append(catChoice.isSonValid());
		}
		logger.debug("validateMapCatChoice - before: " + dbgBefore.toString());
		
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);

			Category currentCategory = catChoice.getFullCategory();
			logger.debug("validateMapCatChoice - catChoice #" + categoID + 
							": Cat#" + currentCategory.getId() + 
							" " + currentCategory.getCompleteName());
			if(alreadyValidatedCategories.containsKey(currentCategory)){
				logger.debug("validateMapCatChoice - category already in list");
				CatChoice previousCatChoiceWithSameCategory = alreadyValidatedCategories.get(currentCategory);
				logger.debug("validateMapCatChoice - other catChoice was #" + previousCatChoiceWithSameCategory.getCategoId());
				previousCatChoiceWithSameCategory.setInvalid();
				catChoice.setInvalid();
				invalidPairsNumber++;
				logger.debug("validateMapCatChoice - invalidPairsNumber is now : " + invalidPairsNumber);
				
			} else {
				catChoice.setValid();
				alreadyValidatedCategories.put(currentCategory, catChoice);
				logger.debug("validateMapCatChoice - category wasn't in list, is now");
			}
		}
		

		StringBuilder dbgAfter = new StringBuilder("");
		for(Integer categoID : catChoiceMapToValidate.keySet()){
			CatChoice catChoice = catChoiceMapToValidate.get(categoID);

			Category currentCategory = catChoice.getFullCategory();
			dbgAfter.append("\n\tcatChoice #")
				.append(categoID)
				.append(" Cat#")
				.append(currentCategory.getId())
				.append(" ")
				.append(currentCategory.getCompleteName())
				.append(" is FatherValid? ")
				.append(catChoice.isFatherValid())
				.append(", is SonValid? ")
				.append(catChoice.isSonValid());			
		}
		logger.debug("validateMapCatChoice - after: " + dbgBefore.toString());
		
		return invalidPairsNumber;
	}

	public void onFatherCategoryChange(AjaxBehaviorEvent abe){
		
		UIOutput source = (UIOutput) abe.getSource();
		String rawCategoIDAsStr = source.getClientId();
		rawCategoIDAsStr = rawCategoIDAsStr.replace("lineForm:fatherCategory", "");
		Integer categoID = null;
		if(StringUtils.isNotBlank(rawCategoIDAsStr)){
			categoID = Integer.parseInt(rawCategoIDAsStr);
		}
		
		Category value = (Category) source.getValue();
		Integer newFatCatID = value.getId();
		
		logger.debug("onFatherCategoryChange - categoId: #" + categoID + ", newFatherCategoryId: #" + newFatCatID);
		
		CatChoice catChoiceToUpdate = catChoiceMap.get(categoID);
		Category fatCat = CategoryService.fetchById(newFatCatID);
		
		catChoiceToUpdate.setFatherCategory(fatCat);
		populateSonCatMap();
		catChoiceToUpdate.setSecondRankCategoryList(sonCatMap.get(newFatCatID));
		catChoiceToUpdate.setSonCategory(null);
		
		RequestContext.getCurrentInstance().execute("validateCatChoices()");
	}
	
	/*
	 * Getters et Setters
	 */
	
	public List<CatChoice> getCatChoiceMapAsList(){
		Collection<CatChoice> collCatChoice = catChoiceMap.values();
		List<CatChoice> catChoiceList = new ArrayList<>();
		catChoiceList.addAll(collCatChoice);
		return catChoiceList;
	}
	
	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public String getIdToFetch() {
		return idToFetch;
	}

	public void setIdToFetch(String idToFetch) {
		this.idToFetch = idToFetch;
	}

	public List<Type> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<Type> typeList) {
		this.typeList = typeList;
	}

	public List<Category> getFirstRankCategoryList() {
		return firstRankCategoryList;
	}

	public void setFirstRankCategoryList(List<Category> firstRankCategoryList) {
		this.firstRankCategoryList = firstRankCategoryList;
	}

}

