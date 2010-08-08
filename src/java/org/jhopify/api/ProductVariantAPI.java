package org.jhopify.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jhopify.Metafield;
import org.jhopify.Order;
import org.jhopify.ProductVariant;

public class ProductVariantAPI extends API {
	public static final String SHOPIFY_API_VARIANT_URI_SUFFIX = "variants";
	public static void createVariantMetaField(String key, String password, String shopifyStoreHandle, String productId, String variantId, Metafield metafield)
	throws ClientProtocolException, IOException, URISyntaxException  {
		// Create URI
		String path = SHOPIFY_API_PRODUCT_URI_PREFIX + "/" + productId + "/" 
		+ SHOPIFY_API_VARIANT_URI_SUFFIX + "/" + variantId + "/" 
		+ SHOPIFY_API_METAFIELD_LIST_FILE_NAME + SHOPIFY_API_XML_EXTENSION_SUFFIX;
		String shopifyStoreHostName = shopifyStoreHandle + "." + SHOPIFY_API_DOMAIN;
		String shopifyStoreUrl = SHOPIFY_API_SCHEME + shopifyStoreHostName;
		createMetaField(key, password, new URI(shopifyStoreUrl + path), metafield);
	}
}
