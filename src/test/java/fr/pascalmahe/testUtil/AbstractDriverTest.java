package fr.pascalmahe.testUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import fr.pascalmahe.services.AccountService;
import fr.pascalmahe.web.TestLogin;
import fr.pascalmahe.web.WebConstants;

public class AbstractDriverTest extends AbstractTest {

	private static Logger logger = LogManager.getLogger();

	protected static final String CHROMEDRIVER_EXE = "chromedriver.exe";
	protected static final String GECKODRIVER_EXE = "geckodriver.exe";

	protected static WebDriver driverFirefox;
	
	protected static WebDriver driverChrome;
	
	protected static String detectedHost;
	
	public static void browserSetup(){
		long timeBeforeDriverExeExtraction = System.currentTimeMillis();

		extractDriverFile(CHROMEDRIVER_EXE);
		extractDriverFile(GECKODRIVER_EXE);
		
		long timeBeforeDriverLaunch = System.currentTimeMillis();
		
		long timeToExtract = timeBeforeDriverLaunch - timeBeforeDriverExeExtraction;
		String formattedElapsedTime = DurationFormatUtils
				.formatDuration(timeToExtract, "mm:ss.SSS");
		
		logger.debug("browserSetup - time to extract drivers: " + formattedElapsedTime);
		
		
		FirefoxProfile firefoxProfile = AccountService.getFFProfile();
		System.setProperty("webdriver.gecko.driver", GECKODRIVER_EXE);
		driverFirefox = new FirefoxDriver(firefoxProfile);
//		driverFirefox = new FirefoxDriver();
		
		long timeAfterDriverLaunch = System.currentTimeMillis();
		
		long elapsedTime = timeAfterDriverLaunch - timeBeforeDriverLaunch;
		formattedElapsedTime = DurationFormatUtils
				.formatDuration(elapsedTime, "mm:ss.SSS");
		
		logger.debug("browserSetup - time to launch FF: " + formattedElapsedTime);
		
		timeBeforeDriverLaunch = System.currentTimeMillis();
		
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
			driverFirefox.get(WebConstants.LOCAL_TOMCAT_HOST);
			detectedHost = WebConstants.LOCAL_TOMCAT_HOST;
			
		} catch (TimeoutException e) {
			try{
				driverFirefox.get(WebConstants.LOCAL_HEROKU_HOST);
				detectedHost = WebConstants.LOCAL_HEROKU_HOST;
			} catch(TimeoutException e2){
				try{
					driverFirefox.get(WebConstants.HEROKUAPP_HOST);
					detectedHost = WebConstants.HEROKUAPP_HOST;
				} catch(TimeoutException e3){
					logger.warn("browserSetup - No server found, skipping tests.");
				}
			}
		}
		
		if(detectedHost != null){
			logger.info("browserSetup - Server found @: " + detectedHost);
		}
	}
	
	private static void extractDriverFile(String pathToDriverExe) {

		URL url = TestLogin.class.getClassLoader().getResource(pathToDriverExe);
		
		try {
			FileOutputStream output = new FileOutputStream(pathToDriverExe);
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
			logger.error("setUp - Error while extracting " + pathToDriverExe + ": " + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			logger.error("setUp - Error while extracting " + pathToDriverExe + ": " + e.getLocalizedMessage(), e);
		}
	}

	protected static void browserTeardown(){
		driverFirefox.quit();
		driverChrome.quit();
		
		deleteDriverFile(CHROMEDRIVER_EXE);
		deleteDriverFile(GECKODRIVER_EXE);
		
	}
	
	private static void deleteDriverFile(String pathname){

		File driverFile = new File(pathname);
		driverFile.delete();
	}
	
	
	
}
