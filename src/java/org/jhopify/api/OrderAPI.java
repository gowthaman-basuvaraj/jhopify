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
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jhopify.Order;
import org.jhopify.api.wrappers.OrderListAPIWrapper;

public class OrderAPI extends API {
	public static List<Order> getAllOrders(
			String key,
			String password,
			String shopHandle) throws URISyntaxException, ClientProtocolException, IOException, JAXBException {
		return findOrdersWithQuery(key, password, shopHandle, "created_at_min=2010-09-13&financial_status=paid&status=any");
	}
	public static List<Order> findOrdersWithQuery(
			String key,
			String password,
			String shopHandle,
			String query) throws URISyntaxException, ClientProtocolException, IOException, JAXBException {
		List<Order> output = new ArrayList<Order>();

		// No ID specified, retrieve all open orders
		if(query == null) query = "";
		else query = "&" + query;
		URI uri = new URI(SHOPIFY_API_SCHEME + shopHandle + SHOPIFY_API_DOMAIN_SUFFIX +  SHOPIFY_API_URI_PREFIX + 
				SHOPIFY_API_ORDERS_SUFFIX + SHOPIFY_API_XML_EXTENSION_SUFFIX + SHOPIFY_API_OBJECT_LIST_LIMIT_PARAMETER_MAXIMUM_QUERY + 
				query);

		System.out.println(uri);
		
		// Prepare HTTP client
		HttpClient httpClient = getAuthenticatedHttpClient(key, password, uri.getHost());
        HttpGet httpGet = new HttpGet(uri);
        

		// Make sure we dont exceed API call allowance
        trafficControl(shopHandle);
        
        // Execute API call
		HttpResponse response = httpClient.execute(httpGet);
		
		// Look at API response
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			// Unarshall from response XML to object model
			JAXBContext jaxbContext = JAXBContext.newInstance(OrderListAPIWrapper.class);
			Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
			String responseString = getContentStringFromResponse(response);
			JAXBElement<OrderListAPIWrapper> root = unmarshaller.unmarshal(new StreamSource(new StringReader(responseString)), OrderListAPIWrapper.class);

			if(root.getValue() != null) output.addAll(root.getValue().getOrders());
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
	public static List<Order> findOrdersById(
			String key,
			String password,
			String shopHandle,
			List<String> idList) throws URISyntaxException, ClientProtocolException, IOException, JAXBException {

		List<Order> output = new ArrayList<Order>();
		
		if(idList == null || idList.size() < 0) {
			output.addAll(getAllOrders(key, password, shopHandle));
		} else  {
			// Iterate through list of IDs and get order to put in output list
			for(String id : idList) {
				Order order = findOrderById(key, password, shopHandle, id);
				if(order != null) output.add(order);
			}
		}
		return output;
	}
	public static Order findOrderById(
			String key,
			String password,
			String shopHandle,
			String id) throws URISyntaxException, ClientProtocolException, IOException, JAXBException {
		Order output = null;
		
		// No ID specified, retrieve all open orders
		URI uri = new URI(SHOPIFY_API_SCHEME + shopHandle + SHOPIFY_API_DOMAIN_SUFFIX +  
				SHOPIFY_API_URI_PREFIX +  SHOPIFY_API_ORDERS_SUFFIX + "/" + 
				String.valueOf(id) + SHOPIFY_API_XML_EXTENSION_SUFFIX);
		
		// Prepare HTTP client
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(uri.getHost(), SHOPIFY_API_PORT_NUMBER), 
				new UsernamePasswordCredentials(key, password));
        HttpGet httpGet = new HttpGet(uri);
        

		// Make sure we dont exceed API call allowance
        trafficControl(shopHandle);
        
        // Execute API call
		HttpResponse response = httpClient.execute(httpGet);
		
		// Look at API response
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// Unarshall from response XML sto object model
			String responseString = getContentStringFromResponse(response);
			output = parseOrderXML(responseString);
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
	
	public static Order parseOrderXML(String xml) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Order.class);
		Unmarshaller unmarshaller =jaxbContext.createUnmarshaller();
		return unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), Order.class).getValue();
	}
}