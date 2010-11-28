package org.jhopify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "order")
public class Order {
	public static final String FULFILLED_FULFILLMENT_STATUS_VALUE = "fulfilled";
	public static final String PARTIAL_FULFILLMENT_STATUS_VALUE = "partial";
	
	Boolean buyerAcceptsMarketing;
	Date closedAt;
	String String;
	String email;
	String financialStatus;
	String fulfillmentStatus;
	String gateway;
	String id;
	String landingSite;
	String landingSiteRef;
	String name;
	String currency;
	String note;
	Integer number;
	String referringSite;
	Float subtotalPrice;
	Boolean taxesIncluded;
	String token;
	Float totalDiscounts;
	Float totalLineItemsPrice;
	Float totalPrice;
	Float totalTax;
	Integer totalWeight;
	Date updatedAt;
	String browserIp;
	Integer orderNumber;
	OrderAddress billingAddress;
	OrderAddress shippingAddress;
	List<OrderLineItem> lineItems = new ArrayList<OrderLineItem>();
	List<OrderShippingLine> shipingLines = new ArrayList<OrderShippingLine>();
	List<OrderTaxLine> taxLines = new ArrayList<OrderTaxLine>();
	OrderPaymentDetails paymentDetails;
	OrderShippingLine shippingLine;
	List<OrderNoteAttribute> noteAttributes = new ArrayList<OrderNoteAttribute>();

	/**
	 * @return the buyerAcceptsMarketing
	 */
	public Boolean getBuyerAcceptsMarketing() {
		return buyerAcceptsMarketing;
	}
	/**
	 * @param buyerAcceptsMarketing the buyerAcceptsMarketing to set
	 */
	@XmlElement(name = "buyer-accepts-marketing")
	public void setBuyerAcceptsMarketing(Boolean buyerAcceptsMarketing) {
		this.buyerAcceptsMarketing = buyerAcceptsMarketing;
	}
	/**
	 * @return the closedAt
	 */
	public Date getClosedAt() {
		return closedAt;
	}
	/**
	 * @param closedAt the closedAt to set
	 */
	@XmlElement(name = "closed-at")
	public void setClosedAt(Date closedAt) {
		this.closedAt = closedAt;
	}
	/**
	 * @return the String
	 */
	public String getString() {
		return String;
	}
	/**
	 * @param String the String to set
	 */
	public void setString(String String) {
		this.String = String;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the financialStatus
	 */
	public String getFinancialStatus() {
		return financialStatus;
	}
	/**
	 * @param financialStatus the financialStatus to set
	 */
	@XmlElement(name = "financial-status")
	public void setFinancialStatus(String financialStatus) {
		this.financialStatus = financialStatus;
	}
	/**
	 * @return the fulfillmentStatus
	 */
	public String getFulfillmentStatus() {
		return fulfillmentStatus;
	}
	/**
	 * @param fulfillmentStatus the fulfillmentStatus to set
	 */
	@XmlElement(name = "fulfillment-status")
	public void setFulfillmentStatus(String fulfillmentStatus) {
		this.fulfillmentStatus = fulfillmentStatus;
	}
	/**
	 * @return the gateway
	 */
	public String getGateway() {
		return gateway;
	}
	/**
	 * @param gateway the gateway to set
	 */
	public void setGateway(String gateway) {
		this.gateway = gateway;
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
	 * @return the landingSite
	 */
	public String getLandingSite() {
		return landingSite;
	}
	/**
	 * @param landingSite the landingSite to set
	 */
	@XmlElement(name = "landing-site")
	public void setLandingSite(String landingSite) {
		this.landingSite = landingSite;
	}
	/**
	 * @return the landingSiteRef
	 */
	public String getLandingSiteRef() {
		return landingSiteRef;
	}
	/**
	 * @param landingSiteRef the landingSiteRef to set
	 */
	@XmlElement(name = "landing-site-ref")
	public void setLandingSiteRef(String landingSiteRef) {
		this.landingSiteRef = landingSiteRef;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}
	/**
	 * @return the referringSite
	 */
	public String getReferringSite() {
		return referringSite;
	}
	/**
	 * @param referringSite the referringSite to set
	 */
	@XmlElement(name = "referring-site")
	public void setReferringSite(String referringSite) {
		this.referringSite = referringSite;
	}
	/**
	 * @return the subtotalPrice
	 */
	public Float getSubtotalPrice() {
		return subtotalPrice;
	}
	/**
	 * @param subtotalPrice the subtotalPrice to set
	 */
	@XmlElement(name = "subtotal-price")
	public void setSubtotalPrice(Float subtotalPrice) {
		this.subtotalPrice = subtotalPrice;
	}
	/**
	 * @return the taxesIncluded
	 */
	public Boolean getTaxesIncluded() {
		return taxesIncluded;
	}
	/**
	 * @param taxesIncluded the taxesIncluded to set
	 */
	@XmlElement(name = "taxes-included")
	public void setTaxesIncluded(Boolean taxesIncluded) {
		this.taxesIncluded = taxesIncluded;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the totalDiscounts
	 */
	public Float getTotalDiscounts() {
		return totalDiscounts;
	}
	/**
	 * @param totalDiscounts the totalDiscounts to set
	 */
	@XmlElement(name = "total-discounts")
	public void setTotalDiscounts(Float totalDiscounts) {
		this.totalDiscounts = totalDiscounts;
	}
	/**
	 * @return the totalLineItemsPrice
	 */
	public Float getTotalLineItemsPrice() {
		return totalLineItemsPrice;
	}
	/**
	 * @param totalLineItemsPrice the totalLineItemsPrice to set
	 */
	@XmlElement(name = "total-line-items-price")
	public void setTotalLineItemsPrice(Float totalLineItemsPrice) {
		this.totalLineItemsPrice = totalLineItemsPrice;
	}
	/**
	 * @return the totalPrice
	 */
	public Float getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	@XmlElement(name = "total-price")
	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the totalTax
	 */
	public Float getTotalTax() {
		return totalTax;
	}
	/**
	 * @param totalTax the totalTax to set
	 */
	@XmlElement(name = "total-tax")
	public void setTotalTax(Float totalTax) {
		this.totalTax = totalTax;
	}
	/**
	 * @return the totalWeight
	 */
	public Integer getTotalWeight() {
		return totalWeight;
	}
	/**
	 * @param totalWeight the totalWeight to set
	 */
	@XmlElement(name = "total-weight")
	public void setTotalWeight(Integer totalWeight) {
		this.totalWeight = totalWeight;
	}
	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}
	/**
	 * @param updatedAt the updatedAt to set
	 */
	@XmlElement(name = "updated-at")
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	/**
	 * @return the browserIp
	 */
	public String getBrowserIp() {
		return browserIp;
	}
	/**
	 * @param browserIp the browserIp to set
	 */
	@XmlElement(name = "browser-ip")
	public void setBrowserIp(String browserIp) {
		this.browserIp = browserIp;
	}
	/**
	 * @return the orderNumber
	 */
	public Integer getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	@XmlElement(name = "order-number")
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the billingAddress
	 */
	public OrderAddress getBillingAddress() {
		return billingAddress;
	}
	/**
	 * @param billingAddress the billingAddress to set
	 */
	@XmlElement( name="billing-address" )
	public void setBillingAddress(OrderAddress billingAddress) {
		this.billingAddress = billingAddress;
	}
	/**
	 * @return the shippingAddress
	 */
	public OrderAddress getShippingAddress() {
		return shippingAddress;
	}
	/**
	 * @param shippingAddress the shippingAddress to set
	 */
	@XmlElement( name="shipping-address" )
	public void setShippingAddress(OrderAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	/**
	 * @return the lineItems
	 */
	public List<OrderLineItem> getLineItems() {
		return lineItems;
	}
	/**
	 * @param lineItems the lineItems to set
	 */
	@XmlElementWrapper( name="line-items" )
	@XmlElement( name="line-item" )
	public void setLineItems(List<OrderLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	/**
	 * @return the shipingLines
	 */
	public List<OrderShippingLine> getShipingLines() {
		return shipingLines;
	}
	/**
	 * @param shipingLines the shipingLines to set
	 */
	@XmlElementWrapper( name="shipping-lines" )
	@XmlElement( name="shipping-line" )
	public void setShipingLines(List<OrderShippingLine> shipingLines) {
		this.shipingLines = shipingLines;
	}
	/**
	 * @return the taxLines
	 */
	public List<OrderTaxLine> getTaxLines() {
		return taxLines;
	}
	/**
	 * @param taxLines the taxLines to set
	 */
	@XmlElementWrapper( name="tax-lines" )
	@XmlElement( name="tax-line" )
	public void setTaxLines(List<OrderTaxLine> taxLines) {
		this.taxLines = taxLines;
	}
	/**
	 * @return the paymentDetails
	 */
	public OrderPaymentDetails getPaymentDetails() {
		return paymentDetails;
	}
	/**
	 * @param paymentDetails the paymentDetails to set
	 */
	@XmlElement( name="payment-details" )
	public void setPaymentDetails(OrderPaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	/**
	 * @return the shippingLine
	 */
	public OrderShippingLine getShippingLine() {
		return shippingLine;
	}
	/**
	 * @param shippingLine the shippingLine to set
	 */
	@XmlElement( name="shipping-line" )
	public void setShippingLine(OrderShippingLine shippingLine) {
		this.shippingLine = shippingLine;
	}
	/**
	 * @return the noteAttributes
	 */
	public List<OrderNoteAttribute> getNoteAttributes() {
		return noteAttributes;
	}
	/**
	 * @param noteAttributes the noteAttributes to set
	 */
	@XmlElementWrapper( name="note-attributes" )
	@XmlElement( name="note-attribute" )
	public void setNoteAttributes(List<OrderNoteAttribute> noteAttributes) {
		this.noteAttributes = noteAttributes;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	} 
}
