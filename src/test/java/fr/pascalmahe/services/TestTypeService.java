package fr.pascalmahe.services;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.pascalmahe.business.Type;

public class TestTypeService extends BulkImportService {

	private static final Logger logger = LogManager.getLogger();

	private static final String AUTO_DEBIT_FIRST_LINE = 
			"Prelevmnt \n" +
			" Direction Generale Des Finances Pub 720291579559796031 222 \n" +
			" Menm272004067057029 Impot \n" +
			" Th \n" +
			" Fr46zzz005002 \n" +
			" ++fr46zzz005002m272004067057";

	private static final String ATM_FIRST_LINE =
			"Retrait Au Distributeur \n" + 
			" Le Mans Republiq 10/07 09h53";
	
	private static final String CC_PAYMENT_FIRST_LINE = 
			"Paiement Par Carte \n" + 
			" Selarl Anespre Le Mans 13/07";

	private static final String CHARGES_FIRST_LINE = 
			"Frais \n" +  
			" Int Deb 2 Trim 16 Taeg=17,67%";
	
	private static final String CHECK_FIRST_LINE = 
			"Cheque Emis \n" + 
			" 3837250";
	
	private static final String CREDIT_FIRST_LINE = 
			"Avoir \n" +
			" Carte La Redoute 13/07";
	
	private static final String FEE_FIRST_LINE = 
			"Cotisation \n" + 
			" Compte A Composer";
	
	private static final String LOAN_PAYMENT_FIRST_LINE = 
			"Remboursement De Pret \n" +
			" 10000067658 Echeance 06/07/16";
	
	private static final String PAYMENT_FIRST_LINE = 
			"Reglement \n" +
			" Assurance Pret Habitat 07/16";
	
	private static final String TRANSFER_FIRST_LINE = 
			"Virement \n" +
			"Caf De La Sarthe 004921319v16 Xpxreference 004921319 Jo \n" + 
			" 1189093jmahe 120716jo \n" +
			" 004921319v16";

	private static final String AUTO_DEBIT_OTHER_LINE = 
			"Frais Carte Google *google Stora\n" +
			" Prelevement";

	private static final String ATM_OTHER_LINE =
			"Cdn Le Mans 1 26/02 12h48 \n" +
			" Retrait Au Distributeur";
	
	private static final String CC_PAYMENT_OTHER_LINE = 
			"Saint Come Le Mans 13/02 \n" + 
			" Paiement Par Carte";

	private static final String CHARGES_OTHER_LINE = 
			"Int Deb 1 Trim 16 Taeg=17,62% \n" + 
			" Frais";
	
	private static final String CHECK_OTHER_LINE = 
			"5269252 \n" +
			" Cheque Emis";
	
	private static final String CREDIT_OTHER_LINE = 
			"Carte Kickers And Co 30/01 \n" + 
			" Avoir";
	
	private static final String FEE_OTHER_LINE = 
			"Compte A Composer \n" + 
			" Cotisation";

	private static final String LOAN_PAYMENT_OTHER_LINE = 
			"10000067656 Echeance 06/02/16 \n" + 
			" Remboursement De Pret";
	
	private static final String PAYMENT_OTHER_LINE = 
			"Assurance Pret Habitat 02/16 \n" + 
			" Reglement";
	
	private static final String TRANSFER_OTHER_LINE = 
			"C.P.A.M. Le Mans 160550006747 \n" +
			" Virement En Votre Faveur \n" +
			" 160550006747160550006747 \n" +
			" 160550006747";
	
	@BeforeClass
	public static void beforeClass(){
		logger.info("Starting beforeClass...");
		
		
		logger.info("beforeClass finished.");
	}

	@AfterClass
	public static void afterClass() {
		logger.info("Starting afterClass...");
		
		logger.info("afterClass finished.");
	}
	
	@Test
	public void testFromDetailedLabel() {
		logger.info("Starting testFromDetailedLabel...");
		
		Type fetchedType;
		
		// Type before rest of label

		// AUTO_DEBIT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.AUTO_DEBIT_LONG);
		fetchedType = TypeService.fromDetailedLabel(AUTO_DEBIT_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.AUTO_DEBIT_LONG));

		// ATM
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.ATM);
		fetchedType = TypeService.fromDetailedLabel(ATM_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.ATM));

		// CCARD_PAYMENT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CCARD_PAYMENT);
		fetchedType = TypeService.fromDetailedLabel(CC_PAYMENT_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CCARD_PAYMENT));

		// CHARGES
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CHARGES);
		fetchedType = TypeService.fromDetailedLabel(CHARGES_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CHARGES));

		// CHECK
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CHECK);
		fetchedType = TypeService.fromDetailedLabel(CHECK_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CHECK));

		// CREDIT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CREDIT);
		fetchedType = TypeService.fromDetailedLabel(CREDIT_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CREDIT));

		// FEE
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.FEE);
		fetchedType = TypeService.fromDetailedLabel(FEE_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.FEE));

		// LOAN_PAYMENT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.LOAN_PAYMENT);
		fetchedType = TypeService.fromDetailedLabel(LOAN_PAYMENT_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.LOAN_PAYMENT));

		// PAYMENT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.PAYMENT);
		fetchedType = TypeService.fromDetailedLabel(PAYMENT_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.PAYMENT));

		// TRANSFER
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.TRANSFER);
		fetchedType = TypeService.fromDetailedLabel(TRANSFER_FIRST_LINE);
		assertThat(fetchedType.getName(), is(TypeService.TRANSFER));
		
		// Type after rest of label

		// AUTO_DEBIT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.AUTO_DEBIT_LONG);
		fetchedType = TypeService.fromDetailedLabel(AUTO_DEBIT_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.AUTO_DEBIT_LONG));

		// ATM
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.ATM);
		fetchedType = TypeService.fromDetailedLabel(ATM_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.ATM));

		// CCARD_PAYMENT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CCARD_PAYMENT);
		fetchedType = TypeService.fromDetailedLabel(CC_PAYMENT_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CCARD_PAYMENT));

		// CHARGES
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CHARGES);
		fetchedType = TypeService.fromDetailedLabel(CHARGES_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CHARGES));

		// CHECK
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CHECK);
		fetchedType = TypeService.fromDetailedLabel(CHECK_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CHECK));

		// CREDIT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.CREDIT);
		fetchedType = TypeService.fromDetailedLabel(CREDIT_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.CREDIT));

		// FEE
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.FEE);
		fetchedType = TypeService.fromDetailedLabel(FEE_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.FEE));

		// LOAN_PAYMENT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.LOAN_PAYMENT);
		fetchedType = TypeService.fromDetailedLabel(LOAN_PAYMENT_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.LOAN_PAYMENT));

		// PAYMENT
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.PAYMENT);
		fetchedType = TypeService.fromDetailedLabel(PAYMENT_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.PAYMENT));

		// TRANSFER
		logger.debug("testFromDetailedLabel - Expecting : " + TypeService.TRANSFER);
		fetchedType = TypeService.fromDetailedLabel(TRANSFER_OTHER_LINE);
		assertThat(fetchedType.getName(), is(TypeService.TRANSFER));
		
		logger.info("testFromDetailedLabel finished.");
	}

}