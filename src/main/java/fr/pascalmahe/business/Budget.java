package fr.pascalmahe.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


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
	
	@ManyToOne(targetEntity=Category.class)
	private List<Category> categoryMap;
	
	private Float maxAmount;
	
	private Date startDate; // the date from which this budget counts lines
	
	private Date endDate; // the date up to which this budget counts lines

	public Budget() {}

	public Budget(Integer id, 
					String name, 
					List<Category> categoryMap, 
					Float maxAmount, 
					Date startDate,
					Date endDate) {
		super();
		this.id = id;
		this.name = name;
		this.categoryMap = categoryMap;
		this.maxAmount = maxAmount;
		this.startDate = startDate;
		this.endDate = endDate;
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

	public List<Category> getCategoryMap() {
		return categoryMap;
	}

	public void setCategoryMap(List<Category> categoryMap) {
		this.categoryMap = categoryMap;
	}

	public Float getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(Float maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryMap == null) ? 0 : categoryMap.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((maxAmount == null) ? 0 : maxAmount.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		Budget other = (Budget) obj;
		if (categoryMap == null) {
			if (other.categoryMap != null)
				return false;
		} else if (!categoryMap.equals(other.categoryMap))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maxAmount == null) {
			if (other.maxAmount != null)
				return false;
		} else if (!maxAmount.equals(other.maxAmount))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Budget [id=" + id + ", name=" + name + ", categoryMap=" + categoryMap + ", maxAmount=" + maxAmount
				+ ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	
	
}
