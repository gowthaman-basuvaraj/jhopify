package org.jhopify.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class API {
	public static final String SHOPIFY_API_XML_EXTENSION_SUFFIX = ".xml";
	public static final String SHOPIFY_API_SCHEME = "http://";
	public static final String SHOPIFY_API_DOMAIN = "myshopify.com";
	public static final int SHOPIFY_API_PORT_NUMBER = 80;
	public static final int QUERY_PER_PERIOD_PER_STORE_ALLOWANCE = 120;
	public static final int QUERY_PERIODIC_RESET_IN_MINUTES = 1;

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
		TrafficController.geTrafficController(storeHandle).trafficControl();
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
			this.apiCallsSinceLastReset = 0;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, QUERY_PERIODIC_RESET_IN_MINUTES);
			this.nextResetTime = calendar.getTimeInMillis();
		}
		static public TrafficController geTrafficController(String storeHandle) {
			TrafficController output = controllers.get(storeHandle);
			if(output == null) output = new TrafficController(storeHandle);
			return output;
		}
	}
}
