package org.jhopify.solr;

import java.io.IOException;
import java.util.Map;

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
	static public void addAllProducts(Map<String, ? extends Product> products, String hostName, int port, String webapp, String core)
		throws SolrServerException, IOException {

		// Setup
		String url = "http://" + hostName + ":" + String.valueOf(port) + "/" + webapp + "/" + core;
		CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
		server.setAllowCompression(true);
		DocumentObjectBinder binder = new DocumentObjectBinder();
		
		// Delete, transform, add (update is not so good)
		System.out.println("Processing products on solr server : " + url);
		// Delete for updated products only with : server.deleteById(new Vector<String>(products.keySet()));
		// Delete everything with : server.deleteByQuery( "*:*" );
		UpdateResponse deleteResponse = server.deleteByQuery( "*:*" );
		if(deleteResponse.getStatus() == 0) System.out.println("Successfully deleted all products from solr server. Elapsed time  : " + String.valueOf(deleteResponse.getElapsedTime()) + "ms.");
		else throw new RuntimeException("Failed to delete all products from solr with response :" + deleteResponse);

		// Adding
		long elapsedTime = 0;
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
			UpdateResponse localAddResponse = server.add(inputDocument);
			if(localAddResponse.getStatus() == 0) elapsedTime += localAddResponse.getElapsedTime();
			else throw new RuntimeException("Failed to add product to solr with response :" + localAddResponse);
			
		}
		System.out.println("Successfully processed products on solr server. Elapsed time  : " + String.valueOf(elapsedTime) + "ms.");


		// Commit
		System.out.println("Commiting product changes to solr server : " + url);
		UpdateResponse commitResponse = server.commit();
		if(commitResponse.getStatus() == 0) System.out.println("Successfully commited products to solr server. Elapsed time  : " + String.valueOf(commitResponse.getElapsedTime()) + "ms.");
		else throw new RuntimeException("Failed to commit products to solr with response :" + commitResponse);
	}
}