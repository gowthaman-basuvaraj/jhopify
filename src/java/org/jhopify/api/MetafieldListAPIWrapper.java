package org.jhopify.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jhopify.Metafield;

@XmlType(name = "")
@XmlRootElement(name = "metafields")
public class MetafieldListAPIWrapper {
	List<Metafield> metafields = new ArrayList<Metafield>();

	/**
	 * @return the products
	 */
	@XmlElement(name = "metafield", required = true)
	public List<Metafield> getMetafields() {
		return metafields;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<Metafield> metafields) {
		this.metafields = metafields;
	}
}
