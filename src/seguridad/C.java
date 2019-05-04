package seguridad;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class C {
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		

		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");

		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		D.initCertificate(certSer, keyPairServidor);
		//Aqui se determina el numero de threads que se van a tener el en pool
		ExecutorService executor = Executors.newFixedThreadPool(1);

		// Blank workbook 
		HSSFWorkbook workbook = new HSSFWorkbook();
		//Para cada configuracion de los threads (un pool de 2, de 1, etc.) se tiene un numero distinto de pruebas e.g:3
		//1) intentar con 800 peticiones
		//2) intentar con 300 peticiones
		//3) intentar con 100 peticiones 

		Map<String, Object[]> data = new TreeMap<String, Object[]>(); 
		data.put("1", new Object[]{ "TIEMPO", "CPU"}); 
		System.out.println("Ingrese el nombre del archivo donde quiere guardar la informacion");
		String nomArchivo = br.readLine();
		System.out.println("Ingrese el numero de peticiones que va a realizar para la prueba");
		int peticiones = Integer.parseInt(br.readLine());
		int kkey = 2;
		//Realice 800 peticiones
		while (peticiones > 0){
			try { 
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				//Start time
				long startTime =System.nanoTime();
				
				executor.submit(new D(sc, idThread));
				//executor.execute(new D(sc, idThread));
				
				//End time
				double endTime = System.nanoTime();
				double timeElapsed = endTime - startTime;
				String time = ""+timeElapsed;
				
				//CPU
				Utility u = new Utility();
				double cpu = u.getSystemCpuLoad();
				
				idThread++;
				String f = ""+kkey;
				System.out.println("CPU " + cpu + "peticiones " + peticiones);
				data.put(f, new Object[]{timeElapsed, cpu}); 
				kkey++;
				peticiones--;
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
		ExcelFile.generate(workbook, nomArchivo, data);
		System.out.println("Se genero el archivo");
		try { 
			// this Writes the workbook gfgcontribute 
			FileOutputStream out = new FileOutputStream(new File(nomArchivo+".xls")); 
			workbook.write(out); 
			out.close(); 
			System.out.println("doc1.xls written successfully on disk."); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	}
}
