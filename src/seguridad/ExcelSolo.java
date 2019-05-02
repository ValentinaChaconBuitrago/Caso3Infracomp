package seguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelSolo {
	public static void main(String args[]) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		// Blank workbook 
		HSSFWorkbook workbook = new HSSFWorkbook();
		String name = "Prueba " + 1;
		HSSFSheet sheet = workbook.createSheet(name);
		File archivo = new File("trialFIYYYY.xls");
		
		while(true) {
			FileOutputStream out = new FileOutputStream(archivo); 
			int rownum = 0;
			System.out.println("Ingrese un nombre ");
			String nombre = br.readLine();
			System.out.println("Ingrese un apellido ");
			String apellido = br.readLine();
			Object[] obj = new Object[] {nombre,apellido};
			generate(workbook, obj, sheet, rownum);
			rownum++;
			workbook.write(out);
			out.close(); 
		}
	}


	public static void generate (HSSFWorkbook workbook,Object[] objArr,  HSSFSheet sheet, int rownum) throws FileNotFoundException, IOException 
	{ 
		Row row = sheet.createRow(rownum++); 
		int cellnum = 0; 
		for (Object obj : objArr) { 
			// this line creates a cell in the next column of that row 
			Cell cell = row.createCell(cellnum++); 
			if (obj instanceof String) 
				cell.setCellValue((String)obj); 
			else if (obj instanceof Integer) 
				cell.setCellValue((Integer)obj); 
		} 

	} 

}
