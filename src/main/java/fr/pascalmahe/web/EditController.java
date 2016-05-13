package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.services.EditService;
import fr.pascalmahe.services.SearchService;


@ManagedBean
public class EditController implements Serializable {

	/*
	 * Variables statiques
	 */
	
	private static final long serialVersionUID = 3973801993975443027L;
	
	private static final Logger logger = LogManager.getLogger();


	/*
	 * Variables d'instance
	 */
	
	private Locale locale = Locale.getDefault();
	
	private String site;

	private String code;
	
	private String desi;

	/*
	 * Constructeurs
	 */
	public EditController(){
		Map<String, String> params = FacesContext.getCurrentInstance()
										.getExternalContext()
										.getRequestParameterMap();
		site = params.get("site");
		code = params.get("code");
	}
	
	/*
	 * M�thodes
	 */
	public List<String> completeSite(String rawIncompleteSite) {
        List<String> results = new ArrayList<>();
        logger.debug("completeText - called with : " + rawIncompleteSite);
        
        String cleanIncompleteSite = rawIncompleteSite.trim();
        cleanIncompleteSite = StringEscapeUtils.escapeEcmaScript(cleanIncompleteSite);
        cleanIncompleteSite = StringEscapeUtils.escapeHtml4(cleanIncompleteSite);
        
        SearchService service = new SearchService();
        
        results = service.findSites(cleanIncompleteSite);
        
        return results;
    }
	
	public void editAction(ActionEvent actionEvent) {
        
        String cleanSite = cleanUp(site);
        
        String cleanCode = cleanUp(code);
        
        String cleanDesi = cleanUp(desi);
        
        EditService editService = new EditService();
        
        boolean success = editService.editMachine(cleanSite, cleanCode, cleanDesi);
        
        if(success){
            addMessage("La Machine a été sauvegardée.", FacesMessage.SEVERITY_INFO);
        } else {
        	addMessage("La Machine n'a été sauvegardée.", FacesMessage.SEVERITY_ERROR);
        }
    }

	public void addMessage(String summary, Severity severity) {
        FacesMessage message = new FacesMessage(severity, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	private String cleanUp(String rawString) {
		

        String cleanString = rawString.trim();
        cleanString = StringEscapeUtils.escapeEcmaScript(cleanString);
        cleanString = StringEscapeUtils.escapeHtml4(cleanString);
        
		return cleanString;
	}

	
	/*
	 * Getters et Setters
	 */
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesi() {
		return desi;
	}

	public void setDesi(String desi) {
		this.desi = desi;
	}
	
}

