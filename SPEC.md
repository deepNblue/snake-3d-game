# 3D Snake Game Specification

## 1. Project Overview
- **Project Name**: 3D Snake Game
- **Type**: WebGL Game
- **Core Functionality**: A 3D version of the classic Snake game built with Three.js, featuring snake movement, food collection, and collision detection in a 3D environment
- **Target Users**: Casual gamers

## 2. UI/UX Specification

### Layout Structure
- Full-screen 3D canvas
- Score display overlay (top-left)
- Game over overlay (centered, shown on collision)
- Control instructions overlay (bottom-center)

### Visual Design
- **Background**: Dark space theme with subtle grid floor
- **Color Palette**:
  - Background: #0a0a1a (deep space blue)
  - Snake Head: #00ff88 (neon green)
  - Snake Body: #00cc66 (darker green)
  - Food: #ff3366 (neon red/pink)
  - Grid: #1a1a3a (subtle blue)
  - UI Text: #ffffff with glow effect
- **Typography**: 
  - Font: "Orbitron" (Google Fonts) - futuristic gaming font
  - Score: 32px bold
  - Instructions: 16px
- **Visual Effects**:
  - Subtle bloom/glow on snake and food
  - Smooth camera following snake
  - Grid floor with perspective

### Components
- **Game Board**: 15x15 grid in 3D space
- **Snake**: Cube segments with slight gaps
- **Food**: Glowing sphere
- **Score Board**: Top-left corner
- **Game Over Screen**: Semi-transparent overlay with restart button

## 3. Functionality Specification

### Core Features
1. **3D Scene Setup**
   - Perspective camera with slight tilt
   - Directional + ambient lighting
   - Grid floor plane

2. **Snake Movement**
   - Constant forward movement
   - Arrow keys / WASD for direction change
   - Cannot reverse direction (180° turn)
   - Smooth grid-based movement (not continuous)

3. **Food System**
   - Random spawn on empty grid cells
   - Respawns when eaten
   - Glowing visual effect

4. **Collision Detection**
   - Wall collision → Game Over
   - Self collision → Game Over
   - Food collision → Grow snake + Score

5. **Scoring**
   - +10 points per food
   - High score tracking (session)

### User Interactions
- Arrow Keys / WASD: Change direction
- Space / Enter: Start game / Restart after game over
- Camera automatically follows snake head

### Edge Cases
- Prevent 180° direction reversal
- Food cannot spawn on snake body
- Handle rapid key presses gracefully

## 4. Acceptance Criteria

### Visual Checkpoints
- [ ] 3D grid floor renders correctly
- [ ] Snake head is visually distinct from body
- [ ] Food has glowing effect
- [ ] Score displays and updates
- [ ] Game over overlay appears on collision

### Functional Checkpoints
- [ ] Snake moves automatically after game starts
- [ ] Direction changes work with arrow keys and WASD
- [ ] Snake grows when eating food
- [ ] Score increases by 10 per food
- [ ] Game ends on wall collision
- [ ] Game ends on self collision
- [ ] Restart works correctly
