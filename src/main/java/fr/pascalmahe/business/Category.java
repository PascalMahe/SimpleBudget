package fr.pascalmahe.business;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * Class representing a category of an account's lines.
 * @author Pascal
 */
@Entity
public class Category implements Serializable {


	private static final long serialVersionUID = 3208053020237026635L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	
	private Category fatherCategory;

	public Category() {}

	public Category(Integer id, String name, Category fatherCategory) {
		super();
		this.id = id;
		this.name = name;
		this.fatherCategory = fatherCategory;
	}

	public Category(String name, Category fatherCategory) {
		super();
		this.name = name;
		this.fatherCategory = fatherCategory;
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

	public Category getFatherCategory() {
		return fatherCategory;
	}

	public void setFatherCategory(Category fatherCategory) {
		this.fatherCategory = fatherCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", fatherCategory=" + fatherCategory + "]";
	}
    
}
