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
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.jhopify.Metafield;
import org.jhopify.Order;
import org.jhopify.OrderLineItem;
import org.jhopify.Product;
import org.jhopify.ProductVariant;
import org.jhopify.api.wrappers.ProductListAPIWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProductAPI extends API {

	public static final String PRODUCT_LIST_LIMIT_PARAMETER_NAME = "limit";
	public static final String PRODUCT_COUNT_SUFFIX = "/count.xml";
	public static final int PRODUCT_LIST_LIMIT_PARAMETER_MAX = 250;
	public static final String PRODUCT_LIST_PAGE_PARAMETER_NAME = "page";
	public static final Pattern PRODUCT_COUNT_PATTERN = Pattern.compile("([0-9]+)");
	public static final String PRODUCT_LIST_COLLECTION_ID_PARAMETER_NAME = "collection_id";
	
	
	public static void saveProducts(String key, String password, String shopifyStoreHandle, Collection<Product> products) 
	throws ClientProtocolException, IOException {
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		// Prepare client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);
		
		try {
			// Prepare for XML marshalling
			JAXBContext jaxbContext = JAXBContext.newInstance(Product.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			StringWriter stringWriter = new StringWriter();
			for(Product product : products) {
				// Marshall product to XML
				marshaller.marshal(product, stringWriter);
				
				// Prepare HTTP connection
		        HttpPost productHttpPost = new HttpPost(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + 
		        		SHOPIFY_API_XML_EXTENSION_SUFFIX);
		        
		        // Stupid fixes for a stupid API XML DTD
		        String productEntityString = stringWriter.toString().replace("<variants>", 
		        		"<variants type=\"array\">").replace("<options>", "<options type=\"array\">");
		        
		        // Prepare request content
		        StringEntity productEntity = new StringEntity(productEntityString);
		        productEntity.setContentType("application/xml");
		        productHttpPost.setEntity(productEntity);
		        
		        // Make sure we dont exceed API call allowance
		        trafficControl(shopifyStoreHandle);
		        
		        // Execute API call
		        HttpResponse productPostResponse = httpClient.execute(productHttpPost);
		        
		        // Look at response
				if(productPostResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) { 
					System.out.println("Successfuly posted product to Shopify…");
				} else {
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
	
	public static List<Product> getProductListFromAPI(String key, String password, String shopifyStoreHandle, List<String> ids) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException {
		List<Product> output = new Vector<Product>();
		for(String id : ids) {
			
			URI uri = new URI(SHOPIFY_API_SCHEME + shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX + 
					SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + id + SHOPIFY_API_XML_EXTENSION_SUFFIX);

			HttpGet httpGet = new HttpGet(uri);
	        
			// Prepare HTTP client
			HttpClient httpClient = getAuthenticatedHttpClient(key, password, uri.getHost());


			System.out.println("Trying to connect to Shopify at " + uri + 
					" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
					" with key \"" + key + "\" and password \"" + password +  "\", to retreive product " + 
					httpGet.getURI() + ".");
	     
			
			// Make sure we dont exceed API call allowance
	        trafficControl(shopifyStoreHandle);
	        
	        // Execute API call
			HttpResponse productListResponse = httpClient.execute(httpGet);
			
			// Look at API response
			if(productListResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				
				// Unarshall from response XML to object model
				JAXBContext jaxbContext = JAXBContext.newInstance(Product.class);
				Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
				String responseString = getContentStringFromResponse(productListResponse);
				JAXBElement<Product> root = unmarshaller.unmarshal(
						new StreamSource(new StringReader(responseString)),
						Product.class);

				output.add(root.getValue());

	            System.out.println("Successfuly received product from Shopify.");
			} else {
				throw new IllegalArgumentException("Halting. Attempt to get product with Shopify API at " + uri + " failed : " + 
						productListResponse.getStatusLine().toString() + " " + 
						getContentStringFromResponse(productListResponse));
			}


	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpClient.getConnectionManager().shutdown();
		}
        
    
		return output;
	}
	public static List<Product> getProductListFromAPI(String key, String password, String shopifyStoreHandle) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException, ParserConfigurationException, SAXException {
		return getProductListFromAPI2(key, password, shopifyStoreHandle, null);
	}
	
	public static List<Product> getProductListFromAPI2(String key, String password, String shopifyStoreHandle, String collectionId) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException, ParserConfigurationException, SAXException {
		List<Product> output = new Vector<Product>();
        System.out.println("Getting list of existing products from Shopify…");
        
        // Get product count
        Integer productCount = getProductCount(key, password, shopifyStoreHandle, collectionId);

		// Prepare constants
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);
        
		int expectedMaximumReceived = 0;
		int pageReceivedCount = 0;
		String collectionSuffix = collectionId == null ? "" : "&" + PRODUCT_LIST_COLLECTION_ID_PARAMETER_NAME + "=" + collectionId;
        while(expectedMaximumReceived < productCount) {
        	int pageNumber = (output.size() / PRODUCT_LIST_LIMIT_PARAMETER_MAX) + 1;
    		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + 
    				SHOPIFY_API_XML_EXTENSION_SUFFIX + "?" +
    				PRODUCT_LIST_LIMIT_PARAMETER_NAME + "=" + String.valueOf(PRODUCT_LIST_LIMIT_PARAMETER_MAX) +
    				"&" + PRODUCT_LIST_PAGE_PARAMETER_NAME + "=" + String.valueOf(pageNumber) + collectionSuffix);
            HttpGet httpGet = new HttpGet(uri);
            

    		System.out.println("Trying to connect to Shopify at " + shopifyStoreUrl + 
    				" on port " + String.valueOf(SHOPIFY_API_PORT_NUMBER)  + 
    				" with key \"" + key + "\" and password \"" + password +  "\", by retrieving product list at " + 
    				httpGet.getURI() + ".");
         
    		
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
    			output.addAll(root.getValue().getProducts());

    	        System.out.println("Successfuly received from Shopify product list containing " + String.valueOf(root.getValue().getProducts().size())
    	        		+ " products for a total of " + String.valueOf(output.size()) + "/" + String.valueOf(productCount) + " products.");
    	        
    	        expectedMaximumReceived += PRODUCT_LIST_LIMIT_PARAMETER_MAX;
    	        pageReceivedCount++;
    		} else {
    			throw new IllegalArgumentException("Halting. Attempt to post product with Shopify API failed : " + 
    					productListResponse.getStatusLine().toString() + " " + getContentStringFromResponse(productListResponse));
    		}
        }

        if(output.size() < productCount) System.err.println("Actually received " + String.valueOf(output.size())
        		+ " products on " + String.valueOf(pageReceivedCount) 
        		+ " pages (for a max of products per page of " + String.valueOf(PRODUCT_LIST_LIMIT_PARAMETER_MAX) +
        		" products), when expected count (based on API product count) is of " + String.valueOf(productCount) + " products.");


        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
        
    
		return output;
	}

	
	private static Integer getProductCount(String key, String password, String shopifyStoreHandle, String collectionId) 
	throws URISyntaxException, ClientProtocolException, IOException, JAXBException, ParserConfigurationException, SAXException {
		Integer output = 0;

		// Prepare HTTP client
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);
		String collectionSuffix = collectionId == null ? "" : "?" + PRODUCT_LIST_COLLECTION_ID_PARAMETER_NAME + "=" + collectionId;
		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_PRODUCT_URI_PREFIX + PRODUCT_COUNT_SUFFIX + collectionSuffix);
        HttpGet httpGet = new HttpGet(uri);
	
        // Make sure we dont exceed API call allowance
        trafficControl(shopifyStoreHandle);
 
        // Execute API call
		HttpResponse response = httpClient.execute(httpGet);
		
		// Look at API response
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			
			// Unmarshall from response XML to object model
			String responseString = getContentStringFromResponse(response);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document responseDOM = db.parse(new InputSource(new StringReader(responseString)));
			NodeList elements = responseDOM.getElementsByTagName("count");
			for(int i = 0; i < elements.getLength(); i++) {
				Node node = elements.item(i);
				output = Integer.parseInt(node.getTextContent());
			}

            System.out.println("Successfuly received from Shopify product count of " + String.valueOf(output) + " products.");
		} else {
			throw new IllegalArgumentException("Halting. Attempt to post product with Shopify API failed : " + 
					response.getStatusLine().toString() + " " + getContentStringFromResponse(response));
		}

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
      
       

		return output;
	}

	public static void addAllMetafields(String key, String password, String shopifyStoreHandle, 
			List<? extends Product> productsFromAPI, Map<String, ? extends Product> productsFromDatabase)
	throws ClientProtocolException, IOException, URISyntaxException, InterruptedException  {
		int productMetafieldAddedCount = 0;
		int failedProductMetafieldCount = 0;
		int variantMetafieldAddedCount = 0;
		int failedVariantMetafieldCount = 0;
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
					try {
						createProductMetaField(key, password, shopifyStoreHandle, productFromAPI.getId(), metafield);
						productMetafieldAddedCount++;
					} catch(Exception e) {
						failedProductMetafieldCount++;
						e.printStackTrace(System.err);
						System.err.println("Skipping and sleeping 10s…");
						Thread.sleep(10000);
					}
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
								try {
									ProductVariantAPI.createVariantMetaField(key, password, shopifyStoreHandle, productFromAPI.getId(), variantFromAPI.getId(), metafield);
									variantMetafieldAddedCount++;
								} catch(Exception e) {
									failedVariantMetafieldCount++;
									e.printStackTrace(System.err);
									System.err.println("Skipping and sleeping 10s…");
									Thread.sleep(10000);
								}
							}
							break;
						}
					}
					if(matchingDatabaseVariant == null) System.out.println("Couldn't find in database variant with SKU \"" + 
							variantFromAPI.getSku() + "\"");
				}
			}
		}
		
		System.out.println("Successfully added "
				+ String.valueOf(productMetafieldAddedCount) + " product metafields (on " 
				+ String.valueOf(productProcessedCount) + " products) and "
				+ String.valueOf(variantMetafieldAddedCount) + " variant metafields (on " 
				+ String.valueOf(variantProcessedCount) + " variants), for a total of "
				+ String.valueOf(productMetafieldAddedCount + variantMetafieldAddedCount) + " metafields.");
		System.out.println("Failed to add "
				+ String.valueOf(failedProductMetafieldCount) + " product metafields and "
				+ String.valueOf(failedVariantMetafieldCount) + " variant metafield.");
	}

	
	public static void createProductMetaField(String key, String password, String shopifyStoreHandle, 
			String productId, Metafield metafield)
	throws ClientProtocolException, IOException, URISyntaxException  {
		// Create URI
		String path = SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + productId + "/" + 
		SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX;
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		createMetaField(key, password, new URI(shopifyStoreUrl + path), metafield);
	}

	public static void updateAllTitles(String key, String password, String shopifyStoreHandle, 
			List<? extends Product> productsFromAPI, Map<String, ? extends Product> productsFromDatabase) 
	throws ClientProtocolException, IOException, URISyntaxException  {
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
								productTitlePutResponse.getStatusLine().toString() + " " + 
								getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + productTitleString);
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
			Collection<Order> orders) throws URISyntaxException, ClientProtocolException, IOException, 
			IllegalStateException, JAXBException, ParserConfigurationException, SAXException {

		List<Product> output = new ArrayList<Product>();

		List<Product> products = getProductListFromAPI(key, password, shopHandle);
		
		// Iterate on all product variant to adjust inventory
		for(Product product : products) {
			Boolean isAffected = false;
			String productId = product.getId();
			for(ProductVariant variant : product.getVariants()) {
				// Iterate on orders to see if we have matching line items
				// And count inventory drops;
				Integer orderedQuantity = 0;
				String sku = variant.getSku();
				String id = variant.getId();
				
				if(sku == null || id == null || productId == null) {
					throw new RuntimeException("BAAAADASSSS PROBLEM");
				} else {
					Integer quantity = variant.getInventoryQuantity();
					sku = sku.toUpperCase();
					for(Order order : orders) {
						// Iterate over line items
						String fulfillmentStatus = order.getFulfillmentStatus();
						for(OrderLineItem item : order.getLineItems()) {
							// Check for match
							String itemSKU = item.getSku();
							if(itemSKU != null && sku.equals(itemSKU.toUpperCase())) {
								// If order fulfilled, 
								String lineFulfillmentStatus = item.getFulfillmentStatus();
								if(Order.PARTIAL_FULFILLMENT_STATUS_VALUE.equals(fulfillmentStatus)) {
									if(Order.FULFILLED_FULFILLMENT_STATUS_VALUE.equals(lineFulfillmentStatus)) {
										orderedQuantity += item.getQuantity();
									} else {
										System.err.println("Skipping sku #"  + String.valueOf(sku) +
												"from partial order #" + String.valueOf(order.getNumber()) + ".");
									}
									
								} else {
									orderedQuantity += item.getQuantity();
								}
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
							throw new RuntimeException("Halting. Attempt to update variant inventory quantity  at " + 
									URI + " failed : " + 
									productTitlePutResponse.getStatusLine().toString() + " " + 
									getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + entityString);
						} else {
							System.out.println("Sucessfully adjusted SKU \"" + sku + 
									"\" for total adjustment: -" + String.valueOf(orderedQuantity));
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
	
	public static List<Product> getProductsWithMetafields(String key, String password, String shopifyStoreHandle, 
			List<String> ids) throws ClientProtocolException, IllegalStateException, IOException, 
			JAXBException, URISyntaxException, ParserConfigurationException, SAXException {
		
		List<Product> output = null;
		if(ids != null && ids.size() > 0) output = getProductListFromAPI(key, password, shopifyStoreHandle, ids);
		else output = getProductListFromAPI(key, password, shopifyStoreHandle);

		// Iterate to get metafields
		for(Product product: output) {
			// Get product metafields
			URI productMetafieldURI = new URI(SHOPIFY_API_SCHEME + shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX + 
					SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + product.getId() + "/" + 
					SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX);
			List<Metafield> productMetafields = getMetafields(key, password, productMetafieldURI);
			if(productMetafields != null && !productMetafields.isEmpty()) {
				product.getMetafields().addAll(productMetafields);
			}

			// Iterate on variants to get metafields
			for(ProductVariant variant : product.getVariants()) {
				// Get product metafields
				URI variantMetafieldURI = new URI(SHOPIFY_API_SCHEME + shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX + SHOPIFY_API_URI_PREFIX
						+ ProductVariantAPI.SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + variant.getId() + "/" 
						+ SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX);
				List<Metafield> variantMetafields = getMetafields(key, password, variantMetafieldURI);
				if(variantMetafields != null && !variantMetafields.isEmpty()) {
					variant.getMetafields().addAll(variantMetafields);
				}
			}
		}
		return output;
	}
	
	static public List<Product> getAffectedProducts(
			String key,
			String password,
			String shopHandle,
			Collection<Order> orders) throws URISyntaxException, ClientProtocolException, IOException, 
			IllegalStateException, JAXBException, ParserConfigurationException, SAXException {

		// Get product IDs from orders
		List<String> ids = new ArrayList<String>();
		for(Order order : orders) {
			for(OrderLineItem item : order.getLineItems()) {
				String productId = item.getProductId();
				if(!ids.contains(productId)) ids.add(productId);
			}
		}
		
		// Get and return products
		return getProductsWithMetafields(key, password, shopHandle, ids);
	}
	
	static public void setPrices(
			String key,
			String password,
			String shopHandle,
			Product product) throws URISyntaxException, ClientProtocolException, IOException {
		for(ProductVariant variant : product.getVariants()) {
			URI URI = new URI(SHOPIFY_API_SCHEME + shopHandle + SHOPIFY_API_DOMAIN_SUFFIX +  
					SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + product.getId() + 
					"/"+ ProductVariantAPI.SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + 
					variant.getId() + SHOPIFY_API_XML_EXTENSION_SUFFIX);

			// Prepare API call client
			HttpClient httpClient = getAuthenticatedHttpClient(key, password, URI.getHost());

			// Prepare HTTP connection
			HttpPut method = new HttpPut(URI);

	        // Prepare request content
	        StringBuffer sb = new StringBuffer();
	        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<variant><compare-at-price type=\"decimal\">");
	        sb.append(variant.getCompareAtPrice());
	        sb.append("</compare-at-price><price type=\"decimal\">");
	        sb.append(variant.getPrice());
	        sb.append("</price></variant>");

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
				System.out.println("Sucessfully adjusted prices for SKU \"" + variant.getSku() + "\".");
			}
		}
	}
	
	static public void setTitle(
			String key,
			String password,
			String shopHandle,
			Product product) throws URISyntaxException, ClientProtocolException, IOException {
		URI URI = new URI(SHOPIFY_API_SCHEME + shopHandle + SHOPIFY_API_DOMAIN_SUFFIX +  
				SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + product.getId() + SHOPIFY_API_XML_EXTENSION_SUFFIX);

		// Prepare API call client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, URI.getHost());

		// Prepare HTTP connection
		HttpPut method = new HttpPut(URI);

        // Prepare request content
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<product><title>");
        sb.append(product.getTitle());
        sb.append("</title></product>");

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
			throw new RuntimeException("Halting. Attempt to update product title at " + URI + " failed : " + 
					productTitlePutResponse.getStatusLine().toString() + " " + 
					getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + entityString);
		} else {
			System.out.println("Sucessfully updated product title for handle \"" + product.getHandle() + "\".");
		}
	}

	static public void delete(
			String key,
			String password,
			String shopHandle,
			String id) throws URISyntaxException, ClientProtocolException, IOException {
		URI URI = new URI(SHOPIFY_API_SCHEME + shopHandle + SHOPIFY_API_DOMAIN_SUFFIX +  
				SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + id + SHOPIFY_API_XML_EXTENSION_SUFFIX);

		// Prepare API call client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, URI.getHost());

		// Prepare HTTP connection
		HttpDelete method = new HttpDelete(URI);
        
        // Make sure we dont exceed API call allowance
        trafficControl(getStoreHandleFromURI(URI));
	        
        // Execute API call
        HttpResponse productTitlePutResponse = httpClient.execute(method);
        
        // Look at response
		if(productTitlePutResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new RuntimeException("Halting. Attempt to delete product  " + URI + " failed : " + 
					productTitlePutResponse.getStatusLine().toString() + " " + 
					getContentStringFromResponse(productTitlePutResponse));
		}
	}
}
