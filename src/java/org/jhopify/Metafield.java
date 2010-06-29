package org.jhopify;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="metafield")
public class Metafield {
	public final static String SHOPIFY_API_METAFIELD_TYPE_INTEGER_VALUE = "integer";
	public final static String SHOPIFY_API_METAFIELD_TYPE_STRING_VALUE = "string";
	
	String id;
	String description;
	String key;
	String namespace;
	String value;
	String valueType;


	public Metafield() {
		super();
	}
	public Metafield(String namespace, String key, String valueType, String value) {
		super();
		this.namespace = namespace;
		this.key = key;
		this.valueType = valueType;
		this.value = value;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}
	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the valueType
	 */
	@XmlElement(name = "value-type")
	public String getValueType() {
		return valueType;
	}
	/**
	 * @param valueType the valueType to set
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
}
