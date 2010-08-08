package org.jhopify.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.apache.http.client.ClientProtocolException;

public class ShopAPI extends API {
		public static String getMetafieldStringValue(
				String APIKey, String password, String shopifyStoreHandle, String namespace, String key)
		throws URISyntaxException, ClientProtocolException, IOException, JAXBException {

			// Create URI
			URI URI = new URI(
					SHOPIFY_API_SCHEME + 
					shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN + 
					SHOPIFY_API_URI_PREFIX + SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX);

			// Get value
			return getMetafieldStringValue(APIKey, password, URI, namespace, key);
		}
}
