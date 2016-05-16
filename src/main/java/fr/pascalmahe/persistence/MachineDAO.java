package fr.pascalmahe.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
		// TODO: real DAO
		List<Machine> listeMachine = new ArrayList<>();
		
		for(Machine machCourante : listeMachineEnMemoire){
			String siteMachCourante = machCourante.getCodeSite();
			String desiMachCourante = machCourante.getDesi();
			boolean isRightSite = false;
			boolean isRightDesi = false;
			if(StringUtils.isNotBlank(siteMachCourante)){
				siteMachCourante = siteMachCourante.toLowerCase();
				if(siteMachCourante.indexOf(site.toLowerCase()) > -1){
					isRightSite = true;
				}
			}
			if(StringUtils.isNotBlank(desiMachCourante)){
				desiMachCourante = desiMachCourante.toLowerCase();
				if(desiMachCourante.indexOf(desi.toLowerCase()) > -1){
					isRightDesi = true;
				}
			}
			
			if(isRightSite && isRightDesi){
				listeMachine.add(machCourante);
			}
		}
		
		return listeMachine;
	}

	public static List<Machine> searchExactlyBySiteCode(String site, String code) {
		// BOUCHON !
		// TODO: real DAO
		List<Machine> listeMachine = new ArrayList<>();
		
		for(Machine machCourante : listeMachineEnMemoire){
			String siteMachCourante = machCourante.getCodeSite();
			String codeMachCourante = machCourante.getCode();
			boolean isRightSite = false;
			boolean isRightCode = false;
			if(StringUtils.isNotBlank(siteMachCourante) && 
					siteMachCourante.equalsIgnoreCase(site)){
				isRightSite = true;
			}
			if(StringUtils.isNotBlank(codeMachCourante) && 
					codeMachCourante.equalsIgnoreCase(code)){
				isRightCode = true;
			}
			
			if(isRightSite && isRightCode){
				listeMachine.add(machCourante);
			}
		}
		
		return listeMachine;
	}

	
	public static boolean saveOrUpdate(Machine machineASauvegarder) {
		// BOUCHON !
		
		List<Machine> listeMachineExistante = 
				searchExactlyBySiteCode(machineASauvegarder.getCodeSite(), 
										machineASauvegarder.getCode());
		listeMachineEnMemoire.removeAll(listeMachineExistante);
		listeMachineEnMemoire.add(machineASauvegarder);
		return true;
	}

}
