package org.jhopify.api.wrappers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jhopify.Fulfillment;

@XmlType(name = "")
@XmlRootElement(name = "fulfillments")
public class FulfillmentListAPIWrapper {
	List<Fulfillment> fulfillments = new ArrayList<Fulfillment>();

	/**
	 * @return the fulfillments
	 */
	@XmlElement(name = "fulfillment", required = true)
	public List<Fulfillment> getFulfillments() {
		return fulfillments;
	}

	/**
	 * @param fulfillments the fulfillments to set
	 */
	public void setFulfillments(List<Fulfillment> fulfillments) {
		this.fulfillments = fulfillments;
	}
}
