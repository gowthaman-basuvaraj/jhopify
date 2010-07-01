package org.jhopify.util;

import java.util.Collection;
import java.util.Map;

import org.jhopify.Product;
import org.jhopify.ProductVariant;

public class DataMerger {
	static public void mergeFromIntoDatabase(boolean productIds, boolean variantIds, boolean variantPositions, boolean productImages, Collection<Product> productsFromAPI, Map<String, Product> productsFromDatabase) {

		for(Product productFromAPI : productsFromAPI) {
			// Look for product in local database
			Product productFromDatabase = productsFromDatabase.get(productFromAPI.getHandle());
			if(productFromDatabase == null) {
				System.out.println("Couldn't find in database product with handle \"" + productFromAPI.getHandle() + "\"");
			} else {
				// Iterate over variants from this product (from API)
				for(ProductVariant variantFromAPI : productFromAPI.getVariants()) {
					// Look for variant from database…
					ProductVariant matchingDatabaseVariant = null;
					for(ProductVariant databaseVariant : productFromDatabase.getVariants()) {
						// Check if variants from database and API match
						if(databaseVariant.getSku() != null && databaseVariant.getSku().equals(variantFromAPI.getSku())) {
							matchingDatabaseVariant = databaseVariant;
						}
					}
					if(matchingDatabaseVariant == null) System.out.println("Couldn't find in database variant with SKU \"" + variantFromAPI.getSku() + "\"");
				}
			}
		}
	}
}