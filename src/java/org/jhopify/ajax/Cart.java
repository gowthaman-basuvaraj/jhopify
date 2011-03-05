package org.jhopify.ajax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class Cart {
	Collection<Map<String, String>> items = new HashSet<Map<String, String>>();
//    "handle": "aquarius",
//    "line_price": 6000,
//    "requires_shipping": true,
//    "price": 2000,
//    "title": "aquarius - medium",
//    "url": "/products/aquarius",
//    "quantity": 3,
//    "id": 30104042,
//    "grams": 181,
//    "sku": "",
//    "vendor": "the candi factory",
//    "image": "http://static.shopify.com/s/files/1/0040/7092/products/aquarius_1.gif?1268045506",
//    "variant_id": 30104042
	String requires_shipping, total_price, attributes, item_count, note, total_weight;
	/**
	 * @return the items
	 */
	public Collection<Map<String, String>> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(Collection<Map<String, String>> items) {
		this.items = items;
	}
	/**
	 * @return the requires_shipping
	 */
	public String getRequires_shipping() {
		return requires_shipping;
	}
	/**
	 * @param requires_shipping the requires_shipping to set
	 */
	public void setRequires_shipping(String requires_shipping) {
		this.requires_shipping = requires_shipping;
	}
	/**
	 * @return the total_price
	 */
	public String getTotal_price() {
		return total_price;
	}
	/**
	 * @param total_price the total_price to set
	 */
	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
	/**
	 * @return the attributes
	 */
	public String getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	/**
	 * @return the item_count
	 */
	public String getItem_count() {
		return item_count;
	}
	/**
	 * @param item_count the item_count to set
	 */
	public void setItem_count(String item_count) {
		this.item_count = item_count;
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
	 * @return the total_weight
	 */
	public String getTotal_weight() {
		return total_weight;
	}
	/**
	 * @param total_weight the total_weight to set
	 */
	public void setTotal_weight(String total_weight) {
		this.total_weight = total_weight;
	}
}
