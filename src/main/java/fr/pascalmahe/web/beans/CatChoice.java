package fr.pascalmahe.web.beans;

import java.io.Serializable;
import java.util.List;

import fr.pascalmahe.business.Category;

/**
 * Class for manipulating Categories in order to choose a father-son pair
 * in the line page.
 * @author Pascal
 */
public class CatChoice implements Serializable {

	private static final long serialVersionUID = -7871636425686858451L;

	public static final String VALID_CLASS = "validField";
	public static final String INVALID_CLASS = "invalidField";
	
	private Integer categoId;
	
	private Float amount;
	
	private Category fatherCategory;
	
	private Category sonCategory;

	private List<Category> secondRankCategoryList;
	
	private boolean validFather;
	
	private boolean validSon;

	public CatChoice(Integer categoId, Category fatCat, Category sonCat, Float amount, List<Category> secondRankCatList) {
		this.categoId = categoId;
		this.fatherCategory = fatCat;
		this.sonCategory = sonCat;
		this.amount = amount;
		this.secondRankCategoryList = secondRankCatList;
		this.validSon = true;
		this.validFather = true;
	}


	public String getFormattedAmount(){
		return String.format("%.2f", amount);
	}


	@Override
	public String toString() {
		return "CatChoice [categoId=" + categoId + ", amount=" + amount + ", fatherCategory=" + fatherCategory
				+ ", sonCategory=" + sonCategory + "]";
	}
	
	public Integer getCategoId() {
		return categoId;
	}


	public void setCategoId(Integer categoId) {
		this.categoId = categoId;
	}


	public Float getAmount() {
		return amount;
	}


	public void setAmount(Float amount) {
		this.amount = amount;
	}


	public Category getFatherCategory() {
		return fatherCategory;
	}


	public void setFatherCategory(Category fatherCategory) {
		this.fatherCategory = fatherCategory;
	}


	public Category getSonCategory() {
		return sonCategory;
	}


	public void setSonCategory(Category sonCategory) {
		this.sonCategory = sonCategory;
	}


	public List<Category> getSecondRankCategoryList() {
		return secondRankCategoryList;
	}


	public void setSecondRankCategoryList(List<Category> secondRankCategoryList) {
		this.secondRankCategoryList = secondRankCategoryList;
	}
	
	public String getValidFather(){
		String styleClass = VALID_CLASS;
		if(!validFather){
			styleClass = INVALID_CLASS;
		}
		return styleClass;
	}

	public String getValidSon(){
		String styleClass = VALID_CLASS;
		if(!validSon){
			styleClass = INVALID_CLASS;
		}
		return styleClass;
	}


	public void setInvalid() {
		if(sonCategory != null){
			validSon = false;
		} else {
			validFather = false;
		}
	}


	public void setValid() {
		validSon = true;
		validFather = true;
	}


	public Category getFullCategory() {
		Category fullCat = fatherCategory;
		if(sonCategory != null){
			if(sonCategory.getFatherCategory() == null){
				sonCategory.setFatherCategory(fatherCategory);
			}
			fullCat = sonCategory;
		}
		return fullCat;
	}

	public boolean isSonValid() {
		return validFather;
	}

	public boolean isFatherValid() {
		return validFather;
	}

}
