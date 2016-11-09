package fr.pascalmahe.web.beans;

import java.io.Serializable;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Class for manipulating values in the categoryList page.
 * One value has a name (the name of the month it represents)
 * and 2 values: the sum of the amounts of the Lines with the
 * CatRow's Category and a positive amount, and the sum for
 * the Lines with the same category but a negative amount.
 * @author Pascal
 */
public class MonthCell implements Serializable {

	private static final long serialVersionUID = 4803419244792393431L;

	private Month monthValue;
	
	private Float negAmount;
	
	private Float posAmount;

	public MonthCell(Month month, float posAmount, float negAmount) {
		this.monthValue = month;
		this.posAmount = posAmount;
		this.negAmount = negAmount;
	}

	public Float getNegAmount() {
		return negAmount;
	}

	public void setNegAmount(Float negAmount) {
		this.negAmount = negAmount;
	}

	public Float getPosAmount() {
		return posAmount;
	}

	public void setPosAmount(Float posAmount) {
		this.posAmount = posAmount;
	}

	public String getName() {
		return monthValue.getDisplayName(TextStyle.FULL, Locale.FRENCH);
	}
}
