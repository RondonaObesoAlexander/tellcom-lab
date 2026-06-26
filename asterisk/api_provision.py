from flask import Flask, request, jsonify
import subprocess

app = Flask(__name__)
PJSIP_FILE = "/etc/asterisk/pjsip.conf"

@app.route('/api/v1/endpoint', methods=['POST'])
def create_endpoint():
    data = request.json
    ext = data.get('extension')
    secret = data.get('secret')
    
    if not ext or not secret:
        return jsonify({"error": "Faltan parametros"}), 400
        
    config_block = f"\n\n[{ext}]\ntype=endpoint\ncontext=financial-context\ndisallow=all\nallow=ulaw,alaw\nauth={ext}\naors={ext}\n\n[{ext}]\ntype=auth\nauth_type=userpass\nusername={ext}\npassword={secret}\n\n[{ext}]\ntype=aor\nmax_contacts=1\n"
    
    with open(PJSIP_FILE, "a") as f:
        f.write(config_block)
        
    subprocess.run(["asterisk", "-rx", "pjsip reload"])
    return jsonify({"status": "success"}), 201

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
