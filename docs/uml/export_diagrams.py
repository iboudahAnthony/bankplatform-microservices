"""
Script d'export des diagrammes PlantUML en PNG
Utilise l'API publique de plantuml.com
"""

import os
import zlib
import base64
import urllib.request
import string

# Dossier contenant les fichiers .puml
UML_DIR = os.path.dirname(os.path.abspath(__file__))
OUTPUT_DIR = os.path.join(UML_DIR, "images")

# Créer le dossier images s'il n'existe pas
os.makedirs(OUTPUT_DIR, exist_ok=True)

def encode_plantuml(text):
    """Encode le texte PlantUML pour l'API plantuml.com"""
    compressed = zlib.compress(text.encode("utf-8"))
    # PlantUML utilise un alphabet base64 modifié
    plantuml_alphabet = (
        string.digits +
        string.ascii_uppercase +
        string.ascii_lowercase +
        "-_"
    )
    standard_alphabet = (
        string.ascii_uppercase +
        string.ascii_lowercase +
        string.digits +
        "+/"
    )
    b64 = base64.b64encode(compressed).decode("ascii")
    result = b64.translate(str.maketrans(standard_alphabet, plantuml_alphabet))
    return result

def download_diagram(puml_file, output_file):
    """Télécharge le diagramme PNG depuis plantuml.com"""
    with open(puml_file, "r", encoding="utf-8") as f:
        content = f.read()

    encoded = encode_plantuml(content)
    url = f"https://www.plantuml.com/plantuml/png/{encoded}"

    print(f"  Téléchargement : {os.path.basename(puml_file)} ...", end=" ")
    try:
        urllib.request.urlretrieve(url, output_file)
        print(f"OK -> {os.path.basename(output_file)}")
        return True
    except Exception as e:
        print(f"ERREUR : {e}")
        return False

def main():
    print("=" * 60)
    print("  Export des diagrammes PlantUML en PNG")
    print("=" * 60)

    # Lister tous les fichiers .puml
    puml_files = sorted([
        f for f in os.listdir(UML_DIR)
        if f.endswith(".puml")
    ])

    if not puml_files:
        print("Aucun fichier .puml trouvé dans", UML_DIR)
        return

    print(f"\n{len(puml_files)} diagramme(s) trouvé(s) :\n")

    success = 0
    for puml_file in puml_files:
        input_path = os.path.join(UML_DIR, puml_file)
        output_name = puml_file.replace(".puml", ".png")
        output_path = os.path.join(OUTPUT_DIR, output_name)
        if download_diagram(input_path, output_path):
            success += 1

    print("\n" + "=" * 60)
    print(f"  Résultat : {success}/{len(puml_files)} diagrammes exportés")
    print(f"  Images sauvegardées dans : {OUTPUT_DIR}")
    print("=" * 60)

if __name__ == "__main__":
    main()
