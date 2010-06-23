package org.jhopify.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jhopify.Product;
import org.jhopify.ProductOption;
import org.jhopify.ProductVariant;


// TODO FR, EN
// TODO NavX Tags 
// TODO Use enum for constants headers
public class WinRetailParser {
	static final String PRODUCT_IMAGE_FORMAT = "jpg";
	static final String PRODUCT_IMAGE_WEB_PREFIX = "http://static.petiteboite.ca/images/2010-06-22/";

	static final String WINRETAIL_VENDOR_HEADER = "Vendor";
	static final String WINRETAIL_STYLE_HEADER = "Style";
	static final String WINRETAIL_SIZE_HEADER = "Size";
	static final String WINRETAIL_QUANTITY_ON_HAND_HEADER = "Qty Onhand";
	static final String WINRETAIL_RETAIL_PRICE_HEADER = "Retail Price";
	static final String WINRETAIL_DISCOUNTED_PRICE_HEADER = "Discounted price";
	static final String WINRETAIL_UPC_HEADER = "UPC";
	static final String WINRETAIL_ALTERNATE_UPCs_HEADER = "Alternate UPC(s)";
	static final String WINRETAIL_BIN_LOCATION_HEADER = "Bin Location Name";
	static final String WINRETAIL_SEASON_HEADER = "Season";
	static final String WINRETAIL_COLOR_NAME_HEADER = "Color Long Name";
	static final String WINRETAIL_COLOR_RGB_HEX_HEADER = "Color_RGB";
	static final String WINRETAIL_DEPT_HEADER = "Dept";
	static final String WINRETAIL_CLASS_HEADER = "Class";
	static final String WINRETAIL_SUBCLASS_HEADER = "Subclass";
	static final String WINRETAIL_CAPTION_HEADER = "Caption";
	static final String WINRETAIL_ALT_CAPTION_HEADER = "Alt Caption";
	static final String WINRETAIL_HEADLINE_HEADER = "Headline";
	static final String WINRETAIL_ALT_HEADLINE_HEADER = "Alt Headline";
	static final String WINRETAIL_DESCRIPTION_HEADER = "Description";
	static final String WINRETAIL_TICKET_DESCRIPTION_HEADER = "Ticket Description";

	static final ProductOption colorOption = new ProductOption();
	static final ProductOption sizeOption = new ProductOption();
	static {
		colorOption.setName("Color");
		sizeOption.setName("Size");
	}

	static Collection<String> mandatoryColumns = new Vector<String>();
	static {
		mandatoryColumns.add(WINRETAIL_VENDOR_HEADER);
		mandatoryColumns.add(WINRETAIL_STYLE_HEADER);
		mandatoryColumns.add(WINRETAIL_SIZE_HEADER);
		mandatoryColumns.add(WINRETAIL_QUANTITY_ON_HAND_HEADER);
		mandatoryColumns.add(WINRETAIL_RETAIL_PRICE_HEADER);
		mandatoryColumns.add(WINRETAIL_DISCOUNTED_PRICE_HEADER);
		mandatoryColumns.add(WINRETAIL_UPC_HEADER);
		mandatoryColumns.add(WINRETAIL_ALTERNATE_UPCs_HEADER);
		mandatoryColumns.add(WINRETAIL_BIN_LOCATION_HEADER);
		mandatoryColumns.add(WINRETAIL_SEASON_HEADER);
		mandatoryColumns.add(WINRETAIL_COLOR_NAME_HEADER);
		mandatoryColumns.add(WINRETAIL_COLOR_RGB_HEX_HEADER);
		mandatoryColumns.add(WINRETAIL_DEPT_HEADER);
		mandatoryColumns.add(WINRETAIL_CLASS_HEADER);
		mandatoryColumns.add(WINRETAIL_SUBCLASS_HEADER);
		mandatoryColumns.add(WINRETAIL_CAPTION_HEADER);
		mandatoryColumns.add(WINRETAIL_ALT_CAPTION_HEADER);
		mandatoryColumns.add(WINRETAIL_HEADLINE_HEADER);
		mandatoryColumns.add(WINRETAIL_ALT_HEADLINE_HEADER);
		mandatoryColumns.add(WINRETAIL_DESCRIPTION_HEADER);
		mandatoryColumns.add(WINRETAIL_TICKET_DESCRIPTION_HEADER);
	}

	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		// Argument check
		if(args.length < 5) throw new IllegalArgumentException("Not enough arguments. All 5 arguments are mandatory : databasePath photoFolderPath shopifyApiKey shopifyPassword shopifyStoreHandle.");
		else System.out.println("All arguments OK…");

		// Checking photo library
		String photoFolderPath = args[1];
		File photoFolder = new File(photoFolderPath);
		if(!photoFolder.isDirectory()) throw new IllegalArgumentException("Halting. The specified photo folder does not exist or is not a directory.");
		else System.out.println("Picture directory found…");
		// Put all files in a case insensitive map because, we need the exact cases when hosted on linux
		List<File> photos = Arrays.asList(photoFolder.listFiles());
		Map<String, File> photoMap = new TreeMap<String, File>();
		for(File photo : photos) {
			photoMap.put(photo.getName().toUpperCase().toLowerCase(), photo);
		}


		// Checking database
		String databasePath = args[0];
		File databaseFile = new File(databasePath);
		if(!databaseFile.isFile()) throw new IllegalArgumentException("Halting. The specified database file does not exist or is not a file.");
		else System.out.println("Database found…");

        // Open the workbook and then create the FormulaEvaluator and
        // DataFormatter instances that will be needed to, respectively,
        // force evaluation of forumlae found in cells and create a
        // formatted String encapsulating the cells contents.
		System.out.println("Opening workbook \"" + databaseFile.getName() + "\"…");
		FileInputStream databaseFileInputStream = new FileInputStream(databaseFile);
        Workbook databaseWorkbook = WorkbookFactory.create(databaseFileInputStream);
        DataFormatter databaseFormatter = new DataFormatter();
        FormulaEvaluator databaseFormulaEvaluator = databaseWorkbook.getCreationHelper().createFormulaEvaluator();
        databaseFileInputStream.close();
  
        // Discover how many sheets there are in the workbook....
        // and then iterate through them.
        int databaseSheetCount = databaseWorkbook.getNumberOfSheets();
  
        System.out.println("Sucessfuly opend database Excel file. Checking database sanity. Database Excel file has " + String.valueOf(databaseSheetCount) + " sheet(s). Iterating through them…");
    	
    	// Prepare product database
    	Map<String, Product> winRetailProductDatabase = new TreeMap<String, Product>();
    	
    	// Stat variables
    	int variantCount = 0;
        for(int i = 0; i < databaseSheetCount; i++) {
            // Get a reference to a sheet and check to see if it contains
            // any rows.
        	Sheet sheet = databaseWorkbook.getSheetAt(i);
            if(sheet.getPhysicalNumberOfRows() > 0) {
  
            	// Note down the index number of the bottom-most row and
                // then iterate through all of the rows on the sheet starting
                // from the very first row - number 1 - even if it is missing.
                // Recover a reference to the row and then call another method
                // which will strip the data from the cells and build lines
                // for inclusion in the resylting CSV file.
                int lastRowNum = sheet.getLastRowNum();
            	System.out.println("Sheet #" + String.valueOf(i) + " has " + String.valueOf(lastRowNum + 1) + " row(s). Iterating through them…");

            	// Prepare column index
            	Map<Integer, String> columnIndex = new TreeMap<Integer, String>();
            	
            	for(int j = 0; j <= lastRowNum; j++) {
 
                	// Check to ensure that a row was recovered from the sheet as it is
                    // possible that one or more rows between other populated rows could be
                    // missing - blank. If the row does contain cells then...
                	Row row = sheet.getRow(j);
                    if(row != null) {

                        // Get the index for the right most cell on the row and then
                        // step along the row from left to right recovering the contents
                        // of each cell, converting that into a formatted String.
                    	int lastCellNum = row.getLastCellNum();
                    	if(j == 0) {
                        	System.out.println("Header (row #" + String.valueOf(j) + ") in sheet #" + 
                        			String.valueOf(i) + " has " + 
                        			String.valueOf(lastCellNum + 1) + " cell(s). Iterating through them and making sure all the right headers are there…");


	                    	for(int k = 0; k <= lastCellNum; k++) {
	                        	Cell cell = row.getCell(k);
	                        	if(cell != null) {
		                        	String cellValue = null;
		                            if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
		                            	cellValue = databaseFormatter.formatCellValue(cell, databaseFormulaEvaluator);
		                            } else {
		                            	cellValue = databaseFormatter.formatCellValue(cell);
		                            }
		                            
		                            // Check if header is in column list
		                            if(cellValue != null) {
		                            	cellValue = cellValue.trim();
		                            	if(mandatoryColumns.contains(cellValue)) {
			                            	columnIndex.put(k, cellValue);
		                            	}
		                            }
	                        	}
	                        }

	                    	if(columnIndex.size() == mandatoryColumns.size()) System.out.println("All required columns in database found and indexed…");
	                    	else {
	                    		StringBuffer sb = new StringBuffer();
	                    		sb.append("Halting. Couldn't find all required columns in database. Missing:");
	                    		for(String column : mandatoryColumns) {
	                    			if(!columnIndex.containsValue(column)) {
	                    				sb.append(' ');
	                    				sb.append(column);
	                    			}
	                    			sb.append('.');
	                    		}
	                    		throw new RuntimeException(sb.toString());
	                    	}
                    	} else {
                    		
                    		// Put all values in Map for now
                    		Map<String, String> winRetailDatabaseRow = new TreeMap<String, String>();
	                    	for(int k = 0; k <= lastCellNum; k++) {
	                    		String columnName = columnIndex.get(k);
	                    		if(columnName != null) {
		                        	Cell cell = row.getCell(k);
		                        	if(cell != null) {
			                        	String cellValue = null;
			                            if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			                            	cellValue = databaseFormatter.formatCellValue(cell, databaseFormulaEvaluator);
			                            } else {
			                            	cellValue = databaseFormatter.formatCellValue(cell);
			                            }

			                            // Check if column is known as required value
			                            if(cellValue != null) {
			                            	cellValue = cellValue.trim();
			                            	winRetailDatabaseRow.put(columnName, cellValue);
			                            }
		                        	}
	                    		}
	                        }

	                    	// Parse Product/Variant from Map
	                    	String style = winRetailDatabaseRow.get(WINRETAIL_STYLE_HEADER);
	                    	if(style != null) {
		                    	Product product = winRetailProductDatabase.get(style);
		                    	if(product == null) {
		                    		// First variant for product, create and parse product
		                    		product = new Product();
		                    		product.setHandle(style);

		                    		// Set product attributes
		                    		product.setBodyHtml(winRetailDatabaseRow.get(WINRETAIL_HEADLINE_HEADER));
		                    		product.setProductType(winRetailDatabaseRow.get(WINRETAIL_SUBCLASS_HEADER));
		                    		product.setTitle(winRetailDatabaseRow.get(WINRETAIL_DESCRIPTION_HEADER));
		                    		product.setVendor(winRetailDatabaseRow.get(WINRETAIL_VENDOR_HEADER));

		                    		// Set option names for product
		                    		product.getOptions().add(colorOption);
		                    		product.getOptions().add(sizeOption);

		                    		// Check for (mandatory) picture
		                    		String variantImageFilePath = getVariantImageFileNameFromDatabaseRow(winRetailDatabaseRow);
		                    		File originalProductImageFile = photoMap.get(variantImageFilePath.toUpperCase().toLowerCase());
		                    		if(originalProductImageFile != null && originalProductImageFile.isFile()) {

		                    			// Check if image is sane
		                    			BufferedImage productOriginalImage;
		                    			try {
			                    			productOriginalImage = ImageIO.read(originalProductImageFile);
		                    			} catch(Exception e) {
			                    			// There must be a picture for every variant AND product
			                    			// Otherwise : on to the next one
			                    			System.out.println("Invalid product image : " + originalProductImageFile.getName());
			                    			continue;
		                    			}
		                    			
		                    			// Prepare BW image
		                    			BufferedImage productImage = new BufferedImage(productOriginalImage.getWidth(), productOriginalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);  
		                    			Graphics g = productImage.getGraphics();  
		                    			g.drawImage(productOriginalImage, 0, 0, null);  
		                    			g.dispose();
		                    			
		                    			// Write image
		                    			File productImageFile = new File(getProductImageFilePathFromDatabaseRow(winRetailDatabaseRow, photoFolder));
		                    			ImageIO.write(productImage, PRODUCT_IMAGE_FORMAT, productImageFile);

		                    			// Add image name to product image list
		                    			product.getImageNames().add(productImageFile.getName());
	
		                    			// register product
		                    			winRetailProductDatabase.put(style, product);
		                    		} else {
		                    			// There must be a picture for every variant AND product
		                    			// Otherwise : on to the next one
		                    			System.out.println("Expected product picture not found : " + variantImageFilePath);
		                    			continue;
		                    		}

		                    	}

		                    	// Create product variant object
		                    	ProductVariant variant = new ProductVariant();
		                    	variant.setCompareAtPrice(Float.parseFloat(winRetailDatabaseRow.get(WINRETAIL_RETAIL_PRICE_HEADER)));
		                    	variant.setInventoryManagement(ProductVariant.SHOPIFY_API_INVENTORY_TRACKED_BY_SHOPIFY_VALUE);
		                    	variant.setInventoryPolicy(ProductVariant.SHOPIFY_API_INVENTORY_POLICY_DENY_VALUE);
		                    	variant.setInventoryQuantity(Integer.parseInt(winRetailDatabaseRow.get(WINRETAIL_QUANTITY_ON_HAND_HEADER)));
		                    	variant.setOption1(winRetailDatabaseRow.get(WINRETAIL_COLOR_NAME_HEADER));
		                    	variant.setOption2(winRetailDatabaseRow.get(WINRETAIL_SIZE_HEADER));
		                    	variant.setPrice(Float.parseFloat(winRetailDatabaseRow.get(WINRETAIL_DISCOUNTED_PRICE_HEADER)));
		                    	variant.setSku(winRetailDatabaseRow.get(WINRETAIL_UPC_HEADER));

		                    	// Check for mandatory variant picture
	                    		String variantImageFileName = getVariantImageFileNameFromDatabaseRow(winRetailDatabaseRow);
	                    		File originalVariantImageFile = photoMap.get(variantImageFileName.toUpperCase().toLowerCase());
	                    		if(originalVariantImageFile != null && product.getImageNames().contains(originalVariantImageFile.getName())) {
	                    			// Product already has picture for this color (there is no picture per color/size, only per color)
	                    			// Register variant
			                    	product.getVariants().add(variant);
	                    			
	                    		} else {
		                    		if(originalVariantImageFile != null && originalVariantImageFile.isFile()) {
		                    			// Product has no picture registered for this color
		                    			// Check if variant image is sane
		                    			try {
		                    				ImageIO.read(originalVariantImageFile);
		                    			} catch(Exception e) {
			                    			// There must be a picture for every variant AND product
			                    			// Otherwise : on to the next one
			                    			System.out.println("Invalid variant image : " + originalVariantImageFile.getName());
			                    			continue;
		                    			}

		                    			// Add image name to product image list
		                    			product.getImageNames().add(originalVariantImageFile.getName());

		                    			// register variant
				                    	product.getVariants().add(variant);
		                    		} else {
		                    			// There must be a picture for every variant AND product
		                    			// Otherwise : on to the next one
		                    			System.out.println("Expected product variant not found : " + variantImageFileName);
		                    			continue;
		                    		}
	                    			
	                    		}
		                    	variantCount++;
	                    	}
                    	}
                    }
                }
            }
        }
        System.out.println("Successfuly parsed WinRetail database. Found " + winRetailProductDatabase.size() + " products in database, for an average of " + 
        		String.valueOf(variantCount / winRetailProductDatabase.size()) + " variation(s) per product…");

        System.out.println("Outputting CSV File…");
        CSVWriter.writeCSV(databaseFile.getParentFile(), databaseFile.getName().replace("xls", "csv"), PRODUCT_IMAGE_WEB_PREFIX, winRetailProductDatabase.values());

//		// Testing Shopify connection
//		String shopifyApiKey = args[2];
//		String shopifyPassword = args[3];
//		String shopifyStoreHandle = args[4];
	}

	public static String getProductImageFilePathFromDatabaseRow(Map<String, String> winRetailDatabaseRow, File photoFolder) {
		StringBuffer sb = new StringBuffer();
		return sb.append(photoFolder.getAbsolutePath()).append(File.separator).append(winRetailDatabaseRow.get(WINRETAIL_STYLE_HEADER)).append(".jpg").toString();
	}
	public static String getVariantImageFileNameFromDatabaseRow(Map<String, String> winRetailDatabaseRow) {
		StringBuffer sb = new StringBuffer();
		return sb.append(winRetailDatabaseRow.get(WINRETAIL_STYLE_HEADER)).append("~").append(winRetailDatabaseRow.get(WINRETAIL_COLOR_NAME_HEADER)).append(".jpg").toString();
	}
}
