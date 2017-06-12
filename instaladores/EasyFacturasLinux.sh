#!/bin/bash
cd ~
mkdir -p .easyfacturas
export user=$USER
curl -LSso ~/.easyfacturas/jar.zip https://github.com/Jairo95/ProyectoLibres/raw/instalacion/instaladores/jar.zip
cd .easyfacturas
unzip jar.zip
echo "java -jar ./jar/AplicacionesLibres.jar" > EasyFacturas.bin
sudo cp jar/settings/EasyFacturas.desktop /usr/share/applications/
sudo cp jar/settings/EasyFacturas.ico /usr/share/icons/
rm jar.zip
