package fr.pascalmahe.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Line;
import fr.pascalmahe.persistence.GenericDao;

public class SearchService {

	private static final Logger logger = LogManager.getLogger();
	
	public List<Line> searchLinesBySiteDesi(String site, String desi){
		
		logger.info("Searching lines - on : {site = '" + site + "', desi = '" + desi + "'}");
		
		GenericDao<Line> dao = new GenericDao<>(Line.class);
		Map<String, Object> mapOfCriteria = new HashMap<>();
		mapOfCriteria.put("mainLabel", desi);
		mapOfCriteria.put("category", site);
		List<Line> listLines = dao.search(mapOfCriteria);
		
		logger.info("Searching lines - returning " + listLines.size() + " result(s).");
		
		if(listLines.size() == 0){
			Line newLine = new Line(null, 
									LocalDate.now(), 
									"test d'insertion", 
									"Oh ça c'est du bon test, ça, madame", 
									15.2654f, 
									false, 
									null);
			
			dao.saveOrUpdate(newLine);
			listLines = dao.search(mapOfCriteria);
			
			logger.info("Searching lines - ACTUALLY, returning " + listLines.size() + " result(s).");
		}
		
		return listLines;
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
