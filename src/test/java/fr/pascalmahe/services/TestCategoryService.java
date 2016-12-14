package fr.pascalmahe.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import fr.pascalmahe.persistence.GenericDao;
import fr.pascalmahe.testUtil.AbstractTest;
import fr.pascalmahe.web.beans.CatRow;

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
		
		Category cat1 = new Category("Armure");
		Category cat2 = new Category("Armes");
		Category cat3 = new Category("Cheval");
		Category cat4 = new Category("Hommes d'armes");
		Category cat5 = new Category("Château");

		Category cat6 = new Category("Sièges", cat2);
		Category cat7 = new Category("Mêlée", cat2);
		Category cat8 = new Category("A distance", cat2);
		
		Account acc = Account.fromName(Account.NAME_LBP);
		
		LocalDate date1 = LocalDate.now().minusMonths(5);
		List<Categorisation> categoList1 = new ArrayList<>();
		Categorisation catego10 = new Categorisation(-100f, cat1);
		categoList1.add(catego10);
		Line l1 = new Line(date1, date1.plusDays(3), "Category Table test 1", "Category Table test 1", "", -100f, false, ccard, acc, categoList1);

		LocalDate date2 = LocalDate.now().minusMonths(4);
		List<Categorisation> categoList2 = new ArrayList<>();
		Categorisation catego20 = new Categorisation(-100f, cat1);
		categoList2.add(catego20);
		Categorisation catego21 = new Categorisation(-25f, cat6);
		categoList2.add(catego21);
		Categorisation catego22 = new Categorisation(-25f, cat7);
		categoList2.add(catego22);
		Categorisation catego23 = new Categorisation(-50f, cat8);
		categoList2.add(catego23);
		Line l2 = new Line(date2, date2.plusDays(3), "Category Table test 2", "Category Table test 2", "", -200f, false, ccard, acc, categoList2);
		
		LocalDate date3 = LocalDate.now().minusMonths(3);
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
		Line l3 = new Line(date3, date3.plusDays(3), "Category Table test 3", "Category Table test 3", "", -300f, false, ccard, acc, categoList3);

		LocalDate date4 = LocalDate.now().minusMonths(3);
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
		Line l4 = new Line(date4, date4.plusDays(3), "Category Table test 4", "Category Table test 4", "", 400f, false, ccard, acc, categoList4);

		LocalDate date5 = LocalDate.now().minusMonths(3);
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
		Line l5 = new Line(date5, date5.plusDays(3), "Category Table test 5", "Category Table test 5", "", -500f, false, ccard, acc, categoList5);
		
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
		
		List<CatRow> catRowList = CategoryService.fetchCategoryTable();
		
		int expectedCatNb = 7; 
		assertThat("Number of Categorys in list of CatRows should be " + expectedCatNb + ": ", 
				catRowList.size(), 
				is(expectedCatNb));
		
		for(CatRow catRow : catRowList){
			
			if(!catRow.isChildFree()){
				Category fatCat = catRow.getCategory();
				assertThat("CatRow with Category #" + fatCat.getId() + " (" + fatCat.getName() + ") should be #?(Armes): ",
						fatCat.getName(), is("Armes"));
				
				List<CatRow> catRowSons = catRow.getSonsCatRowList();
				for(CatRow son : catRowSons){
					Category cat = son.getCategory();
					assertThat("CatRow with Category #" + cat.getId() + " (" + cat.getName() + ") doesn't have 6 months: ",
							son.getNumberOfMonths(),
							is(6));
				}
			} else {
				Category cat = catRow.getCategory();
				
				assertThat("CatRow with Category #" + cat.getId() + " (" + cat.getName() + ") should not be #?(Armes): ",
						cat.getName(), not(is("Armes")));
				
				assertThat("CatRow with Category #" + cat.getId() + " (" + cat.getName() + ") doesn't have 6 months: ",
						catRow.getNumberOfMonths(),
						is(6));
			}
			
			
		}
		
		logger.info("testFetchCategoryTable finished.");
	}

}