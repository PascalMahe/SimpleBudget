package fr.pascalmahe.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import fr.pascalmahe.business.Account;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.testUtil.AbstractTest;

public class TestAccountService extends AbstractTest {

	private static final Logger logger = LogManager.getLogger();
	
	private static WebDriver driver;
	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		preTestDatabaseCheckup();
		
		driver = AccountService.getDriver();
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		
		driver.close();
		
		cleanUpDatabase();
		logger.info("afterClass finished.");
	}

	@Test
	@Ignore // because not implemented yet
	public void testConnect(){

		WebDriverWait wait = new WebDriverWait(driver, AccountService.waitTime);
		
		// CA
		Account caAccount = Account.fromName(Account.NAME_CA);
		
		try {
			AccountService.connect(caAccount, driver);

			WebElement linkToLines = wait.until(
			        ExpectedConditions.visibilityOfElementLocated(caAccount.getSelectorToLinePage()));
			assertThat(linkToLines, is(notNullValue()));
			
		} catch (Exception e){
			logger.error("Error in testConnect (CA account): " + e.getMessage(), e);
		} finally {
			AccountService.disconnect(caAccount, driver);
		}
		
		// LBP
		Account lbpAccount = Account.fromName(Account.NAME_LBP);

		try {

			AccountService.connect(lbpAccount, driver);
			WebElement linkToLines = wait.until(
			        ExpectedConditions.visibilityOfElementLocated(lbpAccount.getSelectorToLinePage()));
			assertThat(linkToLines, is(notNullValue()));
		} catch (Exception e){
			logger.error("Error in testConnect (LBP account): " + e.getMessage(), e);
		} finally {
			AccountService.disconnect(lbpAccount, driver);
		}
		
		driver.close();
	}
	
	@Test
	@Ignore // because not implemented yet
	public void testGoToLoginPage(){

		WebDriverWait wait = new WebDriverWait(driver, AccountService.waitTime);
		
		// CA
		Account caAccount = Account.fromName(Account.NAME_CA);

		try {
			AccountService.connect(caAccount, driver);
			
			AccountService.goToLinePage(caAccount, driver);

			List<WebElement> linesAsWebElmts = wait.until(
			        ExpectedConditions.visibilityOfAllElements(
			        		driver.findElements(caAccount.getSelectorToLine())));
			assertThat(linesAsWebElmts.size(), not(is(0)));
			
		} catch (Exception e){
			logger.error("Error in testGoToLoginPage (CA account): " + e.getMessage(), e);
		} finally {
			AccountService.disconnect(caAccount, driver);
		}
		
		// LBP
		Account lbpAccount = Account.fromName(Account.NAME_LBP);

		try {
			AccountService.connect(lbpAccount, driver);

			AccountService.goToLinePage(lbpAccount, driver);
			
			List<WebElement> linesAsWebElmts = wait.until(
			        ExpectedConditions.visibilityOfAllElements(
			        		driver.findElements(lbpAccount.getSelectorToLine())));
			assertThat(linesAsWebElmts.size(), not(is(0)));
			
		} catch (Exception e){
			logger.error("Error in testGoToLoginPage (LBP account): " + e.getMessage(), e);
		} finally {
			AccountService.disconnect(lbpAccount, driver);
		}

		driver.close();
	}

	@Test
	@Ignore // because not implemented yet
	public void testCrawlLines(){

		// CA
		Account caAccount = Account.fromName(Account.NAME_CA);
		
		try {

			AccountService.connect(caAccount, driver);
			
			AccountService.goToLinePage(caAccount, driver);
			
			List<Line> lines = AccountService.crawlLines(caAccount, driver);
			
			assertThat(lines.size(), not(is(0)));
			
		} catch (Exception e){
			logger.error("Error in testCrawlLines (CA account): " + e.getMessage(), e);
		} finally {
			AccountService.disconnect(caAccount, driver);
		}
		
		// LBP
		Account lbpAccount = Account.fromName(Account.NAME_LBP);
		
		try {
			AccountService.connect(lbpAccount, driver);

			AccountService.goToLinePage(lbpAccount, driver);

			List<Line> lines = AccountService.crawlLines(lbpAccount, driver);
			
			assertThat(lines.size(), not(is(0)));
		} catch (Exception e){
			logger.error("Error in testCrawlLines (LBP account): " + e.getMessage(), e);
		} finally {
			AccountService.disconnect(lbpAccount, driver);
		}
		
		driver.close();
	}
}
