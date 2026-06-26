import groovy.json.JsonOutput

log.info("Iniciando aprovisionamiento automatizado del agente vía REST API")

def extension = attributes['id'] 
def password = attributes['password']

def payload = [
    extension: extension,
    secret: password
]

// Petición HTTP POST dirigida directamente al filesystem del contenedor de Asterisk (Puerto 5000)
def url = "http://172.20.0"
def post = new URL(url).openConnection();
post.setRequestMethod("POST")
post.setDoOutput(true)
post.setRequestProperty("Content-Type", "application/json")
post.getOutputStream().write(JsonOutput.toJson(payload).getBytes("UTF-8"));

def postRC = post.getResponseCode();
if(postRC == 201) {
    log.info("Sincronización Completa: Extensión ${extension} dada de alta de forma física en Asterisk.")
} else {
    log.error("Fallo de aprovisionamiento. Código de error HTTP: " + postRC)
}
