package pe.llama.watana;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WatanaApiUtils {

	public static void assertNull(Object obj, String nombre) throws WatanaApiException {
		if (obj == null) {
			throw new WatanaApiException(nombre + " no puede ser null");
		}
	}
	
	public static void saveToFile(String fileName, InputStream in) throws IOException {
		var outputStream = new FileOutputStream(fileName);			
		outputStream.write(in.readAllBytes());
		outputStream.close();
	}
}
