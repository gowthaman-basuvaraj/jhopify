package org.jhopify.api.wrappers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jhopify.Collect;

@XmlType(name = "")
@XmlRootElement(name = "collects")
public class CollectListAPIWrapper {
	List<Collect> collects = new ArrayList<Collect>();

	/**
	 * @return the Collects
	 */
	@XmlElement(name = "collect", required = true)
	public List<Collect> getCollects() {
		return collects;
	}

	/**
	 * @param Collects the Collects to set
	 */
	public void setCollects(List<Collect> Collects) {
		this.collects = Collects;
	}
}
