package org.jhopify.api;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.commons.codec.binary.Hex;
import org.jhopify.Metafield;

public class API {
	public static final String SHOPIFY_API_SCHEME = "http://";
	public static final String SHOPIFY_API_DOMAIN = "myshopify.com";
	public static final String SHOPIFY_API_URI_PREFIX  = "/admin/";
	public static final String SHOPIFY_API_METAFIELD_LIST_FILE_NAME = "metafields";
	public static final String SHOPIFY_API_METAFIELD_KEY_QUERY_PARAMETER_NAME = "key";
	public static final String SHOPIFY_API_METAFIELD_NAMESPACE_QUERY_PARAMETER_NAME = "namespace";
	public static final String SHOPIFY_API_METAFIELD_VALUE_TYPE_QUERY_PARAMETER_NAME = "value_type";
	public static final String SHOPIFY_API_PRODUCT_URI_PREFIX = SHOPIFY_API_URI_PREFIX + "products";
	public static final String SHOPIFY_API_XML_EXTENSION_SUFFIX = ".xml";
	public static final int SHOPIFY_API_PORT_NUMBER = 80;
	public static final int QUERY_PER_PERIOD_PER_STORE_ALLOWANCE = 120;
	public static final int QUERY_PERIODIC_RESET_IN_MINUTES = 1;
	public static final String SHOPIFY_CALCULATED_SIGNATURE_PARAMETER_NAME = "signature";
	public static final String SHOPIFY_SHOP_PARAMETER_NAME = "shop";
	public static final String SHOPIFY_TOKEN_PARAMETER_NAME = "t";

	public static String getContentStringFromResponse(HttpResponse response) throws IllegalStateException, IOException {
		HttpEntity entity = response.getEntity();
		BufferedReader entityReader = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuffer sb = new StringBuffer();
	    int character = -1;
	    while( ( character = entityReader.read() ) != -1 ) {
	    	sb.append( (char) character );
	    }
	    return sb.toString();
	}

	public static void trafficControl(String storeHandle) {
		TrafficController.getTrafficController(storeHandle).trafficControl();
	}

	public static HttpClient getAuthenticatedHttpClient(String key, String password, String hostName) {
		HttpClient output;

		output = new DefaultHttpClient();
		((AbstractHttpClient) output).getCredentialsProvider().setCredentials(new AuthScope(hostName, SHOPIFY_API_PORT_NUMBER), 
				new UsernamePasswordCredentials(key, password));

		return output;
	}

	public static String getStoreHandleFromURI(URI URI) {
		return URI.getHost().substring(0, URI.getHost().indexOf('.'));
	}
	
	static class TrafficController {
		static Map<String,TrafficController> controllers = new TreeMap<String,TrafficController>();
		long nextResetTime;
		int apiCallsSinceLastReset;
		String storeHandle;
		public TrafficController(String storeHandle) {
			reset();
			this.storeHandle = storeHandle;
			controllers.put(storeHandle, this);
		}
		public void trafficControl() {
			long now = System.currentTimeMillis();
			if(now > nextResetTime) {
				// We are past reset time, reset...
				reset();
			} else if(apiCallsSinceLastReset > QUERY_PER_PERIOD_PER_STORE_ALLOWANCE) {
				// Too many calls, we have to pause for a while..
				try {
					long sleepTime = nextResetTime - now;
					System.out.println("Sleeping for " + String.valueOf(sleepTime) + "ms on \"" + storeHandle + "\".");
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				reset();
			}
			apiCallsSinceLastReset++;
		}
		public void reset() {
			System.out.println("Resetting API timeout…");
			this.apiCallsSinceLastReset = 0;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, QUERY_PERIODIC_RESET_IN_MINUTES);
			this.nextResetTime = calendar.getTimeInMillis();
		}
		static public TrafficController getTrafficController(String storeHandle) {
			TrafficController output = controllers.get(storeHandle);
			if(output == null) output = new TrafficController(storeHandle);
			return output;
		}
	}

	// As per : http://api.shopify.com/authentication.html
	public static Boolean isGenuinelyFromShopify(String appSharedSecret, Map<String, String> parameters) {
		Boolean output = false;

		String signature = parameters.remove(SHOPIFY_CALCULATED_SIGNATURE_PARAMETER_NAME);
		
		String preDigest = String.valueOf(appSharedSecret);

		if(signature != null && parameters != null) {
			List<String> sortedKeyList = new ArrayList<String>(parameters.keySet());
			Collections.sort(sortedKeyList);
			for(String key : sortedKeyList) {
				preDigest += key;
				preDigest += "=";
				preDigest += String.valueOf(parameters.get(key));
			}
			if(signature.equals(toHexMD5Digest(preDigest))) output = true; 
		}
		
		return output;
	}

	static String toHexMD5Digest(String string) {
		String output = null;
		if(string != null) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
			    output = Hex.encodeHexString(md5.digest(string.getBytes()));
			} catch (NoSuchAlgorithmException e) {
				// this won't happen, we know Java has MD5!
				e.printStackTrace();
			}
		}
		return output;
	}

	public static String getAPIPasswordForShop(String appSharedSecret, Map<String, String> parameters) {
		return toHexMD5Digest(String.valueOf(appSharedSecret) + String.valueOf(parameters.get(SHOPIFY_TOKEN_PARAMETER_NAME)));
	}

	public static String getShopName(Map<String, String> parameters) {
		return parameters.get(SHOPIFY_SHOP_PARAMETER_NAME);
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
						metafieldPostResponse.getStatusLine().toString() + " " + getContentStringFromResponse(metafieldPostResponse) + "\n\n\n\nXML:\n" + metafieldEntityString);
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}		

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpClient.getConnectionManager().shutdown();  
	}
	public static String getMetafieldStringValue(String APIKey, String password, URI URI, String namespace, String key) throws URISyntaxException, ClientProtocolException, IOException, JAXBException  {
		String output = null;

		// Set query parameters
		URI URIWithQuery = new URI(URI.toString() + "?" +
				SHOPIFY_API_METAFIELD_NAMESPACE_QUERY_PARAMETER_NAME + "=" + namespace + "&" + 
				SHOPIFY_API_METAFIELD_KEY_QUERY_PARAMETER_NAME + "=" + key + "&" + 
				SHOPIFY_API_METAFIELD_VALUE_TYPE_QUERY_PARAMETER_NAME + "=" + Metafield.SHOPIFY_API_METAFIELD_TYPE_STRING_VALUE);
		
		List<Metafield> metafields = getMetafields(APIKey, password, URIWithQuery);
		
		if(metafields != null && metafields.size() > 0) output = metafields.get(0).getValue();

        return output;
	}
	public static List<Metafield> getMetafields(String APIKey, String password, URI URI) throws ClientProtocolException, IOException, JAXBException {
        System.out.println("Getting list of metafields from Shopify at: " + String.valueOf(URI));
		List<Metafield> output = null;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(URI.getHost(), SHOPIFY_API_PORT_NUMBER), 
				new UsernamePasswordCredentials(APIKey, password));

		
		// Make sure we dont exceed API call allowance
        trafficControl(URI.getHost().substring(0, URI.getHost().indexOf('.')));
        
        // Execute API call
        HttpGet httpGet = new HttpGet(URI);
		HttpResponse productListResponse = httpClient.execute(httpGet);

		// Look at API response
		if(productListResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			
			// Unarshall from response XML to object model
			JAXBContext jaxbContext = JAXBContext.newInstance(MetafieldListAPIWrapper.class);
			Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
			String responseString = getContentStringFromResponse(productListResponse);
			JAXBElement<MetafieldListAPIWrapper> root = unmarshaller.unmarshal(new StreamSource(new StringReader(responseString)), MetafieldListAPIWrapper.class);

			output = root.getValue().getMetafields();

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
}
