package seguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelFile {
	public static void main(String args[]) throws FileNotFoundException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// Blank workbook 
		HSSFWorkbook workbook = new HSSFWorkbook();
		System.out.println("Ingrese el numero de pruebas que va a realizar");
		int takes = Integer.parseInt(br.readLine());
		//Para cada configuracion de los threads (un pool de 2, de 1, etc.) se tiene un numero distinto de pruebas e.g:3
		//1) intentar con 800 peticiones
		//2) intentar con 300 peticiones
		//3) intentar con 100 peticiones
		int counter = 1;
		while(takes >= counter) {
			Map<String, Object[]> data = new TreeMap<String, Object[]>(); 
			data.put("1", new Object[]{"NAME", "LASTNAME" }); 
			System.out.println("Ingrese el numero de peticiones que va a realizar para la prueba # " + counter);
			int peticiones = Integer.parseInt(br.readLine());
			int kkey = 2;

			//Realice 800 peticiones
			while (peticiones > 0) {

				String f = ""+kkey;
				//TODO aqui se crea un objeto con los resultados de las medidas de respuesta tomadas en la peticion
				data.put(f, new Object[]{"CHACON", "VV" }); 
				kkey++;
				peticiones--;
			}
			ExcelFile.generate(workbook, counter, data);
			counter++;;
		}

		try { 
			// this Writes the workbook gfgcontribute 
			System.out.println("Ingrese el nombre del archivo donde desea almacenar los resutados ");
			FileOutputStream out = new FileOutputStream(new File(br.readLine() + ".xls"));  
			workbook.write(out); 
			out.close(); 
			System.out.println("doc1.xls written successfully on disk."); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	}


	public static void generate (HSSFWorkbook workbook, int numTest,Map<String,Object[]> data) throws FileNotFoundException, IOException 
	{ 
		// Create a blank sheet 
		String name = "Prueba " + numTest;
		HSSFSheet sheet = workbook.createSheet(name);

		// Iterate over data and write to sheet 
		Set<String> keyset = data.keySet(); 
		int rownum = 0; 
		for (String key : keyset) { 
			// this creates a new row in the sheet 
			Row row = sheet.createRow(rownum++); 
			Object[] objArr = data.get(key); 
			int cellnum = 0; 
			for (Object obj : objArr) { 
				// this line creates a cell in the next column of that row 
				Cell cell = row.createCell(cellnum++); 
				if (obj instanceof String) 
					cell.setCellValue((String)obj); 
				else if (obj instanceof Integer) 
					cell.setCellValue((Integer)obj); 
				else if(obj instanceof Double)
					cell.setCellValue((Double)obj);
				else if(obj instanceof Long)
					cell.setCellValue((Long)obj);
			} 
		}         	
	}  
} 


