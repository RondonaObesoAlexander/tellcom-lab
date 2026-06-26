import groovy.json.JsonOutput
import org.identityconnectors.framework.common.objects.AttributesAccessor

log.info("--- MIDPOINT OUTBOUND: Iniciando aprovisionamiento via REST API ---")

def accessor = new AttributesAccessor(attributes)
def extensionAttr = accessor.findString("extension")
def secretAttr = accessor.findString("secret")

// Validación de Reglas de Negocio en tiempo de ejecución
if (!extensionAttr || !secretAttr) {
    log.error("Atributos invalidos para la provision de la cuenta SIP.")
    throw new IllegalArgumentException("Extension o Secreto ausentes.")
}

def payload = [
    extension: extensionAttr,
    secret: secretAttr
]

// Consumir el endpoint provisto por el contenedor compañero en la subred aislada
def url = "http://172.20.0"
def connection = new URL(url).openConnection()
connection.setRequestMethod("POST")
connection.setDoOutput(true)
connection.setRequestProperty("Content-Type", "application/json")
connection.getOutputStream().write(JsonOutput.toJson(payload).getBytes("UTF-8"))

def responseCode = connection.getResponseCode()
if (responseCode == 201) {
    log.info("Aprovisionamiento Exitoso de la extension ${extensionAttr} en el filesystem.")
    return extensionAttr
} else {
    log.error("Error de conexion con Asterisk API. Codigo HTTP: " + responseCode)
    throw new RuntimeException("Fallo en la provision remota.")
}
