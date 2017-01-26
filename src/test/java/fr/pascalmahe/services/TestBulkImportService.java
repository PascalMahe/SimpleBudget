package fr.pascalmahe.services;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Account;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.Type;
import fr.pascalmahe.ex.MalformedTextException;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.testUtil.AbstractTest;
import fr.pascalmahe.testUtil.Validator;
import fr.pascalmahe.web.beans.BulkImportResult;

public class TestBulkImportService extends AbstractTest {

	private static final Logger logger = LogManager.getLogger();
	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		preTestDatabaseCheckup();
			
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");

		cleanUpDatabase();
		logger.info("afterClass finished.");
	}

	@Test
	public void testArrayOfLineAsStrToLineList(){
		logger.info("Starting testArrayOfLineAsStrToLineList...");
			
		String[] arrayOfLineAsStr = {"2016-04-28,"
									+ "\"Mma Sa Iard Virt. Des Salaires 0 \n" + 
										" Virement En Votre Faveur\n" +
										" Virement Salaire\n" +
										" Virt. Des Salaires 0043591\","
									+ ","
									+ "\"2 347,09 €\","
									+ "Salaires,"
									+ ","
									+ ","
									+ ","
									+ ","
									+ "\n", 
									"2016-04-28,"
									+ "\"Monoprix 2301 Leman2045223 27/04\n" + 
										" Paiement Par Carte\","
									+ "\"55,07€\","
									+ ","
									+ "Courses,"
									+ "Quotidiennes,"
									+ ","
									+ ","
									+ ","
									+ "\n",
									"2016-04-27,"
									+ "\"Le Mans St Pavin 27/04 18h20\n" + 
										" Retrait Au Distributeur\","
									+ "\"20,00€\","
									+ ","
									+ "Courses,"
									+ "Retraits,"
									+ ","
									+ ","
									+ ","
									+ "\n",
									"2016-02-29,"
									+ "\"Du Bruit Dans La Cu Le Man 26/02\n" +
										" Paiement Par Carte\","
									+ "\"10,50€\","
									+ ","
									+ "Meubles & Déco,"
									+ "Cuisine,"
									+ ","
									+ ","
									+ ","
									+ "",
									"2016-04-01,"
									+ "\"Int Deb 1 Trim 16 Taeg=17,62%\n" +
										" Frais\","
									+ "\"3,11€\","
									+ ","
									+ "Banque,"
									+ "Intérêts,"
									+ ","
									+ ","
									+ ","
									+ ""};
		
		BulkImportResult result = new BulkImportResult();
		result = BulkImportService.
				arrayOfLineAsStrToLineList(arrayOfLineAsStr, result);
		
		Line line0 = new Line();
		line0.setDetailedLabel("Mma Sa Iard Virt. Des Salaires 0 \n" + 
								" Virement En Votre Faveur\n" +
								" Virement Salaire\n" +
								" Virt. Des Salaires 0043591");
		line0.setShortLabel("Mma Sa Iard Virt. Des Salaires 0" + 
								" Salaire" + 
								" Virt. Des Salaires 0043591");
		line0.setDate(LocalDate.of(2016, 04, 28));
		line0.setType(TypeService.fromDetailedLabel(line0.getDetailedLabel()));
		line0.setAmount(2347.09f);
		line0.setNote("");
		line0.addCategorisation(2347.09f, "Salaires");
		
		Line line1 = new Line();
		line1.setDetailedLabel("Monoprix 2301 Leman2045223 27/04\n" + 
								" Paiement Par Carte");
		line1.setShortLabel("Monoprix 2301");
		line1.setDate(LocalDate.of(2016, 04, 28));
		line1.setCCardDate(LocalDate.of(2016, 04, 27));
		line1.setType(TypeService.fromDetailedLabel(line1.getDetailedLabel()));
		line1.setAmount(-55.07f);
		line1.setNote("");
		line1.addCategorisation(-55.07f, "Courses", "Quotidiennes");
		
		Line line2 = new Line();
		line2.setDetailedLabel("Le Mans St Pavin 27/04 18h20\n" + 
								" Retrait Au Distributeur");
		line2.setShortLabel("Le Mans St Pavin");
		line2.setDate(LocalDate.of(2016, 04, 27));
		line2.setType(TypeService.fromDetailedLabel(line2.getDetailedLabel()));
		line2.setAmount(-20.00f);
		line2.setNote("");
		line2.addCategorisation(-20.00f, "Courses", "Retraits");

		Line line3 = new Line();
		line3.setDetailedLabel("Du Bruit Dans La Cu Le Man 26/02\n" +
								" Paiement Par Carte");
		line3.setShortLabel("Du Bruit Dans La Cu Le Man");
		line3.setDate(LocalDate.of(2016, 02, 29));
		line3.setCCardDate(LocalDate.of(2016, 02, 26));
		line3.setType(TypeService.fromDetailedLabel(line3.getDetailedLabel()));
		line3.setAmount(-10.50f);
		line3.setNote("");
		line3.addCategorisation(-10.50f, "Meubles & Déco", "Cuisine");

		Line line4 = new Line();
		line4.setDetailedLabel("Int Deb 1 Trim 16 Taeg=17,62%\n" +
								" Frais");
		line4.setShortLabel("Int Deb 1 Trim 16");
		line4.setDate(LocalDate.of(2016, 04, 01));
		line4.setType(TypeService.fromDetailedLabel(line4.getDetailedLabel()));
		line4.setAmount(-3.11f);
		line4.setNote("");
		line4.addCategorisation(-3.11f, "Banque", "Intérêts");

		List<Line> expectedList = new ArrayList<>();
		expectedList.add(line0);
		expectedList.add(line1);
		expectedList.add(line2);
		expectedList.add(line3);
		expectedList.add(line4);
		
		List<Line> actualList = result.getLineList();
		
		assertThat(actualList.size(), is(5));
		Validator.validateLine("#0 of testArrayOfLineAsStrToLineList", expectedList.get(0), actualList.get(0));
		Validator.validateLine("#1 of testArrayOfLineAsStrToLineList", expectedList.get(1), actualList.get(1));
		Validator.validateLine("#2 of testArrayOfLineAsStrToLineList", expectedList.get(2), actualList.get(2));
		Validator.validateLine("#3 of testArrayOfLineAsStrToLineList", expectedList.get(3), actualList.get(3));
		Validator.validateLine("#4 of testArrayOfLineAsStrToLineList", expectedList.get(4), actualList.get(4));
		
		logger.info("testArrayOfLineAsStrToLineList finished.");
	}
	
	@Test
	public void testInsertLineList(){
		logger.info("Starting testInsertLineList...");
		
		List<Line> listToInsert = new ArrayList<>();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS");
		
		LocalDateTime dateForCatDiff = LocalDateTime.now();
		
		String categoryDifferentiator = dateForCatDiff.format(formatter);
		String fatCatName = "TestFatherCategory" + categoryDifferentiator;
		Category fatherCategory = new Category(fatCatName);
		Category category = new Category("TestCategory" + categoryDifferentiator, fatherCategory);
		Category otherCategory = new Category("OtherTestCategory" + categoryDifferentiator, fatherCategory);
		
		listToDelete.add(fatherCategory);
		listToDelete.add(category);
		listToDelete.add(otherCategory);
		
		for(int i = 0; i < 12; i++){
			List<Categorisation> categoList = new ArrayList<Categorisation>();
			
			LocalDateTime dateTime = LocalDateTime.now();
			String differentiator = dateTime.format(formatter);
			
			LocalDate date = LocalDate.now();
			LocalDate cCardDate = LocalDate.of(2016, Month.NOVEMBER, 17);
			String detailedLabel = "testInsertLineList" + differentiator;
			String shortLabel = "testInsertLineList" + differentiator;
			String note = "testInsertLineList" + differentiator;
			float amount = 7357.00f;
			boolean recurring = false;
			Type type = TypeService.fromDetailedLabel("Frais");
			
			Account acc = Account.fromName(Account.NAME_CA);
			
			// choose category on oddness of i
			boolean isOdd = i % 2 == 0;
			if(isOdd){
				categoList.add(new Categorisation(amount, category));
			} else {
				categoList.add(new Categorisation(amount, otherCategory));
			}
			
			
			Line currentLine = new Line(date, 
					cCardDate,
					detailedLabel, 
					shortLabel,
					note, 
					amount, 
					recurring,
					type,
					acc,
					categoList);
			
			listToInsert.add(currentLine);
			
			listToDelete.add(currentLine);
		}

		BulkImportResult insertionResult = BulkImportService.insertLineList(listToInsert, new BulkImportResult());
		assertThat(insertionResult.getNbLinesCreated(), is(12));
		assertThat(insertionResult.getNbCategorysCreated(), is(3));
		
		logger.info("testInsertLineList finished.");
	}
	
	@Test
	public void testConsume(){
		logger.info("Starting testConsume...");
		
		GenericDao<Line> linDao = new GenericDao<>(Line.class);
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		int nbOfLinesBeforeConsumption = linDao.count();
		int nbOfCategorysBeforeConsumption = catDao.count();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss.SSS");
		
		LocalDateTime dateForCatDiff = LocalDateTime.now();
		
		String differentiator = "Test" + dateForCatDiff.format(formatter);
		
		String rawTextToInsert = "2016-07-11,"
									+ "\"Prelevmnt \n" + 
										" Edf Clients Particuliers Numero De Client : 6002643630 - Num\n" + 
										" Ero De Compte : Xxx 004013118985 \n"+
										" Fr47edf001007 \n"+
										" Mm9760026436300001\"" + differentiator + ","
									+ "\"187,96 €\","
									+ ","
									+ "Factures" + differentiator + ","
									+ "Électricité" + differentiator + ","
									+ "Note de test,"
									+ ","
									+ ","
									+ "\n"+
								"2016-07-11,"
									+ "\"Prelevmnt \n"+
										" Setram Illico \n"+
										" Fr30zzz461772 \n"+
										" Titan--900021952\"" + differentiator + ","
									+ "\"31,80 €\","
									+ ","
									+ "Transports en commun" + differentiator + ","
									+ ","
									+ ","
									+ ","
									+ ","
									+ "\n"+
								"2016-07-11,"
									+ "\"Retrait Au Distributeur \n" +
										" Le Mans Republiq 10/07 09h53\"" + differentiator + ","
									+ "\"20,00 €\","
									+ ","
									+ "Courses" + differentiator + ","
									+ "Retrait" + differentiator + ","
									+ ","
									+ "Transports en commun" + differentiator + ","
									+ ","
									+ "\"10,00 €\"\n"+
								"2016-07-11,"
									+ "\"Paiement Par Carte \n" +
										" Pathe Le Mans 10/07\"" + differentiator + ","
									+ "\"11,30 €\","
									+ ","
									+ "Loisirs" + differentiator + ","
									+ "Sortie" + differentiator + ","
									+ ","
									+ ","
									+ ","
									+ "\n" + 
								"2016-07-11," // same as the one before, should be skipped
									+ "\"Paiement Par Carte \n" +
										" Pathe Le Mans 10/07\"" + differentiator + ","
									+ "\"11,30 €\","
									+ ","
									+ "Loisirs" + differentiator + ","
									+ "Sortie" + differentiator + ","
									+ ","
									+ ","
									+ ","
									+ "\n" + 
								"2016-07-15," // category should be skipped
									+ "\"Prelevmnt \n" + 
										" Setram Setram Pass Liberte 120427020140519 \n" + 
										" 1027500003 : Echeance Du 15/07/2016 \n" +
										" Fr30zzz461772 \n" +
										" 1204270201405191027500003\"" + differentiator + ","
									+ "\"7,74 €\","
									+ ","
									+ "Dejeuners" + differentiator + ","
									+ ","
									+ ","
									+ ","
									+ ","
									+ "\n";
		
		BulkImportResult returned = null;
		try {
			returned = BulkImportService.consume(rawTextToInsert);
		} catch (MalformedTextException e) {
			logger.error("Exception occurred in testConsume: " + e.getLocalizedMessage());
			fail(e.getMessage());
		}
		
		int nbOfLinesAfterConsumption = linDao.count();
		int nbOfCategorysAfterConsumption = catDao.count();
		
		int nbOfLinesAdded = nbOfLinesAfterConsumption - nbOfLinesBeforeConsumption;
		int nbOfCategorysAdded = nbOfCategorysAfterConsumption - nbOfCategorysBeforeConsumption;
		
		int expectedNbOfCorrectlyFormedLines = 6;
		int expectedNbOfLinesAdded = 5;
		int expectedNbOfLinesSkipped = 1;
		int expectedNbOfCategorysAdded = 8; // NB: father categories count for 1
		int expectedNbOfCategorysSkipped = 1;
		
		assertThat("Number of lines detected", 
				returned.getNbLinesDetected(), 
				is(expectedNbOfCorrectlyFormedLines));
		
		assertThat("Number of lines created", 
				returned.getNbLinesCreated(), 
				is(expectedNbOfLinesAdded));
		
		assertThat("Number of categories added", 
				returned.getNbCategorysCreated(), 
				is(expectedNbOfCategorysAdded));
		
		assertThat("Number of lines skipped", 
				returned.getNbLinesSkipped(), 
				is(expectedNbOfLinesSkipped));
		
		assertThat("Number of categories skipped", 
				returned.getNbCategorysSkipped(), 
				is(expectedNbOfCategorysSkipped));
		
		assertThat("Number of lines created (compared w/ DB)", 
				returned.getNbLinesCreated(), 
				is(nbOfLinesAdded));
		
		assertThat("Number of categories detected (compared w/ DB)", 
				returned.getNbCategorysCreated(), 
				is(nbOfCategorysAdded));
		
		
		// Prep for clean up...
		Map<String, Object> searchCriteriaCat = new HashMap<>();
		searchCriteriaCat.put("name", "%" + differentiator);
		List<Category> catToDelete = catDao.search(searchCriteriaCat);
		logger.debug("testConsume - found " + catToDelete.size() + " categorys "
				+ "with the differentiator: " + differentiator + ". "
				+ "(" + nbOfCategorysAdded + " were added.)");
		assertThat(catToDelete.size(), is(nbOfCategorysAdded));
		
		listToDelete.addAll(catToDelete);
		
		Map<String, Object> searchCriteriaLin = new HashMap<>();
		searchCriteriaLin.put("detailedLabel", "%" + differentiator);
		List<Line> linToDelete = linDao.search(searchCriteriaLin);
		assertThat(linToDelete.size(), is(nbOfLinesAdded));
		
		listToDelete.addAll(linToDelete);
		
		logger.info("testConsume finished.");
	}
	
	@Test
	public void testErrorOnFatherCategoryInBulkImport(){
		logger.info("Starting testErrorOnFatherCategoryInBulkImport...");
		
		// Reproduces an error only seen from the browser:
		// inserting two lines with the same father category
		// causes a TransientObjectException in BulkImportService.saveIfNotExists
		// What has been tried:
		//	- catching the exception and replacing the father category with one fetched
		//		dynamically from the DB -> causes a StaleStateException in
		//		GenericDao.saveOrUpdate
		
		Category fatCat = new Category("Courses");
		listToDelete.add(fatCat);
		listToDelete.add(new Category("Quotidiennes", fatCat));
		listToDelete.add(new Category("Retrait", fatCat));
		
		String linesToImport = "2016-02-04,\"Monoprix 2301 Leman2045223 03/02" +
								" Paiement Par Carte\",\"57,87€\",,Courses,Quotidiennes,,,," +
								"2016-02-02,\"Le Mans Novaxis 02/02 12h09" +
								" Retrait Au Distributeur\",\"40,00€\",,Courses,Retrait,,,,";
		
		try {
			// test
			BulkImportResult bir = BulkImportService.consume(linesToImport);
			
			// preparing objects to delete BEFORE the validation
			// otherwise it might be skipped
			GenericDao<Line> linDao = new GenericDao<>(Line.class);
			
			Map<String, Object> searchCriteria = new HashMap<>();
			searchCriteria.put("date", LocalDate.of(2016, 02, 04));
			searchCriteria.put("amount", -57.87f);
			searchCriteria.put("detailedLabel", "%Monoprix%");
			
			List<Line> results = linDao.search(searchCriteria);
			listToDelete.add(results.get(0));
			
			searchCriteria = new HashMap<>();
			searchCriteria.put("date", LocalDate.of(2016, 02, 02));
			searchCriteria.put("amount", -40.00f);
			searchCriteria.put("detailedLabel", "%Le Mans Novaxis%");
			
			results = linDao.search(searchCriteria);
			if(!results.isEmpty()){
				listToDelete.add(results.get(0));
			}
			
			GenericDao<Category> catDao = new GenericDao<>(Category.class);
			Category coursesCat = catDao.fetchByName("Courses");
			if(coursesCat == null){
				fail("Category 'Courses' is null, it shouldn't be.");
			} else {
				listToDelete.add(coursesCat);
			}

			Category quoCat = catDao.fetchByName("Quotidiennes");
			if(quoCat == null){
				fail("Category 'Quotidiennes' is null, it shouldn't be.");
			} else {
				listToDelete.add(quoCat);
			}
			
			Category retCat = catDao.fetchByName("Retrait");
			if(retCat == null){
				logger.warn("Category 'Retrait' is null, it shouldn't be.");
			} else {
				listToDelete.add(retCat);
			}
			
			// validation : will only pass when the problem is fixed!
			assertThat("Wrong number of Lines created: ", bir.getNbLinesCreated(), is(2));
			assertThat("Wrong number of malformed Lines: ", bir.getNbLinesMalformed(), is(0));
			
		} catch (MalformedTextException e) {
			logger.error(e);
			fail(e.getLocalizedMessage());
		}
		
		logger.info("testErrorOnFatherCategoryInBulkImport finished.");
	}
	
}