package org.jhopify;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


public class ProductVariant {
	public final static String SHOPIFY_API_MANUAL_FULLFILMENT = "manual";
	public final static String SHOPIFY_API_INVENTORY_TRACKED_BY_SHOPIFY_VALUE = "shopify";
	public final static String SHOPIFY_API_INVENTORY_POLICY_CONTINUE_VALUE = "continue";
	public final static String SHOPIFY_API_INVENTORY_POLICY_DENY_VALUE = "deny";
	public final static String SHOPIFY_API_PRODUCT_DEFAULT_VALUE = "Default";
	
	String id;
	String title = SHOPIFY_API_PRODUCT_DEFAULT_VALUE;
	Integer position;
	Float price;
	Float compareAtPrice;
	String fullfilmentService = SHOPIFY_API_MANUAL_FULLFILMENT;
	String inventoryManagement = SHOPIFY_API_INVENTORY_TRACKED_BY_SHOPIFY_VALUE;
	String inventoryPolicy = SHOPIFY_API_INVENTORY_POLICY_DENY_VALUE;
	Integer inventoryQuantity;
	String option1 = SHOPIFY_API_PRODUCT_DEFAULT_VALUE;
	String option2 = SHOPIFY_API_PRODUCT_DEFAULT_VALUE;
	String option3 = SHOPIFY_API_PRODUCT_DEFAULT_VALUE;
	String sku;
	Boolean requiresShipping = true;
	Boolean taxable = true;
	Double grams;
	List<Metafield> metafields = new ArrayList<Metafield>();

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
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
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
	 * @return the compareAtPrice
	 */
	@XmlElement(name = "compare-at-price")
	public Float getCompareAtPrice() {
		return compareAtPrice;
	}
	/**
	 * @param compareAtPrice the compareAtPrice to set
	 */
	public void setCompareAtPrice(Float compareAtPrice) {
		this.compareAtPrice = compareAtPrice;
	}
	/**
	 * @return the fullfilmentService
	 */
	@XmlElement(name = "fullfilment-service")
	public String getFullfilmentService() {
		return fullfilmentService;
	}
	/**
	 * @param fullfilmentService the fullfilmentService to set
	 */
	public void setFullfilmentService(String fullfilmentService) {
		this.fullfilmentService = fullfilmentService;
	}
	/**
	 * @return the inventoryManagement
	 */
	@XmlElement(name = "inventory-management")
	public String getInventoryManagement() {
		return inventoryManagement;
	}
	/**
	 * @param inventoryManagement the inventoryManagement to set
	 */
	public void setInventoryManagement(String inventoryManagement) {
		this.inventoryManagement = inventoryManagement;
	}
	/**
	 * @return the inventoryPolicy
	 */
	@XmlElement(name = "inventory-policy")
	public String getInventoryPolicy() {
		return inventoryPolicy;
	}
	/**
	 * @param inventoryPolicy the inventoryPolicy to set
	 */
	public void setInventoryPolicy(String inventoryPolicy) {
		this.inventoryPolicy = inventoryPolicy;
	}
	/**
	 * @return the inventoryQuantity
	 */
	@XmlElement(name = "inventory-quantity")
	public Integer getInventoryQuantity() {
		return inventoryQuantity;
	}
	/**
	 * @param inventoryQuantity the inventoryQuantity to set
	 */
	public void setInventoryQuantity(Integer inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}
	/**
	 * @return the option1
	 */
	public String getOption1() {
		return option1;
	}
	/**
	 * @param option1 the option1 to set
	 */
	public void setOption1(String option1) {
		this.option1 = option1;
	}
	/**
	 * @return the option2
	 */
	public String getOption2() {
		return option2;
	}
	/**
	 * @param option2 the option2 to set
	 */
	public void setOption2(String option2) {
		this.option2 = option2;
	}
	/**
	 * @return the option3
	 */
	public String getOption3() {
		return option3;
	}
	/**
	 * @param option3 the option3 to set
	 */
	public void setOption3(String option3) {
		this.option3 = option3;
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
	 * @return the requiresShipping
	 */
	@XmlElement(name = "requires-shipping")
	public Boolean getRequiresShipping() {
		return requiresShipping;
	}
	/**
	 * @param requiresShipping the requiresShipping to set
	 */
	public void setRequiresShipping(Boolean requiresShipping) {
		this.requiresShipping = requiresShipping;
	}
	/**
	 * @return the taxable
	 */
	public Boolean getTaxable() {
		return taxable;
	}
	/**
	 * @param taxable the taxable to set
	 */
	public void setTaxable(Boolean taxable) {
		this.taxable = taxable;
	}
	/**
	 * @return the metafields
	 */
	public List<Metafield> getMetafields() {
		return metafields;
	}
	/**
	 * @param metafields the metafields to set
	 */
	public void setMetafields(List<Metafield> metafields) {
		this.metafields = metafields;
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
	 * @return the grams
	 */
	public Double getGrams() {
		return grams;
	}
	/**
	 * @param grams the grams to set
	 */
	public void setGrams(Double grams) {
		this.grams = grams;
	}
}
