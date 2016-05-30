package fr.pascalmahe.business;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Classe representing an account's line
 * @author Pascal
 */
@Entity
public class Line implements Serializable {

    
	private static final long serialVersionUID = 8125873957308428169L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Temporal(TemporalType.DATE)
    private Date date;
    
    private String mainLabel;
    
    private String secondaryLabel;
    
    private Float amount;
    
    private Boolean recurring;
    
    private Category category;


	public Line() {}

    
	public Line(Integer id, Date date, String mainLabel, String secondaryLabel, Float amount, Boolean recurring,
			Category category) {
		super();
		this.id = id;
		this.date = date;
		this.mainLabel = mainLabel;
		this.secondaryLabel = secondaryLabel;
		this.amount = amount;
		this.recurring = recurring;
		this.category = category;
	}


	@Override
	public String toString() {
		return "Line [id=" + id + ", date=" + date + ", mainLabel=" + mainLabel + ", secondaryLabel=" + secondaryLabel
				+ ", amount=" + amount + ", recurring=" + recurring + ", category=" + category + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mainLabel == null) ? 0 : mainLabel.hashCode());
		result = prime * result + ((recurring == null) ? 0 : recurring.hashCode());
		result = prime * result + ((secondaryLabel == null) ? 0 : secondaryLabel.hashCode());
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
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mainLabel == null) {
			if (other.mainLabel != null)
				return false;
		} else if (!mainLabel.equals(other.mainLabel))
			return false;
		if (recurring == null) {
			if (other.recurring != null)
				return false;
		} else if (!recurring.equals(other.recurring))
			return false;
		if (secondaryLabel == null) {
			if (other.secondaryLabel != null)
				return false;
		} else if (!secondaryLabel.equals(other.secondaryLabel))
			return false;
		return true;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMainLabel() {
		return mainLabel;
	}

	public void setMainLabel(String mainLabel) {
		this.mainLabel = mainLabel;
	}

	public String getSecondaryLabel() {
		return secondaryLabel;
	}

	public void setSecondaryLabel(String secondaryLabel) {
		this.secondaryLabel = secondaryLabel;
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
    
    
    
}
