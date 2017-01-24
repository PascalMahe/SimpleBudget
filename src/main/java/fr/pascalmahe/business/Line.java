package fr.pascalmahe.business;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.pascalmahe.ex.MalformedLineException;
import fr.pascalmahe.services.AccountService;
import fr.pascalmahe.services.TypeService;


/**
 * Class representing an account's line
 * @author Pascal
 */
@Entity
public class Line implements Serializable, Comparable<Line> {

	private static final Logger logger = LogManager.getLogger();
    
	private static final long serialVersionUID = 8125873957308428169L;

	private static final DateTimeFormatter frDTFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private static final DateTimeFormatter isoDTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Id
	@GeneratedValue
	private Integer id;
	
    private LocalDate date;
    
    private LocalDate cCardDate;
    
    private String detailedLabel;
    
    private String shortLabel;
    
    private String note;
    
    private Float amount;
    
    private Boolean recurring;
    
    private Type type;
    
    private Account account;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categorisation> categorisationList;

	public Line() {
		categorisationList = new ArrayList<>();
	}
    
	public Line(Integer id, 
				LocalDate date, 
				String detailedLabel, 
				String shortLabel, 
				Float amount, 
				Boolean recurring,
				Type type,
				Account account,
				List<Categorisation> categorisationList) {
		super();
		this.id = id;
		this.date = date;
		this.detailedLabel = detailedLabel;
		this.shortLabel = shortLabel;
		this.amount = amount;
		this.recurring = recurring;
		this.type = type;
		this.account = account;
		this.categorisationList = categorisationList;
	}

	public Line(LocalDate date, 
				LocalDate cCardDate,
				String detailedLabel, 
				String shortLabel,
				String note, 
				Float amount, 
				Boolean recurring,
				Type type,
				Account account,
				List<Categorisation> categorisationList) {
		super();
		this.date = date;
		this.cCardDate = cCardDate;
		this.detailedLabel = detailedLabel;
		this.shortLabel = shortLabel;
		this.note = note;
		this.amount = amount;
		this.recurring = recurring;
		this.type = type;
		this.account = account;
		this.categorisationList = categorisationList;
	}

	public Line(String[] currentLineValues) throws MalformedLineException {
		// Line models: 10 values (9 commas)
		// 					Date, 
		// 					DetailedLabel, 
		// 					Debit €, 
		// 					Credit €, 
		// 					Category, 
		// 					Child-category, 
		// 					Notes, 
		// 					2nd Category, 
		// 					2nd Child-catégorie, 
		// 					Amount
		
		// cCardDate and shortLabel are deduced from the detailedLabel
		
		if(currentLineValues.length != 10){
			throw new MalformedLineException("Error in array of values to project "
					+ "to Line: array should contain exactly 10 values, but has " 
					+ currentLineValues.length + ".");
		}
		
		// loading string values
		String strDate = currentLineValues[0].replace("\n", ""); // removing the newlines
		String detailedLabel = currentLineValues[1].replace("\"", ""); // stripping the quotes from CSV file
		String strDebitAmount = currentLineValues[2].replace(" ", "").replace("€", "");
		String strCreditAmount = currentLineValues[3].replace(" ", "").replace("€", "");
		
		String categoryName = currentLineValues[4].trim().replace("\n", "");
		String childCategoryName = currentLineValues[5].trim().replace("\n", "");
		String note = currentLineValues[6].trim();

		String secCategoryName = currentLineValues[7].trim();
		String secChildCategoryName = currentLineValues[8].trim();
		String strSecCategoryAmount = currentLineValues[9].trim();
		
		// date parsing
		LocalDate date;
		try {
			date = LocalDate.parse(
					strDate, 
					isoDTFormatter);
		} catch (DateTimeParseException dtpe){
			throw new MalformedLineException("Error while parsing date "
					+ "'" + strDate + "': " + dtpe.getLocalizedMessage(), 
					dtpe);
		}
		
		// amount (float) parsing
		String strAmount;
		boolean creditIsBlank = StringUtils.isBlank(strCreditAmount);
		boolean debitIsBlank = StringUtils.isBlank(strDebitAmount);
		if(!creditIsBlank && !debitIsBlank){
			throw new MalformedLineException("Line can't have both debit and credit amount. "
					+ "Found: '" + strCreditAmount + "' for credit and '" + strDebitAmount + '.');
		}
		
		if(!creditIsBlank){
			// credit
			strAmount = strCreditAmount;
		} else {
			// debit
			strAmount = "-" + strDebitAmount;
		}
		
		float amount = strToFloatAmount(strAmount);
		
		// category (& categorisation) parsing
		Category cat = new Category(categoryName);
		
		if(StringUtils.isNotBlank(childCategoryName)){
			// if there is a child Category, that's the category we want
			cat = new Category(childCategoryName, cat);
		}
		
		// assignments (simple, direct ones)
		this.amount = amount;
		this.date = date;
		this.detailedLabel = detailedLabel;
		this.shortLabel = detailedToShort(detailedLabel);
		this.type = TypeService.fromDetailedLabel(detailedLabel);
		this.note = note;
		this.account = AccountService.fromDetailedLabel(detailedLabel);
		
		Type cCardType = TypeService.fromDetailedLabel(Type.CCARD_PAYMENT);
		Type autoType = TypeService.fromDetailedLabel(Type.AUTO_DEBIT_LONG);
		if(type.equals(cCardType) || type.equals(autoType)){
			this.cCardDate = extractDate(detailedLabel, date);
		}
		
		this.categorisationList = new ArrayList<>();
		
		// amount assignment here so that it can be modified if 2nd cat
		float firstCategoAmount = amount;
		
		// 2nd cat -> only if needed
		if(StringUtils.isNotBlank(secCategoryName)){
			Category secCat = new Category(secCategoryName);
			if(StringUtils.isNotBlank(secChildCategoryName)){
				secCat = new Category(secChildCategoryName, secCat);
			}
			
			float secAmount = strToFloatAmount(strSecCategoryAmount);
			if(Math.abs(amount) < Math.abs(secAmount)){
				throw new MalformedLineException("Error: can't have the amount for the "
						+ "second category superior to the total amount.");
			}
			
			// making sure there both negative XOR both positive
			if((amount < 0 && secAmount > 0) ||
					(amount > 0 && secAmount < 0)){
				secAmount = -secAmount;
			}
			
			firstCategoAmount = amount - secAmount;
			
			categorisationList.add(new Categorisation(secAmount, secCat));
		}
		
		// add to catego here, in case it was modified because of 2nd amount
		categorisationList.add(new Categorisation(firstCategoAmount, cat));
	}

	protected static LocalDate extractDate(String detailedLabel, LocalDate date) {
		
		LocalDate result = null;
		int dateStartIndex, dateEndIndex;
		
		String datePatternRegex = "\\d\\d/\\d\\d"; // digit digit / digit digit
		Pattern datePattern = Pattern.compile(datePatternRegex);
		Matcher dateMatcher = datePattern.matcher(detailedLabel);
		
		if(dateMatcher.find()){ // NB : find() moves cursor in String
			dateStartIndex = dateMatcher.start();
			dateEndIndex = dateMatcher.end();
			
			String dateStr = detailedLabel.substring(dateStartIndex, dateEndIndex);
			dateStr += "/" + date.getYear();
			DateTimeFormatter formatter = frDTFormatter;
			try {
				result = LocalDate.parse(dateStr, formatter);
				
				// checking months to make sure that using date's year 
				// was the right idea (it's not when the date is in december
				// but the credit card date is in january, for example)
				
				// if date.month = 12 & cCardDate = 1
				// cCardDate is next year from date
				// cCardDate'year is date's year + 1
				if(date.getMonthValue() == 12 && result.getMonthValue() == 1){
					result = result.plusYears(1);
				} else if(date.getMonthValue() == 1 && result.getMonthValue() == 12){
					result = result.plusYears(-1);
				}
			} catch (DateTimeException dte) {
				logger.debug("extractDate - Date '" + dateStr + "' could not be parsed, leaving null as cCardDate.");
			}
		}
		
		return result;
	}

	private float strToFloatAmount(String strAmount) {
		strAmount = strAmount.replace("€", ""); // final € sign
		strAmount = strAmount.replace("€", "");
		strAmount = strAmount.replace(" ", ""); // no space between thousands (4th & 3rd digits) THIS IS SPACE FROM CSV
		strAmount = strAmount.replace(" ", ""); // another type of space (?) THIS IS SPACE FROM SPACE KEY
		strAmount = strAmount.replace(",", "."); // no comma between units and decimals (0th and -1th digit)
		strAmount = strAmount.replace("\"", ""); // quotes come with text from CSV
		return Float.parseFloat(strAmount);
	}

	protected static String detailedToShort(String detailedLabel) {
		
		String shortLabel = detailedLabel.replace("\n", "");
		
		// to avoid a problem when an auto debit is labelled "fee" by the
		// other party, auto debits don't affect the others
		// (and to keep it readable they're the only ones)
		if(shortLabel.contains(Type.AUTO_DEBIT_LONG) ||
				shortLabel.contains(Type.AUTO_DEBIT_SHORT)){
			shortLabel = shortLabel.replace(Type.AUTO_DEBIT_LONG, "");
			shortLabel = shortLabel.replace(Type.AUTO_DEBIT_SHORT, "");
		} else {
			shortLabel = shortLabel.replace(Type.ATM, "");
			shortLabel = shortLabel.replace(Type.CCARD_PAYMENT, "");
			shortLabel = shortLabel.replace(Type.CHARGES, "");
			// shortLabel = shortLabel.replace(TypeService.CHECK, "");
			// Cheque can stay 'cause it's often all there is as label (or close enough)
			shortLabel = shortLabel.replace(Type.CREDIT, "");
			shortLabel = shortLabel.replace(Type.FEE, "");
			shortLabel = shortLabel.replace(Type.LOAN_PAYMENT, "");
			shortLabel = shortLabel.replace(Type.PAYMENT, "");
			shortLabel = shortLabel.replace(Type.TRANSFER_IN_UR_FAVOR, "");
			// TRANSFER after TRANSFER_IN_UR_FAVOR so that TRANSFER_IN_UR_FAVOR is not partially erased
			shortLabel = shortLabel.replace(Type.TRANSFER, "");
		}
		
		
		// removing references from label:
		// references (IDs...) are for computers and hurt readability
		// -> chuck'em!
		// by breaking the string down word by word and putting it 
		// back together without long numbers (10+) and alphanumerical
		// thingies
		
		// split by word (sorta)
		String[] shortAsArray = shortLabel.split(" ");
		shortLabel = "";
		for(String currWord : shortAsArray){
			boolean containsNumber = currWord.matches("[\\d/]+"); 	// \d -> digit
																	// /  -> for dates
			// works because only ASCII can be in a label
			boolean containsLetters = currWord.matches("[a-zA-Z.:]+");
			
			boolean excluded = false;
			if(containsNumber && !containsLetters && currWord.length() > 10){
				// numbers with more than 10 digits are excluded
				// 10 so that dates like dd/mm/yyyy are included
				excluded = true;
			}
			
			// for some reason when there are letters and numbers, both regex fail
			if((containsNumber && containsLetters) || 
					(!containsNumber && !containsLetters)){
				// unholy abominations mixing words and letters are excluded
				excluded = true;
			}
			
			// short dates are excluded (they're redundant:
			// the line's date is always shown)
			if(currWord.matches("\\d\\d/\\d\\d")){
				excluded = true;
			}
			
			if(!excluded){
				if(shortLabel.length() > 0){
					shortLabel += " ";
				}
				shortLabel += currWord;
			}
		}
		
		return shortLabel;
	}

	public void addCategorisation(float amount, String catName) {
		addCategorisation(amount, null, catName);
	}

	public void addCategorisation(float amount, String fatherCatName, String catName) {
		if(this.categorisationList == null){
			categorisationList = new ArrayList<>();
		}
		Category fatherCat = null;
		if(fatherCatName != null){
			fatherCat = new Category(fatherCatName);
		}
		Categorisation catego = new Categorisation(amount, new Category(catName, fatherCat));
		categorisationList.add(catego);
	}
	
	public LocalDate getEffectiveDate(){
		LocalDate effectiveDate = cCardDate;
		if(effectiveDate == null){
			effectiveDate = date;
		}
		return effectiveDate;
	}
	
	public String getFormattedDate(){
		return getEffectiveDate().format(frDTFormatter);
	}

	public String getFormattedRealDate(){
		return date.format(frDTFormatter);
	}

	public String getFormattedCCardDate(){
		String ret = "";
		if(cCardDate != null){
			ret = cCardDate.format(frDTFormatter);
		}
		return ret;
	}
	
	public String getFormattedTypeName(){
		String typeName = type.getName();
		if(amount > 0.0f){
			typeName += "-positif";
		}
		return typeName;
	}

	public String getFormattedClass(){
		String fClass = "neg";
		if(amount > 0.0f){
			fClass = "pos";
		}
		return fClass;
	}

	public String getFormattedAmount(){
		return String.format("%.2f", amount);
	}
	
	@Override
	public String toString() {
		return "Line [id=" + id + 
				", date=" + date +
				", cCardDate=" + cCardDate + 
				", detailedLabel=" + detailedLabel + 
				", secondaryLabel=" + shortLabel + 
				", note=" + note + 
				", amount=" + amount + 
				", type=" + type + 
				", recurring=" + recurring + 
				", categorisationList=" + categorisationList + 
		"]";
	}

	@Override
	public int compareTo(Line otherLine) {
		return this.getEffectiveDate().compareTo(otherLine.getEffectiveDate());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Line other = (Line) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDate getCCardDate() {
		return cCardDate;
	}

	public void setCCardDate(LocalDate cCardDate) {
		this.cCardDate = cCardDate;
	}

	public String getDetailedLabel() {
		return detailedLabel;
	}

	public void setDetailedLabel(String detailedLabel) {
		this.detailedLabel = detailedLabel;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getShortLabel() {
		return shortLabel;
	}

	public void setShortLabel(String shortLabel) {
		this.shortLabel = shortLabel;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Boolean getRecurring() {
		return recurring;
	}

	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Categorisation> getCategorisationList() {
		return categorisationList;
	}

	public void setCategorisationList(List<Categorisation> categorisationList) {
		this.categorisationList = categorisationList;
	}

	public Categorisation getCategorisationById(Integer categoId) {
		
		Categorisation returnedOne = null;
		
		for(Categorisation catego : this.categorisationList){
			if(catego.getId() == categoId){
				returnedOne = catego;
				break;
			}
		}
		return returnedOne;
	}

	public LocalDate getcCardDate() {
		return cCardDate;
	}

	public void setcCardDate(LocalDate cCardDate) {
		this.cCardDate = cCardDate;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	
	
}
