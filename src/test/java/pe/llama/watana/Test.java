package pe.llama.watana;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {

	public static String CARPETA001 = "DOC001";
	public static String CARPETA002 = "DOC002";

	public static String RUTAARCHIVO1 = "/home/jairo/Escritorio/testdocument.pdf";
	public static String RUTAARCHIVO2 = "/home/jairo/Escritorio/testdocument.pdf";

	public static String RUTADIRECTORIO = "/tmp";

	public static String ARCHIVOVALIDAR = "/tmp/archivo1.pdf";
	
	public static String ARCHIVOSELLAR = "/tmp/firmado.pdf";

	/*
	 * 
	 */
	public static void test1(WatanaApiClient client) throws WatanaApiException {
		var resp = client.consultarCarpeta(CARPETA001);
		var success = resp.getBoolValue("success").orElse(false);
		if (success) {
			var mensaje = resp.getStrValue("mensaje").orElse("");
			var solicitudNro = resp.getStrValue("solicitud_numero").orElse("");
			var estado = resp.getStrValue("estado").orElse("");
			System.out.println(CARPETA001 + ": " + mensaje + " " + solicitudNro + " " + estado);
		} else {
			var error = resp.getStrValue("error").orElse("");
			System.out.println(CARPETA001 + ": " + error);
		}
	}

	/*
	 * Consultar carpeta y enviar carpeta
	 */
	public static void test2(WatanaApiClient client) throws WatanaApiException {
		var resp = client.consultarCarpeta(CARPETA002);
		var success = resp.getBoolValue("success").orElse(false);
		if (!success) {
			var datos = new WatanaApiObject() //
					.add("carpeta_codigo", CARPETA002) //
					.add("titulo", "Documento o carpeta de ejemplo") //
					.add("descripcion", "Esta es la descripción de este documento") //
					.add("observaciones", "obs") //
					.add("vigencia_horas", 24) //
					.add("reemplazar", false);

			var firmante = new WatanaApiObject() //
					.add("email", "jairo@llama.pe")//
					.add("nombre_completo", "FULANO");

			var archivos = new WatanaApiListObject();

			var archivo1 = new WatanaApiObject()//
					.add("nombre", "archivo1.pdf")//
					.add("zip_base64", new File(RUTAARCHIVO1));

			archivos.add(archivo1);

			try (var fis = new FileInputStream(new File(RUTAARCHIVO2))) {
				var archivo2 = new WatanaApiObject()//
						.add("nombre", "archivo2.pdf")//
						.add("zip_base64", fis);
				archivos.add(archivo2);
			} catch (IOException e) {
				throw new WatanaApiException(e.getMessage());
			}

			resp = client.enviarCarpeta(datos, firmante, archivos);
			success = resp.getBoolValue("success").orElse(false);
			if (success) {
				var mensaje = resp.getStrValue("mensaje").orElse("");
				var solicitudNro = resp.getStrValue("solicitud_numero").orElse("");
				System.out.println(CARPETA002 + ": " + mensaje + " " + solicitudNro);
			} else {
				var error = resp.getStrValue("error").orElse("");
				System.out.println(CARPETA002 + ": " + error);
			}
		} else {
			resp = client.eliminarCarpeta(CARPETA002);
			success = resp.getBoolValue("success").orElse(false);
			if (success) {
				var mensaje = resp.getStrValue("mensaje").orElse("");
				System.out.println(CARPETA002 + ": " + mensaje);
			} else {
				var error = resp.getStrValue("error").orElse("");
				System.out.println(CARPETA002 + ": " + error);
			}
		}
	}

	/*
	 * Descargar carpeta
	 */
	public static void test3(WatanaApiClient client) throws WatanaApiException {
		var resp = client.descargarCarpeta(CARPETA001);
		var success = resp.getBoolValue("success").orElse(false);
		if (success) {
			var mensaje = resp.getStrValue("mensaje").orElse("");
			System.out.println(CARPETA001 + ": " + mensaje);
			var archivos = resp.getListObject("archivos").orElse(WatanaApiListObject.emptyListObject());
			for (var archivo : archivos.getList()) {
				var nombre = archivo.getStrValue("nombre").orElse("");
				var pdfISOpt = archivo.getInputStreamValue("zip_base64");
				if (pdfISOpt.isPresent()) {
					try {
						WatanaApiUtils.saveToFile(RUTADIRECTORIO + File.separator + nombre, pdfISOpt.get());
						System.out.println(CARPETA001 + ": " + "Archivo guardado! " + nombre);
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				} else {
					System.out.println(CARPETA001 + ": " + "No se pudo recuperar el archivo " + nombre);
				}
			}
		} else {
			var error = resp.getStrValue("error").orElse("");
			System.out.println(CARPETA001 + ": " + error);
		}
	}
	/*
	 * Validar PDF
	 */
	public static void test4(WatanaApiClient client) throws WatanaApiException {
		var archivo = new WatanaApiObject();
		archivo.add("zip_base64", new File(ARCHIVOVALIDAR));
		var resp = client.validarPdf(archivo);
		var success = resp.getBoolValue("success").orElse(false);
		if (success) {

		} else {
			var error = resp.getStrValue("error").orElse("");
			System.out.println(error);
		}
	}
	/*
	 * Firmar PDF
	 */
	public static void test5(WatanaApiClient client) throws WatanaApiException {
		var firmaVisual = new WatanaApiObject()//
				.add("ubicacion_x",100)//
				.add("ubicacion_y",100)//
				.add("largo",300)//
				.add("alto",40)//
				.add("pagina",1)//
				.add("texto","Firmado digitalmente por: <FIRMANTE>\r\n<ORGANIZACION>\r\n<TITULO>\r\n<CORREO>\r\n<DIRECCION>\r\n<FECHA>\r\n Firmado con Watana");
		var datos = new WatanaApiObject()//
				.add("sello_de_tiempo",false)
				.add("firma_visual",firmaVisual)
				.add("zip_base64",new File(RUTAARCHIVO1));
		var resp = client.firmarPdf(datos);
		var success = resp.getBoolValue("success").orElse(false);
		if (success) {
			String nombre = "firmado.pdf";
			var pdfISOpt = resp.getInputStreamValue("zip_base64");
			if (pdfISOpt.isPresent()) {
				try {
					WatanaApiUtils.saveToFile(RUTADIRECTORIO + File.separator + nombre, pdfISOpt.get());
					System.out.println("Archivo guardado! " + nombre);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			} else {
				System.out.println("No se pudo recuperar el archivo " + nombre);
			}
		} else {
			var error = resp.getStrValue("error").orElse("");
			System.out.println(error);
		}
	}
	/*
	 * Sellar PDF
	 */
	public static void test6(WatanaApiClient client) throws WatanaApiException {
		var archivo = new WatanaApiObject();
		archivo.add("zip_base64", new File(ARCHIVOSELLAR));
		var resp = client.validarPdf(archivo);
		var success = resp.getBoolValue("success").orElse(false);
		if (success) {
			String nombre = "sellado.pdf";
			var pdfISOpt = resp.getInputStreamValue("zip_base64");
			if (pdfISOpt.isPresent()) {
				try {
					WatanaApiUtils.saveToFile(RUTADIRECTORIO + File.separator + nombre, pdfISOpt.get());
					System.out.println("Archivo guardado! " + nombre);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			} else {
				System.out.println("No se pudo recuperar el archivo " + nombre);
			}
		} else {
			var error = resp.getStrValue("error").orElse("");
			System.out.println(error);
		}
	}
	
	/*
	 * 
	 */
	public static void main(String[] args) throws WatanaApiException {

		var auth = new WatanaApiAuth();
		var client = new WatanaApiClient(auth);

		// consultamos si existe una carpeta
		test1(client);

		// consultamos si no existe una carpeta para crearla sino la eliminamos
		// test2(client);

		// descargamos la carpeta DOC001
		// test3(client);
		test6(client);
	}

}
