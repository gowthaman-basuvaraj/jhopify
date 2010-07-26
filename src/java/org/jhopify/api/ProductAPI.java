package org.jhopify.api;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jhopify.Metafield;
import org.jhopify.Product;
import org.jhopify.ProductVariant;

public class ProductAPI extends API {
	public static final String SHOPIFY_API_PRODUCT_URI_PREFIX = "/admin/products";
	public static final String SHOPIFY_API_VARIANT_URI_SUFFIX = "variants";

	public static final String SHOPIFY_API_METAFIELD_LIST_FILE_NAME = "metafields";
	public static final String PRODUCT_LIST_LIMIT_PARAMETER_NAME = "limit";
	public static final int PRODUCT_LIST_LIMIT_PARAMETER_MAX = 250;
	
	public static void saveProducts(String key, String password, String shopifyStoreHandle, Collection<Product> products) throws ClientProtocolException, IOException {
		String shopifyStoreHostName = shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(new AuthScope(shopifyStoreHostName, SHOPIFY_API_PORT_NUMBER), 
				new UsernamePasswordCredentials(key, password));
		
		try {
			// Prepare for XML marshalling
			JAXBContext jaxbContext = JAXBContext.newInstance( Product.class );
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			StringWriter stringWriter = new StringWriter();
			for(Product product : products) {
				// Marshall product to XML
				marshaller.marshal(product, stringWriter);
				
				// Prepare HTTP connection
		        HttpPost productHttpPost = new HttpPost(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + SHOPIFY_API_XML_EXTENSION_SUFFIX);
		        
		        // Stupid fixes for a stupid API XML DTD
		        String productEntityString = stringWriter.toString().replace("<variants>", "<variants type=\"array\">").replace("<options>", "<options type=\"array\">");
		        
		        // Prepare request content
		        StringEntity productEntity = new StringEntity(productEntityString);
		        productEntity.setContentType("application/xml");
		        productHttpPost.setEntity(productEntity);
		        
		        // Make sure we dont exceed API call allowance
		        trafficControl(shopifyStoreHandle);
		        
		        // Execute API call
		        HttpResponse productPostResponse = httpClient.execute(productHttpPost);
		        
		        // Look at response
				if(productPostResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) System.out.println("Successfuly posted product to Shopify…");
				else {
					throw new IllegalArgumentException("Halting. Attempt to post product with Shopify API failed : " + 
							productPostResponse.getStatusLine().toString() + " " + getContentStringFromResponse(productPostResponse));
				}
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}		

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
	}
	
	public static List<Product> getProductListFromAPI(String key, String password, String shopifyStoreHandle) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException {
        System.out.println("Getting list of existing products from Shopify…");
		List<Product> output = new Vector<Product>();

		String shopifyStoreHostName = shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(new AuthScope(shopifyStoreHostName, SHOPIFY_API_PORT_NUMBER), 
				new UsernamePasswordCredentials(key, password));


		// Look here for XML bindings : https://jaxb.dev.java.net/tutorial/
		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + 
				SHOPIFY_API_XML_EXTENSION_SUFFIX + "?" + PRODUCT_LIST_LIMIT_PARAMETER_NAME + "=" + String.valueOf(PRODUCT_LIST_LIMIT_PARAMETER_MAX));
        HttpGet httpGet = new HttpGet(uri);
        httpGet.getParams().setIntParameter(PRODUCT_LIST_LIMIT_PARAMETER_NAME, PRODUCT_LIST_LIMIT_PARAMETER_MAX);
        

		System.out.println("Trying to connect to Shopify at " + shopifyStoreUrl + 
				" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
				" with key \"" + key + "\" and password \"" + password +  "\", by retrieving product list at " + httpGet.getURI() + ".");
     
		
		// Make sure we dont exceed API call allowance
        trafficControl(shopifyStoreHandle);
        
        // Execute API call
		HttpResponse productListResponse = httpClient.execute(httpGet);
		
		// Look at API response
		if(productListResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			
			// Unarshall from response XML to object model
			JAXBContext jaxbContext = JAXBContext.newInstance(ProductListAPIWrappper.class);
			Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
			String responseString = getContentStringFromResponse(productListResponse);
			JAXBElement<ProductListAPIWrappper> root = unmarshaller.unmarshal(new StreamSource(new StringReader(responseString)), ProductListAPIWrappper.class);

			output = root.getValue().getProducts();

            System.out.println("Successfuly received from Shopify product list containing " + String.valueOf(output.size()) + " products.");
		} else {
			throw new IllegalArgumentException("Halting. Attempt to post product with Shopify API failed : " + 
					productListResponse.getStatusLine().toString() + " " + getContentStringFromResponse(productListResponse));
		}


        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
        
    
		return output;
	}

	
	public static void addAllMetafields(String key, String password, String shopifyStoreHandle, List<? extends Product> productsFromAPI, Map<String, ? extends Product> productsFromDatabase)
	throws ClientProtocolException, IOException, URISyntaxException  {
		int productMetafieldAddedCount = 0;
		int variantMetafieldAddedCount = 0;
		int productProcessedCount = 0;
		int variantProcessedCount = 0;

		System.out.println("Adding metafields to Shopify…");

		for(Product productFromAPI : productsFromAPI) {
			// Look for product in local database
			Product productFromDatabase = productsFromDatabase.get(productFromAPI.getHandle());
			if(productFromDatabase == null) {
				System.out.println("Couldn't find in database product with handle \"" + productFromAPI.getHandle() + "\"");
			} else {
				productProcessedCount++;
				
				// Look for product metafields
				for(Metafield metafield : productFromDatabase.getMetafields()) {
					createProductMetaField(key, password, shopifyStoreHandle, productFromAPI.getId(), metafield);
					productMetafieldAddedCount++;
				}
				// Iterate over variants from this product (from API)
				for(ProductVariant variantFromAPI : productFromAPI.getVariants()) {
					// Look for variant from database…
					ProductVariant matchingDatabaseVariant = null;
					for(ProductVariant databaseVariant : productFromDatabase.getVariants()) {
						// Check if variants from database and API match
						if(databaseVariant.getSku() != null && databaseVariant.getSku().equals(variantFromAPI.getSku())) {
							matchingDatabaseVariant = databaseVariant;
							variantProcessedCount++;
							// Look for variant metafields
							for(Metafield metafield : matchingDatabaseVariant.getMetafields()) {
								createVariantMetaField(key, password, shopifyStoreHandle, productFromAPI.getId(), variantFromAPI.getId(), metafield);
								variantMetafieldAddedCount++;
							}
							break;
						}
					}
					if(matchingDatabaseVariant == null) System.out.println("Couldn't find in database variant with SKU \"" + variantFromAPI.getSku() + "\"");
				}
			}
		}
		
		System.out.println("Successfully added "
				+ String.valueOf(productMetafieldAddedCount) + " product metafields (on " 
				+ String.valueOf(productProcessedCount) + " products) and "
				+ String.valueOf(variantMetafieldAddedCount) + " variant metafields (on " 
				+ String.valueOf(variantProcessedCount) + " variants), for a total of "
				+ String.valueOf(productMetafieldAddedCount + variantMetafieldAddedCount) + " metafields.");
	}

	
	public static void createProductMetaField(String key, String password, String shopifyStoreHandle, String productId, Metafield metafield)
	throws ClientProtocolException, IOException, URISyntaxException  {
		// Create URI
		String path = SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + productId + "/" + SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX;
		String shopifyStoreHostName = shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		createMetaField(key, password, new URI(shopifyStoreUrl + path), metafield);
	}
	public static void createVariantMetaField(String key, String password, String shopifyStoreHandle, String productId, String variantId, Metafield metafield)
	throws ClientProtocolException, IOException, URISyntaxException  {
		// Create URI
		String path = SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + productId + "/" 
		+ SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + variantId + "/" 
		+ SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX;
		String shopifyStoreHostName = shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		createMetaField(key, password, new URI(shopifyStoreUrl + path), metafield);
	}
	public static void createMetaField(String key, String password, URI URI, Metafield metafield) throws ClientProtocolException, IOException  {
		// Prepare API call client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, URI.getHost());
		
		try {
			// Prepare for XML marshalling
			JAXBContext jaxbContext = JAXBContext.newInstance(Metafield.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter stringWriter = new StringWriter();
			// Marshall metafield to XML
			marshaller.marshal(metafield, stringWriter);
			
			// Prepare HTTP connection
	        HttpPost metafieldHttpPost = new HttpPost(URI);
	        
	        // Stupid fixes for a stupid API XML DTD
	        String metafieldEntityString = stringWriter.toString().replace("<value>", "<value type=\"" + metafield.getValueType() + "\">");
	        
	        // Prepare request content
	        StringEntity metafieldEntity = new StringEntity(metafieldEntityString);
	        metafieldEntity.setContentType("application/xml");
	        metafieldHttpPost.setEntity(metafieldEntity);
	        
	        // Make sure we dont exceed API call allowance
	        trafficControl(getStoreHandleFromURI(URI));
		        
	        // Execute API call
	        HttpResponse metafieldPostResponse = httpClient.execute(metafieldHttpPost);
	        
	        // Look at response
			if(metafieldPostResponse.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
				throw new RuntimeException("Halting. Attempt to post metafield with Shopify API at " + URI + " failed : " + 
						metafieldPostResponse.getStatusLine().toString() + " " + getContentStringFromResponse(metafieldPostResponse));
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}		

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
	}
}
