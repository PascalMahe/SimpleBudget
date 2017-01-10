package fr.pascalmahe.web.beans;

import java.io.Serializable;
import java.time.format.TextStyle;
import java.util.Locale;

import fr.pascalmahe.business.MonthInYear;

/**
 * Class for manipulating values in the categoryList page.
 * One value has a name (the name of the month it represents)
 * and 2 values: the sum of the amounts of the Lines with the
 * CatRow's Category and a positive amount, and the sum for
 * the Lines with the same category but a negative amount.
 * @author Pascal
 */
public class MonthCell implements Serializable, Comparable<MonthCell> {

	private static final long serialVersionUID = 4803419244792393431L;

	private MonthInYear monthValue;
	
	private Float negAmount;
	
	private Float posAmount;

	public MonthCell(MonthInYear month, float posAmount, float negAmount) {
		this.monthValue = month;
		this.posAmount = posAmount;
		this.negAmount = negAmount;
	}

	public MonthInYear getMonthValue() {
		return monthValue;
	}

	public void setMonthValue(MonthInYear monthValue) {
		this.monthValue = monthValue;
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
		return monthValue.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
	}

	public void addPosAmount(Float posAmount) {
		this.posAmount += posAmount;
	}
	
	public void addNegAmount(Float negAmount) {
		this.negAmount += negAmount;
	}
	
	

	@Override
	public String toString() {
		return "[" + negAmount + ", " + posAmount + "]";
	}

	@Override
	public int compareTo(MonthCell o) {
		// invert to get an inverse order (desc instead of asc order
		// and vice-versa)
		return -this.monthValue.compareTo(o.getMonthValue());
	}

	public void addAmount(Float amount) {
		if(amount > 0){
			posAmount += amount;
		} else {
			negAmount += amount;
		}
		
	}

}
