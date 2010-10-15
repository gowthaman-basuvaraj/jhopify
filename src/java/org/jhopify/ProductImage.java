package org.jhopify;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="image")
public class ProductImage {
	String id;
	String src;
	String attachement;
	String filename;
	Integer position;
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
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProductImage [id=" + id + ", position=" + position + ", src=" + src + "]";
	}
	public String getAttachement() {
		return attachement;
	}
	public void setAttachement(String attachement) {
		this.attachement = attachement;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
