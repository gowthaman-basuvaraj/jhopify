package org.jhopify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.jhopify.solr.SolrFacade;

@XmlRootElement(name="product")
public class Product {
	
	@Field String id;

	Date publishedAt;


	@Field String handle;
	@Field String productType;
	@Field String vendor;
	@Field String title;
	@Field String tags;
	@Field String bodyHtml;

	@Field("imageName") List<String> imageNames = new ArrayList<String>();

	List<ProductOption> options = new ArrayList<ProductOption>();
	List<ProductVariant> variants = new ArrayList<ProductVariant>();
	List<Metafield> metafields = new ArrayList<Metafield>();
	List<ProductImage> images = new ArrayList<ProductImage>();

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
	/**
	 * @return the images
	 */
	@XmlElementWrapper( name="images" )
	@XmlElement( name="image" )
	public List<ProductImage> getImages() {
		return images;
	}
	/**
	 * @param images the images to set
	 */
	public void setImages(List<ProductImage> images) {
		this.images = images;
	}
	


	
	
	
	
	
	
	/******************************
	* Solr Escaped Field Wrappers *
	*******************************
	* JSolr bogusly expects the   *
	* annotation to be on setters *
	* we made setters that do     *
	* nothing                     *
	******************************/
	@Field("body") public void setBody(String body) {}
	public String getBody() {
		String output = getBodyHtml();
		if(output == null) {
			// Do nothing.
		} else {
			output = StringEscapeUtils.unescapeHtml(output);
			output = output.replaceAll("\\<.*?\\>", "");
		}
		return output;
	}
	
	
	
	
	
	
	
	
	
	/**********************************
	* Solr Multivalued Field Wrappers *
	***********************************
	* JSolr bogusly expects the       *
	* annotation to be on setters     *
	* we made setters that do nothing *
	**********************************/

	
	@Field("metafieldKey") public void setMetafieldKeys(List<String> key) {}
	public List<String> getMetafieldNames() {
		List<String> output = new ArrayList<String>();
		for(Metafield metafield : getMetafields()) {
			String key = metafield.getKey();
			if(key == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(key);
			}
		}
		return output;
	}

	@Field("metafieldValue") public void setMetafieldValues(List<String> names) {}
	public List<String> getMetafieldValues() {
		List<String> output = new ArrayList<String>();
		for(Metafield metafield : getMetafields()) {
			String value = metafield.getKey();
			if(value == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(value);
			}
		}
		return output;
	}

	@Field("imageSRC") public void setImageSRCs(List<String> urls) {}
	public List<String> getImageSRCs() {
		List<String> output = new ArrayList<String>();
		for(ProductImage image : getImages()) {
			String imageSRC = image.getSrc();
			if(imageSRC == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(imageSRC);
			}
		}
		return output;
	}

	@Field("variantId") public void setVariantIds(List<String> ids) {}
	public List<String> getVariantIds() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String id = variant.getId();
			if(id == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(id);
			}
		}
		return output;
	}

	@Field("variantTitle") public void setVariantTitles(List<String> titles) {}
	public List<String> getVariantTitles() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String title = variant.getTitle();
			if(title == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(title);
			}
		}
		return output;
	}

	@Field("variantPosition") public void setVariantPositions(List<Integer> positions) {}
	public List<Integer> getVariantPositions() {
		List<Integer> output = new ArrayList<Integer>();
		for(ProductVariant variant : getVariants()) {
			Integer position = variant.getPosition();
			if(position == null) {
				output.add(SolrFacade.NULL_INTEGER_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(position);
			}
		}
		return output;
	}

	@Field("variantPrice") public void setVariantPrices(List<Float> prices) {}
	public List<Float> getVariantPrices() {
		List<Float> output = new ArrayList<Float>();
		for(ProductVariant variant : getVariants()) {
			Float price = variant.getPrice();
			if(price == null) {
				output.add(SolrFacade.NULL_FLOAT_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(price);
			}
		}
		return output;
	}

	@Field("variantCompareAtPrice") public void setVariantCompareAtPrices(List<Float> prices) {}
	public List<Float> getVariantCompareAtPrices() {
		List<Float> output = new ArrayList<Float>();
		for(ProductVariant variant : getVariants()) {
			Float price = variant.getCompareAtPrice();
			if(price == null) {
				output.add(SolrFacade.NULL_FLOAT_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(price);
			}
		}
		return output;
	}

	@Field("variantInventoryQuantity") public void setVariantInventoryQuantities(List<Float> quantities) {}
	public List<Integer> getVariantInventoryQuantities() {
		List<Integer> output = new ArrayList<Integer>();
		for(ProductVariant variant : getVariants()) {
			Integer quantity = variant.getInventoryQuantity();
			if(quantity == null) {
				output.add(SolrFacade.NULL_INTEGER_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(quantity);
			}
		}
		return output;
	}

	@Field("variantOption1Value") public void setVariantOption1Values(List<String> values) {}
	public List<String> getVariantOption1Values() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String optionValue = variant.getOption1();
			if(optionValue == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(optionValue);
			}
		}
		return output;
	}

	@Field("variantOption2Value") public void setVariantOption2Values(List<String> values) {}
	public List<String> getVariantOption2Values() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String optionValue = variant.getOption2();
			if(optionValue == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(optionValue);
			}
		}
		return output;
	}

	@Field("variantOption3Value") public void setVariantOption3Values(List<String> values) {}
	public List<String> getVariantOption3Values() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String optionValue = variant.getOption3();
			if(optionValue == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(optionValue);
			}
		}
		return output;
	}

	@Field("variantSKU") public void setVariantSKUs(List<String> skus) {}
	public List<String> getVariantSKUs() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String sku = variant.getSku();
			if(sku == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(id);
			}
		}
		return output;
	}

	@Field("variantFirstMetafieldKey") public void setVariantFirstMetafieldKeys(List<String> keys) {}
	public List<String> getVariantFirstMetafieldKeys() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String key = null;
			for(Metafield metafield : variant.getMetafields()) {
				key = metafield.getKey();
			}
			if(key == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(key);
			}
		}
		return output;
	}

	@Field("variantFirstMetafieldValue") public void setVariantFirstMetafieldValues(List<String> values) {}
	public List<String> getVariantFirstMetafieldValues() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String id = variant.getId();
			if(id == null) {
				output.add(SolrFacade.NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(id);
			}
		}
		return output;
	}
}