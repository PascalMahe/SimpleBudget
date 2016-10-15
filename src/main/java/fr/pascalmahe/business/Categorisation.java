package fr.pascalmahe.business;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Class representing the category for part (up to 100%) of a Line.
 * @author Pascal
 */
@Entity
public class Categorisation implements Serializable {

	private static final long serialVersionUID = -7871636425686858451L;

	@Id
	@GeneratedValue
	private Integer id;
	
	private Float amount;
	
	@Column(nullable = false)
	private Category category;

	public Categorisation() {}
	
	@Override
	public String toString() {
		return "Categorisation [id=" + id + ", amount=" + amount + ", category=" + category + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Categorisation other = (Categorisation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getFormattedAmount(){
		return String.format("%.2f", amount);
	}
	
	public Categorisation(Float amount, Category cat) {
		this.amount = amount;
		this.category = cat;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}


    
}
