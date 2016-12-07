package fr.pascalmahe.business;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * Interface implementing password decoding and (webElement) line to (object) Line
 * @author Pascal
 */
public interface AccountInterface {

	public List<Line> webElmtsToLines(List<WebElement> linesAsWebElmts);

	public void fillPassword(WebDriver driver);
	
}
