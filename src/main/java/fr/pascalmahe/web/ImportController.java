package fr.pascalmahe.web;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.ex.MalformedTextException;
import fr.pascalmahe.services.BulkImportService;
import fr.pascalmahe.web.beans.BulkImportResult;


@ManagedBean
public class ImportController implements Serializable {

	/*
	 * Variables statiques
	 */
	private static final long serialVersionUID = 4352913799147551481L;

	private static final Logger logger = LogManager.getLogger();

	/*
	 * Variables d'instance
	 */
	private String text;
	
	/*
	 * Constructeurs
	 */
	public ImportController(){
		logger.debug("constructor");
	}
	
	/*
	 * Méthodes
	 */
	public void importAction(){
		logger.debug("importAction - start");
		
		String shortMessage;
		Severity msgSeverity = FacesMessage.SEVERITY_INFO;
		
		if(text != null && StringUtils.isNotBlank(text)){

			logger.debug("importAction - text is not empty, passing it on...");
			
			BulkImportResult result = null;
			try {
				result = BulkImportService.consume(text);
				
				shortMessage = "Sur les " + result.getNbLinesDetected() + " lignes detectées, " + 
								result.getNbLinesCreated() + " ont été insérées, " + 
								result.getNbLinesSkipped() + " étaient déjà présentes, " +
								result.getNbLinesMalformed() + " étaient malformées et " + 
								result.getNbCategorysCreated() + " catégories ont été crées (et " + 
								result.getNbCategorysSkipped() + " étaient déjà présentes).";
				
				logger.debug("importAction - text imported: on the " + result.getNbLinesDetected() + 
						" lines it contained, " + 
						result.getNbLinesCreated() + " were inserted, " + 
						result.getNbLinesSkipped() + " skipped, " + 
						result.getNbLinesMalformed() + " malformed and " + 
						result.getNbCategorysCreated()+ " categories were created (and " + 
						result.getNbCategorysSkipped() + " skipped).");
				
			} catch (MalformedTextException e) {
				logger.error("Error while consuming text: " + e.getMessage(), e);
				msgSeverity = FacesMessage.SEVERITY_ERROR;
				shortMessage = "Erreur à l'import, le texte ne comporte pas de "
						+ "date au format JJ/MM/AAAA, necéssaire pour délimiter les lignes,"
						+ " et ne peut donc pas être importé.";
			}
			
		} else {
			shortMessage = "Rien à importer.";			
		}
		
		FacesMessage fmInfoBulkImport = new FacesMessage(msgSeverity, 
														shortMessage, 
														"");
		FacesContext.getCurrentInstance().addMessage("wrong_login_pwd", fmInfoBulkImport);
		
		logger.debug("importAction - end");
	}


	/*
	 * Getters et Setters
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

