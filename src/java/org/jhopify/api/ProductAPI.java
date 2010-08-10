package org.jhopify.api;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.jhopify.Metafield;
import org.jhopify.Order;
import org.jhopify.OrderLineItem;
import org.jhopify.Product;
import org.jhopify.ProductVariant;

public class ProductAPI extends API {

	public static final String PRODUCT_LIST_LIMIT_PARAMETER_NAME = "limit";
	public static final int PRODUCT_LIST_LIMIT_PARAMETER_MAX = 250;
	
	public static void saveProducts(String key, String password, String shopifyStoreHandle, Collection<Product> products) throws ClientProtocolException, IOException {
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		// Prepare client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);
		
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

		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);


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
			JAXBContext jaxbContext = JAXBContext.newInstance(ProductListAPIWrapper.class);
			Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
			String responseString = getContentStringFromResponse(productListResponse);
			JAXBElement<ProductListAPIWrapper> root = unmarshaller.unmarshal(new StreamSource(new StringReader(responseString)), ProductListAPIWrapper.class);

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
								ProductVariantAPI.createVariantMetaField(key, password, shopifyStoreHandle, productFromAPI.getId(), variantFromAPI.getId(), metafield);
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
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		createMetaField(key, password, new URI(shopifyStoreUrl + path), metafield);
	}

	public static void updateAllTitles(String key, String password, String shopifyStoreHandle, List<? extends Product> productsFromAPI, Map<String, ? extends Product> productsFromDatabase) throws ClientProtocolException, IOException, URISyntaxException  {
		int productTitleModifiedCount = 0;

		System.out.println("Updating product titles to Shopify…");
		
		// Create URI components
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		

		

		for(Product productFromAPI : productsFromAPI) {
			// Look for product in local database
			Product productFromDatabase = productsFromDatabase.get(productFromAPI.getHandle());
			if(productFromDatabase != null) {
				String databaseProductTitle = productFromDatabase.getTitle();
				if(databaseProductTitle != null && !databaseProductTitle.equals(productFromAPI.getTitle())) {
					String path = SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + productFromAPI.getId() + ".xml";

					// Prepare API call client
					HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);

					// Prepare HTTP connection
					URI URI = new URI(shopifyStoreUrl + path);
			        HttpPut productTitleHttpPut = new HttpPut(URI);

			        // Prepare request content
			        StringBuffer sb = new StringBuffer();
			        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<product><title>");
			        sb.append(databaseProductTitle.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
			        sb.append("</title>");
			        sb.append("<id type=\"integer\">");
			        sb.append(productFromAPI.getId());
			        sb.append("</id></product>");

			        // Set request content
			        String productTitleString = sb.toString();
			        StringEntity productTitleEntity = new StringEntity(productTitleString);
			        productTitleEntity.setContentType("application/xml");
			        productTitleHttpPut.setEntity(productTitleEntity);
			        
			        // Make sure we dont exceed API call allowance
			        trafficControl(getStoreHandleFromURI(URI));
				        
			        // Execute API call
			        HttpResponse productTitlePutResponse = httpClient.execute(productTitleHttpPut);
			        
			        // Look at response
					if(productTitlePutResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						throw new RuntimeException("Halting. Attempt to update product title at " + URI + " failed : " + 
								productTitlePutResponse.getStatusLine().toString() + " " + getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + productTitleString);
					}

			        // When HttpClient instance is no longer needed, 
			        // shut down the connection manager to ensure
			        // immediate deallocation of all system resources
			        httpClient.getConnectionManager().shutdown(); 

					productTitleModifiedCount++;
				}
			}
		} 
		
		System.out.println("Successfully modified "+ String.valueOf(productTitleModifiedCount) + " product titles.");
	}
	
	static public List<Product> adjustInventoryFromLineItemSKUs(
			String key,
			String password,
			String shopHandle,
			Collection<Order> orders) throws URISyntaxException, ClientProtocolException, IOException, IllegalStateException, JAXBException {

		List<Product> output = new ArrayList<Product>();

		List<Product> products = getProductListFromAPI(key, password, shopHandle);
		
		// Iterate on all product variant to adjust inventory
		for(Product product : products) {
			Boolean isAffected = false;
			for(ProductVariant variant : product.getVariants()) {
				// Iterate on orders to see if we have matching line items
				// And count inventory drops;
				Integer orderedQuantity = 0;
				String sku = variant.getSku();
				String id = variant.getId();
				String productId = variant.getProductId();
				Integer quantity = variant.getInventoryQuantity();
				if(sku != null) {
					sku = sku.toUpperCase();
					for(Order order : orders) {
						// Iterate over line items
						for(OrderLineItem item : order.getLineItems()) {
							// Check for match
							String itemSKU = item.getSku();
							if(itemSKU != null && sku.equals(itemSKU.toUpperCase())) {
								// Adjust quantity
								System.out.println("Adjusting SKU \"" + itemSKU + "\" from order #" + order.getOrderNumber() + " : -" + item.getQuantity());
								orderedQuantity += item.getQuantity();
							}
						}
					}
					// Check if adjustment is necessary
					if(orderedQuantity > 0) {
						URI URI = new URI(SHOPIFY_API_SCHEME + shopHandle + SHOPIFY_API_DOMAIN_SUFFIX +  
								SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + String.valueOf(productId) + 
								"/"+ ProductVariantAPI.SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + 
								String.valueOf(id) + SHOPIFY_API_XML_EXTENSION_SUFFIX);

						// Prepare API call client
						HttpClient httpClient = getAuthenticatedHttpClient(key, password, URI.getHost());

						// Prepare HTTP connection
				        HttpPut method = new HttpPut(URI);

				        // Prepare request content
				        StringBuffer sb = new StringBuffer();
				        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<variant><inventory-quantity type=\"integer\">");
				        sb.append(quantity - orderedQuantity);
				        sb.append("</inventory-quantity></variant>");

				        // Set request content
				        String entityString = sb.toString();
				        StringEntity entity = new StringEntity(entityString);
				        entity.setContentType("application/xml");
				        method.setEntity(entity);
				        
				        // Make sure we dont exceed API call allowance
				        trafficControl(getStoreHandleFromURI(URI));
					        
				        // Execute API call
				        HttpResponse productTitlePutResponse = httpClient.execute(method);
				        
				        // Look at response
						if(productTitlePutResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							throw new RuntimeException("Halting. Attempt to update variant inventory quantity  at " + URI + " failed : " + 
									productTitlePutResponse.getStatusLine().toString() + " " + 
									getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + entityString);
						} else {
							System.out.println("Sucessfully adjusted SKU \"" + sku + "\" for total adjustment: -" + String.valueOf(orderedQuantity));
						}


						// TODO : make it work even when we throw exception
				        // When HttpClient instance is no longer needed, 
				        // shut down the connection manager to ensure
				        // immediate deallocation of all system resources
				        httpClient.getConnectionManager().shutdown(); 
				        
				        // Mark as affected
						isAffected = true;
					}
				}
			}
			if(isAffected) output.add(product);
		}
		return output;
	}
}
