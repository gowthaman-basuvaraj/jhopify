package org.jhopify.api;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.jhopify.Product;
import org.jhopify.ProductImage;
import org.jhopify.util.Base64;

public class ProductImageAPI extends API {
	public static String SHOPIFY_API_IMAGES_URI_PREFIX = "/images";
	
	public static void clearProductImages(String key, String password, String shopifyStoreHandle, Product product) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException {

		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		
		for(ProductImage image : product.getImages()) {

			// Prepare HTTP client
			HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);


			// TODO : what happens if more than 250 products (maximum in a request)?
			URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + "/" +
					String.valueOf(product.getId()) + SHOPIFY_API_IMAGES_URI_PREFIX + "/" + image.getId() + SHOPIFY_API_XML_EXTENSION_SUFFIX);
	        HttpDelete httpDelete = new HttpDelete(uri);
	        

			System.out.println("Trying to delete Shopify image on " + shopifyStoreUrl + 
					" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
					" with key \"" + key + "\" and password \"" + password +  "\", at " + 
					httpDelete.getURI() + ".");
	     
			
			// Make sure we dont exceed API call allowance
	        trafficControl(shopifyStoreHandle);

	        // Execute API call
			HttpResponse response = httpClient.execute(httpDelete);

			// Look at API response
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            System.out.println("Successfuly delete image.");
			} else {
				throw new IllegalArgumentException("Halting. Attempt to delete image product with Shopify API failed : " + 
						response.getStatusLine().toString() + " " + getContentStringFromResponse(response));
			}


	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpClient.getConnectionManager().shutdown();  
			
		}
	}
	
	public static void addProductImage(String key, String password, String shopifyStoreHandle, String productId, File imageFile) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException {

		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		
		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);


		// TODO : what happens if more than 250 products (maximum in a request)?
		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + "/" +
				String.valueOf(productId) + SHOPIFY_API_IMAGES_URI_PREFIX + SHOPIFY_API_XML_EXTENSION_SUFFIX);
        HttpPost httpPost = new HttpPost(uri);
        

		// Prepare for XML marshalling
		JAXBContext jaxbContext = JAXBContext.newInstance(ProductImage.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter stringWriter = new StringWriter();
		// Marshall product image to XML
		ProductImage image = new ProductImage();
		image.setFilename(imageFile.getName());
		image.setAttachement(Base64.encodeFromFile(imageFile.getAbsolutePath()));
		marshaller.marshal(image, stringWriter);
        // Prepare request content
		String entityString = stringWriter.toString();
        StringEntity entity = new StringEntity(entityString);
		entity.setContentType("application/xml");
		httpPost.setEntity(entity);


		System.out.println("Trying to add Shopify image on " + shopifyStoreUrl + 
				" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
				" with key \"" + key + "\" and password \"" + password +  "\", at " + 
				httpPost.getURI() + ".");
     

		// Make sure we dont exceed API call allowance
        trafficControl(shopifyStoreHandle);

        // Execute API call
		HttpResponse response = httpClient.execute(httpPost);

		// Look at API response
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            System.out.println("Successfuly added image.");
		} else {
			throw new IllegalArgumentException("Halting. Attempt to add image product with Shopify API failed : " + 
					response.getStatusLine().toString() + " " + getContentStringFromResponse(response) +
					"\n\n\n\n\nFor XML:\n" + entityString);
		}


        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();
	}
	public static void addProductHTTPImage(String key, String password, String shopifyStoreHandle, String productId, String src) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException {

		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		
		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);


		// TODO : what happens if more than 250 products (maximum in a request)?
		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + "/" +
				String.valueOf(productId) + SHOPIFY_API_IMAGES_URI_PREFIX + SHOPIFY_API_XML_EXTENSION_SUFFIX);
        HttpPost httpPost = new HttpPost(uri);
        

		// Prepare for XML marshalling
		JAXBContext jaxbContext = JAXBContext.newInstance(ProductImage.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter stringWriter = new StringWriter();
		// Marshall product image to XML
		ProductImage image = new ProductImage();
		image.setSrc(src);
		marshaller.marshal(image, stringWriter);
        // Prepare request content
		String entityString = stringWriter.toString();
        StringEntity entity = new StringEntity(entityString);
		entity.setContentType("application/xml");
		httpPost.setEntity(entity);


		System.out.println("Trying to add Shopify image on " + shopifyStoreUrl + 
				" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
				" with key \"" + key + "\" and password \"" + password +  "\", at " + 
				httpPost.getURI() + ".");
     

		// Make sure we dont exceed API call allowance
        trafficControl(shopifyStoreHandle);

        // Execute API call
		HttpResponse response = httpClient.execute(httpPost);

		// Look at API response
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            System.out.println("Successfuly added image.");
		} else {
			throw new IllegalArgumentException("Halting. Attempt to add image product with Shopify API failed : " + 
					response.getStatusLine().toString() + " " + getContentStringFromResponse(response) +
					"\n\n\n\n\nFor XML:\n" + entityString);
		}


        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();
	}
}
