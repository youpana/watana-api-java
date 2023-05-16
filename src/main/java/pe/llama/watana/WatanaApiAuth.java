package pe.llama.watana;

import static pe.llama.watana.WatanaApiUtils.assertNull;

public class WatanaApiAuth {

	private static String WATANA_API_RUTA = "WATANA_API_RUTA";
	private static String WATANA_API_TOKEN = "WATANA_API_TOKEN";
	private String ruta;
	private String token;

	/**
	 * 
	 * @throws WatanaApiException
	 */
	public WatanaApiAuth() throws WatanaApiException {
		ruta = System.getenv(WATANA_API_RUTA);
		token = System.getenv(WATANA_API_TOKEN);
		if (ruta == null) {
			throw new WatanaApiException("Falta la variable " + WATANA_API_RUTA);
		}
		if (token == null) {
			throw new WatanaApiException("Falta la variable " + WATANA_API_TOKEN);
		}
	}
	/**
	 * 
	 * @param ruta
	 * @param token
	 * @throws WatanaApiException
	 */
	public WatanaApiAuth(String ruta, String token) throws WatanaApiException {
		assertNull(ruta,"ruta");
		assertNull(token,"token");
		setRuta(ruta);
		setToken(token);
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
