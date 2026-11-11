@echo off
rem Compila y ejecuta la Simulacion de Asentamiento Humano (requiere JDK 8 o superior)
if exist out rmdir /s /q out
mkdir out
javac -encoding UTF-8 -cp "lib\*" -sourcepath src -d out src\presentacion\Main.java
if errorlevel 1 (
  echo.
  echo Error al compilar. Revise que la carpeta lib tenga los 3 JAR y que tenga instalado el JDK.
  pause
  exit /b 1
)
java -cp "out;lib\*" presentacion.Main
