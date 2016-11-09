package fr.pascalmahe.web.conv;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Type;
import fr.pascalmahe.services.TypeService;

@FacesConverter("typeConverter")
public class TypeConverter implements Converter {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Override
	public Object getAsObject(FacesContext facesCtx, UIComponent uiComp, String typeName) {
		Type returnType = null;
		if(typeName != null && typeName.trim().length() > 0){
			returnType = TypeService.fromDetailedLabel(typeName);
		}
		return returnType;
	}

	@Override
	public String getAsString(FacesContext facesCtx, UIComponent uiComp, Object obj) {
		String returnString = null;
		if(obj != null && obj instanceof Type){
			Type typeObj = (Type) obj;
			returnString = typeObj.getName();
		}
		return returnString;
	}

	
}
