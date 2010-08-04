package org.jhopify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name="product")
public class Product {
	
	public static String NULL_STRING_MULTIVALUED_FIELD_VALUE = "N/A";
	public static Integer NULL_INTEGER_MULTIVALUED_FIELD_VALUE = -1;
	public static Float NULL_FLOAT_MULTIVALUED_FIELD_VALUE = -1.00f;
	
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
	 * @param title the title to set
	 */
	public void optimizeAndSetTitle(String title) {
		// Process title (remove anything from it that is already in the tags)
	    StringTokenizer st = new StringTokenizer(title);
	    StringBuffer productTitleStringBuffer = new StringBuffer();
	    StringBuffer productTypePrefixStringBuffer = new StringBuffer();
	    Vector<String> addToTagsLater = new Vector<String>();
	    String season = null;
	    for(Metafield metafield : getMetafields()) {
	    	if("season".equals(metafield.getKey())) {
	    		season = metafield.getValue();
	    	}
	    }
	    while (st.hasMoreTokens()) {
	    	String token = st.nextToken();
	    	String tokenLowercase = token.toUpperCase().toLowerCase();
	    	String tokenLowercaseWithoutApostrophe = tokenLowercase.replace("'", "");
    		boolean wasFoundInTagList = false;
    		boolean wasFoundInSeason = false;
    		
    		// Search for token in season first, then, if still not found, in tags
	    	if(token.length() > 2 && season != null && season.toUpperCase().toLowerCase().contains(tokenLowercase)) {
		    	// Found in season
	    		wasFoundInSeason = true;
	    	} else {
		    	// Look in tags if still not found
		    	for(String tag : getTagList()) {
		    		String tagLowerCase = tag.toUpperCase().toLowerCase();
			    	String tagLowercaseWithoutApostrophe = tagLowerCase.replace("'", "");
		    		if((token.length() > 2 && tag.contains(token))) {
		    			wasFoundInTagList = true;
		    			break;
		    		} else if(tagLowerCase.equals(tokenLowercase)) {
		    			// Found in tag, replace token by tag, because tag are usually better capitalized
		    			token = tag;
		    			wasFoundInTagList = true;
		    			break;
		    		} else if(tagLowercaseWithoutApostrophe.equals(tokenLowercaseWithoutApostrophe)) {
		    			// Capitalize just in case
		    			if(token.length() > 1) token = token.charAt(0) + token.substring(1).toUpperCase().toLowerCase();

		    			// Add to tags
		    			if(token.length() > 3 && !addToTagsLater.contains(token)) addToTagsLater.add(token);

		    			wasFoundInTagList = true;
		    			break;
		    		}
		    	}
	    	}
	    	if(wasFoundInTagList) {
	    		// Was found, but is a prefix to product name, append to product type
    			if(productTitleStringBuffer.length() == 0) productTypePrefixStringBuffer.append(token).append(' ');
	    	} else if(!wasFoundInSeason) {
	    		// Was not found in tags, keep in product name and append to tags later;
				if(productTitleStringBuffer.length() == 0) productTitleStringBuffer.append(token);
				else productTitleStringBuffer.append(' ').append(token);
				if(token.length() > 3 && !addToTagsLater.contains(token)) addToTagsLater.add(token);
	    	}
	    }
	    List<String> tagList = getTagList();
	    tagList.addAll(addToTagsLater);
	    setTagList(tagList);
	    setProductType(productTypePrefixStringBuffer.toString() + productType);
		setTitle(productTitleStringBuffer.toString());
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
	public ProductVariant getVariantFromSku(String sku) {
		ProductVariant output = null;
		if(sku != null) {
			for(ProductVariant variant : getVariants()) {
				if(sku.equals(variant.getSku())) output = variant;
			}
		}
		return output;
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
	


	
	
	
	/*
	 * Tag management methods
	 */
	List<String> getTagList() {
		ArrayList<String> output = new ArrayList<String>();
		if(getTags() != null) {
			StringTokenizer st = new StringTokenizer(getTags(), ",");
			while(st.hasMoreTokens()) {
				output.add(st.nextToken().trim());
			}
		}
		return output;
	}
	void setTagList(List<String> tagList) {
		StringBuffer sb = new StringBuffer();
		for(String tag : tagList) {
			if(!"".equals(tag) && !tag.toUpperCase().toLowerCase().equals("null")) {
				if(sb.length() > 0) sb.append(", ");
				sb.append(tag);
			}
		}
		setTags(sb.toString());
	}
	public void addTag(String tag) {
		if(tag != null) {
			List<String> tagList = getTagList();
			if(!tagList.contains(tag)) {
				tagList.add(tag);
			}
			setTagList(tagList);
		}
	}
	public String addTags(String tags, String delimiter) {
		String output = null;
		if(tags != null) {
			StringTokenizer st = new StringTokenizer(tags, delimiter);
			List<String> tagList = getTagList();
			while(st.hasMoreTokens()) {
				String tag = st.nextToken().trim();
				if(!tagList.contains(tag)) {
					tagList.add(tag);
					output = tag;
				}
			}
			setTagList(tagList);
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

	public void addMetafield(Metafield metafield) {
		this.metafields.add(metafield);
	}
	
	
	public void setOptionNames(List<String> names) {}
	public List<String> getOptionNames() {
		List<String> output = new ArrayList<String>();
		for(ProductOption option : getOptions()) {
			String name = option.getName();
			if(name == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(name);
			}
		}
		return output;
	}

	public void setImageSRCs(List<String> urls) {}
	public List<String> getImageSRCs() {
		List<String> output = new ArrayList<String>();
		for(ProductImage image : getImages()) {
			String imageSRC = image.getSrc();
			if(imageSRC == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(imageSRC);
			}
		}
		return output;
	}

	public void setVariantIds(List<String> ids) {}
	public List<String> getVariantIds() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String id = variant.getId();
			if(id == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(id);
			}
		}
		return output;
	}

	public void setVariantPositions(List<Integer> positions) {}
	public List<Integer> getVariantPositions() {
		List<Integer> output = new ArrayList<Integer>();
		for(ProductVariant variant : getVariants()) {
			Integer position = variant.getPosition();
			if(position == null) {
				output.add(NULL_INTEGER_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(position);
			}
		}
		return output;
	}

	public void setVariantPrices(List<Float> prices) {}
	public List<Float> getVariantPrices() {
		List<Float> output = new ArrayList<Float>();
		for(ProductVariant variant : getVariants()) {
			Float price = variant.getPrice();
			if(price == null) {
				output.add(NULL_FLOAT_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(price);
			}
		}
		return output;
	}

	public void setVariantCompareAtPrices(List<Float> prices) {}
	public List<Float> getVariantCompareAtPrices() {
		List<Float> output = new ArrayList<Float>();
		for(ProductVariant variant : getVariants()) {
			Float price = variant.getCompareAtPrice();
			if(price == null) {
				output.add(NULL_FLOAT_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(price);
			}
		}
		return output;
	}

	public void setVariantInventoryQuantities(List<Float> quantities) {}
	public List<Integer> getVariantInventoryQuantities() {
		List<Integer> output = new ArrayList<Integer>();
		for(ProductVariant variant : getVariants()) {
			Integer quantity = variant.getInventoryQuantity();
			if(quantity == null) {
				output.add(NULL_INTEGER_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(quantity);
			}
		}
		return output;
	}

	public void setVariantOption1Values(List<String> values) {}
	public List<String> getVariantOption1Values() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String optionValue = variant.getOption1();
			if(optionValue == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(optionValue);
			}
		}
		return output;
	}

	public void setVariantOption2Values(List<String> values) {}
	public List<String> getVariantOption2Values() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String optionValue = variant.getOption2();
			if(optionValue == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(optionValue);
			}
		}
		return output;
	}

	public void setVariantOption3Values(List<String> values) {}
	public List<String> getVariantOption3Values() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String optionValue = variant.getOption3();
			if(optionValue == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(optionValue);
			}
		}
		return output;
	}

	public void setVariantSKUs(List<String> skus) {}
	public List<String> getVariantSKUs() {
		List<String> output = new ArrayList<String>();
		for(ProductVariant variant : getVariants()) {
			String sku = variant.getSku();
			if(sku == null) {
				output.add(NULL_STRING_MULTIVALUED_FIELD_VALUE);
			} else {
				output.add(id);
			}
		}
		return output;
	}
}