package fr.pascalmahe.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Account;
import fr.pascalmahe.business.Categorisation;
import fr.pascalmahe.business.Category;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.business.MonthInYear;
import fr.pascalmahe.business.Type;
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.testUtil.AbstractTest;
import fr.pascalmahe.testUtil.Validator;
import fr.pascalmahe.web.beans.CatRow;
import fr.pascalmahe.web.beans.MonthCell;

public class TestCategoryService extends AbstractTest {

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
	public void testFetchFirstRankCategoryList() {
		logger.info("Starting testFetchFirstRankCategoryList...");
		
		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		
		List<Category> listAllCat = catDao.fetchAll();
		
		List<Category> listFirstRankCatExpected = new ArrayList<>();
		
		for(Category cat : listAllCat){
			if(cat.getFatherCategory() == null){
				listFirstRankCatExpected.add(cat);
			}
		}
		
		List<Category> listFirstRankCatActual = 
				CategoryService.fetchFirstRankCategoryList();
		
		assertThat(listFirstRankCatActual.size(), 
				is(listFirstRankCatExpected.size()));
		
		for(Category cat : listFirstRankCatExpected){
			assertThat(listFirstRankCatActual, hasItem(cat));
		}
		
//		logger.debug("testFetchFirstRankCategoryList - listExpected : " + listFirstRankCatExpected);
//		
//		logger.debug("testFetchFirstRankCategoryList - listActual : " + listFirstRankCatActual);
		
		logger.info("testFetchFirstRankCategoryList finished.");
	}

	@Test
	public void testFetchSecondRankCategoryList() {
		logger.info("Starting testFetchSecondRankCategoryList...");
		
		List<Category> listAllCat = null;
		try {
			GenericDao<Category> catDao = new GenericDao<>(Category.class);
			listAllCat = catDao.fetchAll();
		} catch (Exception e){
			logger.error("", e);
		}
		
		
		List<Category> listSecondRankCatExpected = new ArrayList<>();
		
		for(Category cat : listAllCat){
			if(cat.getFatherCategory() != null){
				listSecondRankCatExpected.add(cat);
			}
		}
		
		List<Category> listSecondRankCatActual = 
				CategoryService.fetchSecondRankCategoryList();
		
		assertThat(listSecondRankCatActual.size(), 
				is(listSecondRankCatExpected.size()));
		
		for(Category cat : listSecondRankCatExpected){
			assertThat(listSecondRankCatActual, hasItem(cat));
			assertThat(cat.getFatherCategory(), notNullValue());
			logger.debug("testFetchSecondRankCategoryList - category " + cat.getName() + 
					" has for fatherCategory : " + cat.getFatherCategory().getName() + 
					" (cat#" + cat.getId() + ")");
		}

//		logger.debug("testFetchSecondRankCategoryList - listExpected : " + listSecondRankCatExpected);
//		
//		logger.debug("testFetchSecondRankCategoryList - listActual : " + listSecondRankCatActual);

		logger.info("testFetchSecondRankCategoryList finished.");
	}
	
	@Test
	public void testFetchCategoryTable(){
		logger.info("Starting testFetchCategoryTable...");
		
		logger.debug("testFetchCategoryTable - inserting data...");
		
		Type ccard = TypeService.fromDetailedLabel(Type.CCARD_PAYMENT);
		
		String armure = "Armure";
		String armes = "Armes";
		String cheval = "Cheval";
		String hommesDArmes = "Hommes d'armes";
		String chateau = "Château";
		String siege = "Sièges";
		String melee = "Mêlée";
		String aDistance = "A distance";
		
		Category cat1 = new Category(armure);
		Category cat2 = new Category(armes);
		Category cat3 = new Category(cheval);
		Category cat4 = new Category(hommesDArmes);
		Category cat5 = new Category(chateau);

		Category cat6 = new Category(siege, cat2);
		Category cat7 = new Category(melee, cat2);
		Category cat8 = new Category(aDistance, cat2);
		
		Account acc = Account.fromName(Account.NAME_LBP);
		
		LocalDate todayMinus5Months = LocalDate.now().minusMonths(5);
		List<Categorisation> categoList1 = new ArrayList<>();
		Categorisation catego10 = new Categorisation(-100f, cat1);
		categoList1.add(catego10);
		Line l1 = new Line(todayMinus5Months, todayMinus5Months.plusDays(3), "Category Table test 1", "Category Table test 1", "", -100f, false, ccard, acc, categoList1);

		LocalDate todayMinus4Months = LocalDate.now().minusMonths(4);
		List<Categorisation> categoList2 = new ArrayList<>();
		Categorisation catego20 = new Categorisation(-100f, cat1);
		categoList2.add(catego20);
		Categorisation catego21 = new Categorisation(-25f, cat6);
		categoList2.add(catego21);
		Categorisation catego22 = new Categorisation(-25f, cat7);
		categoList2.add(catego22);
		Categorisation catego23 = new Categorisation(-50f, cat8);
		categoList2.add(catego23);
		Line l2 = new Line(todayMinus4Months, todayMinus4Months.plusDays(3), "Category Table test 2", "Category Table test 2", "", -200f, false, ccard, acc, categoList2);
		
		LocalDate todayMinus3Months = LocalDate.now().minusMonths(3);
		List<Categorisation> categoList3 = new ArrayList<>();
		Categorisation catego30 = new Categorisation(-100f, cat1);
		categoList3.add(catego30);
		Categorisation catego31 = new Categorisation(-25f, cat6);
		categoList3.add(catego31);
		Categorisation catego32 = new Categorisation(-25f, cat7);
		categoList3.add(catego32);
		Categorisation catego33 = new Categorisation(-50f, cat8);
		categoList3.add(catego33);
		Categorisation catego34 = new Categorisation(-100f, cat3);
		categoList3.add(catego34);
		Line l3 = new Line(todayMinus3Months, todayMinus3Months.plusDays(3), "Category Table test 3", "Category Table test 3", "", -300f, false, ccard, acc, categoList3);

		List<Categorisation> categoList4 = new ArrayList<>();
		Categorisation catego40 = new Categorisation(100f, cat1);
		categoList4.add(catego40);
		Categorisation catego41 = new Categorisation(-25f, cat6);
		categoList4.add(catego41);
		Categorisation catego42 = new Categorisation(-25f, cat7);
		categoList4.add(catego42);
		Categorisation catego43 = new Categorisation(-50f, cat8);
		categoList4.add(catego43);
		Categorisation catego44 = new Categorisation(100f, cat3);
		categoList4.add(catego44);
		Categorisation catego45 = new Categorisation(100f, cat4);
		categoList4.add(catego45);
		Line l4 = new Line(todayMinus3Months, todayMinus3Months.plusDays(3), "Category Table test 4", "Category Table test 4", "", 400f, false, ccard, acc, categoList4);

		List<Categorisation> categoList5 = new ArrayList<>();
		Categorisation catego50 = new Categorisation(-100f, cat1);
		categoList5.add(catego50);
		Categorisation catego51 = new Categorisation(-25f, cat6);
		categoList5.add(catego51);
		Categorisation catego52 = new Categorisation(-25f, cat7);
		categoList5.add(catego52);
		Categorisation catego53 = new Categorisation(-50f, cat8);
		categoList5.add(catego53);
		Categorisation catego54 = new Categorisation(-100f, cat3);
		categoList5.add(catego54);
		Categorisation catego55 = new Categorisation(-100f, cat4);
		categoList5.add(catego55);
		Categorisation catego56 = new Categorisation(-100f, cat5);
		categoList5.add(catego56);
		Line l5 = new Line(todayMinus3Months, todayMinus3Months.plusDays(3), "Category Table test 5", "Category Table test 5", "", -500f, false, ccard, acc, categoList5);
		
		logger.debug("testFetchCategoryTable - all dates used: ");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		logger.debug("todayMinus5Months: " + todayMinus5Months.format(dtf));
		logger.debug("todayMinus5Months + 3 days: " + todayMinus5Months.plusDays(3).format(dtf));
		logger.debug("todayMinus4Months: " + todayMinus4Months.format(dtf));
		logger.debug("todayMinus4Months + 3 days: " + todayMinus4Months.plusDays(3).format(dtf));
		logger.debug("todayMinus3Months: " + todayMinus3Months.format(dtf));
		logger.debug("todayMinus3Months + 3 days: " + todayMinus3Months.plusDays(3).format(dtf));
		
		listToDelete.add(ccard);
		listToDelete.add(cat1);
		listToDelete.add(cat2);
		listToDelete.add(cat3);
		listToDelete.add(cat4);
		listToDelete.add(cat5);
		listToDelete.add(cat6);
		listToDelete.add(cat7);
		listToDelete.add(cat8);
		listToDelete.add(catego10);
		listToDelete.add(catego20);
		listToDelete.add(catego21);
		listToDelete.add(catego22);
		listToDelete.add(catego23);
		listToDelete.add(catego30);
		listToDelete.add(catego31);
		listToDelete.add(catego32);
		listToDelete.add(catego33);
		listToDelete.add(catego34);
		listToDelete.add(catego40);
		listToDelete.add(catego41);
		listToDelete.add(catego42);
		listToDelete.add(catego43);
		listToDelete.add(catego44);
		listToDelete.add(catego45);
		listToDelete.add(catego50);
		listToDelete.add(catego50);
		listToDelete.add(catego51);
		listToDelete.add(catego52);
		listToDelete.add(catego53);
		listToDelete.add(catego54);
		listToDelete.add(catego55);
		listToDelete.add(catego56);
		listToDelete.add(l1);
		listToDelete.add(l2);
		listToDelete.add(l3);
		listToDelete.add(l4);
		listToDelete.add(l5);

		GenericDao<Category> catDao = new GenericDao<>(Category.class);
		catDao.saveOrUpdate(cat1);
		catDao.saveOrUpdate(cat2);
		catDao.saveOrUpdate(cat3);
		catDao.saveOrUpdate(cat4);
		catDao.saveOrUpdate(cat5);
		catDao.saveOrUpdate(cat6);
		catDao.saveOrUpdate(cat7);
		catDao.saveOrUpdate(cat8);
		
		GenericDao<Categorisation> categoDao = new GenericDao<>(Categorisation.class);
		categoDao.saveOrUpdate(catego10);
		categoDao.saveOrUpdate(catego20);
		categoDao.saveOrUpdate(catego21);
		categoDao.saveOrUpdate(catego22);
		categoDao.saveOrUpdate(catego23);
		categoDao.saveOrUpdate(catego30);
		categoDao.saveOrUpdate(catego30);
		categoDao.saveOrUpdate(catego31);
		categoDao.saveOrUpdate(catego32);
		categoDao.saveOrUpdate(catego33);
		categoDao.saveOrUpdate(catego34);
		categoDao.saveOrUpdate(catego40);
		categoDao.saveOrUpdate(catego41);
		categoDao.saveOrUpdate(catego42);
		categoDao.saveOrUpdate(catego43);
		categoDao.saveOrUpdate(catego44);
		categoDao.saveOrUpdate(catego45);
		categoDao.saveOrUpdate(catego50);
		categoDao.saveOrUpdate(catego51);
		categoDao.saveOrUpdate(catego52);
		categoDao.saveOrUpdate(catego53);
		categoDao.saveOrUpdate(catego54);
		categoDao.saveOrUpdate(catego55);
		categoDao.saveOrUpdate(catego56);
		
		GenericDao<Line> linDao = new GenericDao<>(Line.class);
		linDao.saveOrUpdate(l1);
		linDao.saveOrUpdate(l2);
		linDao.saveOrUpdate(l3);
		linDao.saveOrUpdate(l4);
		linDao.saveOrUpdate(l5);
		
		logger.debug("testFetchCategoryTable - data inserted.");
		
		// Expected result (as of 03/01/2017)
		// All values not shown are 0
		// 						-5 months	-4 months	-3 months
		// cat1 Armure			-100		-100		-200 (-100 -100) + 100 
		// 
		// cat2 Armes
		// 	cat6 Sièges						-25			-75 (-25 -25 -25)
		// 	cat7 Mêlée						-25			-75 (-25 -25 -25)
		// 	cat8 A distance					-50			-150 (-50 -50 -50)
		// 
		// cat3 Cheval									-200 (-100 -100) + 100 
		// 
		// cat4 Hommes d'armes							-100 + 100
		// 
		// cat5 Château									-100
		
		List<CatRow> catRowList = CategoryService.fetchCategoryTable();
		
		prettyPrint(catRowList);
		
		int expectedCatNb = 5; // only childless and father categories count 
		assertThat("Number of Categorys in list of CatRows should be " + expectedCatNb + ": ", 
				catRowList.size(), 
				is(expectedCatNb));
		
		for(CatRow catRow : catRowList){
			// only one category with children
			if(!catRow.isChildFree()){
				Category fatCat = catRow.getCategory();
				assertThat("CatRow with Category #" + fatCat.getId() + " (" + fatCat.getName() + ") should be #?(Armes): ",
						fatCat.getName(), is("Armes"));
				
				List<CatRow> catRowSons = catRow.getSonsCatRowList();
				for(CatRow son : catRowSons){
					Category cat = son.getCategory();
					Map<MonthInYear, MonthCell> monthList = son.getMonthList();
					
					// checking number of months
					assertThat("CatRow with Category #" + cat.getId() + " (" + cat.getName() + ") doesn't have 6 months: ",
							son.getNumberOfMonths(),
							is(6));
					
					// checking values
					for(MonthInYear moy : monthList.keySet()){

						String validatorMessage = " test to fetchCategoryTable, category: " + cat.getName() + ", month: " + moy;
						if(cat.getName().equalsIgnoreCase(siege)){
							
							if(moy.getMonth() == todayMinus4Months.getMonth()){
								Validator.validateMonthCell(validatorMessage + " (todayMinus4Months)", 
															new MonthCell(moy, 0f, -25f), 
															monthList.get(moy));
								
							} else if(moy.getMonth() == todayMinus3Months.getMonth()){
								Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
															new MonthCell(moy, 0f, -75f), 
															monthList.get(moy));
							} else {
								Validator.validateMonthCell(validatorMessage, 
															new MonthCell(moy, 0f, 0f), 
															monthList.get(moy));
							}

						} else if (cat.getName().equalsIgnoreCase(melee)){
							
							if(moy.getMonth() == todayMinus4Months.getMonth()){
								Validator.validateMonthCell(validatorMessage + " (todayMinus4Months)", 
															new MonthCell(moy, 0f, -25f), 
															monthList.get(moy));
								
							} else if(moy.getMonth() == todayMinus3Months.getMonth()){
								Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
															new MonthCell(moy, 0f, -75f), 
															monthList.get(moy));
							} else {
								Validator.validateMonthCell(validatorMessage, 
															new MonthCell(moy, 0f, 0f), 
															monthList.get(moy));
							}
								
						} else if (cat.getName().equalsIgnoreCase(aDistance)){
							if(moy.getMonth() == todayMinus4Months.getMonth()){
								Validator.validateMonthCell(validatorMessage + " (todayMinus4Months)", 
															new MonthCell(moy, 0f, -50f), 
															monthList.get(moy));
								
							} else if(moy.getMonth() == todayMinus3Months.getMonth()){
								Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
															new MonthCell(moy, 0f, -150f), 
															monthList.get(moy));
							} else {
								Validator.validateMonthCell(validatorMessage, 
															new MonthCell(moy, 0f, 0f), 
															monthList.get(moy));
							}
						} else {
							fail("Unexpected child category of " + fatCat.getName() + ": " + cat.getName());
						}
					}
					
				}
			} else {
				Category cat = catRow.getCategory();
				
				assertThat("CatRow with Category #" + cat.getId() + " (" + cat.getName() + ") should not be #?(Armes): ",
						cat.getName(), not(is("Armes")));
				
				// checking number of months
				assertThat("CatRow with Category #" + cat.getId() + " (" + cat.getName() + ") doesn't have 6 months: ",
						catRow.getNumberOfMonths(),
						is(6));
				
				Map<MonthInYear, MonthCell> monthList = catRow.getMonthList();

				// checking values
				for(MonthInYear moy : monthList.keySet()){

					String validatorMessage = " test to fetchCategoryTable, category: " + cat.getName() + ", month: " + moy;
					if(cat.getName().equalsIgnoreCase(armure)){
						
						if(moy.getMonth() == todayMinus5Months.getMonth()){
							Validator.validateMonthCell(validatorMessage + " (todayMinus5Months)", 
														new MonthCell(moy, 0f, -100f), 
														monthList.get(moy));
							
						} else if(moy.getMonth() == todayMinus4Months.getMonth()){
							Validator.validateMonthCell(validatorMessage + " (todayMinus4Months)", 
														new MonthCell(moy, 0f, -100f), 
														monthList.get(moy));
							
						} else if(moy.getMonth() == todayMinus3Months.getMonth()){
							Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
														new MonthCell(moy, 100f, -200f), 
														monthList.get(moy));
						} else {
							Validator.validateMonthCell(validatorMessage, 
														new MonthCell(moy, 0f, 0f), 
														monthList.get(moy));
						}

					} else if (cat.getName().equalsIgnoreCase(cheval)){
						 if(moy.getMonth() == todayMinus3Months.getMonth()){
							Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
														new MonthCell(moy, 100f, -200f), 
														monthList.get(moy));
						} else {
							Validator.validateMonthCell(validatorMessage, 
														new MonthCell(moy, 0f, 0f), 
														monthList.get(moy));
						}
					} else if (cat.getName().equalsIgnoreCase(hommesDArmes)){
						if(moy.getMonth() == todayMinus3Months.getMonth()){
							Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
														new MonthCell(moy, 100f, -100f), 
														monthList.get(moy));
						} else {
							Validator.validateMonthCell(validatorMessage, 
														new MonthCell(moy, 0f, 0f), 
														monthList.get(moy));
						}
					} else if (cat.getName().equalsIgnoreCase(chateau)){
						if(moy.getMonth() == todayMinus3Months.getMonth()){
							Validator.validateMonthCell(validatorMessage + " (todayMinus3Months)", 
														new MonthCell(moy, 0f, -100f), 
														monthList.get(moy));
						} else {
							Validator.validateMonthCell(validatorMessage, 
														new MonthCell(moy, 0f, 0f), 
														monthList.get(moy));
						}
					}
				}
			}
			
		}
		
		logger.info("testFetchCategoryTable finished.");
	}

	private void prettyPrint(List<CatRow> catRowList) {
		StringBuilder strBuilder = new StringBuilder("\n");
		for(CatRow catR : catRowList){
			// formatted on 14 chars so that they're all on the same level
			strBuilder.append(String.format("%14s", catR.getCategory().getName()));
			if(catR.isChildFree()){
				
				strBuilder.append(String.format("%14s", " "));
				strBuilder.append(prettyPrintChildFree(catR.getMonthList()));
			} else {
				for(int i = 0; i < catR.getSonsCatRowList().size(); i++){
					CatRow son = catR.getSonsCatRowList().get(i);
					if(i == 0){
						// the first son is on the same line as the father,
						// so formatted on 14 chars (2/2 of childfree)
						strBuilder.append(String.format("%14s", son.getCategory().getName()));
					} else {
						// the others are on their own lines, so like childfree cats 
						strBuilder.append(String.format("%28s", son.getCategory().getName()));
					}
					strBuilder.append(prettyPrintChildFree(son.getMonthList()));
				}
			}
		}
		logger.warn(strBuilder.toString());
	}

	private StringBuilder prettyPrintChildFree(Map<MonthInYear, MonthCell> monthList) {
		StringBuilder strBuilder = new StringBuilder();
		
		for(MonthInYear moy : monthList.keySet()){
			MonthCell moCe = monthList.get(moy);
			strBuilder.append(" ");
			strBuilder.append(moy);
			strBuilder.append(" (");
			strBuilder.append(String.format("%6.2f", moCe.getNegAmount())); // format on 6 char, 2 digits after comma
			strBuilder.append(" ; ");
			strBuilder.append(String.format("%6.2f", moCe.getPosAmount()));
			strBuilder.append(")\t");
		}
		strBuilder.append("\n");
		
		return strBuilder;
	}

}