package pe.llama.watana;

public enum HttpStatusCode {

	OK(200, "Ok"), //
	BAD_REQUEST(400, "Bad request"), //
	AUTHORIZATION_REQUIRED(401, "Authorization required"), //
	FORBIDDEN(403, "Forbidden"), //
	NOT_FOUND(404, "Not found"), //
	INTERNAL_SERVER_ERROR(500, "Internal server error"), //
	BAD_GATEWAY(502, "Bad gateway");

	private int statusCode;
	private String reason;

	HttpStatusCode(int statusCode, String reason) {
		this.statusCode = statusCode;
		this.reason = reason;
	}

	public int value() {
		return statusCode;
	}

	public String getReason() {
		return reason;
	}

	public static String getReason(int statusCode) {
		for (HttpStatusCode httpStatusCode : values()) {
			if (httpStatusCode.value() == statusCode) {
				return httpStatusCode.getReason();
			}
		}
		return "";
	}
}
