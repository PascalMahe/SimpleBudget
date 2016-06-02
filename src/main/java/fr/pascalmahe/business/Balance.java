package fr.pascalmahe.business;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * Classe representing an account's line
 * @author Pascal
 */
@Entity
public class Balance implements Serializable {

    
	private static final long serialVersionUID = 8125873957308428169L;

	@Id
	@GeneratedValue
	private Integer id;
	
    private LocalDate fetchDate;
    
    private Float amount;

	public Balance() {}

	public Balance(Integer id, LocalDate fetchDate, Float amount) {
		super();
		this.id = id;
		this.fetchDate = fetchDate;
		this.amount = amount;
	}

	public Balance(LocalDate fetchDate, Float amount) {
		super();
		this.fetchDate = fetchDate;
		this.amount = amount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return fetchDate;
	}

	public void setDate(LocalDate fetchDate) {
		this.fetchDate = fetchDate;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((fetchDate == null) ? 0 : fetchDate.hashCode());
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
		Balance other = (Balance) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (fetchDate == null) {
			if (other.fetchDate != null)
				return false;
		} else if (!fetchDate.equals(other.fetchDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Balance [id=" + id + ", fetchDate=" + fetchDate + ", amount=" + amount + "]";
	}

	   
    
    
}
