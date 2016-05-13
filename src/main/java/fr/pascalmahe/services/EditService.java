package fr.pascalmahe.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Machine;
import fr.pascalmahe.persistence.MachineDAO;

public class EditService {

	private static final Logger logger = LogManager.getLogger();
	
	public boolean editMachine(String site, String desi, String code){
		
		boolean success = false;
		logger.info("Edition d'une machine");
		
		// exemple de test métier : les champs ne doivent pas être vides
		if(StringUtils.isNotEmpty(site) && 
				StringUtils.isNotEmpty(desi) && 
				StringUtils.isNotEmpty(code)){
			
			// constitution de l'objet métier
			Machine machineASauvegarder = new Machine(desi, code, site);
			
			// appel du DAO approprié
			success = MachineDAO.saveOrUpdate(machineASauvegarder);
		}
		
		if(success){
			logger.info("Edition d'une machine - réussite.");
		} else {
			logger.info("Edition d'une machine - échec.");
		}
		
		return success;
	}
	
}
