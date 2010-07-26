package org.jhopify.util;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import org.jhopify.Product;
import org.jhopify.ProductVariant;

import com.csvreader.CsvWriter;

public class CSVWriter {
	 public static void writeCSV(File outputDirectory, String fileName, String webImageRepositoryPrefix, Collection<? extends Product> products) throws IOException {
		 CsvWriter writer = new CsvWriter(outputDirectory.getAbsolutePath() + File.separator + fileName, ',', Charset.forName("UTF-8"));
		 
		 // Write headers
		 writeHeaders(writer);

		 // Write products
		 for(Product product : products) {
			 writeProduct(writer, product, webImageRepositoryPrefix);
		 }

		 writer.flush();
	 }

	 public static void writeHeaders(CsvWriter writer) throws IOException {
		 // Write headers
		 writer.write("Handle");
		 writer.write("Title");
		 writer.write("Body (HTML)");
		 writer.write("Vendor");
		 writer.write("Type");
		 writer.write("Tags");
		 writer.write("Option1 Name");
		 writer.write("Option1 Value");
		 writer.write("Option2 Name");
		 writer.write("Option2 Value");
		 writer.write("Option3 Name");
		 writer.write("Option3 Value");
		 writer.write("Variant SKU");
		 writer.write("Variant Grams");
		 writer.write("Variant Inventory Tracker");
		 writer.write("Variant Inventory Qty");
		 writer.write("Variant Inventory Policy");
		 writer.write("Variant Fulfillment Service");
		 writer.write("Variant Price");
		 writer.write("Variant Compare At Price");
		 writer.write("Variant Requires Shipping");
		 writer.write("Variant Taxable");
		 writer.write("Image Src");
		 writer.endRecord();
	 }

	 public static void writeProduct(CsvWriter writer, Product product, String webImageRepositoryPrefix) throws IOException {
		 // Write product fields
		 writeProductFields(writer, product);

		 // Write first variant's fields
		 Iterator<ProductVariant> variantIterator = product.getVariants().iterator();
		 writeVariantFields(writer, product, variantIterator.next());
		 
		 // Write first image field
		 Iterator<String> imageNameIterator = product.getImageNames().iterator();
		 if(imageNameIterator.hasNext()) writeImageField(writer, webImageRepositoryPrefix, imageNameIterator.next());
		 else writer.write("");
		 
		 // End "product" record
		 writer.endRecord();

		 // Iterate for additional variants
		 while(variantIterator.hasNext()) {
			 // Write product padding fields
			 writeProductPaddingFields(writer, product);

			 // Write variant fields
			 writeVariantFields(writer, product, variantIterator.next());
			 
			 // Write image field (if available)
			 if(imageNameIterator.hasNext()) writeImageField(writer, webImageRepositoryPrefix, imageNameIterator.next());
			 else writer.write("");
			 
			 // End "variant" record
			 writer.endRecord();
		 }

		 // Iterate for additional images
		 while(imageNameIterator.hasNext()) {
			 // Write variant fields
			 writeVariantPaddingFields(writer, product);
			 
			 // Write image field (if available)
			 writeImageField(writer, webImageRepositoryPrefix, imageNameIterator.next());
			 
			 // End "image" record
			 writer.endRecord();
		 }
	 }

	 public static void writeProductFields(CsvWriter writer, Product product) throws IOException {
		 // Write headers
		 writer.write(product.getHandle());
		 writer.write(product.getTitle());
		 writer.write(product.getBodyHtml());
		 writer.write(product.getVendor());
		 writer.write(product.getProductType());
		 writer.write(product.getTags());
	 }

	 public static void writeVariantFields(CsvWriter writer, Product product, ProductVariant variant) throws IOException {
		 // Write variant fields

		 // Option 1
		 writer.write(product.getOptions().get(0).getName());
		 writer.write(variant.getOption1());

		 // Option 2
		 if(product.getOptions().size() > 1) {
			 writer.write(product.getOptions().get(1).getName());
			 writer.write(variant.getOption2());
		 } else {
			 writer.write("");
			 writer.write("");
		 }

		 // Option 3
		 if(product.getOptions().size() > 2) {
			 writer.write(product.getOptions().get(2).getName());
			 writer.write(variant.getOption3());
		 } else {
			 writer.write("");
			 writer.write("");
		 }
		 
		 // SKU
		 writer.write(variant.getSku());
		 
		 // Weight
		 if(variant.getGrams() != null) writer.write(variant.getGrams().toString());
		 else writer.write("");

		 // Trailing fields
		 writer.write(variant.getInventoryManagement());
	
		 if(variant.getInventoryQuantity() != null) writer.write(String.valueOf(variant.getInventoryQuantity()));
		 else writer.write("0");

		 writer.write(variant.getInventoryPolicy());
		 writer.write(variant.getFullfilmentService());
		 writer.write(String.valueOf(variant.getPrice()));
		 writer.write(String.valueOf(variant.getCompareAtPrice()));
		 writer.write(String.valueOf(variant.getRequiresShipping()));
		 writer.write(String.valueOf(variant.getTaxable()));
	 }

	 public static void writeImageField(CsvWriter writer, String webImageRepositoryPrefix, String imageName) throws IOException {
		 // Write headers
		 writer.write(webImageRepositoryPrefix + URLEncoder.encode(imageName, "UTF-8").replace("+", "%20"));
	 }

	 public static void writeProductPaddingFields(CsvWriter writer, Product product) throws IOException {
		 // Write product padding fields
		 writer.write(product.getHandle());
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
	 }

	 public static void writeVariantPaddingFields(CsvWriter writer, Product product) throws IOException {
		 // Write product & variant padding fields
		 writer.write(product.getHandle());
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
		 writer.write("");
	 }
}
