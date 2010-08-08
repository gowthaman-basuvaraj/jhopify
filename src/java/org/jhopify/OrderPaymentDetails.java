package org.jhopify;

import javax.xml.bind.annotation.XmlElement;

public class OrderPaymentDetails {
	String creditCardNumber;
	String creditCardCompany;

	/**
	 * @return the creditCardNumber
	 */
	public String getCreditCardNumber() {
		return creditCardNumber;
	}
	/**
	 * @param creditCardNumber the creditCardNumber to set
	 */
	@XmlElement( name="credit-card-number" )
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	/**
	 * @return the creditCardCompany
	 */
	public String getCreditCardCompany() {
		return creditCardCompany;
	}
	/**
	 * @param creditCardCompany the creditCardCompany to set
	 */
	@XmlElement( name="credit-card-company" )
	public void setCreditCardCompany(String creditCardCompany) {
		this.creditCardCompany = creditCardCompany;
	}
}