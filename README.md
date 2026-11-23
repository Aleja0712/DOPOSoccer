# DOPOSoccer

Juego de fútbol de cabezas en Java (Swing) — proyecto de Desarrollo Orientado por Objetos (DOPO).

**Autores:** Mary Alejandra Guanga García y Samuel Esteban Cruz

## Estructura

- `src/domain` — lógica del juego (fachada `SoccerGame`)
- `src/presentation` — interfaz gráfica (`SoccerGUI`)
- `src/test` — pruebas de unidad (JUnit 4) y diagramas de secuencia
- `recursos` — imágenes
- `lib` — JUnit 4.13.2 y Hamcrest 1.3
- `Headsoocer.asta` — diagramas (Astah)
- `RETROSPECTIVA.txt` — retrospectiva del proyecto

## Compilar y ejecutar

```bash
javac -d bin -cp "lib/*" src/domain/*.java src/presentation/*.java src/test/*.java
java -cp bin presentation.SoccerGUI
```

## Pruebas

```bash
java -cp "bin:lib/*" org.junit.runner.JUnitCore test.SoccerGameTest
```
(En Windows usa `;` en lugar de `:` en el classpath.)
