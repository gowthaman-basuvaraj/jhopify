package org.jhopify.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.jhopify.Metafield;
import org.jhopify.Order;
import org.jhopify.OrderLineItem;
import org.jhopify.Product;
import org.jhopify.ProductVariant;

public class ProductVariantAPI extends API {
	public static final String SHOPIFY_API_VARIANT_URI_SUFFIX = "variants";
	public static void createVariantMetaField(String key, String password, String shopifyStoreHandle, String productId, String variantId, Metafield metafield)
	throws ClientProtocolException, IOException, URISyntaxException  {
		// Create URI
// OLD VERSION
//		String path = SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + productId + "/" 
//		+ SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + variantId + "/" 
//		+ SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX;
		String path = SHOPIFY_API_URI_PREFIX 
		+ SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + variantId + "/" 
		+ SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX;
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		createMetaField(key, password, new URI(shopifyStoreUrl + path), metafield);
	}

	
	public static void adjustInventoryFromLineItemSKUs(
			String key,
			String password,
			String shopHandle,
			Collection<Order> orders) throws URISyntaxException, ClientProtocolException, IOException, IllegalStateException, JAXBException {
		List<ProductVariant> variants = getAllProductVariants(key, password, shopHandle);
		
		// Iterate on all product variant to adjust inventory
		for(ProductVariant variant : variants) {
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
							"/"+ SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + 
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
				}
			}
		}
	}


	private static List<ProductVariant> getAllProductVariants(String key,
			String password, String shopHandle) throws ClientProtocolException, IllegalStateException, IOException, JAXBException, URISyntaxException {
		List<ProductVariant> output = new ArrayList<ProductVariant>();
		List<Product> products = ProductAPI.getProductListFromAPI(key, password, shopHandle);
		for(Product product : products) {
			for(ProductVariant variant : product.getVariants()) {
				variant.setProductId(product.getId());
				output.add(variant);
			}
		}
		return output;
	}
}
