package org.jhopify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name="fulfillment" )
public class Fulfillment {
	Boolean notifyCustomer;
	String id;
	Date createdAt, updatedAt;
	String orderId, trackingNumber, status;
	Receipt receipt;
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
	@XmlElementWrapper( name="line-items" )
	public List<OrderLineItem> getLineItems() {
		return lineItems;
	}
	/**
	 * @param lineItems the lineItems to set
	 */
	public void setLineItems(List<OrderLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the createdAt
	 */
	@XmlElement(name = "created_at")
	public Date getCreatedAt() {
		return createdAt;
	}
	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @return the updatedAt
	 */
	@XmlElement(name = "updated_at")
	public Date getUpdatedAt() {
		return updatedAt;
	}
	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	/**
	 * @return the orderId
	 */
	@XmlElement(name = "order_id")
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the receipt
	 */
	@XmlElement(name = "receipt")
	public Receipt getReceipt() {
		return receipt;
	}
	/**
	 * @param receipt the receipt to set
	 */
	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}
}
