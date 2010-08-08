package org.jhopify;

public class OrderTaxLine {
	Float price;
	Float rate;
	String title;

	/**
	 * @return the price
	 */
	public Float getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Float price) {
		this.price = price;
	}
	/**
	 * @return the rate
	 */
	public Float getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(Float rate) {
		this.rate = rate;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}