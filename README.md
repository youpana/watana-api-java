# WatanaApi Java


Nuestra Biblioteca Java oficial de WatanaApi, es compatible con la [v1.0](https://ayuda.llama.pe/integracion/) de Watana API, con el cual tendrás la posibilidad de crear carpetas, consultarlas, eliminarlas, firmar pdfs, validarlos, y aplicarles sello de tiempo.


## Requisitos 

* Java JDK 11 o superior
* Maven 3.8.0 o superior
* Registrate [aquí](https://watana.pe/registro).
* Una vez registrado, si vas a realizar pruebas obtén tus llaves desde [aquí](https://watana.pe/auths).

> Recuerda que para obtener tus llaves debes ingresar a tu Watana.pe > WATANA API > ***Autenticacion***.

![alt tag](https://i.imgur.com/6i1moyJ.png)



## Instalación

Clonar el repositorio o descargar el código fuente

```bash
git clone git@github.com:youpana/watana-api-java.git
cd watana-api-java/
mvn package install
```
Incluir la dependencia en el archivo pom.xml

```xml
<dependencies>
	<dependency>
		<groupId>pe.llama.watana</groupId>
		<artifactId>watana-api</artifactId>
		<version>1.0</version>
	</dependency>
</dependencies>
```

## Configuración

Hay dos formas para establecer la conexión con WatanaApi

* Definir las variables de entorno WATANA_API_RUTA, WATANA_API_TOKEN e instanciar las clases WatanaApiAuth y WatanaApiClient

```java
	var auth = new WatanaApiAuth();
	var client = new WatanaApiClient(auth);
```
* Pasar como parámetro a la clase WatanaApiAuth las dos variables

```java
	var ruta = "xxx";
	var token = "xxx";
	var auth = new WatanaApiAuth(ruta, token);
	var client = new WatanaApiClient(auth);
```
## Enviar Carpeta

```java
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
```


## Consultar Carpeta

```java
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
```

## Descargar Carpeta

```java
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
```

## Eliminar Carpeta

```java
	var resp = client.eliminarCarpeta(CARPETA001);
	success = resp.getBoolValue("success").orElse(false);
	if (success) {
		var mensaje = resp.getStrValue("mensaje").orElse("");
		System.out.println(CARPETA002 + ": " + mensaje);
	} else {
		var error = resp.getStrValue("error").orElse("");
		System.out.println(CARPETA002 + ": " + error);
	}
```

## Validar PDF

```java
	var archivo = new WatanaApiObject();
	archivo.add("zip_base64", new File(ARCHIVOVALIDAR));
	var resp = client.validarPdf(archivo);
	var success = resp.getBoolValue("success").orElse(false);
	if (success) {
			//
	} else {
		var error = resp.getStrValue("error").orElse("");
		System.out.println(error);
	}
```

## Firmar PDF

```java
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
```

## Sellar PDF

```java
	var archivo = new WatanaApiObject();
	archivo.add("zip_base64", new File(ARCHIVOSELLAR));
	var resp = client.sellarPdf(archivo);
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
```

## Documentación
¿Necesitas más información para integrar `watana-api-java`? La documentación completa se encuentra en [https://ayuda.llama.pe/integracion](https://ayuda.llama.pe/integracion)

