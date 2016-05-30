package fr.pascalmahe.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Line;
import fr.pascalmahe.persistence.GenericDao;

public class EditService {

	private static final Logger logger = LogManager.getLogger();
	
	public boolean editLine(String site, String desi, String code){
		
		boolean success = false;
		logger.info("Edition d'une ligne");
		
		// exemple de test métier : les champs ne doivent pas être vides
		if(StringUtils.isNotEmpty(site) && 
				StringUtils.isNotEmpty(desi) && 
				StringUtils.isNotEmpty(code)){
			
			// constitution de l'objet métier
			Line ligneASauvegarder = new Line();
			
			// appel du DAO approprié
			success = GenericDao.saveOrUpdate(ligneASauvegarder);
		}
		
		if(success){
			logger.info("Edition d'une ligne - réussite.");
		} else {
			logger.info("Edition d'une ligne - échec.");
		}
		
		return success;
	}
	
}
