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
	public int compareTo(MonthInYear o) {
		int yearComparison = year.compareTo(o.year);
		int result = yearComparison;
		if(yearComparison == 0){
			result = month.compareTo(o.month);
		}
		return result;
	}
	
	public String toString(){
		return month.getValue() + "/" + year;
	}

	public Month getMonth() {
		return month;
	}

	public List<MonthInYear> rangeTo(MonthInYear youngMonth) {
		List<MonthInYear> result = new ArrayList<>();
		MonthInYear currentMonth = this.nextMonth();
		while(currentMonth.compareTo(youngMonth) < 0){
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
