package fr.pascalmahe.business;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


/**
 * Class representing a budget
 * @author Pascal
 */
@Entity
public class Budget implements Serializable {

	private static final long serialVersionUID = -8362985702527336043L;

	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Category> categoryList;
	
	private Float maxAmount;
	
	private LocalDate startDate; // the date from which this budget counts lines
	
	private LocalDate endDate; // the date up to which this budget counts lines

	public Budget() {}

	public Budget(Integer id, 
					String name, 
					List<Category> categoryList, 
					Float maxAmount, 
					LocalDate startDate,
					LocalDate endDate) {
		super();
		this.id = id;
		this.name = name;
		this.categoryList = categoryList;
		this.maxAmount = maxAmount;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Budget(String name, 
					List<Category> categoryList, 
					Float maxAmount, 
					LocalDate startDate,
					LocalDate endDate) {
		super();
		this.name = name;
		this.categoryList = categoryList;
		this.maxAmount = maxAmount;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Budget other = (Budget) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "Budget [id=" + id + ", name=" + name + ", categoryList=" + categoryList + ", maxAmount=" + maxAmount
				+ ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

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

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryMap) {
		this.categoryList = categoryMap;
	}

	public Float getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(Float maxAmount) {
		this.maxAmount = maxAmount;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

}
