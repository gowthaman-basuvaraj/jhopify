package org.jhopify.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class FulfillmentAPI extends API {

	static String SHOPIFY_API_FULFILLMENT_SUFFIX = "fulfillments";


	public static void fullfillAllLineItemsAndNotifyCustomer(
			String key, String password, String shopifyStoreHandle, 
			String orderId, String trackingNumber) 
	throws URISyntaxException, ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		URI uri = new URI(SHOPIFY_API_SCHEME + shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX + 
				SHOPIFY_API_URI_PREFIX + OrderAPI.SHOPIFY_API_ORDERS_SUFFIX
				+ "/" + orderId + "/" + SHOPIFY_API_FULFILLMENT_SUFFIX + SHOPIFY_API_XML_EXTENSION_SUFFIX);

		// Prepare API call client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, uri.getHost());

		// Prepare HTTP connection
		HttpPost method = new HttpPost(uri);

        // Prepare request content
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<fulfillment><notify-customer type=\"boolean\">true</notify-customer><tracking-number>");
        sb.append(trackingNumber);
        sb.append("</tracking-number></fulfillment>");

        // Set request content
        String entityString = sb.toString();
        StringEntity entity = new StringEntity(entityString);
        entity.setContentType("application/xml");
        method.setEntity(entity);
        
        // Make sure we dont exceed API call allowance
        trafficControl(getStoreHandleFromURI(uri));
	        
        // Execute API call
        HttpResponse productTitlePutResponse = httpClient.execute(method);
        
        // Look at response
		if(productTitlePutResponse.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
			throw new RuntimeException("Halting. Failure to create fulfillment at " + uri + " failed : " + 
					productTitlePutResponse.getStatusLine().toString() + " " + 
					getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + entityString);
		}
	}
}
