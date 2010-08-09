package org.jhopify;

import javax.xml.bind.annotation.XmlElement;

public class OrderLineItem {
	String fulfillmentService;
	String fulfillmentStatus;
	Integer grams;
	String id;
	Float price;
	String productId;
	Integer quantity;
	Boolean requiresShipping;
	String sku;
	String title;
	String variantId;
	String variantTitle;
	String vendor;
	String name;

	/**
	 * @return the fulfillmentService
	 */
	public String getFulfillmentService() {
		return fulfillmentService;
	}
	/**
	 * @param fulfillmentService the fulfillmentService to set
	 */
	@XmlElement( name="fulfillment-service" )
	public void setFulfillmentService(String fulfillmentService) {
		this.fulfillmentService = fulfillmentService;
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
	@XmlElement( name="fulfillment-status" )
	public void setFulfillmentStatus(String fulfillmentStatus) {
		this.fulfillmentStatus = fulfillmentStatus;
	}
	/**
	 * @return the grams
	 */
	public Integer getGrams() {
		return grams;
	}
	/**
	 * @param grams the grams to set
	 */
	public void setGrams(Integer grams) {
		this.grams = grams;
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
	 * @return the productId
	 */
	@XmlElement( name="product-id" )
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the requiresShipping
	 */
	public Boolean getRequiresShipping() {
		return requiresShipping;
	}
	/**
	 * @param requiresShipping the requiresShipping to set
	 */
	@XmlElement( name="requires-shipping" )
	public void setRequiresShipping(Boolean requiresShipping) {
		this.requiresShipping = requiresShipping;
	}
	/**
	 * @return the sku
	 */
	public String getSku() {
		return sku;
	}
	/**
	 * @param sku the sku to set
	 */
	public void setSku(String sku) {
		this.sku = sku;
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
	/**
	 * @return the variantId
	 */
	public String getVariantId() {
		return variantId;
	}
	/**
	 * @param variantId the variantId to set
	 */
	@XmlElement( name="variant-id" )
	public void setVariantId(String variantId) {
		this.variantId = variantId;
	}
	/**
	 * @return the variantTitle
	 */
	public String getVariantTitle() {
		return variantTitle;
	}
	/**
	 * @param variantTitle the variantTitle to set
	 */
	@XmlElement( name="variant-title" )
	public void setVariantTitle(String variantTitle) {
		this.variantTitle = variantTitle;
	}
	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}
	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
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
}
