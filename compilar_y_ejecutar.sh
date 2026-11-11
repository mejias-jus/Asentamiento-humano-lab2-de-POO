#!/bin/sh
# Compila y ejecuta la Simulacion de Asentamiento Humano (requiere JDK 8 o superior)
cd "$(dirname "$0")" || exit 1
rm -rf out && mkdir out
javac -encoding UTF-8 -cp "lib/*" -sourcepath src -d out src/presentacion/Main.java || exit 1
java -cp "out:lib/*" presentacion.Main
