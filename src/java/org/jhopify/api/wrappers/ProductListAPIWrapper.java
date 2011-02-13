package org.jhopify.api.wrappers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jhopify.Product;


@XmlType(name = "")
@XmlRootElement(name = "products")
public class ProductListAPIWrapper {
	List<Product> products = new ArrayList<Product>();

	/**
	 * @return the products
	 */
	@XmlElement(name = "product", required = true)
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
