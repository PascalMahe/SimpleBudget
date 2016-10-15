package fr.pascalmahe.web;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class TestLogin {

	private static final Logger logger = LogManager.getLogger();

	private static final String CHROMEDRIVER_EXE = "chromedriver.exe";

	private static final String LOGIN_FORM_ID = "loginForm";
	
	private static final String LOGIN_ID = LOGIN_FORM_ID + ":login";

	private static final String PWD_ID = LOGIN_FORM_ID + ":password";
	
	private static final String LOGIN_PASCAL = "pascal";
	
	private static final String PWD_PASCAL = "";

	private static final String CONNECT_BUTTON_ID = LOGIN_FORM_ID + ":searchButton";

	private static final String BUDGET_PAGE_LINK_ID = "budgetPageLink";

	private static final String CATEGORY_PAGE_LINK_ID = "categoryPageLink";

	private static final String IMPORT_PAGE_LINK_ID = "importPageLink";

	private static final String LINE_PAGE_LINK_ID = "linePageLink";

	private static final String DISCONNECT_BUTTON_ID = "disconnectForm:disconnectButton";

	private static final String REMEMBERME_ID = LOGIN_FORM_ID + ":rememberMe";

	private static WebDriver driverFirefox;
	
	private static WebDriver driverChrome;
	
	private static String detectedHost;
	
	@BeforeClass
	public static void browserSetup(){
		long timeBeforeDriverLaunch = System.currentTimeMillis();
		
		FirefoxProfile firefoxProfile = getFFProfile();
		driverFirefox = new FirefoxDriver(firefoxProfile);
//		driverFirefox = new FirefoxDriver();
		
		long timeAfterDriverLaunch = System.currentTimeMillis();
		
		long elapsedTime = timeAfterDriverLaunch - timeBeforeDriverLaunch;
		String formattedElapsedTime = DurationFormatUtils
				.formatDuration(elapsedTime, "mm:ss.SSS");
		
		logger.debug("browserSetup - time to launch FF: " + formattedElapsedTime);
		
		timeBeforeDriverLaunch = System.currentTimeMillis();
		

		URL url = TestLogin.class.getClassLoader().getResource(CHROMEDRIVER_EXE);
		
		try {
			FileOutputStream output = new FileOutputStream(CHROMEDRIVER_EXE);
			InputStream input = url.openStream();
			byte [] buffer = new byte[4096];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
			    output.write(buffer, 0, bytesRead);
			    bytesRead = input.read(buffer);
			}
			output.close();
			input.close();
		} catch (FileNotFoundException e) {
			logger.error("setUp - Error while extracting chromedriver.exe: " + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.error("setUp - Error while extracting chromedriver.exe: " + e.getLocalizedMessage(), e);
		}
		
		DesiredCapabilities capa = new DesiredCapabilities();
		ChromeOptions co = new ChromeOptions();
		Proxy proxy = new Proxy();
		proxy.setProxyType(ProxyType.MANUAL);
		
		proxy.setNoProxy("true");
		capa.setCapability(CapabilityType.PROXY, proxy);
		capa.setCapability(ChromeOptions.CAPABILITY, co);
		
		driverChrome = new ChromeDriver(capa);
//		driverChrome = new ChromeDriver();
		timeAfterDriverLaunch = System.currentTimeMillis();
		
		elapsedTime = timeAfterDriverLaunch - timeBeforeDriverLaunch;
		formattedElapsedTime = DurationFormatUtils
				.formatDuration(elapsedTime, "mm:ss.SSS");
		
		logger.debug("browserSetup - time to launch Chrome: " + formattedElapsedTime);
		
		
		
		try {
			driverFirefox.get(WebConstants.TEST_HOST);
			detectedHost = WebConstants.TEST_HOST;
			
		} catch (TimeoutException e) {
			try{
				driverFirefox.get(WebConstants.HEROKUAPP_HOST);
				detectedHost = WebConstants.HEROKUAPP_HOST;
			} catch(TimeoutException e2){
				logger.warn("browserSetup - No server found, skipping tests.");
			}
		}
		
		if(detectedHost != null){
			logger.info("browserSetup - Server found @: " + detectedHost);
		}
	}
	
	private static FirefoxProfile getFFProfile() {

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

	@AfterClass
	public static void browserTeardown(){
		driverFirefox.quit();
		driverChrome.quit();
		
		File chromeDriverFile = new File(CHROMEDRIVER_EXE);
		chromeDriverFile.delete();
	}
	
	@Test
	public void testSimpleLogin(){
		if(detectedHost != null){
			testSimpleLogin(driverFirefox);
			testSimpleLogin(driverChrome);
		} else {
			logger.debug("testSimpleLogin - Skipping test.");
		}
	}
	
	private void testSimpleLogin(WebDriver driver){
	
		// go to ROOT_URL
		driver.get(detectedHost);

		checkConnectFormPresent(driver);
		
		boolean rememberMe = false;
		connect(driver, rememberMe);
				
		checkMenuPresent(driver);
	}
	
	private void connect(WebDriver driver, boolean rememberMe) {
		// input login & pwd, submit
		driver.findElement(By.id(LOGIN_ID)).sendKeys(LOGIN_PASCAL);
		driver.findElement(By.id(PWD_ID)).sendKeys(PWD_PASCAL);
		WebElement rememberMeElmt = driver.findElement(By.id(REMEMBERME_ID));
		boolean rememberMeElmtIsChecked = rememberMeElmt.isSelected();
		if(rememberMe != rememberMeElmtIsChecked){
			// click on element if it's clear and we want to be remembered
			// or if it's clicked and we want to be forgotten
			rememberMeElmt.click();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			logger.debug("Interrupted while waiting (to click on connect button): " + e.getLocalizedMessage()) ;
		}
		driver.findElement(By.id(CONNECT_BUTTON_ID)).click();
		
		// check connection success
		waitForStalenessOf(driver, CONNECT_BUTTON_ID);
	}

	private void waitForStalenessOf(WebDriver driver, String elmtId) {
		try {
			WebElement elmt = driver.findElement(By.id(elmtId));
			(new WebDriverWait(driver, 5)).until(ExpectedConditions.stalenessOf(elmt));
		} catch (NoSuchElementException nsee) {
			logger.debug("waitForStalenessOf - Looks like the element "
					+ "we're waiting for (#" + elmtId + ") isn't here...");
		}
	}

	private void checkMenuPresent(WebDriver driver) {
		// check that menu & disconnect are present
		try {
			driver.findElement(By.id(BUDGET_PAGE_LINK_ID));
		} catch(NoSuchElementException nsee){
			fail("Link to budget page (#" + BUDGET_PAGE_LINK_ID + ") not found.");
		}
		
		try {
			driver.findElement(By.id(CATEGORY_PAGE_LINK_ID));
		} catch(NoSuchElementException nsee){
			fail("Link to category page (#" + CATEGORY_PAGE_LINK_ID + ") not found.");
		}
		try {
			driver.findElement(By.id(IMPORT_PAGE_LINK_ID));
		} catch(NoSuchElementException nsee){
			fail("Link to import page (#" + IMPORT_PAGE_LINK_ID + ") not found.");
		}
		try {
			driver.findElement(By.id(LINE_PAGE_LINK_ID));
		} catch(NoSuchElementException nsee){
			fail("Link to line page (#" + LINE_PAGE_LINK_ID + ") not found.");
		}
		
		try {
			driver.findElement(By.id(DISCONNECT_BUTTON_ID));
		} catch(NoSuchElementException nsee){
			fail("Disonnect button (#" + CONNECT_BUTTON_ID + ") not found.");
		}
	}

	@Test 
	public void testDisconnect(){
		if(detectedHost != null){
			testDisconnect(driverFirefox);
			testDisconnect(driverChrome);
		} else {
			logger.debug("testDisconnect - Skipping test.");
		}
	}
	
	private void testDisconnect(WebDriver driver){
		// go to ROOT_URL
		driver.get(detectedHost);
		
		checkMenuPresent(driver);
		
		disconnect(driver);
		
		checkConnectFormPresent(driver);
	}
	
	private void disconnect(WebDriver driver) {
		driver.findElement(By.id(DISCONNECT_BUTTON_ID)).click();
		waitForStalenessOf(driver, DISCONNECT_BUTTON_ID);
	}

	private void checkConnectFormPresent(WebDriver driver) {
		// check that login, password, rememberMe & 
		// connect button are present
		try {
			driver.findElement(By.id(LOGIN_ID));
		} catch(NoSuchElementException nsee){
			fail("Login field (#" + LOGIN_ID + ") not found.");
		}
		
		try {
			driver.findElement(By.id(PWD_ID));
		} catch(NoSuchElementException nsee){
			fail("Password field (#" + PWD_ID + ") not found.");
		}
		
		try {
			driver.findElement(By.id(REMEMBERME_ID));
		} catch(NoSuchElementException nsee){
			fail("RememberMe field (#" + REMEMBERME_ID + ") not found.");
		}
		
		try {
			driver.findElement(By.id(CONNECT_BUTTON_ID));
		} catch(NoSuchElementException nsee){
			fail("Connect button (#" + CONNECT_BUTTON_ID + ") not found.");
		}
	}

	@Test
	public void testRememberMe(){
		if(detectedHost != null){
			testRememberMe(driverFirefox);
			testRememberMe(driverChrome);
		} else {
			logger.debug("testRememberMe - Skipping test.");
		}
		
	}
	
	private void testRememberMe(WebDriver driver) {
		// first: connection with rememberMe
		driver.get(detectedHost);
		checkConnectFormPresent(driver);
		
		// input rememberMe, login & pwd, submit
		boolean rememberMe = true;
		connect(driver, rememberMe);
		checkMenuPresent(driver);
		
		// disconnect 
		disconnect(driver);
		// check you're on login.xhtml (or logout.xhtml)
		checkConnectFormPresent(driver);
		
		// two: ask for root and check it goes to secured
		goToSettingsPage(driver);
		
		driver.get(detectedHost);
		checkMenuPresent(driver);
		
		// three: disconnect and it goes through
		// if asked for another URL than root
		disconnect(driver);
		// check you're on login.xhtml (or logout.xhtml)
		checkConnectFormPresent(driver);
		
		// two: ask for root and check it goes to secured
		goToSettingsPage(driver);
		
		driver.get(detectedHost + "/secured/dashboard.xhtml");
		checkMenuPresent(driver);
	}

	private void goToSettingsPage(WebDriver driver) {
		if(driver.getClass().getSimpleName().contains("Firefox")){
			driver.get("about:config");
		} else {
			driver.get("chrome://settings");
		}
	}

	@Test
	public void testForgetMe(){
		if(detectedHost != null){
			testForgetMe(driverFirefox);
			testForgetMe(driverChrome);
		} else {
			logger.debug("testForgetMe - Skipping test.");
		}
		
	}

	private void testForgetMe(WebDriver driver) {
		// first: check cookie is present
		driver.get(detectedHost);
		
		// (it connects all by itself)
		checkMenuPresent(driver);
		
		// disconnect
		disconnect(driver);
		checkConnectFormPresent(driver);
		
		// second: unchecking rememberMe
		// removes cookie
		boolean rememberMe = false;
		connect(driver, rememberMe);
		
		checkMenuPresent(driver);
		disconnect(driver);
		
		// change page
		goToSettingsPage(driver);
		
		// go back to root
		driver.get(detectedHost);
		
		// -> should not be connected
		checkConnectFormPresent(driver);
		
		// three: asking for another page
		// doesn't work
		driver.get(detectedHost + "/secured/dashboard.xhtml");
		// -> should not be connected
		checkConnectFormPresent(driver);
	}
}
