# Tic Tac Toe Android App (Ocean Professional)

A simple two-player local Tic Tac Toe Android application using Kotlin and AndroidX.  
Modern UI with the Ocean Professional theme (blue primary, amber secondary, rounded corners, subtle shadows).

## Features
- 3x3 board, player turn indicator, and restart button
- Win/draw detection with dialog outcome
- State persistence across configuration changes (ViewModel + SavedState)
- Accessibility with content descriptions
- Unit tests for core game logic

## Build and Run
- Build: `./gradlew build`
- Install debug: `./gradlew :app:installDebug`
- Launch on device: Open "Tic Tac Toe"