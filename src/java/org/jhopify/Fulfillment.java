package org.jhopify;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name="fulfillment" )
public class Fulfillment {
	Boolean notifyCustomer;
	String trackingNumber;
	List<OrderLineItem> lineItems = new ArrayList<OrderLineItem>();
	/**
	 * @return the notifyCustomer
	 */
	@XmlElement( name="notify-customer" )
	public Boolean getNotifyCustomer() {
		return notifyCustomer;
	}
	/**
	 * @param notifyCustomer the notifyCustomer to set
	 */
	public void setNotifyCustomer(Boolean notifyCustomer) {
		this.notifyCustomer = notifyCustomer;
	}
	/**
	 * @return the trackingNumber
	 */
	@XmlElement( name="tracking-number" )
	public String getTrackingNumber() {
		return trackingNumber;
	}
	/**
	 * @param trackingNumber the trackingNumber to set
	 */
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	/**
	 * @return the lineItems
	 */
	@XmlElement( name="line-items" )
	public List<OrderLineItem> getLineItems() {
		return lineItems;
	}
	/**
	 * @param lineItems the lineItems to set
	 */
	public void setLineItems(List<OrderLineItem> lineItems) {
		this.lineItems = lineItems;
	}
}
