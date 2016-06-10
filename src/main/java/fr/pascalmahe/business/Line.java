package fr.pascalmahe.business;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;


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
	
    private LocalDate date;
    
    private String mainLabel;
    
    private String secondaryLabel;
    
    private Float amount;
    
    private Boolean recurring;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categorisation> categorisationList;

	public Line() {
		categorisationList = new ArrayList<>();
	}
    
	public Line(Integer id, 
			LocalDate date, 
				String mainLabel, 
				String secondaryLabel, 
				Float amount, 
				Boolean recurring,
				List<Categorisation> categorisationList) {
		super();
		this.id = id;
		this.date = date;
		this.mainLabel = mainLabel;
		this.secondaryLabel = secondaryLabel;
		this.amount = amount;
		this.recurring = recurring;
		this.categorisationList = categorisationList;
	}

	public Line(LocalDate date, 
				String mainLabel, 
				String secondaryLabel, 
				Float amount, 
				Boolean recurring,
				List<Categorisation> categorisationList) {
		super();
		this.date = date;
		this.mainLabel = mainLabel;
		this.secondaryLabel = secondaryLabel;
		this.amount = amount;
		this.recurring = recurring;
		this.categorisationList = categorisationList;
	}

	@Override
	public String toString() {
		return "Line [id=" + id + ", date=" + date + ", mainLabel=" + mainLabel + ", secondaryLabel=" + secondaryLabel
				+ ", amount=" + amount + ", recurring=" + recurring + ", categorisationList=" + categorisationList + "]";
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

	public List<Categorisation> getCategorisationList() {
		return categorisationList;
	}

	public void setCategorisationList(List<Categorisation> categorisationList) {
		this.categorisationList = categorisationList;
	}
}
