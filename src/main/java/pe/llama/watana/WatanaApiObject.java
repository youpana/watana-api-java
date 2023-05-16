package pe.llama.watana;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.annotation.JsonValue;

public class WatanaApiObject {

	public static final int BUFFER_SIZE = 4096;

	private Map<String, Object> data;

	public WatanaApiObject() {
		data = new HashMap<String, Object>();
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, String value) {
		data.put(key, value);
		return this;
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, int value) {
		data.put(key, value);
		return this;
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, boolean value) {
		data.put(key, value);
		return this;
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, WatanaApiObject value) {
		data.put(key, value);
		return this;
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, List<WatanaApiObject> value) {
		data.put(key, value);
		return this;
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, InputStream fileInput) throws WatanaApiException {
		try {
			var byteArrayOutputStream = new ByteArrayOutputStream();
			var zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
			var zipEntry = new ZipEntry("file.pdf");
			zipOutputStream.putNextEntry(zipEntry);
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;

			while ((length = fileInput.read(buffer)) != -1) {
				zipOutputStream.write(buffer, 0, length);
			}
			zipOutputStream.closeEntry();
			zipOutputStream.close();
			var value = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
			data.put(key, value);
			return this;
		} catch (IOException e) {
			throw new WatanaApiException(e.getMessage());
		}
	}

	/*
	 * 
	 */
	public WatanaApiObject add(String key, File file) throws WatanaApiException {
		try (var fileInput = new FileInputStream(file)) {
			add(key, fileInput);
			return this;
		} catch (IOException e) {
			throw new WatanaApiException(e.getMessage());
		}
	}

	/*
	 * 
	 */
	public WatanaApiObject add(Map<String, Object> data) {
		for (var entry : data.entrySet()) {
			this.data.put(entry.getKey(), entry.getValue());
		}
		return this;
	}

	/*
	 * 
	 */
	public boolean exist(String key) {
		return data.containsKey(key);
	}

	/*
	 * 
	 */
	public Optional<String> getStrValue(String key) {
		if (data.containsKey(key)) {
			if (data.get(key) instanceof String) {
				return Optional.of((String) data.get(key));
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	/*
	 * 
	 */
	public Optional<Boolean> getBoolValue(String key) {
		if (data.containsKey(key)) {
			if (data.get(key) instanceof Boolean) {
				return Optional.of((Boolean) data.get(key));
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	public Optional<InputStream> getInputStreamValue(String key) {
		if (data.containsKey(key)) {
			if (data.get(key) instanceof String) {
				try {
					var value = (String) data.get(key);
					byte[] decodedBytes = Base64.getDecoder().decode(value);
					var in = new ByteArrayInputStream(decodedBytes);
					var zipInputStream = new ZipInputStream(in);
					ZipEntry zipEntry;
					while ((zipEntry = zipInputStream.getNextEntry()) != null) {
						if (zipEntry.getName().equals("dummy/")) {
							continue;
						}
						break;
					}
					if (zipEntry == null) {
						return Optional.empty();
					}
					if (zipEntry.getSize() == 0) {
						return Optional.empty();
					}
					try (var result = new ByteArrayOutputStream()) {
						int length;
						byte[] buffer = new byte[BUFFER_SIZE];
						while ((length = zipInputStream.read(buffer)) != -1) {
							result.write(buffer, 0, length);
						}
						return Optional.of(new ByteArrayInputStream(result.toByteArray()));
					}
				} catch (IllegalArgumentException | IOException e) {
					return Optional.empty();
				}
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	/*
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Optional<WatanaApiListObject> getListObject(String key) {
		if (data.containsKey(key)) {
			if (data.get(key) instanceof List<?>) {
				var lista = (List<?>) data.get(key);
				var listaNew = new WatanaApiListObject();
				for (var item : lista) {
					if (item instanceof Map<?, ?>) {
						var obj = new WatanaApiObject();
						obj.add((Map<String, Object>) item);
						listaNew.add(obj);
					}
				}
				return Optional.of(listaNew);
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	/*
	 * 
	 */
	@JsonValue
	public Map<String, Object> getMap() {
		return data;
	}

	@Override
	public String toString() {
		return "WatanaApiObject [data=" + data + "]";
	}
}
