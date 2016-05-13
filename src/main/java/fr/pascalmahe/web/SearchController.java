package fr.pascalmahe.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Machine;
import fr.pascalmahe.services.SearchService;


@ManagedBean
@ViewScoped
public class SearchController implements Serializable {

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

	private String desi;
	
	private List<Machine> listeMachine;

	

	/*
	 * Méthodes
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
	
	public void searchAction(ActionEvent actionEvent) {
        
        logger.debug("searchAction - called with : {site = " + site + ", desi = " + desi + "}");
        
        String cleanSite = site.trim();
        cleanSite = StringEscapeUtils.escapeEcmaScript(cleanSite);
        cleanSite = StringEscapeUtils.escapeHtml4(cleanSite);
        
        String cleanDesi = desi.trim();
        cleanDesi = StringEscapeUtils.escapeEcmaScript(cleanDesi);
        cleanDesi = StringEscapeUtils.escapeHtml4(cleanDesi);
        
        SearchService service = new SearchService();
        
        listeMachine = service.searchMachinesBySiteDesi(cleanSite, cleanDesi);
        
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

	public String getDesi() {
		return desi;
	}

	public void setDesi(String desi) {
		this.desi = desi;
	}

	public List<Machine> getListeMachine() {
		return listeMachine;
	}

	public void setListeMachine(List<Machine> listeMachine) {
		this.listeMachine = listeMachine;
	}
}

