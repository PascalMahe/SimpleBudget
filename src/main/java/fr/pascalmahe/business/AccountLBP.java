package fr.pascalmahe.business;

import java.util.List;

import javax.persistence.Entity;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * Class grouping the LBP account's info
 * @author Pascal
 */
@Entity
public class AccountLBP extends Account {

	private static final long serialVersionUID = 5399103333266118403L;

	public AccountLBP() {
		this.name = NAME_LBP;
		url = LBP_URL;
		selectorToLoginPage = LBP_SELECTOR_TO_LOGIN_LINK;
	    selectorToLoginLink = LBP_SELECTOR_TO_LOGIN_LINK;
	    selectorToLoginField = LBP_SELECTOR_TO_LOGIN_FIELD;
	    selectorToPasswordField = LBP_SELECTOR_TO_PASSWORD_FIELD;
	    selectorToLoginButton = LBP_SELECTOR_TO_LOGIN_BUTTON;
	    selectorToLinePage = LBP_SELECTOR_TO_LINE_PAGE;
	    selectorToLine = LBP_SELECTOR_TO_LINE;
	    selectorToNextPage = LBP_SELECTOR_TO_NEXT_PAGE;
	    selectorToDeconnectionButton = LBP_SELECTOR_TO_DECO_BUTTON;
	    login = LBP_LOGIN;
	    password = LBP_PASSWORD;
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
