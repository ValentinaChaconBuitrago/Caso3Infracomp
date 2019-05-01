package srv;

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
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
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
			data.put("1", new Object[]{ "NAME", "LASTNAME" }); 
			System.out.println("Ingrese el numero de peticiones que va a realizar para la prueba # " + counter);
			int peticiones = Integer.parseInt(br.readLine());
			int kkey = 2;
			
			//Realice 800 peticiones
			while (peticiones > 0) {
				try { 
					Socket sc = ss.accept();
					System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
					executor.submit(new D(sc, idThread));
					idThread++;
					String f = ""+kkey;
					//TODO aqui se crea un objeto con los resultados de las medidas de respuesta tomadas en la peticion
					data.put(f, new Object[]{ "CHACON", "Kumar" }); 
					kkey++;
					peticiones--;
				} catch (IOException e) {
					System.out.println(MAESTRO + "Error creando el socket cliente.");
					e.printStackTrace();
				}
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
}
