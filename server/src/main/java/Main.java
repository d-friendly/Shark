import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.log4j.BasicConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;




public class Main {
   


    /**
	 * Read Excel File into JsonObject of JsonArrays
	 * 
	 * @param filePath
	 * @return
	 */
	private static JsonObject readExcelFile(String filePath){
		
		File excelFile = new File(filePath);
		JsonObject sheetsJsonObject = new JsonObject();
		Workbook workbook = null;

		try {
			workbook = new XSSFWorkbook(excelFile);
		} catch(InvalidFormatException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

			JsonArray sheetArray = new JsonArray();
			ArrayList<String> columnNames = new ArrayList<String>();
			Sheet sheet = workbook.getSheetAt(i);
			Iterator<Row> sheetIterator = sheet.iterator();

			while (sheetIterator.hasNext()) {

				Row currentRow = sheetIterator.next();
				JsonObject jsonObject = new JsonObject();

				if (currentRow.getRowNum() != 0) {

					for (int j = 0; j < columnNames.size(); j++) {

						if (currentRow.getCell(j) != null) {
							if (currentRow.getCell(j).getCellType() == CellType.STRING) {
								jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getStringCellValue());
							} else if (currentRow.getCell(j).getCellType() == CellType.NUMERIC) {
								jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getNumericCellValue());
							} else if (currentRow.getCell(j).getCellType() == CellType.BOOLEAN) {
								jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getBooleanCellValue());
							} else if (currentRow.getCell(j).getCellType() == CellType.BLANK) {
								jsonObject.addProperty(columnNames.get(j), "");
							}
						} else {
							jsonObject.addProperty(columnNames.get(j), "");
						}

					}

					sheetArray.add(jsonObject);

				} else {
					// store column names
					for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
						columnNames.add(currentRow.getCell(k).getStringCellValue());
					}
				}

			}
			sheetsJsonObject.add(workbook.getSheetName(i), sheetArray);
	
		}
		// System.out.println(sheetsJsonObject.get("Randy's Candies").getAsJsonArray().get(0));
		return sheetsJsonObject;
	}
	/**
	 * takes a JsonArray and finds the cheapest cost based on id.
	 * @param sheet JsonArray 
	 * @param id to look for 
	 * @param lowestPrice current lowest price
	 * @param costHeader toggle for Dentists Hate us Sheet
	 * @return
	 */
    private static double idAndPriceCheck(JsonArray sheet, String id, double lowestPrice, String costHeader){
		for (int i = 0; i < sheet.size(); i ++) {
			String candyCorpId = sheet.get(i).getAsJsonObject().get("ID").getAsString();
			String candyCorpCost = sheet.get(i).getAsJsonObject().get(costHeader).getAsString();
			
			// Debug: had some issues here 
			// found docs about .getAsString() to elminiate double quotes from String s.
			// String s = candyCorp.get(i).getAsJsonObject().get("ID").toString();
			// logger.info(s);
			// logger.info(id);
			if ((candyCorpId).equals((id))){
				// logger.info("Entered If Statement" + candyCorp.get(i).getAsJsonObject().get("ID").toString());
				// logger.info("Candy Corp Cost: " + candyCorp.get(i).getAsJsonObject().get("Cost").toString());
				if(Double.parseDouble(candyCorpCost) < lowestPrice) {
					//idLowestPrice = (int)Double.parseDouble(candyCorpId);
					lowestPrice = Double.parseDouble(candyCorpCost);
				}
			}
		}
		return lowestPrice;
	}


	public static double findLowestCost(String body){
		JsonElement jElement = JsonParser.parseString(body);
		JsonArray inputFields = jElement.getAsJsonArray();

		/* debug statements
		logger.info(""+reqArray);
		logger.info(""+reqArray.size());*/

		/**
		 * Seperates Excel file into 3 sheets (represented by JsonArrays)
		 * A better implementation would do this dynamically
		 */
		JsonObject jObject1 = readExcelFile("server/resources/Distributors.xlsx");
		JsonArray candyCorp = jObject1.get("Candy Corp").getAsJsonArray();
		JsonArray sweetSuite = jObject1.get("The Sweet Suite").getAsJsonArray();
		JsonArray dentistsHateUs = jObject1.get("Dentists Hate Us").getAsJsonArray();
		double totalCost = 0;
		for (int j = 0; j < inputFields.size(); j++) {
			String id = (inputFields.get(j).getAsJsonObject().get("id").getAsString()+".0");
			int multipler = Integer.parseInt(inputFields.get(j).getAsJsonObject().get("value").getAsString());
			double lowestPrice = Double.MAX_VALUE;
			//int idLowestPrice = 0; USED for debug logger statements
			
			//LowestPrice updates to itself or to new lower price 
			//logger comments were for debugging
			lowestPrice = idAndPriceCheck(candyCorp,id,lowestPrice,"Cost");
			//logger.info("Candy Corp Cheapest: " + idLowestPrice);
			// logger.info("Cost:" + lowestPrice);
			lowestPrice = idAndPriceCheck(sweetSuite, id, lowestPrice,"Cost");
			//logger.info("Sweet Suite Cheapest: " + idLowestPrice);
			// logger.info("Cost:" + lowestPrice);
			lowestPrice = idAndPriceCheck(dentistsHateUs, id, lowestPrice, "Cost (per unit)");
			// logger.info("DHU cheap: " + idLowestPrice);
			// logger.info("Cost:" + lowestPrice);
			totalCost+=(lowestPrice*multipler);
		}
		return totalCost;
	}

	public static void main(String[] args) {
        BasicConfigurator.configure();
		
		
		/**
		 * Debugger SSetup 
		 * Sets up the logger to create a file with logger output.
		 * Allowed easier debugging, using frontend to test the backend 
		 */
		Logger logger = Logger.getLogger("MyLog");  
		FileHandler fh;  
		try {
			// This block configure the logger with handler and formatter  
			fh = new FileHandler("MyLogFile.log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
			// the following statement is used to log any messages  
		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		
        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                        response.header("Access-Control-Allow-Headers",
                                "content-type");

                        response.header("Access-Control-Allow-Methods",
                                "GET, POST");

								
                    return "OK";
                });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));
        
        //Returns JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            JsonObject jObject = readExcelFile("server/resources/Inventory.xlsx");
			System.out.println(jObject.get("Randy's Candies"));
            return jObject.get("Randy's Candies");
        });

        //Returns JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
			String body = request.body();
            return findLowestCost(body);
        });

    }
}