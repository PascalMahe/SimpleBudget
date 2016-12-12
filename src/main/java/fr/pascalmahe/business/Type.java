package fr.pascalmahe.business;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * Class representing an line's type (credit card, transfer, ATM...)
 * @author Pascal
 */
@Entity
public class Type implements Serializable {

	private static final long serialVersionUID = 9159139161365087772L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
    private String name;

	public static final String TRANSFER_IN_UR_FAVOR = "Virement En Votre Faveur";

	public static final String TRANSFER = "Virement";

	public static final String PAYMENT = "Reglement";

	public static final String LOAN_PAYMENT = "Remboursement De Pret";

	public static final String FEE = "Cotisation";

	public static final String CREDIT = "Avoir";

	public static final String CHECK = "Cheque";

	public static final String CHARGES = "Frais";

	public static final String CCARD_PAYMENT = "Paiement Par Carte";

	public static final String ATM = "Retrait Au Distributeur";

	public static final String AUTO_DEBIT_LONG = "Prelevement";

	public static final String AUTO_DEBIT_SHORT = "Prelevmnt";

	public Type() {}
	
	public Type(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Type [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Type other = (Type) obj;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
