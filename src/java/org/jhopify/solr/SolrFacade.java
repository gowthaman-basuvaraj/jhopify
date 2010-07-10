package org.jhopify.solr;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jhopify.Metafield;
import org.jhopify.Product;
import org.jhopify.ProductVariant;

public class SolrFacade {
	public static String NULL_STRING_MULTIVALUED_FIELD_VALUE = "N/A";
	public static String PRODUCT_METAFIELD_SUFFIX = "_metafield_";
	public static String VARIANT_METAFIELD_SUFFIX = "_variantMetafield_";
	public static Integer NULL_INTEGER_MULTIVALUED_FIELD_VALUE = -1;
	public static Float NULL_FLOAT_MULTIVALUED_FIELD_VALUE = -1.00f;
	static public void addAllProducts(Map<String, Product> products, String hostName, int port, String webapp, String core)
		throws SolrServerException, IOException {

		// Setup
		String url = "http://" + hostName + ":" + String.valueOf(port) + "/" + webapp + "/" + core;
		CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
		server.setAllowCompression(true);
		DocumentObjectBinder binder = new DocumentObjectBinder();
		
		// Delete, transform, add (update is not so good)
		System.out.println("Processing products on solr server : " + url);
		server.deleteById(new Vector<String>(products.keySet()));
		for(Product product : products.values()) {
			SolrInputDocument inputDocument  = binder.toSolrInputDocument(product);
			
			
			// Adding metafields (_metafield_)
			for(Metafield metafield : product.getMetafields()) {
				String value = metafield.getValue();
				String name = metafield.getKey() + PRODUCT_METAFIELD_SUFFIX + metafield.getValueType();
				inputDocument.addField(name, value);
			}
			
			
			// Adding variant metafields (_variantMetafield_)
			for(ProductVariant variant : product.getVariants()) {
				for(Metafield metafield : variant.getMetafields()) {
					String value = metafield.getValue();
					String name = metafield.getKey() + VARIANT_METAFIELD_SUFFIX + metafield.getValueType();
					inputDocument.addField(name, value);
				}
			}
			
			// Adding document
			server.add(inputDocument);
		}
		System.out.println("Successfully processed products on solr server : " + url);
		
		
		// Commit
		System.out.println("Commiting product changes to solr server : " + url);
		UpdateResponse commitResponse = server.commit();
		System.out.println("Successfully commited products to solr server with response " + commitResponse);
	}
}