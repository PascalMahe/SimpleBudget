package fr.pascalmahe.business;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class MonthInYear implements Comparable<MonthInYear> {

	private Month month;
	
	private Integer year;
	
	public MonthInYear(Month mo, int ye){
		month = mo;
		year = ye;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		MonthInYear other = (MonthInYear) obj;
		if (month != other.month)
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}



	@Override
	public int compareTo(MonthInYear o) {
		int yearComparison = year.compareTo(o.year);
		int result = yearComparison;
		if(yearComparison == 0){
			result = month.compareTo(o.month);
		}
		return result;
	}
	
	public String toString(){
		int value = month.getValue();
		String strValue = "";
		if(value < 10){
			strValue = "0";
		}
		strValue += value + "/" + year;
		return strValue;
	}

	public Month getMonth() {
		return month;
	}

	public List<MonthInYear> rangeTo(MonthInYear youngMonth) {
		List<MonthInYear> result = new ArrayList<>();
		MonthInYear currentMonth = this;
		while(currentMonth.compareTo(youngMonth) <= 0){
			result.add(currentMonth);
			currentMonth = currentMonth.nextMonth();
		}
		return result;
	}

	private MonthInYear nextMonth() {
		Month nextMonthWithin = this.month.plus(1l);
		Integer year = this.year;
		if(nextMonthWithin.getValue() < month.getValue()){
			// if we've looped back from DEC to JAN
			// add one to year
			year++;
		}
		return new MonthInYear(nextMonthWithin, year);
	}
	
}
