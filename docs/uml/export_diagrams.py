"""
Script pour exporter les diagrammes PlantUML en PNG
Utilise l'API de plantuml.com
"""
import os
import zlib
import base64
import urllib.request

UML_DIR = os.path.dirname(os.path.abspath(__file__))
OUTPUT_DIR = os.path.join(UML_DIR, "images")
os.makedirs(OUTPUT_DIR, exist_ok=True)

def encode_plantuml(text):
    """Encode le texte pour l'API plantuml.com"""
    data = zlib.compress(text.encode('utf-8'), 9)
    
    # Alphabet PlantUML (différent du base64 standard)
    plantuml_alphabet = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_'
    standard_alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
    
    b64 = base64.b64encode(data).decode('ascii')
    result = ''
    for c in b64:
        if c in standard_alphabet:
            result += plantuml_alphabet[standard_alphabet.index(c)]
        elif c == '=':
            pass  # ignorer padding
    return result

def download_diagram(puml_file, output_file):
    """Télécharge le diagramme PNG depuis plantuml.com"""
    with open(puml_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    encoded = encode_plantuml(content)
    url = f"https://www.plantuml.com/plantuml/png/{encoded}"
    
    print(f"  Génération : {os.path.basename(puml_file)} ...", end=' ')
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=30) as response:
            with open(output_file, 'wb') as f:
                f.write(response.read())
        size = os.path.getsize(output_file)
        print(f"OK ({size} bytes) -> {os.path.basename(output_file)}")
        return True
    except Exception as e:
        print(f"ERREUR: {e}")
        return False

def main():
    print("=" * 60)
    print("  Export des diagrammes PlantUML en PNG")
    print("=" * 60)
    
    puml_files = sorted([
        f for f in os.listdir(UML_DIR)
        if f.endswith('.puml')
    ])
    
    if not puml_files:
        print("Aucun fichier .puml trouvé")
        return
    
    print(f"\n{len(puml_files)} diagramme(s) trouvé(s)\n")
    
    success = 0
    for puml_file in puml_files:
        input_path = os.path.join(UML_DIR, puml_file)
        output_name = puml_file.replace('.puml', '.png')
        output_path = os.path.join(OUTPUT_DIR, output_name)
        if download_diagram(input_path, output_path):
            success += 1
    
    print("\n" + "=" * 60)
    print(f"  Résultat : {success}/{len(puml_files)} diagrammes exportés")
    print(f"  Dossier  : {OUTPUT_DIR}")
    print("=" * 60)
    
    # Ouvrir le dossier automatiquement
    if success > 0:
        os.startfile(OUTPUT_DIR)

if __name__ == "__main__":
    main()
