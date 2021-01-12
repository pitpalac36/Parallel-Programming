
$param1 = $args[0] # Nume fisier java

$param2 = $args[1] # No of rows

$param3 = $args[2] # No of columns

$param4 = $args[3] # No of rows / columns for kernel

# Executare class Java
$suma = 0

for ($i = 0; $i -lt 5; $i++){
    Write-Host "Rulare" ($i+1)
    $a = java $args[0] $args[1] $args[2] $args[3] # rulare class java
    Write-Host $a
    $suma += $a
    Write-Host ""
}
$media = $suma / 5
#Write-Host $suma
Write-Host "Timp de executie mediu:" $media

# Creare fisier .csv
if (!(Test-Path outJ.csv)){
    New-Item outJ.csv -ItemType File
    #Scrie date in csv
    Set-Content outJ.csv 'Nr linii matrice,Nr coloane matrice,Nr linii kernel,Nr threaduri,Timp executie'
}

# Append
Add-Content outJ.csv "$param2,$param3,$param4,0,$($media)"