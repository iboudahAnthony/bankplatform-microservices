# Script PowerShell - Export des diagrammes PlantUML en PNG
# Utilise l'API publique plantuml.com

$UmlDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$OutputDir = Join-Path $UmlDir "images"

# Créer le dossier images
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

function Encode-PlantUML {
    param([string]$Text)

    # Compression zlib
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($Text)
    $ms = New-Object System.IO.MemoryStream
    $deflate = New-Object System.IO.Compression.DeflateStream($ms, [System.IO.Compression.CompressionMode]::Compress)
    $deflate.Write($bytes, 0, $bytes.Length)
    $deflate.Close()
    $compressed = $ms.ToArray()

    # Ajouter header zlib (0x78 0x9C)
    $zlibBytes = @(0x78, 0x9C) + $compressed

    # Base64 standard
    $b64 = [Convert]::ToBase64String($zlibBytes)

    # Convertir vers alphabet PlantUML
    $standard = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    $plantuml = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_"

    $result = ""
    foreach ($char in $b64.ToCharArray()) {
        $idx = $standard.IndexOf($char)
        if ($idx -ge 0) {
            $result += $plantuml[$idx]
        } elseif ($char -eq "=") {
            # padding, ignorer
        }
    }
    return $result
}

Write-Host "============================================================"
Write-Host "  Export des diagrammes PlantUML en PNG"
Write-Host "============================================================"

$pumlFiles = Get-ChildItem -Path $UmlDir -Filter "*.puml" | Sort-Object Name

if ($pumlFiles.Count -eq 0) {
    Write-Host "Aucun fichier .puml trouvé."
    exit
}

Write-Host "`n$($pumlFiles.Count) diagramme(s) trouvé(s) :`n"

$success = 0

foreach ($file in $pumlFiles) {
    $outputName = $file.Name -replace "\.puml$", ".png"
    $outputPath = Join-Path $OutputDir $outputName

    Write-Host "  Téléchargement : $($file.Name) ..." -NoNewline

    try {
        $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
        $encoded = Encode-PlantUML -Text $content
        $url = "https://www.plantuml.com/plantuml/png/$encoded"

        $webClient = New-Object System.Net.WebClient
        $webClient.DownloadFile($url, $outputPath)

        Write-Host " OK -> $outputName" -ForegroundColor Green
        $success++
    } catch {
        Write-Host " ERREUR : $_" -ForegroundColor Red
    }
}

Write-Host "`n============================================================"
Write-Host "  Résultat : $success/$($pumlFiles.Count) diagrammes exportés"
Write-Host "  Images sauvegardées dans : $OutputDir"
Write-Host "============================================================"

# Ouvrir le dossier images
Start-Process explorer.exe $OutputDir
