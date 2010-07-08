package org.jhopify.solr;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.util.NamedList;
import org.jhopify.Product;

public class SolrFacade {
	public static String NULL_STRING_MULTIVALUED_FIELD_VALUE = "N/A";
	public static Integer NULL_INTEGER_MULTIVALUED_FIELD_VALUE = -1;
	public static Float NULL_FLOAT_MULTIVALUED_FIELD_VALUE = -1.00f;
	static public void addAllProducts(Map<String, Product> products, String hostName, int port, String webapp, String core)
		throws SolrServerException, IOException {

		String url = "http://" + hostName + ":" + String.valueOf(port) + "/" + webapp + "/" + core;
		CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
		server.setAllowCompression(true);
		

		System.out.println("Adding products to solr server : " + url);
		NamedList<Object> addResponse = server.addBeans(products.values()).getResponse();
		System.out.println("Successfully added products to solr server with response "  + addResponse);

		System.out.println("Commiting product changes to solr server : " + url);
		UpdateResponse commitResponse = server.commit();
		System.out.println("Successfully commited products to solr server with response " + commitResponse);
	}
}