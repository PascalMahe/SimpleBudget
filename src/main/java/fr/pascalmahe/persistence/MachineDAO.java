package fr.pascalmahe.persistence;

import java.util.ArrayList;
import java.util.List;

import fr.pascalmahe.business.Machine;

public class MachineDAO {

	// bouchon pour simuler les recherches et les créations
	private static List<Machine> listeMachineEnMemoire = new ArrayList<>();
	
	// bouchon pour alimenter la liste des machines en mémoire au 
	// démarrage de l'application
	// bloc statique
	static {
		listeMachineEnMemoire.add(new Machine("Bouchon", "BOU01", "SHA"));
		listeMachineEnMemoire.add(new Machine("Bouchon", "BOU02", "LAV"));
		listeMachineEnMemoire.add(new Machine("Bouchon", "BOU03", "LON"));
		listeMachineEnMemoire.add(new Machine("Encapsuleuse", "ENCAP01", "SHA"));
		listeMachineEnMemoire.add(new Machine("Encapsuleuse", "ENCAP02", "LON"));
		listeMachineEnMemoire.add(new Machine("Moule à bouteille", "MOUB03", "LAV"));
		listeMachineEnMemoire.add(new Machine("Four à pain", "FOU03", "LAV"));
		listeMachineEnMemoire.add(new Machine("Moule à gaufre", "MOUG03", "LAV"));
		
		
	}
	
	public static List<Machine> searchBySiteDesi(String site, String desi) {
		// BOUCHON !
		
		// S'appuiera sur la bibliothèque QuartisProMetier
		// quand elle existera...
		
		List<Machine> listeMachine = new ArrayList<>();
		
		
		
		return listeMachine;
	}

	public static boolean saveOrUpdate(Machine machineASauvegarder) {
		// BOUCHON !
		
		// S'appuiera sur la bibliothèque QuartisProMetier
		// quand elle existera
		
		
		
		return false;
	}

}
