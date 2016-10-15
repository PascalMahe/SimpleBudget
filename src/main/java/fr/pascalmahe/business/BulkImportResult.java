package fr.pascalmahe.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to manipulate results from a bulk import
 * @author Pascal
 */
public class BulkImportResult {

	int nbLinesCreated;
	
	int nbLinesDetected;
	
	int nbLinesMalformed;
	
	int nbLinesSkipped;
	
	Set<String> categorysCreated;
	
	Set<String> categorysSkipped;
	
	List<Line> lineList;
	
	public BulkImportResult(){
		nbLinesCreated = 0;
		nbLinesDetected = 0;
		nbLinesMalformed = 0;
		nbLinesSkipped = 0;
		
		categorysSkipped = new HashSet<>();
		categorysCreated = new HashSet<>();
	}

	public int getNbLinesDetected() {
		return nbLinesDetected;
	}

	public void setNbLinesDetected(int nbLinesDetected) {
		this.nbLinesDetected = nbLinesDetected;
	}

	public int getNbLinesSkipped() {
		return nbLinesSkipped;
	}

	public void setNbLinesSkipped(int nbLinesSkipped) {
		this.nbLinesSkipped = nbLinesSkipped;
	}

	public int getNbLinesCreated() {
		return nbLinesCreated;
	}

	public void setNbLinesCreated(int nbLinesCreated) {
		this.nbLinesCreated = nbLinesCreated;
	}

	public int getNbLinesMalformed() {
		return nbLinesMalformed;
	}

	public void setNbLinesMalformed(int nbLinesMalformed) {
		this.nbLinesMalformed = nbLinesMalformed;
	}

	public int getNbCategorysCreated() {
		return categorysCreated.size();
	}

	public int getNbCategorysSkipped() {
		return categorysSkipped.size();
	}

	public List<Line> getLineList() {
		return lineList;
	}

	public void setLineList(List<Line> lineList) {
		this.lineList = lineList;
	}

	public void addCategoryCreated(String catName) {
		categorysCreated.add(catName);
	}

	public void addLineCreated() {
		nbLinesCreated++;
	}

	public void addCategorySkipped(String catName) {
		categorysSkipped.add(catName);
	}

	public void addLineSkipped() {
		nbLinesSkipped++;
	}

	public void addLineMalformed() {
		nbLinesMalformed++;
	}

	public void addLineDetected() {
		nbLinesDetected++;
	}

}
