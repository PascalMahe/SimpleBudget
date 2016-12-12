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

	/**
	 * Returns the type based on the content of the label
	 * @param detailedLabel
	 * @return
	 */
	public static Type fromDetailedLabel(String detailedLabel){
		
		String detectedTypeName;
		if(detailedLabel.contains(Type.AUTO_DEBIT_LONG)){
			detectedTypeName = Type.AUTO_DEBIT_LONG;
		} else if(detailedLabel.contains(Type.AUTO_DEBIT_SHORT)){
			detectedTypeName = Type.AUTO_DEBIT_LONG;
		} else if(detailedLabel.contains(Type.ATM)){
			detectedTypeName = Type.ATM;
		} else if(detailedLabel.contains(Type.CCARD_PAYMENT)){
			detectedTypeName = Type.CCARD_PAYMENT;
		} else if(detailedLabel.contains(Type.CHARGES)){
			detectedTypeName = Type.CHARGES;
		} else if(detailedLabel.contains(Type.CHECK)){
			detectedTypeName = Type.CHECK;
		} else if(detailedLabel.contains(Type.CREDIT)){
			detectedTypeName = Type.CREDIT;
		} else if(detailedLabel.contains(Type.FEE)){
			detectedTypeName = Type.FEE;
		} else if(detailedLabel.contains(Type.LOAN_PAYMENT)){
			detectedTypeName = Type.LOAN_PAYMENT;
		} else if(detailedLabel.contains(Type.PAYMENT)){
			detectedTypeName = Type.PAYMENT;
		
		} else {
			detectedTypeName = Type.TRANSFER;
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
