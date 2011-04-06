package org.jhopify.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.jhopify.Collect;
import org.jhopify.Product;
import org.jhopify.api.wrappers.CollectListAPIWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CollectAPI extends API {
	public static final String COLLECT_LIST_LIMIT_PARAMETER_NAME = "limit";
	public static final String COLLECT_COUNT_SUFFIX = "/count.xml";
	public static final int COLLECT_LIST_LIMIT_PARAMETER_MAX = 250;
	public static final String COLLECT_LIST_PAGE_PARAMETER_NAME = "page";
	public static final Pattern COLLECT_COUNT_PATTERN = Pattern.compile("([0-9]+)");
	public static final String COLLECT_LIST_COLLECTION_ID_PARAMETER_NAME = "collection_id";
	
	
	
	
	
	
	public static List<Collect> getCollectsFromAPIForCollection(String key, String password, String shopifyStoreHandle, String collectionId) 
	throws ClientProtocolException, IOException, IllegalStateException, JAXBException, URISyntaxException, ParserConfigurationException, SAXException {
		List<Collect> output = new ArrayList<Collect>();
		
        // Get collect count
        Integer collectCount = getCollectCount(key, password, shopifyStoreHandle, collectionId);

		// Prepare constants
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;

		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);
        
		
		int expectedMaximumReceived = 0;
		int pageReceivedCount = 0;
		String collectionSuffix = "&" + COLLECT_LIST_COLLECTION_ID_PARAMETER_NAME + "=" + collectionId;
		while(expectedMaximumReceived < collectCount) {
        	int pageNumber = (output.size() / COLLECT_LIST_LIMIT_PARAMETER_MAX) + 1;
    		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_COLLECT_URI_PREFIX + 
    				SHOPIFY_API_XML_EXTENSION_SUFFIX + "?" +
    				COLLECT_LIST_LIMIT_PARAMETER_NAME + "=" + String.valueOf(COLLECT_LIST_LIMIT_PARAMETER_MAX) +
    				"&" + COLLECT_LIST_PAGE_PARAMETER_NAME + "=" + String.valueOf(pageNumber) + collectionSuffix);
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

				output.addAll(root.getValue().getCollects());
				expectedMaximumReceived += COLLECT_LIST_LIMIT_PARAMETER_MAX;
    	        pageReceivedCount++;
    		} else {
    			throw new IllegalArgumentException("Halting. Attempt to retrieve order list with Shopify API failed : " + 
    					response.getStatusLine().toString() + " " + getContentStringFromResponse(response));
    		}
		}
		

		

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();
		return output;
	}


	private static Integer getCollectCount(String key, String password, String shopifyStoreHandle, String collectionId)
	throws URISyntaxException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		Integer output = 0;

		// Prepare HTTP client
		String shopifyStoreHostName = shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, shopifyStoreHostName);
		URI uri = new URI(shopifyStoreUrl + SHOPIFY_API_COLLECT_URI_PREFIX + COLLECT_COUNT_SUFFIX + "?" + COLLECT_LIST_COLLECTION_ID_PARAMETER_NAME + "=" + collectionId);
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
		} else {
			throw new IllegalArgumentException("Halting. Attempt to post collect with Shopify API failed : " + 
					response.getStatusLine().toString() + " " + getContentStringFromResponse(response));
		}

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();       

		return output;
	}


	public static void createCollect(String key, String password, String shopifyStoreHandle, 
			String collectionId, Product product) 
	throws URISyntaxException, ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		URI uri = new URI(SHOPIFY_API_SCHEME + shopifyStoreHandle + SHOPIFY_API_DOMAIN_SUFFIX + 
				SHOPIFY_API_URI_PREFIX + "collects.xml");

		// Prepare API call client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, uri.getHost());

		// Prepare HTTP connection
		HttpPost method = new HttpPost(uri);

        // Prepare request content
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<collect><product-id type=\"integer\">");
        sb.append(product.getId());
        sb.append("</product-id><collection-id type=\"integer\">");
        sb.append(collectionId);
        sb.append("</collection-id></collect>");

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
			throw new RuntimeException("Halting. Failure to create collect at " + uri + " failed : " + 
					productTitlePutResponse.getStatusLine().toString() + " " + 
					getContentStringFromResponse(productTitlePutResponse) + "\n\n\n\nXML:\n" + entityString);
		}
	}
}
