# 3D贪吃蛇游戏 - 项目设计文档

> 使用Agent集群系统v3.0.0开发
> 日期：2026-03-10

---

## 📋 项目概述

**项目名称**: 3D贪吃蛇游戏
**目标平台**: Web + Android
**开发方式**: 使用Agent集群系统v3.0.0智能开发

---

## 🎮 游戏核心逻辑

### 1. 蛇的移动和生长机制

```javascript
// 蛇的数据结构
class Snake {
    constructor() {
        this.segments = [{x: 0, y: 0, z: 0}];  // 3D坐标
        this.direction = {x: 1, y: 0, z: 0};   // 移动方向
        this.speed = 5;  // 移动速度
    }

    // 移动蛇
    move() {
        const head = this.segments[0];
        const newHead = {
            x: head.x + this.direction.x,
            y: head.y + this.direction.y,
            z: head.z + this.direction.z
        };
        this.segments.unshift(newHead);
        this.segments.pop();
    }

    // 生长
    grow() {
        const tail = this.segments[this.segments.length - 1];
        this.segments.push({...tail});
    }
}
```

### 2. 食物生成算法

```javascript
class FoodGenerator {
    constructor(gridSize = 20) {
        this.gridSize = gridSize;
    }

    generate(snakeSegments) {
        let position;
        do {
            position = {
                x: Math.floor(Math.random() * this.gridSize) - this.gridSize/2,
                y: 0,
                z: Math.floor(Math.random() * this.gridSize) - this.gridSize/2
            };
        } while (this.collidesWithSnake(position, snakeSegments));

        return position;
    }

    collidesWithSnake(pos, segments) {
        return segments.some(seg =>
            seg.x === pos.x && seg.z === pos.z
        );
    }
}
```

### 3. 碰撞检测

```javascript
class CollisionDetector {
    checkWallCollision(head, gridSize) {
        return Math.abs(head.x) >= gridSize/2 ||
               Math.abs(head.z) >= gridSize/2;
    }

    checkSelfCollision(head, segments) {
        return segments.slice(1).some(seg =>
            seg.x === head.x && seg.z === head.z
        );
    }

    checkFoodCollision(head, food) {
        return head.x === food.x && head.z === food.z;
    }
}
```

---

## 🎨 3D渲染方案（Web版）

### 技术栈

- **Three.js**: 3D渲染引擎
- **HTML5 Canvas**: 渲染目标
- **ES6 Modules**: 模块化

### 场景设计

```javascript
// 场景初始化
const scene = new THREE.Scene();
scene.background = new THREE.Color(0x1a1a2e);

// 相机设置
const camera = new THREE.PerspectiveCamera(
    75,  // FOV
    window.innerWidth / window.innerHeight,  // Aspect
    0.1,  // Near
    1000  // Far
);
camera.position.set(0, 30, 30);
camera.lookAt(0, 0, 0);

// 渲染器
const renderer = new THREE.WebGLRenderer({antialias: true});
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.shadowMap.enabled = true;
```

### 蛇的3D模型

```javascript
class SnakeRenderer {
    constructor(scene) {
        this.scene = scene;
        this.meshes = [];
    }

    render(snakeSegments) {
        // 清除旧的mesh
        this.meshes.forEach(mesh => this.scene.remove(mesh));
        this.meshes = [];

        // 创建新的mesh
        snakeSegments.forEach((seg, index) => {
            const geometry = new THREE.BoxGeometry(1, 1, 1);
            const material = new THREE.MeshPhongMaterial({
                color: index === 0 ? 0x00ff00 : 0x00cc00
            });
            const cube = new THREE.Mesh(geometry, material);

            cube.position.set(seg.x, seg.y, seg.z);
            cube.castShadow = true;
            cube.receiveShadow = true;

            this.scene.add(cube);
            this.meshes.push(cube);
        });
    }
}
```

### 食物的3D模型

```javascript
class FoodRenderer {
    constructor(scene) {
        this.scene = scene;
        this.mesh = null;
    }

    render(foodPosition) {
        if (this.mesh) {
            this.scene.remove(this.mesh);
        }

        const geometry = new THREE.SphereGeometry(0.5, 32, 32);
        const material = new THREE.MeshPhongMaterial({
            color: 0xff0000,
            emissive: 0x330000
        });
        this.mesh = new THREE.Mesh(geometry, material);

        this.mesh.position.set(
            foodPosition.x,
            foodPosition.y + 0.5,
            foodPosition.z
        );

        this.scene.add(this.mesh);
    }
}
```

---

## 📱 多平台支持策略

### Web版本架构

```
web-version/
├── index.html           # 主HTML
├── css/
│   └── style.css       # 样式
├── js/
│   ├── main.js         # 入口
│   ├── game.js         # 游戏逻辑
│   ├── snake.js        # 蛇类
│   ├── food.js         # 食物类
│   ├── renderer.js     # 3D渲染
│   └── controls.js     # 控制
└── assets/
    └── textures/       # 纹理资源
```

### Android版本架构

**方案A: 跨平台（推荐）**
```
android-version/
├── app/
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com.snake3d/
│   │   │       ├── MainActivity.java
│   │   │       ├── GameView.java
│   │   │       └── Renderer.java
│   │   ├── res/
│   │   │   └── layout/
│   │   │       └── activity_main.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

**方案B: Unity跨平台**
```
unity-version/
├── Assets/
│   ├── Scripts/
│   │   ├── SnakeController.cs
│   │   ├── FoodSpawner.cs
│   │   └── GameManager.cs
│   ├── Scenes/
│   │   └── Main.unity
│   └── Prefabs/
│       ├── Snake.prefab
│       └── Food.prefab
└── ProjectSettings/
```

### 代码复用策略

1. **核心逻辑层**（100%复用）
   - 蛇的移动逻辑
   - 食物生成算法
   - 碰撞检测

2. **渲染层**（平台适配）
   - Web: Three.js
   - Android: OpenGL ES / Unity

3. **控制层**（平台适配）
   - Web: 键盘事件
   - Android: 触摸屏滑动

---

## 🏗️ 技术架构

### 前端架构（Web）

```javascript
// 主游戏类
class Game {
    constructor() {
        this.snake = new Snake();
        this.food = new FoodGenerator();
        this.renderer = new SnakeRenderer(scene);
        this.collision = new CollisionDetector();
        this.score = 0;
        this.isRunning = false;
    }

    start() {
        this.isRunning = true;
        this.gameLoop();
    }

    gameLoop() {
        if (!this.isRunning) return;

        this.snake.move();

        // 碰撞检测
        const head = this.snake.segments[0];

        // 撞墙
        if (this.collision.checkWallCollision(head, 20)) {
            this.gameOver();
            return;
        }

        // 撞自己
        if (this.collision.checkSelfCollision(head, this.snake.segments)) {
            this.gameOver();
            return;
        }

        // 吃到食物
        if (this.collision.checkFoodCollision(head, this.food.position)) {
            this.snake.grow();
            this.score += 10;
            this.food.generate(this.snake.segments);
        }

        // 渲染
        this.renderer.render(this.snake.segments);

        // 下一帧
        setTimeout(() => this.gameLoop(), 1000 / this.snake.speed);
    }

    gameOver() {
        this.isRunning = false;
        alert(`Game Over! Score: ${this.score}`);
    }
}
```

### 状态管理

```javascript
class GameState {
    constructor() {
        this.score = 0;
        this.level = 1;
        this.isPaused = false;
        this.highScore = localStorage.getItem('highScore') || 0;
    }

    updateScore(points) {
        this.score += points;
        if (this.score > this.highScore) {
            this.highScore = this.score;
            localStorage.setItem('highScore', this.highScore);
        }
    }

    nextLevel() {
        this.level++;
        // 增加难度
    }
}
```

---

## 🎯 开发计划

### Phase 1: 游戏设计文档 ✅
- 时间：30秒
- 状态：完成

### Phase 2: Web版本核心
- 时间：2-3分钟
- 任务：
  - [ ] 搭建Three.js场景
  - [ ] 实现蛇的3D渲染
  - [ ] 实现食物3D模型
  - [ ] 添加光照和阴影
  - [ ] 实现键盘控制

### Phase 3: Android版本
- 时间：3-4分钟
- 任务：
  - [ ] 创建Android项目
  - [ ] 实现OpenGL ES渲染
  - [ ] 添加触摸控制
  - [ ] 优化性能

### Phase 4: 优化和测试
- 时间：1-2分钟
- 任务：
  - [ ] 添加音效
  - [ ] 添加粒子效果
  - [ ] 编写测试用例
  - [ ] 性能优化

---

## 📊 性能指标

### 目标性能

| 平台 | FPS | 内存 | 启动时间 |
|------|-----|------|---------|
| Web | 60 | <50MB | <2秒 |
| Android | 60 | <100MB | <3秒 |

---

## 🚀 立即开始

### Web版本启动命令

```bash
cd web-version
python3 -m http.server 8000
# 访问 http://localhost:8000
```

### Android版本构建

```bash
cd android-version
./gradlew assembleDebug
```

---

**设计完成时间**: 2026-03-10 16:10
**下一步**: 实现Web版本核心功能
