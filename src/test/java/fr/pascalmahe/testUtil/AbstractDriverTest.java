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

	protected static WebDriver driverFirefox;
	
	protected static WebDriver driverChrome;
	
	protected static String detectedHost;
	
	public static void browserSetup(){
		long timeBeforeDriverLaunch = System.currentTimeMillis();
		
		FirefoxProfile firefoxProfile = AccountService.getFFProfile();
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
	
	protected static void browserTeardown(){
		driverFirefox.quit();
		driverChrome.quit();
		
		File chromeDriverFile = new File(CHROMEDRIVER_EXE);
		chromeDriverFile.delete();
	}
	
	
	
}
