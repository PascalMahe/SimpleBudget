package fr.pascalmahe.business;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Class representing a category of an account's lines.
 * @author Pascal
 */
@Entity
public class Category implements Serializable, Comparable<Category> {

	private static final long serialVersionUID = 3208053020237026635L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private Category fatherCategory;

	public Category() {}

	public Category(Integer id, String name, Category fatherCategory) {
		this.id = id;
		this.name = name;
		this.fatherCategory = fatherCategory;
	}

	public Category(String name, Category fatherCategory) {
		this.name = name;
		this.fatherCategory = fatherCategory;
	}
	
	public Category(String categoryName) {
		this.name = categoryName;
	}
	
	public String getCompleteName(){
		String completeName = "";
		if(this.fatherCategory != null){
			completeName += fatherCategory.name + ":";
		}
		completeName += name;
		return completeName;
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
	
//	@Override
//	public String toString() {
//		return "Category [id=" + id + ", name=" + name + ", fatherCategory=" + fatherCategory + "]";
//	}

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
		Category other = (Category) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Category o) {
		int result = 0;
		if(this.name != null){
			result = this.name.compareTo(o.name);
		} else {
			result = -1;
		}
		return result;
	}
    
}
