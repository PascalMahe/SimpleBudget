package fr.pascalmahe.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Machine;
import fr.pascalmahe.persistence.MachineDAO;

public class SearchService {

	private static final Logger logger = LogManager.getLogger();
	
	public List<Machine> searchMachinesBySiteDesi(String site, String desi){
		
		logger.info("Recherche des machines - critères : {site = " + site + ", desi = " + desi + "}");
		
		List<Machine> listeMachine = MachineDAO.searchBySiteDesi(site, desi);
		
		logger.info("Recherche des machines - retour de " + listeMachine.size() + " résultat(s).");
		return listeMachine;
	}

	public List<String> findSites(String site) {
		
		logger.info("Recherche des site - critères : {site = " + site + "}");
		
		// /!\ Attention ! 
		// Ce passage (la recherche de site) devrait appeler un DAO approprié
		// mais pour alléger le code (en ne créant pas un bouchon de DAO 
		// supplémentaire), il est ici
		List<String> listeSites = new ArrayList<>();
		if(site.length() == 1){
			String premiereLettre = site.substring(0, 1);
			
			if(premiereLettre.equalsIgnoreCase("l")){
				listeSites.add("LAV");
				listeSites.add("LON");
			} else if(premiereLettre.equalsIgnoreCase("s")){
				listeSites.add("SHA");
			}
		}
		
		if(site.length() == 2){
			String premieresLettres = site.substring(0, 2);
			if(premieresLettres.equalsIgnoreCase("la")){
				listeSites.add("LAV");
			} else if(premieresLettres.equalsIgnoreCase("sh")){
				listeSites.add("SHA");
			} else if(premieresLettres.equalsIgnoreCase("lo")){
				listeSites.add("LON");
			}
		}
		
		logger.info("Recherche des site - retour de " + listeSites.size() + " résultat(s).");
		
		return listeSites;
	}
	
}
