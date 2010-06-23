package org.jhopify.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jhopify.Product;

public class ProductAPI extends API {
	public static void saveProducts(String key, String password, String shopifyStoreHandle, Collection<Product> products) throws ClientProtocolException, IOException {


		String shopifyStoreHostName = shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(new AuthScope(shopifyStoreHostName, SHOPIFY_API_PORT_NUMBER), 
				new UsernamePasswordCredentials(key, password));


		// Look here for XML bindings : https://jaxb.dev.java.net/tutorial/
        HttpGet httpGet = new HttpGet(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI);
        

		System.out.println("Trying to connect to Shopify at " + shopifyStoreUrl + 
				" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
				" with key \"" + key + "\" and password \"" + password +  "\", by retrieving product list at " + httpGet.getURI() + ".");
		HttpResponse connectionTestResponse = httpClient.execute(httpGet);
		HttpEntity productListEntity = connectionTestResponse.getEntity();
		productListEntity.getContent().close();
		if(connectionTestResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) System.out.println("Successfuly connected to Shopify…");
		else throw new IllegalArgumentException("Halting. Connection with Shopify API failed : " + connectionTestResponse.getStatusLine().toString());

		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance( Product.class );
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			StringWriter stringWriter = new StringWriter();
			for(Product product : products) {
				marshaller.marshal(product, stringWriter);
		        HttpPost productHttpPost = new HttpPost(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI);
		        String productEntityString = stringWriter.toString().replace("<variants>", "<variants type=\"array\">").replace("<options>", "<options type=\"array\">");
		        StringEntity productEntity = new StringEntity(productEntityString);
		        productEntity.setContentType("application/xml");
		        productHttpPost.setEntity(productEntity);
		        HttpResponse productPostResponse = httpClient.execute(productHttpPost);
				if(productPostResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) System.out.println("Successfuly posted product to Shopify…");
				else {
					HttpEntity errorMessageEntity = productPostResponse.getEntity();
					BufferedReader errorEntityReader = new BufferedReader(new InputStreamReader(errorMessageEntity.getContent()));
					StringBuffer sb = new StringBuffer();
				    int character = -1;
				    while( ( character = errorEntityReader.read() ) != -1 ) {
				    	sb.append( (char) character );
				    }
					throw new IllegalArgumentException("Halting. Attempt to post product with Shopify API failed : " + productPostResponse.getStatusLine().toString() + " " + sb);
				}
				
				// Get entity from response
				HttpEntity productResponseEntity = productPostResponse.getEntity();
				BufferedReader productEntityReader = new BufferedReader(new InputStreamReader(productResponseEntity.getContent()));
				StringBuffer sb = new StringBuffer();
			    int character = -1;
			    while( ( character = productEntityReader.read() ) != -1 ) {
			    	sb.append( (char) character );
			    }
			    System.out.println(sb);
				break;
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}


        // Create first 2 products

        // Append images for those 2 products

        // Update solr indexes
		

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
	}
}
