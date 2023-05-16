package pe.llama.watana;

import java.util.ArrayList;
import java.util.List;

public class WatanaApiListObject {

	private final List<WatanaApiObject> objetos;

	public static final WatanaApiListObject EMPTYLISTOBJECT = new WatanaApiListObject();

	public static WatanaApiListObject emptyListObject() {
		return EMPTYLISTOBJECT;
	}

	public WatanaApiListObject() {
		objetos = new ArrayList<WatanaApiObject>();
	}

	public WatanaApiListObject add(WatanaApiObject object) {
		objetos.add(object);
		return this;
	}

	public List<WatanaApiObject> getList() {
		return objetos;
	}

	public boolean isEmpty() {
		return objetos.isEmpty();
	}
}
