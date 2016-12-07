package fr.pascalmahe.business;

import java.util.List;

import javax.persistence.Entity;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * Class grouping the CA account's info
 * @author Pascal
 */
@Entity
public class AccountCA extends Account {
	
	private static final long serialVersionUID = 6513481975646088455L;

	public AccountCA() {
		this.name = NAME_CA;
		url = CA_URL;
	    selectorToLoginPage = CA_SELECTOR_TO_LOGIN_LINK;
	    selectorToLoginLink = CA_SELECTOR_TO_LOGIN_LINK;
	    selectorToLoginField = CA_SELECTOR_TO_LOGIN_FIELD;
	    selectorToPasswordField = CA_SELECTOR_TO_PASSWORD_FIELD;
	    selectorToLoginButton = CA_SELECTOR_TO_LOGIN_BUTTON;
	    selectorToLinePage = CA_SELECTOR_TO_LINE_PAGE;
	    selectorToLine = CA_SELECTOR_TO_LINE;
	    selectorToNextPage = CA_SELECTOR_TO_NEXT_PAGE;
	    selectorToDeconnectionButton = CA_SELECTOR_TO_DECO_BUTTON;
	    login = CA_LOGIN;
	    password = CA_PASSWORD;
	}

	@Override
	public List<Line> webElmtsToLines(List<WebElement> linesAsWebElmts) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void fillPassword(WebDriver driver) {
		// TODO Auto-generated method stub
		
	}
	
}
