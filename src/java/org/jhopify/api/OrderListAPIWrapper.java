package org.jhopify.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jhopify.Order;


@XmlType(name = "")
@XmlRootElement(name = "order")
public class OrderListAPIWrapper {
	List<Order> orders = new ArrayList<Order>();

	/**
	 * @return the orders
	 */
	@XmlElement(name = "order", required = true)
	public List<Order> getOrders() {
		return orders;
	}

	/**
	 * @param orders the orders to set
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
}
