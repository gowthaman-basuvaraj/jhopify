package org.jhopify.solr;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.jhopify.Product;

public class SolrFacade {
	static public void addAllProducts(Map<String, Product> products, String host, int port, String webapp, String core) throws SolrServerException, IOException {
		String url = "http://" + host + ":" + String.valueOf(port) + "/" + webapp + "/" + core;
		CommonsHttpSolrServer server = new CommonsHttpSolrServer(url);
		server.setAllowCompression(true);
		
		// Add all products to
		server.addBeans(products.values());
		
		server.commit();
	}
}