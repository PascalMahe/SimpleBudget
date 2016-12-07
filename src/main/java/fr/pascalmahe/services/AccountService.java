package fr.pascalmahe.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.pascalmahe.business.Account;
import fr.pascalmahe.business.Line;
import fr.pascalmahe.persistence.GenericDao;

public class AccountService {
	
	private static final Logger logger = LogManager.getLogger();
	
	protected static final int waitTime = 10; // waitTime in sec
	
	protected static WebDriver getDriver(){

		long timeBeforeDriverLaunch = System.currentTimeMillis();
		
		FirefoxProfile firefoxProfile = getFFProfile();
		WebDriver driver = new FirefoxDriver(firefoxProfile);

		long timeAfterDriverLaunch = System.currentTimeMillis();
		
		long elapsedTime = timeAfterDriverLaunch - timeBeforeDriverLaunch;
		String formattedElapsedTime = DurationFormatUtils
				.formatDuration(elapsedTime, "mm:ss.SSS");
		
		logger.debug("getDriver - time to launch driver (FF): " + formattedElapsedTime);
		
		return driver;
	}
	
	protected static void crawlAccount(Account account){		
		logger.info("Starting to crawl account: " + account.getName()+ "...");
		
		WebDriver driver = getDriver();
		
		connect(account, driver);
		goToLinePage(account, driver);
		List<Line> lines = crawlLines(account, driver);
		
		logger.debug("crawlAccount - Got " + lines.size() + " lines. Saving them...");
		
		int nbOfLinesBefore = LineService.countLines();
		for(Line line : lines){
			LineService.updateLine(line);
		}
		int nbOfLinesAfter = LineService.countLines();

		// TODO: add in way to fetch all that's not already in the DB
		disconnect(account, driver);

		driver.close();
		logger.info("Crawl account " + account.getName()+ " done. Found " + (nbOfLinesAfter - nbOfLinesBefore) + " new line(s).");
	}

	protected static void disconnect(Account account, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		WebElement disCoButton = wait.until(
		        ExpectedConditions.visibilityOfElementLocated(account.getSelectorToDeconnectionButton()));
		
		logger.debug("disconnect - Found disconnect button. Clicking it...");
		
		disCoButton.click();		
	}

	protected static List<Line> crawlLines(Account account, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, waitTime);

		List<WebElement> linesAsWebElmts = wait.until(
		        ExpectedConditions.visibilityOfAllElements(
		        		driver.findElements(account.getSelectorToLine())));

		logger.debug("crawlLines - Found " + linesAsWebElmts.size() + " lines. Objectifying them...");
		
		List<Line> lines = account.webElmtsToLines(linesAsWebElmts);

		return lines;
	}

	protected static void goToLinePage(Account account, WebDriver driver) {

		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		WebElement linkToLines = wait.until(
		        ExpectedConditions.visibilityOfElementLocated(account.getSelectorToLinePage()));

		logger.debug("goToLinePage - Found link to line pages. Following it...");
		
		linkToLines.click();
	}

	protected static void connect(Account account, WebDriver driver) {

		WebDriverWait wait = new WebDriverWait(driver, waitTime);

		driver.get(account.getUrl());
		
		WebElement loginLink = wait.until(
		        ExpectedConditions.visibilityOfElementLocated(account.getSelectorToLoginLink()));
		logger.debug("connect - Found link to login page. Clicking it...");
		
		loginLink.click();
		logger.debug("connect - Link to login page clicked.");
		
		WebElement loginField = wait.until(
		        ExpectedConditions.visibilityOfElementLocated(account.getSelectorToLoginField()));
		logger.debug("connect - Found login field. Filling it...");
		
        loginField.sendKeys(account.getLogin());
		logger.debug("connect - Login field filled.");
		
        // do password thingy
        account.fillPassword(driver);
                
		WebElement loginButton = driver.findElement(account.getSelectorToLoginButton());
		logger.debug("connect - Found login button. Clicking it...");
		
        loginButton.click();
		logger.debug("connect - Login button clicked.");
		
	}

	public static Account fromDetailedLabel(String detailedLabel) {
		String detectedTypeName;
		// look for lower case letter => if yes : CA
		// 								 if not : LBP
		Pattern p = Pattern.compile("[a-z]");
		Matcher m = p.matcher(detailedLabel);
		
		if(m.find()){
			detectedTypeName = Account.NAME_CA;
		} else {
			detectedTypeName = Account.NAME_LBP;
		}
		
		GenericDao<Account> accDao = new GenericDao<>(Account.class);
		
		Map<String, Object> mapOfCriteria = new HashMap<>();
		mapOfCriteria.put("name", detectedTypeName);
		
		List<Account> returnedList = accDao.search(mapOfCriteria);
		
		Account toReturn;
		if(returnedList.isEmpty()){
//			logger.debug("fromDetailedLabel - no Account found. Creating it...");
			toReturn = Account.fromName(detectedTypeName);
			accDao.saveOrUpdate(toReturn);
		} else {
//			logger.debug("fromDetailedLabel - returning fetched Account fetched from DB...");
			toReturn = returnedList.get(0);
		}
		
		return toReturn;
	}

	public static FirefoxProfile getFFProfile() {
	
		FirefoxProfile firefoxProfile = new FirefoxProfile();
	    firefoxProfile.setAcceptUntrustedCertificates(true);
	    firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
	    firefoxProfile.setPreference("browser.download.folderList",2);
	    firefoxProfile.setPreference("browser.download.manager.showWhenStarting",false);
	    
	    firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","text/csv,application/pdf,application/csv,application/vnd.ms-excel");
	    firefoxProfile.setPreference("browser.download.manager.showAlertOnComplete",false);  firefoxProfile.setPreference("browser.download.manager.showAlertOnComplete",false);
	    firefoxProfile.setPreference("browser.download.manager.showWhenStartinge",false);
	    firefoxProfile.setPreference("browser.download.panel.shown",false);
	    firefoxProfile.setPreference("browser.download.useToolkitUI",true);
	    firefoxProfile.setPreference("pdfjs.disabled", true);
	    firefoxProfile.setPreference("pdfjs.firstRun", false);
	    
	    firefoxProfile.setPreference("browser.bookmarks.max_backups", 0);
		firefoxProfile.setPreference("browser.cache.memory.enable", true);
		firefoxProfile.setPreference("browser.cache.disk.enable", true);
		firefoxProfile.setPreference("browser.download.manager.scanWhenDone", false);
		firefoxProfile.setPreference("browser.formfill.enable", false);
		firefoxProfile.setPreference("network.http.pipeline", true);
		firefoxProfile.setPreference("network.http.pipeline.maxrequests", 8);
		firefoxProfile.setPreference("browser.search.suggest.enabled", false);
		firefoxProfile.setPreference("browser.sessionhistory.max_entries", 3);
		firefoxProfile.setPreference("browser.sessionhistory.max_tabs_undo", 0);
		firefoxProfile.setPreference("browser.sessionhistory.max_total_viewer", 1);
		firefoxProfile.setPreference("browser.sessionhistory.max_total_viewers", 1);
		firefoxProfile.setPreference("browser.startup.homepage", "about:blank");
		firefoxProfile.setPreference("browser.tabs.animated", false);
		firefoxProfile.setPreference("image.animation_modr", "none");
		firefoxProfile.setPreference("general.useragent.override", "FF Selenium UA");
		firefoxProfile.setPreference("nglayout.initialpaint.delay", 0);
	    
	    return firefoxProfile;
	}
}
