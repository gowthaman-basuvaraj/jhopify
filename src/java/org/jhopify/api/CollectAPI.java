package org.jhopify.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jhopify.Collect;
import org.jhopify.api.wrappers.CollectListAPIWrapper;

public class CollectAPI extends API {
	public static List<Collect> getCollectsFromAPIForCollection(String key, String password, String shopifyStoreHandle, String collectionId) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException {
		List<Collect> output = new ArrayList<Collect>();
		
		// GET /admin/collects.xml?collection_id=841564295	
		URI uri = new URI(SHOPIFY_API_SCHEME + shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX + 
				SHOPIFY_API_URI_PREFIX + "collects.xml?collection_id=" + collectionId);
		

		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, uri.getHost());
        HttpGet httpGet = new HttpGet(uri);
        

		// Make sure we dont exceed API call allowance
        trafficControl(shopifyStoreHandle);
        
        // Execute API call
		HttpResponse response = httpClient.execute(httpGet);
		
		// Look at API response
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			// Unarshall from response XML to object model
			JAXBContext jaxbContext = JAXBContext.newInstance(CollectListAPIWrapper.class);
			Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
			String responseString = getContentStringFromResponse(response);
			JAXBElement<CollectListAPIWrapper> root = unmarshaller.unmarshal(new StreamSource(new StringReader(responseString)), CollectListAPIWrapper.class);

			if(root.getValue() != null) {
				output.addAll(root.getValue().getCollects());
			}
		} else {
			throw new IllegalArgumentException("Halting. Attempt to retrieve order list with Shopify API failed : " + 
					response.getStatusLine().toString() + " " + getContentStringFromResponse(response));
		}

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();
		return output;
	}
}
