package fr.pascalmahe.business;

import java.io.Serializable;
import java.util.List;

/**
 * Class for manipulating Categories in order to choose a father-son pair
 * @author Pascal
 */
public class CatChoice implements Serializable {

	private static final long serialVersionUID = -7871636425686858451L;

	private Integer categoId;
	
	private Float amount;
	
	private Category fatherCategory;
	
	private Category sonCategory;

	private List<Category> secondRankCategoryList;
	

	public CatChoice(Integer categoId, Category fatCat, Category sonCat, Float amount, List<Category> secondRankCatList) {
		this.categoId = categoId;
		this.fatherCategory = fatCat;
		this.sonCategory = sonCat;
		this.amount = amount;
		this.secondRankCategoryList = secondRankCatList;
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

}
