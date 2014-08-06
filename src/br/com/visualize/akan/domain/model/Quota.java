/*
 * File: 	Quota.java
 * Purpose: Bringing the implementation of model Quota, a class that 
 * directly references the business domain.
 */
package br.com.visualize.akan.domain.model;

import br.com.visualize.akan.domain.enumeration.Month;


/**
 * This class represents the model of quota for application. It contains the 
 * essential data to represent and maintain a quota.
 */
public class Quota {
	private int idQuota = 0;
	private int idCongressmanQuota = 0;
	private int idUpdateQuota = 0;
	private int typeQuota = 0;
	
	private Month monthReferenceQuota = Month.WITHOUT_MONTH;
	private int yearReferenceQuota = 0;
	private String descriptionQuota = "";
	private double valueQuota = 0;
	
	public int getIdQuota() {
		return idQuota;
	}
	
	public void setIdQuota( int idQuota ) {
		this.idQuota = idQuota;
	}
	
	public int getIdCongressmanQuota() {
		return idCongressmanQuota;
	}
	
	public void setIdCongressmanQuota( int idCongressmanQuota ) {
		this.idCongressmanQuota = idCongressmanQuota;
	}
	
	public int getIdUpdateQuota() {
		return idUpdateQuota;
	}
	
	public void setIdUpdateQuota( int idUpdateQuota ) {
		this.idUpdateQuota = idUpdateQuota;
	}
	
	public int getTypeQuota() {
		return typeQuota;
	}
	
	public void setTypeQuota( int typeQuota ) {
		this.typeQuota = typeQuota;
	}
	
	public Month getMonthReferenceQuota() {
		return monthReferenceQuota;
	}
	
	public void setMonthReferenceQuota( Month monthReferenceQuota ) {
		this.monthReferenceQuota = monthReferenceQuota;
	}
	
	public int getYearReferenceQuota() {
		return yearReferenceQuota;
	}
	
	public void setYearReferenceQuota( int yearReferenceQuota ) {
		this.yearReferenceQuota = yearReferenceQuota;
	}
	
	public String getDescriptionQuota() {
		return descriptionQuota;
	}
	
	public void setDescriptionQuota( String descriptionQuota ) {
		this.descriptionQuota = descriptionQuota;
	}
	
	public double getValueQuota() {
		return valueQuota;
	}
	
	public void setValueQuota( double valueQuota ) {
		this.valueQuota = valueQuota;
	}
}
