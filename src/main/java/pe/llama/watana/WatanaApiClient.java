package pe.llama.watana;

import static pe.llama.watana.WatanaApiUtils.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WatanaApiClient {

	private WatanaApiAuth auth;
	private ObjectMapper objectMapper;

	/*
	 * 
	 */
	public WatanaApiClient(WatanaApiAuth auth) {
		this.auth = auth;
		objectMapper = new ObjectMapper();
	}

	/*
	 * 
	 */
	private HttpRequest createHttpRequest(Map<String, Object> data) throws JsonProcessingException {
		var json = objectMapper.writeValueAsString(data);
		var requestHttp = HttpRequest.newBuilder() //
				.uri(URI.create(auth.getRuta())) //
				.header("Authorization", auth.getToken()) //
				.header("Content-Type", "application/json") //
				.POST(HttpRequest.BodyPublishers.ofString(json))//
				.build();
		return requestHttp;
	}

	private WatanaApiObject send(Map<String, Object> data) throws WatanaApiException {
		try {
			var requestHttp = createHttpRequest(data);
			var client = HttpClient.newHttpClient();
			var response = client.send(requestHttp, HttpResponse.BodyHandlers.ofString());
			int statusCode = response.statusCode();
			String body = response.body();
			//System.out.println(body);
			if (statusCode == HttpStatusCode.OK.value()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> result = objectMapper.readValue(body, HashMap.class);
				var watanaObject = new WatanaApiObject();
				watanaObject.add(result);
				return watanaObject;
			} else {
				String message = HttpStatusCode.getReason(statusCode);
				throw new WatanaApiException(message);
			}
		} catch (IOException e) {
			throw new WatanaApiException(e.getMessage());
		} catch (InterruptedException ie) {
			throw new WatanaApiException(ie.getMessage());
		}

	}

	/*
	 * 
	 */
	public WatanaApiObject consultarCarpeta(String carpeta) throws WatanaApiException {
		var data = new WatanaApiObject();
		data.add("operacion", "consultar_carpeta");
		data.add("carpeta_codigo", carpeta);
		return send(data.getMap());
	}
	/*
	 * 
	 */
	public WatanaApiObject eliminarCarpeta(String carpeta) throws WatanaApiException {
		var data = new WatanaApiObject();
		data.add("operacion", "eliminar_carpeta");
		data.add("carpeta_codigo", carpeta);
		return send(data.getMap());
	}
	/*
	 * 
	 */
	public WatanaApiObject enviarCarpeta(WatanaApiObject datos, WatanaApiObject firmante, WatanaApiListObject archivos)
			throws WatanaApiException {
		assertNull(datos,"datos");
		assertNull(firmante,"firmante");
		assertNull(archivos,"archivos");
		if (archivos.isEmpty()) {
			throw new WatanaApiException("Debe incluir al menos un archivo");
		}
		var data = new WatanaApiObject();
		data.add("operacion", "enviar_carpeta");
		data.add(datos.getMap());
		data.add("firmante", firmante);
		data.add("archivos", archivos.getList());
		return send(data.getMap());
	}
	/*
	 * 
	 */
	public WatanaApiObject descargarCarpeta(String carpeta) throws WatanaApiException {
		var data = new WatanaApiObject();
		data.add("operacion", "descargar_carpeta");
		data.add("carpeta_codigo", carpeta);
		return send(data.getMap());
	}
	
	/*
	 * 
	 */
	public WatanaApiObject validarPdf(WatanaApiObject archivo) throws WatanaApiException {
		assertNull(archivo,"archivo");
		var fileOpt = archivo.getStrValue("zip_base64");
		if (fileOpt.isPresent()) {
			var data = new WatanaApiObject();
			data.add("operacion", "validar_pdf");
			data.add("zip_base64", fileOpt.get());
			return send(data.getMap());
		}
		else {
			throw new WatanaApiException("Debe incluir el archivo");
		}
	}

	public WatanaApiObject firmarPdf(WatanaApiObject datos) throws WatanaApiException {
		assertNull(datos,"datos");
		var fileOpt = datos.getStrValue("zip_base64");
		if (fileOpt.isPresent()) {
			var data = new WatanaApiObject();
			data.add(datos.getMap());
			data.add("operacion", "firmar_pdf");
			return send(data.getMap());
		}
		else {
			throw new WatanaApiException("Debe incluir el archivo");
		}
	}
	
	public WatanaApiObject sellarPdf(WatanaApiObject archivo) throws WatanaApiException {
		assertNull(archivo,"archivo");
		var fileOpt = archivo.getStrValue("zip_base64");
		if (fileOpt.isPresent()) {
			var data = new WatanaApiObject();
			data.add("operacion", "sellar_pdf");
			data.add("zip_base64", fileOpt.get());
			return send(data.getMap());
		}
		else {
			throw new WatanaApiException("Debe incluir el archivo");
		}
	}
}
