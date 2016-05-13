package fr.pascalmahe.business;

import java.io.Serializable;



public class Machine implements Serializable {

    
	private static final long serialVersionUID = 8125873957308428169L;

    private String desi;
    private String code;
    private String codeSite;

    public Machine(String desi, String code, String codeSite){
    	this.desi = desi;
    	this.code = code;
    	this.codeSite = codeSite;
    }

	public String getDesi() {
		return desi;
	}

	public void setDesi(String desi) {
		this.desi = desi;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeSite() {
		return codeSite;
	}

	public void setCodeSite(String codeSite) {
		this.codeSite = codeSite;
	}
    
    
}
