import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.log4j.BasicConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;



public class Main {
   


    /**
	 * Read Excel File into Java List Objects
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
    
    public static void main(String[] args) {
        BasicConfigurator.configure();
        // System.out.println("hello world");
		// List lowStock = readExcelFile("server/resources/Inventory.xlsx");
		JsonObject jsonObject = readExcelFile("server/resources/Inventory.xlsx");
		System.out.println(jsonObject);
		
		// for(int i =0; i < lowStock.size();i++){
		// 	System.out.println(lowStock.get(i));
		// // }
		// String jsonString = convertObjects2JsonString(lowStock);
	
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		// System.out.println(jsonArray.get(5).getAsJsonObject().get("Stock"));
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
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
        
        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            JsonObject jObject = readExcelFile("server/resources/Inventory.xlsx");
            return jObject;
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            JsonObject jObject = readExcelFile("server/resources/Distributors.xlsx");
            return 0;
        });

    }
}
