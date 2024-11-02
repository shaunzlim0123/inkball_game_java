# Inkball Game

Welcome to the **Inkball Game** repository! This project is a recreation and extension of the classic Inkball game, developed as a portfolio piece to showcase object-oriented design principles, modular architecture, and clean code practices.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Architecture](#architecture)
  - [Core Classes](#core-classes)
  - [Extension: AccelerationTile](#extension-accelerationtile)
- [Design Principles](#design-principles)
- [Installation and Setup](#installation-and-setup)
- [How to Play](#how-to-play)
- [Future Improvements](#future-improvements)
- [Contact Information](#contact-information)
- [License](#license)

## Introduction

The Inkball Game is a puzzle game where players guide balls into holes of matching colors by drawing lines and manipulating various game elements. This project demonstrates proficiency in Java programming, game development, and software design patterns, emphasizing code reuse, flexibility, and modularity.

## Features

- **Interactive Gameplay**: Draw lines to guide balls into their respective holes.
- **Dynamic Obstacles**: Walls, tiles, and acceleration tiles alter the movement of balls.
- **Level Management**: Progress through multiple levels with increasing difficulty.
- **Scoring System**: Earn points based on performance and remaining time.
- **Visual Effects**: Smooth animations and rendering of game elements.

## Architecture

The game's architecture is designed for clarity and extensibility, using object-oriented principles to separate concerns and promote code reuse.

### Core Classes

- **`Interactable` Interface**: Defines a contract for game objects that interact with the `Ball`, such as `Wall`, `Line`, and `AccelerationTile`. It includes methods for collision handling, allowing for easy expansion of game elements.

- **`GameObject` Abstract Class**: Encapsulates shared properties like position and dimensions. Classes like `Wall`, `Tile`, `Ball`, and `Hole` inherit from this, streamlining their core functionality.

- **`Level` Class**: Manages key game entities and orchestrates gameplay through state management, collision detection, and rendering. It integrates all game elements, including `Wall`, `Hole`, `Spawner`, `Ball`, `Tile`, and `Line`.

- **`InputHandler` Class**: Manages user input for drawing and removing lines, interacting with the game state to modify gameplay elements.

- **`LevelCompletionAnimation` Class**: Handles the animation sequence when a level is completed, moving tiles and updating the score based on remaining time.

- **`Renderer` Class**: Responsible for drawing all game elements, including tiles, walls, balls, and game messages, ensuring a clean separation of visual rendering from game logic.

- **`App` Class**: Serves as the central hub, handling game setup, level management, user input, rendering, and score tracking. It initializes the environment by loading configurations and resources, and manages progression through levels using a JSON configuration file.

### Extension: AccelerationTile

The `AccelerationTile` class extends the game's functionality by introducing a new type of tile that applies directional acceleration to balls. Implemented as a subclass of `GameObject` and implementing the `Interactable` interface, it allows tiles to interact with the game's core mechanics.

- **Properties**:
  - `accelerationX` and `accelerationY`: Determine the acceleration applied to a ball's velocity.
  - `image`: Used for rendering the tile.

- **Methods**:
  - `checkCollision(Ball ball)`: Detects when a ball enters the tile's area and applies acceleration, altering the ball's movement.

## Design Principles

- **Object-Oriented Programming**: Utilizes interfaces and abstract classes to promote code reuse and flexibility.
- **Modularity**: Separates concerns through dedicated classes for input handling, rendering, and game logic.
- **Extensibility**: Designed to easily incorporate new game elements and mechanics.

## Installation and Setup

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/yourusername/inkball-game.git
   ```

2. **Navigate to the Project Directory**:

   ```bash
   cd inkball-game
   ```

3. **Compile the Source Code**:

   ```bash
   javac -d bin src/*.java
   ```

4. **Run the Game**:

   ```bash
   java -cp bin App
   ```

## How to Play

- **Objective**: Guide balls into holes of the same color.
- **Controls**:
  - **Draw Lines**: Click and drag the mouse to draw lines that direct balls.
  - **Erase Lines**: Right-click on lines to remove them.
- **Game Elements**:
  - **Walls**: Static obstacles that affect ball movement.
  - **Tiles**: Form the gameplay grid.
  - **Acceleration Tiles**: Change the velocity of balls.
  - **Spawners**: Generate balls at specific locations.
  - **Holes**: Capture balls of matching colors.

## Future Improvements

- **Additional Levels**: Create more challenging levels with new obstacles.
- **Enhanced Graphics**: Improve visual effects and animations.
- **Power-Ups**: Introduce new game mechanics like power-ups or special abilities.
- **Multiplayer Mode**: Implement a mode where players can compete or cooperate.

## Contact Information

For any inquiries or feedback, please contact:

- **Name**: Shaun Lim
- **Email**: shaunzlim0123@gmail.com  
- **LinkedIn**: https://www.linkedin.com/in/shaun-lim-a2848928a/
- **GitHub**: [[Your GitHub Profile](https://github.com/yourusername)](https://github.com/shaunzlim0123)

## License

This project is licensed under the [MIT License](LICENSE).

---

Thank you for checking out the Inkball Game! Your feedback and contributions are welcome.
