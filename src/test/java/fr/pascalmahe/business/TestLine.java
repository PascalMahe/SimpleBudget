package fr.pascalmahe.business;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import fr.pascalmahe.ex.MalformedLineException;
import fr.pascalmahe.services.TypeService;
import fr.pascalmahe.testUtil.Validator;

public class TestLine {

	private static final Logger logger = LogManager.getLogger();
	
	@Test
	public void testRegex(){
		String zRegex = "Fr37z26318";
		
		String lettersRegex = "[a-zA-Z.:]+";
		String numRegex = "[\\d/]+";
		
		String mixedTarget = "Fr37zzz263184";
		String bigNumberedTarget = "888888888888888888";
		String smallNumberedTarget = "888";
		String letteredTarget = "vertbaudet.fr";
		String colonTarget = ":";
		String dateTarget = "12/05";
		String longDateTarget = "15/03/2016";
		
		assertThat(mixedTarget.matches(zRegex), is(false)); // WTF ?!
		
		assertThat(letteredTarget.matches(lettersRegex), is(true));
		assertThat(letteredTarget.matches(numRegex), is(false));

		assertThat(colonTarget.matches(lettersRegex), is(true));
		assertThat(colonTarget.matches(numRegex), is(false));

		assertThat(smallNumberedTarget.matches(lettersRegex), is(false));
		assertThat(smallNumberedTarget.matches(numRegex), is(true));

		assertThat(bigNumberedTarget.matches(lettersRegex), is(false));
		assertThat(bigNumberedTarget.matches(numRegex), is(true));

		assertThat(dateTarget.matches(lettersRegex), is(false));
		assertThat(dateTarget.matches(numRegex), is(true));

		assertThat(longDateTarget.matches(lettersRegex), is(false));
		assertThat(longDateTarget.matches(numRegex), is(true));

		assertThat(mixedTarget.matches(numRegex), is(false));
		assertThat(mixedTarget.matches(lettersRegex), is(false));
		
		
	}
	
	@Test
	public void testConstructorFromArray(){
		logger.info("Starting testConstructorFromArray...");
		
		// less than 10 values
		String[] valuesArray5 = {"2016-02-24",
				"\"Carte La Redoute 24/02\n Avoir\"",
				"\"55,25€\"",
				"Remboursement achat",
				""};

		try {
			new Line(valuesArray5);
		} catch (MalformedLineException e) {
			assertThat(e.getMessage(), is("Error in array of values to project"
					+ " to Line: array should contain exactly 10 values, but has 5."));
		}
		
		// more than 10 values
		String[] valuesArray11 = {"2016-02-24",
				"\"Carte La Redoute 24/02\n Avoir\"",
				"\"55,25€\"",
				"Remboursement achat",
				"Remboursement achat",
				"Remboursement achat",
				"Remboursement achat",
				"Remboursement achat",
				"Remboursement achat",
				"Remboursement achat",
				""};

		try {
			new Line(valuesArray11);
		} catch (MalformedLineException e) {
			assertThat(e.getMessage(), is("Error in array of values to project "
					+ "to Line: array should "
					+ "contain exactly 10 values, but has 11."));
		}
				
		
		// credit, no child category
		String[] valuesArrayCreditChildless = {"2016-02-24",
												"\"Carte La Redoute 24/02\n Avoir\"",
												"",
												"\"55,25€\"",
												"Remboursement achat",
												"",
												"",
												"",
												"",
												""};
		
		Line lineCreditChildlessActual = null;
		try {
			lineCreditChildlessActual = new Line(valuesArrayCreditChildless);
		} catch (MalformedLineException e) {
			logger.error("Exception in Line constructor from string array "
					+ "(credit, childless): " + e.getMessage(), e);
			fail(e.getMessage());
		}
		Line lineCreditChildlessExpected = new Line();
		lineCreditChildlessExpected.setAmount(55.25f);
		lineCreditChildlessExpected.setDate(LocalDate.of(2016, 02, 24));
		lineCreditChildlessExpected.setDetailedLabel("Carte La Redoute 24/02\n Avoir");
		lineCreditChildlessExpected.setNote("");
		lineCreditChildlessExpected.setShortLabel("Carte La Redoute");
		lineCreditChildlessExpected.setType(
				TypeService.fromDetailedLabel(TypeService.CREDIT));
		lineCreditChildlessExpected.addCategorisation(55.25f, "Remboursement achat");
		
		Validator.validateLine(" constructor test (credit, childless) ", 
								lineCreditChildlessExpected, 
								lineCreditChildlessActual);
		
		// debit, child category
		String[] valuesArrayDebitChildCat = {"2016-02-19",
												"\"Cyrillus.Fr Paris 16/02\n Paiement Par Carte\"",
												"\"14,90€\"",
												"",
												"Courses",
												"Quotidiennes",
												"",
												"",
												"",
												""};
		
		Line lineDebitChildCatActual = null;
		try {
			lineDebitChildCatActual = new Line(valuesArrayDebitChildCat);
		} catch (MalformedLineException e) {
			logger.error("Exception in Line constructor from string array "
					+ "(debit, child cat): " + e.getMessage(), e);
			fail(e.getMessage());
		}
		Line lineDebitChildCatExpected = new Line();
		lineDebitChildCatExpected.setAmount(-14.90f);
		lineDebitChildCatExpected.setDate(LocalDate.of(2016, 02, 19));
		lineDebitChildCatExpected.setCCardDate(LocalDate.of(2016, 02, 16));
		lineDebitChildCatExpected.setDetailedLabel("Cyrillus.Fr Paris 16/02\n Paiement Par Carte");
		lineDebitChildCatExpected.setNote("");
		lineDebitChildCatExpected.setShortLabel("Cyrillus.Fr Paris");
		lineDebitChildCatExpected.setType(
				TypeService.fromDetailedLabel(TypeService.CCARD_PAYMENT));
		lineDebitChildCatExpected.addCategorisation(-14.90f, "Courses", "Quotidiennes");
		
		Validator.validateLine(" constructor test (debit, child cat) ", 
								lineDebitChildCatExpected, 
								lineDebitChildCatActual);
		

		
		// debit, childless, 2nd cat
		String[] valuesArray9DebitChildless = {"2016-06-20",
												"\"Paiement Par Carte Objets Et Cie Massy 17/06\"",
												"48,95€",
												"",
												"Meubles & Déco",
												"",
												"",
												"Déjeuners",
												"",
												"\"10,00€\""};

		Line lineDebitChildless2CatActual = null;
		try {
			lineDebitChildless2CatActual = new Line(valuesArray9DebitChildless);
		} catch (MalformedLineException e) {
			logger.error("Exception in Line constructor from string array "
						+ "(debit, childless): " + e.getMessage(), e);
			fail(e.getMessage());
		}
		Line lineDebitChildlessExpected = new Line();
		lineDebitChildlessExpected.setAmount(-48.95f);
		lineDebitChildlessExpected.setDate(LocalDate.of(2016, 06, 20));
		lineDebitChildlessExpected.setCCardDate(LocalDate.of(2016, 06, 17));
		lineDebitChildlessExpected.setDetailedLabel("Paiement Par Carte Objets Et Cie Massy 17/06");
		lineDebitChildlessExpected.setNote("");
		lineDebitChildlessExpected.setShortLabel("Objets Et Cie Massy");
		lineDebitChildlessExpected.setType(
				TypeService.fromDetailedLabel(TypeService.CCARD_PAYMENT));
		lineDebitChildlessExpected.addCategorisation(-38.95f, "Meubles & Déco");
		lineDebitChildlessExpected.addCategorisation(-10.00f, "Déjeuners");
		
		Validator.validateLine(" constructor test (debit, childless, 2nd cat) ", 
								lineDebitChildlessExpected, 
								lineDebitChildless2CatActual);
		
		
		// 9 values, debit, with child category
		String[] valuesArray9DebitWithChild = {"2016-06-09",
												"\"Prelevmnt \n" +
												" Edf Clients Particuliers Numero De Client : 6002643630 - Num \n" + 
												" Ero De Compte : Xxx 004013118985 " +
												" Fr47edf001007 " +
												" Mm9760026436300001\"",
												"\"187,96€\"",
												"",
												"Factures",
												"Électricité",
												"",
												"Loisirs",
												"Livres",
												"30,00€"};
		
		Line lineDebitWithChildActual = null;
		try {
			lineDebitWithChildActual = new Line(valuesArray9DebitWithChild);
		} catch (MalformedLineException e) {
			logger.error("Exception in Line constructor from string array "
						+ "(debit, with child): " + e.getMessage(), e);
			fail(e.getMessage());
		}
		Line lineDebitWithChildExpected = new Line();
		lineDebitWithChildExpected.setAmount(-187.96f);
		lineDebitWithChildExpected.setDate(LocalDate.of(2016, 06, 9));
		
		lineDebitWithChildExpected.setDetailedLabel("Prelevmnt \n" +
												" Edf Clients Particuliers Numero De Client : 6002643630 - Num \n" + 
												" Ero De Compte : Xxx 004013118985 " +
												" Fr47edf001007 " +
												" Mm9760026436300001");
		lineDebitWithChildExpected.setNote("");
		lineDebitWithChildExpected.setShortLabel("Edf Clients Particuliers Numero De Client : 6002643630 Num" + 
												" Ero De Compte : Xxx");
		lineDebitWithChildExpected.setType(
				TypeService.fromDetailedLabel(TypeService.AUTO_DEBIT_LONG));
		lineDebitWithChildExpected.addCategorisation(-187.96f, "Factures", "Électricité");
		lineDebitWithChildExpected.addCategorisation(-30.00f, "Loisirs", "Livres");
		
		Validator.validateLine(" constructor test (debit, with child) ", 
								lineDebitWithChildExpected, 
								lineDebitWithChildActual);

		logger.info("testConstructorFromArray finished.");
	}
	
	@Test
	public void testDetailedToShortLabel(){
		logger.info("Starting testDetailedToShortLabel...");

		// ATM (almost not affected)
		String input = "Retrait Au Distributeur \n" + 
						" Ing Amsterdam 19/06";
		
		String expectedOutput = "Ing Amsterdam";
		
		String actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (ATM)", actualOutput, is(expectedOutput));

		// CCard (almost not affected)
		input = "Vertbaudet.Fr Tourcoing Ce 11/05 \n" + 
				" Paiement Par Carte";
		
		expectedOutput = "Vertbaudet.Fr Tourcoing Ce";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (CCard)", actualOutput, is(expectedOutput));
		
		// impots
		input = "Prelevmnt \n" + 
				" Direction Generale Des Finances Pub 720291579559796031 222\n" +  
				" Menm372001156231029 Impot \n" + 
				" Tf \n" + 
				" Fr46zzz005002\n" + 
				" Nnfr46zzz005002m372001156231";
		
		expectedOutput = "Direction Generale Des Finances Pub 222 Impot Tf";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (impots)", actualOutput, is(expectedOutput));
		
		// EDF
		input = "Edf Clients Particuliers \n" + 
				" Prelevmnt \n" + 
				" Numero De Client : 6002643630 - Num \n" + 
				" Ero De Compte : Xxx 004013118985 \n" + 
				" Fr47edf001007 \n" + 
				" Mm9760026436300001";
		
		expectedOutput = "Edf Clients Particuliers" + 
						" Numero De Client : 6002643630 Num" + 
						" Ero De Compte : Xxx";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (EDF)", actualOutput, is(expectedOutput));

		// SETRAM
		input = "Prelevmnt \n" + 
				" Setram Setram Pass Liberte 120427020140519 \n" + 
				" 1027500003 : Echeance Du 15/03/2016 \n" + 
				" Fr30zzz461772 \n" + 
				" 1204270201405191027500003";
		
		expectedOutput = "Setram Setram Pass Liberte 1027500003 : Echeance Du 15/03/2016";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (SETRAM)", actualOutput, is(expectedOutput));

		// CPAM
		input = "C.P.A.M. Le Mans 160710002767 \n" + 
				" Virement En Votre Faveur \n" + 
				" 160710002767160710002767 \n" + 
				" 160710002767";
		
		expectedOutput = "C.P.A.M. Le Mans";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (SETRAM)", actualOutput, is(expectedOutput));

		// Suravenir
		input = "Suravenir \n" + 
				" Prelevmnt \n" + 
				" Cotisation Emprunteur - Police 0058 \n" + 
				" 30577 \n" + 
				" Fr37zzz263184 \n" + 
				" Surav14051000000000862261";
		
		expectedOutput = "Suravenir " + 
						"Cotisation Emprunteur Police 0058 " + 
						"30577";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (Suravenir)", actualOutput, is(expectedOutput));

		// Tres. du Mans
		input = "Prelevmnt \n" + 
				" Tres. Le Mans Ville Tipsepa Produits Locaux 249:ville D \n" + 
				" U Mans Fr22zzz504111 Centre \n" +  
				" Encaissement De Rennes \n" +  
				" Fr22zzz504111 \n" +  
				" Tipsepa0720332490000000000136764816";
		
		expectedOutput = "Tres. Le Mans Ville Tipsepa Produits Locaux D " + 
						"U Mans Centre " +  
						"Encaissement De Rennes";
		
		actualOutput = Line.detailedToShort(input);
		
		assertThat("from testDetailedToShortLabel (Tres. du Mans)", actualOutput, is(expectedOutput));
		
		
		logger.info("testDetailedToShortLabel finished.");
	}

	@Test
	public void testExtractDate(){
		logger.info("Starting testExtractDate...");

		// Auto Debit
		String input = "Cdn Le Mans 1 18/03 12h48  \n" +  
						" Retrait Au Distributeur";
		
		LocalDate expectedOutput = LocalDate.of(2016, 03, 18);
		
		LocalDate actualOutput = Line.extractDate(input);
		
		assertThat("wrong date from auto debit", actualOutput, is(expectedOutput));

		// CCARD (date before)
		input = "Dpam Le Mans 26/02 \n" +
				" Paiement Par Carte";
		
		expectedOutput = LocalDate.of(2016, 02, 26);
		
		actualOutput = Line.extractDate(input);
		
		assertThat("wrong date from CCARD (date before)", actualOutput, is(expectedOutput));

		// CCARD (date after)
		input = "Paiement Par Carte  \n" +
				" Phcie Leduc Le Mans 27/07";
		
		expectedOutput = LocalDate.of(2016, 07, 27);
		
		actualOutput = Line.extractDate(input);
		
		assertThat("wrong date from CCARD (date after)", actualOutput, is(expectedOutput));

		// no date
		input = "Paiement";
		
		expectedOutput = null;
		
		actualOutput = Line.extractDate(input);
		
		assertThat("wrong date, should be null", actualOutput, is(expectedOutput));
		
		logger.info("testExtractDate finished.");
	}
	
}
