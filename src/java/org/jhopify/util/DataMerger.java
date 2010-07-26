package org.jhopify.util;

import java.util.Collection;
import java.util.Map;

import org.jhopify.Product;
import org.jhopify.ProductVariant;



public class DataMerger {
	static public void mergeFromAPIIntoDatabase(boolean productIds, boolean productImages,
			boolean variantIds, boolean variantPositions, boolean applyVariantPositionComparator,
			Collection<? extends Product> productsFromAPI, Map<String, ? extends Product> productsFromDatabase) {

		System.out.println("Starting merging API data in to database data…");
		for(Product productFromAPI : productsFromAPI) {
			// Look for product in local database
			Product productFromDatabase = productsFromDatabase.get(productFromAPI.getHandle());
			if(productFromDatabase == null) {
				throw new RuntimeException("Couldn't find in database product with handle \"" + productFromAPI.getHandle() + "\"");
			} else {
				if(productIds) productFromDatabase.setId(productFromAPI.getId());
				if(productImages)  {
					if(productFromAPI.getImages().size() == 0 || productFromAPI.getImages().size() < productFromDatabase.getImages().size()) {
						System.out.println("Warning. For product  \"" + productFromAPI.getHandle() 
								+ "\", we found only " + productFromAPI.getImages().size() + " image(s) from the API, but there were "
								+ productFromDatabase.getImages().size() + " image(s) registered in the database. "
								+ "Most probably, we are loosing some of them here.");
					}
					productFromDatabase.getImages().clear();
					productFromDatabase.getImages().addAll(productFromAPI.getImages());
				}
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
					if(matchingDatabaseVariant == null) throw new RuntimeException("Couldn't find in database variant with SKU \"" + variantFromAPI.getSku() + "\"");
					else {
						if(variantIds) matchingDatabaseVariant.setId(variantFromAPI.getId());
						if(variantPositions) matchingDatabaseVariant.setPosition(variantFromAPI.getPosition());
					}
				}
			}
		}
		System.out.println("Successfully merged API data in to database data…");
	}
}