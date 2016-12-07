package fr.pascalmahe.business;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.openqa.selenium.By;


/**
 * Class representing an account 
 * @author Pascal
 */
@Entity
public abstract class Account implements Serializable, AccountInterface {

	protected static final long serialVersionUID = -4568723765858681796L;

	public static final String NAME_CA = "Crédit Agricole";
	
	public static final String NAME_LBP = "La Banque Postale";
	
	public static final String LBP_URL = "https://www.labanquepostale.fr/";

	public static final By LBP_SELECTOR_TO_LOGIN_LINK = By.xpath("//*[@id=\"nav-main\"]/div/nav/a");
	
	public static final By LBP_SELECTOR_TO_LOGIN_FIELD = By.id("val_cel_identifiant");

	public static final By LBP_SELECTOR_TO_PASSWORD_FIELD = By.id("imageclavier");

	public static final By LBP_SELECTOR_TO_LOGIN_BUTTON = By.id("valider");

	public static final By LBP_SELECTOR_TO_LINE_PAGE = By.xpath("//*[@id=\"comptes\"]/tbody/tr/td[1]/span/a");
	
	public static final By LBP_SELECTOR_TO_LINE = By.xpath("//*[@id=\"mouvements\"]/tbody/tr[1]");

	public static final By LBP_SELECTOR_TO_NEXT_PAGE = By.xpath("//*[@id=\"main\"]/div[5]/div/ul[1]/li[4]/a");

	public static final By LBP_SELECTOR_TO_DECO_BUTTON = By.xpath("//*[@id=\"header-main\"]/div[2]/div/div/ul/li[3]/a");

	public static final String CA_URL = "http://www.ca-anjou-maine.fr/";

	public static final By CA_SELECTOR_TO_LOGIN_LINK = By.xpath("//*[@id=\"acces_aux_comptes\"]/a");
	
	public static final By CA_SELECTOR_TO_LOGIN_FIELD = By.name("CCPTE");
	
	public static final By CA_SELECTOR_TO_PASSWORD_FIELD = By.id("pave-saisie-code");
	
	public static final By CA_SELECTOR_TO_LOGIN_BUTTON = By.xpath("//*[@id=\"col-centre\"]/table/tbody/tr[1]/td/form/table/tbody/tr/td/fieldset/div/p[2]/span[2]/a[2]");

	public static final By CA_SELECTOR_TO_LINE_PAGE = By.xpath("//*[@id=\"comptes\"]/tbody/tr/td[1]/span/a");
	
	public static final By CA_SELECTOR_TO_LINE = By.xpath("//*[@id=\"trPagePu\"]/table[1]/tbody/tr/td/table[2]/tbody/tr[12]/td[1]/a");
	
	public static final By CA_SELECTOR_TO_NEXT_PAGE = By.id("lien_page_suivante");
	
	public static final By CA_SELECTOR_TO_DECO_BUTTON = By.id("btn-bloc-login_2");

	protected static final String CA_LOGIN = System.getenv("CA_LOGIN");
	
	protected static final String LBP_LOGIN = System.getenv("LBP_LOGIN");
	
	protected static final String CA_PASSWORD = System.getenv("CA_PASSWORD");
	
	protected static final String LBP_PASSWORD = System.getenv("LBP_PASSWORD");
	
	
	@Id
	@GeneratedValue
	protected Integer id;
	
    protected String name;
    
    @Transient
    protected String url;

    @Transient
    protected By selectorToLoginPage;

    @Transient
    protected By selectorToLoginLink;

    @Transient
    protected By selectorToLoginField;

    @Transient
    protected By selectorToPasswordField;

    @Transient
    protected By selectorToLoginButton;

    @Transient
    protected By selectorToLinePage;
    
    @Transient
    protected By selectorToLine;

    @Transient
    protected By selectorToNextPage;

    @Transient
    protected By selectorToDeconnectionButton;

    @Transient
    protected String login;
    
    @Transient
    protected String password;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public By getSelectorToLoginPage() {
		return selectorToLoginPage;
	}


	public void setSelectorToLoginPage(By selectorToLoginPage) {
		this.selectorToLoginPage = selectorToLoginPage;
	}


	public By getSelectorToLoginLink() {
		return selectorToLoginLink;
	}


	public void setSelectorToLoginLink(By selectorToLoginLink) {
		this.selectorToLoginLink = selectorToLoginLink;
	}


	public By getSelectorToLoginField() {
		return selectorToLoginField;
	}


	public void setSelectorToLoginField(By selectorToLoginField) {
		this.selectorToLoginField = selectorToLoginField;
	}


	public By getSelectorToPasswordField() {
		return selectorToPasswordField;
	}


	public void setSelectorToPasswordField(By selectorToPasswordField) {
		this.selectorToPasswordField = selectorToPasswordField;
	}


	public By getSelectorToLoginButton() {
		return selectorToLoginButton;
	}


	public void setSelectorToLoginButton(By selectorToLoginButton) {
		this.selectorToLoginButton = selectorToLoginButton;
	}


	public By getSelectorToLine() {
		return selectorToLine;
	}


	public void setSelectorToLine(By selectorToLine) {
		this.selectorToLine = selectorToLine;
	}


	public By getSelectorToNextPage() {
		return selectorToNextPage;
	}


	public void setSelectorToNextPage(By selectorToNextPage) {
		this.selectorToNextPage = selectorToNextPage;
	}

	public By getSelectorToDeconnectionButton() {
		return selectorToDeconnectionButton;
	}

	public void setSelectorToDeconnectionButton(By selectorToDeconnectionButton) {
		this.selectorToDeconnectionButton = selectorToDeconnectionButton;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public By getSelectorToLinePage() {
		return selectorToLinePage;
	}

	public void setSelectorToLinePage(By selectorToLinePage) {
		this.selectorToLinePage = selectorToLinePage;
	}

	public static Account fromName(String detectedAccountName) {
		if(detectedAccountName.equalsIgnoreCase(NAME_CA)){
			return new AccountCA();
		}
		return new AccountLBP();
	}
	
}
