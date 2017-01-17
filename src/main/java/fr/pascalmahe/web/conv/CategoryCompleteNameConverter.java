package fr.pascalmahe.web.conv;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Category;
import fr.pascalmahe.services.CategoryService;

@FacesConverter("categoryCompleteNameConverter")
public class CategoryCompleteNameConverter implements Converter {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Override
	public Object getAsObject(FacesContext facesCtx, UIComponent uiComp, String catName) {
		
		if(catName.contains(Category.COMPLETE_NAME_SEPARATOR)){
			catName = catName.substring(catName.indexOf(Category.COMPLETE_NAME_SEPARATOR));
		}
		Category returnCat = CategoryService.fetchOrCreateFromName(catName);
		
//		logger.debug("getAsObject - got " + catName + " returned " + returnCat);
		return returnCat;
	}

	@Override
	public String getAsString(FacesContext facesCtx, UIComponent uiComp, Object obj) {
		String returnString = null;
		if(obj != null && obj instanceof Category){
			Category catObj = (Category) obj;
			returnString = catObj.getCompleteName();
		}
//		logger.debug("getAsString - got " + obj + " returned " + returnString);
		return returnString;
	}

	
}
