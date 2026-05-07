# Automatas

Aplicación Android que simula un Autómata Finito Determinista (AFD) para el lenguaje regular `a*b` (cero o más letras 'a' seguidas de exactamente una 'b').

Desarrollada con Kotlin y Jetpack Compose. Muestra paso a paso las transiciones del autómata e indica si la cadena ingresada es aceptada o rechazada.

## Características

- Valida cadenas según el patrón `a*b`
- Muestra cada transición: estado actual, símbolo leído, siguiente estado
- Representación visual de los estados (inicial, aceptación, trampa)
- Interfaz sencilla y limpia

## Funcionamiento

El autómata tiene tres estados:

- `q0` (estado inicial)
- `q1` (estado de aceptación)
- `q_trap` (estado trampa)

Transiciones:

- Desde `q0` con 'a' -> permanece en `q0`
- Desde `q0` con 'b' -> va a `q1` (aceptación)
- Desde `q1` con cualquier símbolo -> va a `q_trap`
- Desde `q_trap` con cualquier símbolo -> permanece en `q_trap`

La aplicación procesa la cadena carácter por carácter y registra cada paso.

## Ejemplos

| Cadena | ¿Aceptada? | Estado final |
|--------|------------|--------------|
| "b"    | Sí         | q1           |
| "ab"   | Sí         | q1           |
| "aaab" | Sí         | q1           |
| "aaaab"| Sí         | q1           |
| "a"    | No         | q0           |
| "ba"   | No         | q_trap       |
| "abc"  | No         | q_trap       |


## Requisitos

- Android SDK
- Kotlin
- Jetpack Compose

## Licencia

Este proyecto se distribuye bajo la licencia MIT.

## Autor
Jaime CM
Alias: Yormix - https://github.com/Yormix
