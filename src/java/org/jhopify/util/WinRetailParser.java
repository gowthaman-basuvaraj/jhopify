package org.jhopify.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class WinRetailParser {
	static Map<String, Integer> columnIndex = new TreeMap<String, Integer>();
	static Collection<String> columns = new Vector<String>();
	static {
		columns.add("Vendor");
		columns.add("Style");
		columns.add("Size");
		columns.add("SizeNo");
		columns.add("Qty Onhand");
		columns.add("Retail Price");
		columns.add("UPC");
		columns.add("Alternate UPC(s)");
		columns.add("Bin Location Name");
		columns.add("Season");
		columns.add("Color Long Name");
		columns.add("Color_RGB");
		columns.add("Nav1");
		columns.add("Nav2");
		columns.add("Nav3");
		columns.add("Dept");
		columns.add("Class");
		columns.add("Subclass");
		columns.add("Caption");
		columns.add("Alt Caption");
		columns.add("Headline");
		columns.add("Alt Headline");
		columns.add("Description");
		columns.add("Ticket Description");
	}
	
	
	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		// Argument check
		if(args.length < 4) throw new IllegalArgumentException("Not enough arguments. All 4 arguments are mandatory : databasePath photoFolderPath shopifyApiKey shopifyPassword.");
		else System.out.println("All arguments OK…");
		
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
  

        System.out.println("Checking database sanity…");
        // Discover how many sheets there are in the workbook....
        // and then iterate through them.
        int databaseSheetCount = databaseWorkbook.getNumberOfSheets();
    	System.out.println("Database Excel file has " + String.valueOf(databaseSheetCount) + " sheet(s). Iterating through them…");
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
                        	System.out.println("Header (row #" + String.valueOf(j) + ") in sheet #" + String.valueOf(i) + " has " + String.valueOf(lastCellNum + 1) + " cell(s). Iterating through them and making sure all the right headers are there…");


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
		                            	if(columns.contains(cellValue)) {
			                            	columnIndex.put(cellValue, k);
		                            	}
		                            }
	                        	}
	                        }

	                    	if(columnIndex.size() == columns.size()) System.out.println("All required columns in database found and indexed…");
	                    	else throw new RuntimeException("Halting. Couldn't find all required columns in database.");

	                    	// No need to iterate over everything.
	                    	break;
                    	}
                    }
                }
            }
        }

		// Checking photo library
		String photoFolderPath = args[1];
		File photoFolder = new File(photoFolderPath);
		if(!photoFolder.isDirectory()) throw new IllegalArgumentException("Halting. The specified photo folder does not exist or is not a directory.");
		else System.out.println("Picture directory found…");

		
		System.out.println("Trying to connect to Shopify…");
		String shopifyApiKey = args[2];
		String shopifyPassword = args[3];
	}
}
