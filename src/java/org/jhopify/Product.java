package org.jhopify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="product")
public class Product {
	
	String id;
	Date publishedAt;
	String handle;
	String productType;
	String vendor;
	String title;
	String tags;
	String bodyHtml;
	List<String> imageNames = new ArrayList<String>();
	List<ProductOption> options = new ArrayList<ProductOption>();
	List<ProductVariant> variants = new ArrayList<ProductVariant>();
	List<Metafield> metafields = new ArrayList<Metafield>();

	
	/**
	 * @return the publishedAt
	 */
	public Date getPublishedAt() {
		return publishedAt;
	}
	/**
	 * @param publishedAt the publishedAt to set
	 */
	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}
	/**
	 * @return the handle
	 */
	public String getHandle() {
		return handle;
	}
	/**
	 * @param handle the handle to set
	 */
	public void setHandle(String handle) {
		this.handle = handle;
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
	 * @return the productType
	 */
	@XmlElement(name = "product-type")
	public String getProductType() {
		return productType;
	}
	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
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
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}
	/**
	 * @return the bodyHtml
	 */
	@XmlElement(name = "body-html")
	public String getBodyHtml() {
		return bodyHtml;
	}
	/**
	 * @param bodyHtml the bodyHtml to set
	 */
	public void setBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}
	/**
	 * @return the variants
	 */
	@XmlElementWrapper( name="variants" )
	@XmlElement( name="variant" )
	public List<ProductVariant> getVariants() {
		return variants;
	}
	/**
	 * @param variants the variants to set
	 */
	public void setVariants(List<ProductVariant> variants) {
		this.variants = variants;
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
	 * @return the options
	 */
	@XmlElementWrapper( name="options" )
	@XmlElement( name="option" )
	public List<ProductOption> getOptions() {
		return options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(List<ProductOption> options) {
		this.options = options;
	}
	/**
	 * @return the imageNames
	 */
	@XmlTransient
	public List<String> getImageNames() {
		return imageNames;
	}
	/**
	 * @param imageNames the imageNames to set
	 */
	public void setImageNames(List<String> imageNames) {
		this.imageNames = imageNames;
	}
}