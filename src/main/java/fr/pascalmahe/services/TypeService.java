package fr.pascalmahe.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.business.Type;
import fr.pascalmahe.persistence.GenericDao;

public class TypeService {

	private static final Logger logger = LogManager.getLogger();

	public static final String AUTO_DEBIT_SHORT = "Prelevmnt";
	
	public static final String AUTO_DEBIT_LONG = "Prelevement"; 
	
	public static final String ATM = "Retrait Au Distributeur";
	
	public static final String CCARD_PAYMENT = "Paiement Par Carte";

	public static final String CHARGES = "Frais";

	public static final String CHECK = "Cheque";
	
	public static final String CREDIT = "Avoir";
	
	public static final String FEE = "Cotisation";
	
	public static final String LOAN_PAYMENT = "Remboursement De Pret";
	
	public static final String PAYMENT = "Reglement";
	
	public static final String TRANSFER = "Virement";
	
	public static final String TRANSFER_IN_UR_FAVOR = "Virement En Votre Faveur";
	
	
	/**
	 * Returns the type based on the content of the label
	 * @param detailedLabel
	 * @return
	 */
	public static Type fromDetailedLabel(String detailedLabel){
		
		String detectedTypeName;
		if(detailedLabel.contains(AUTO_DEBIT_LONG)){
			detectedTypeName = AUTO_DEBIT_LONG;
		} else if(detailedLabel.contains(AUTO_DEBIT_SHORT)){
			detectedTypeName = AUTO_DEBIT_LONG;
		} else if(detailedLabel.contains(ATM)){
			detectedTypeName = ATM;
		} else if(detailedLabel.contains(CCARD_PAYMENT)){
			detectedTypeName = CCARD_PAYMENT;
		} else if(detailedLabel.contains(CHARGES)){
			detectedTypeName = CHARGES;
		} else if(detailedLabel.contains(CHECK)){
			detectedTypeName = CHECK;
		} else if(detailedLabel.contains(CREDIT)){
			detectedTypeName = CREDIT;
		} else if(detailedLabel.contains(FEE)){
			detectedTypeName = FEE;
		} else if(detailedLabel.contains(LOAN_PAYMENT)){
			detectedTypeName = LOAN_PAYMENT;
		} else if(detailedLabel.contains(PAYMENT)){
			detectedTypeName = PAYMENT;
		
		} else {
			detectedTypeName = TRANSFER;
		}
		
//		logger.debug("fromDetailedLabel - found name: " + detectedTypeName);
		
		GenericDao<Type> typeDao = new GenericDao<>(Type.class);
		
		Map<String, Object> mapOfCriteria = new HashMap<>();
		mapOfCriteria.put("name", detectedTypeName);
		
		List<Type> returnedList = typeDao.search(mapOfCriteria);
		
		Type toReturn;
		if(returnedList.isEmpty()){
//			logger.debug("fromDetailedLabel - no Type found. Creating it...");
			toReturn = new Type(detectedTypeName);
			typeDao.saveOrUpdate(toReturn);
		} else {
//			logger.debug("fromDetailedLabel - returning fetched Type fetched from DB...");
			toReturn = returnedList.get(0);
		}
		
		return toReturn;
	}


	public static List<Type> getTypeList() {

		logger.info("Fetching all types (ordered by name)...");
		
		List<Type> allTypeList;
		GenericDao<Type> typeDao = new GenericDao<>(Type.class);
		
		allTypeList = typeDao.fetchNameOrdered();
		
		logger.info("Fetched " + allTypeList.size() + " types...");
		return allTypeList;
	}

}
