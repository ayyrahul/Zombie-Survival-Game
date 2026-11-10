# Zombie Survival (Java 2D)

A simple top-down survival game built with pure **Java Swing/AWT** — no game engine, no JavaFX, no external libraries. The player dodges zombies that spawn continuously from the screen edges and chase them down; score is based on survival time.

## How to Build & Run

Requires only a JDK (Java 8+). No dependencies to install.

```bash
cd ZombieSurvival
javac *.java
java Main
```

A high score file (`highscore.txt`) will be created automatically in the folder you run it from.

## Controls

| Key         | Action                     |
|-------------|----------------------------|
| ↑ ↓ ← →     | Move                       |
| Shift       | Sprint (2x speed)          |
| P           | Pause / Resume             |
| R           | Restart (after Game Over)  |

## How It Works

- **Game loop**: a `javax.swing.Timer` fires every 16ms (~60 FPS). Each tick: move the player → move zombies → check collisions → maybe spawn a zombie → maybe tick the score → repaint.
- **Zombie AI**: each zombie steers straight at the player every frame using vector normalization:
  ```
  dx = player.x - zombie.x
  dy = player.y - zombie.y
  distance = sqrt(dx² + dy²)
  zombie.x += (dx / distance) * speed
  zombie.y += (dy / distance) * speed
  ```
- **Spawning**: a new zombie appears roughly every 2 seconds at a random point along one of the four screen edges (top/bottom/left/right), with a randomized speed (slow/normal/fast).
- **Collision**: `Rectangle.intersects()` between the player's bounds and each zombie's bounds. On a hit, the zombie is removed and the player loses 1 health (of 3) instead of dying instantly.
- **Scoring**: +1 point per second survived. Both the current score and the current survival time are shown on screen, along with the persisted all-time high score.
- **Difficulty ramp**: every 20 seconds, zombie speed increases and the spawn interval shortens slightly, so later runs get progressively harder.
- **Persistence**: the high score is written to `highscore.txt` via `FileWriter` and read back on startup via `Scanner`.

## Project Structure

```
ZombieSurvival/
├── Main.java       # Entry point — creates the JFrame
├── GamePanel.java  # Game loop, rendering, input, spawning, collisions
├── Player.java     # Player position/speed/size, movement, drawing
├── Zombie.java     # Zombie position/speed, chase AI, drawing
├── Game.java       # Score, health, pause/game-over state, high score file I/O
└── README.md
```

## Features Implemented

- Player controlled with arrow keys, plus Shift-to-sprint
- Zombies spawn continuously from random screen edges every ~2 seconds
- Zombies chase the player using normalized direction vectors
- Health system (3 hits before Game Over) instead of instant death
- Score based on survival time (1 point/second)
- Persistent high score saved to `highscore.txt`
- Restart (R) and Pause (P)
- Increasing difficulty over time (zombies speed up, spawn faster every 20s)
- Simple graphics: blue circle (player), green squares (zombies), dark gray background, white/red HUD text
- Built entirely with core OOP concepts: classes, constructors, ArrayLists, loops, if-else, Timer, KeyListener, JPanel/Graphics

## Ideas for Further Extension

The design leaves room to add coins (+10 score pickups), sound effects via `javax.sound.sampled`, an on-screen FPS counter, or multiple distinct zombie "types" with fixed speed tiers, without needing to restructure the existing classes.
