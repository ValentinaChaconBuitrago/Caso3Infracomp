package sinSeguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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

import seguridad.D;
import seguridad.ExcelFile;
import seguridad.Utility;

public class C {
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO SIN SEGURIDAD: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

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
		//Aqui se determina el numero de threads que se van a tener en el pool
		ExecutorService executor = Executors.newFixedThreadPool(1);

		// Blank workbook 
		HSSFWorkbook workbook = new HSSFWorkbook();

		Map<String, Object[]> data = new TreeMap<String, Object[]>(); 
		data.put("1", new Object[]{ "TIEMPO", "CPU"}); 
		System.out.println("Ingrese el nombre del archivo donde quiere guardar la informacion");
		String nomArchivo = br.readLine();
		System.out.println("Ingrese el numero de peticiones que va a realizar para la prueba");
		int peticiones = Integer.parseInt(br.readLine());
		int kkey = 2;


		while (peticiones > 0) {
			try { 
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");

				//Start time
				double startTime = System.nanoTime();
				System.out.println("El TIEMPO INICIAL" + startTime);
				
				executor.submit(new D(sc, idThread));
				
				//End time
				double endTime = System.nanoTime();
				System.out.println("El TIEMPO FINAL" + endTime);
				double timeElapsed =(double)endTime - (double)startTime;
				System.out.println("El TIEMPO ES" + timeElapsed);
				
				//CPU
				Utility u = new Utility();
				double cpu = u.getSystemCpuLoad();
				
				idThread++;
				String f = ""+kkey;
				//TODO aqui se crea un objeto con los resultados de las medidas de respuesta tomadas en la peticion
				data.put(f, new Object[]{ timeElapsed, cpu }); 
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
			System.out.println("Ingrese el nombre del archivo donde desea almacenar los resutados ");
			FileOutputStream out = new FileOutputStream(new File(nomArchivo + ".xls")); 
			workbook.write(out); 
			out.close(); 
			System.out.println("doc1.xls written successfully on disk."); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		}


	}
}
