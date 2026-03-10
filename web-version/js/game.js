/**
 * 3D贪吃蛇游戏 - 主游戏逻辑
 * 使用Three.js渲染
 */

// 游戏配置
const CONFIG = {
    gridSize: 20,
    cellSize: 1,
    initialSpeed: 5,
    speedIncrement: 0.5,
    maxSpeed: 15
};

// 游戏状态
class GameState {
    constructor() {
        this.score = 0;
        this.level = 1;
        this.highScore = parseInt(localStorage.getItem('snakeHighScore')) || 0;
        this.isPaused = false;
        this.isGameOver = false;
    }

    updateScore(points) {
        this.score += points;
        if (this.score > this.highScore) {
            this.highScore = this.score;
            localStorage.setItem('snakeHighScore', this.highScore);
        }
    }

    nextLevel() {
        this.level++;
    }

    reset() {
        this.score = 0;
        this.level = 1;
        this.isPaused = false;
        this.isGameOver = false;
    }
}

// 蛇类
class Snake {
    constructor() {
        this.segments = [{x: 0, y: 0, z: 0}];
        this.direction = {x: 1, y: 0, z: 0};
        this.nextDirection = {x: 1, y: 0, z: 0};
        this.speed = CONFIG.initialSpeed;
    }

    move() {
        this.direction = {...this.nextDirection};

        const head = this.segments[0];
        const newHead = {
            x: head.x + this.direction.x,
            y: head.y + this.direction.y,
            z: head.z + this.direction.z
        };

        this.segments.unshift(newHead);
        this.segments.pop();
    }

    grow() {
        const tail = this.segments[this.segments.length - 1];
        this.segments.push({...tail});
    }

    setDirection(dir) {
        // 防止反向移动
        if (dir.x !== -this.direction.x || dir.z !== -this.direction.z) {
            this.nextDirection = dir;
        }
    }

    reset() {
        this.segments = [{x: 0, y: 0, z: 0}];
        this.direction = {x: 1, y: 0, z: 0};
        this.nextDirection = {x: 1, y: 0, z: 0};
        this.speed = CONFIG.initialSpeed;
    }
}

// 食物生成器
class FoodGenerator {
    constructor() {
        this.position = {x: 5, y: 0, z: 5};
    }

    generate(snakeSegments) {
        let position;
        let attempts = 0;
        const maxAttempts = 100;

        do {
            position = {
                x: Math.floor(Math.random() * CONFIG.gridSize) - CONFIG.gridSize/2,
                y: 0,
                z: Math.floor(Math.random() * CONFIG.gridSize) - CONFIG.gridSize/2
            };
            attempts++;
        } while (this.collidesWithSnake(position, snakeSegments) && attempts < maxAttempts);

        this.position = position;
    }

    collidesWithSnake(pos, segments) {
        return segments.some(seg => seg.x === pos.x && seg.z === pos.z);
    }
}

// 碰撞检测器
class CollisionDetector {
    checkWallCollision(head) {
        const halfGrid = CONFIG.gridSize / 2;
        return Math.abs(head.x) >= halfGrid || Math.abs(head.z) >= halfGrid;
    }

    checkSelfCollision(head, segments) {
        return segments.slice(1).some(seg => seg.x === head.x && seg.z === head.z);
    }

    checkFoodCollision(head, food) {
        return head.x === food.x && head.z === food.z;
    }
}

// 3D渲染器
class Renderer {
    constructor() {
        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color(0x1a1a2e);

        // 相机设置
        this.camera = new THREE.PerspectiveCamera(
            75,
            window.innerWidth / window.innerHeight,
            0.1,
            1000
        );
        this.camera.position.set(0, 25, 25);
        this.camera.lookAt(0, 0, 0);

        // 渲染器
        this.renderer = new THREE.WebGLRenderer({antialias: true});
        this.renderer.setSize(window.innerWidth, window.innerHeight);
        this.renderer.shadowMap.enabled = true;
        this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        document.getElementById('game-container').appendChild(this.renderer.domElement);

        // 光照
        this.setupLights();

        // 地面
        this.createGround();

        // 网格辅助线
        this.createGridHelper();

        // 存储mesh引用
        this.snakeMeshes = [];
        this.foodMesh = null;

        // 窗口大小调整
        window.addEventListener('resize', () => this.onWindowResize());
    }

    setupLights() {
        // 环境光
        const ambientLight = new THREE.AmbientLight(0x404040, 0.5);
        this.scene.add(ambientLight);

        // 主光源
        const mainLight = new THREE.DirectionalLight(0xffffff, 1);
        mainLight.position.set(10, 20, 10);
        mainLight.castShadow = true;
        mainLight.shadow.mapSize.width = 2048;
        mainLight.shadow.mapSize.height = 2048;
        this.scene.add(mainLight);

        // 补光
        const fillLight = new THREE.DirectionalLight(0x8888ff, 0.3);
        fillLight.position.set(-10, 10, -10);
        this.scene.add(fillLight);
    }

    createGround() {
        const groundGeometry = new THREE.PlaneGeometry(CONFIG.gridSize, CONFIG.gridSize);
        const groundMaterial = new THREE.MeshPhongMaterial({
            color: 0x2d3436,
            side: THREE.DoubleSide
        });
        this.ground = new THREE.Mesh(groundGeometry, groundMaterial);
        this.ground.rotation.x = Math.PI / 2;
        this.ground.receiveShadow = true;
        this.scene.add(this.ground);
    }

    createGridHelper() {
        const gridHelper = new THREE.GridHelper(CONFIG.gridSize, CONFIG.gridSize, 0x444444, 0x222222);
        this.scene.add(gridHelper);
    }

    renderSnake(segments) {
        // 清除旧的mesh
        this.snakeMeshes.forEach(mesh => this.scene.remove(mesh));
        this.snakeMeshes = [];

        // 创建新的mesh
        segments.forEach((seg, index) => {
            const geometry = new THREE.BoxGeometry(
                CONFIG.cellSize * 0.9,
                CONFIG.cellSize * 0.9,
                CONFIG.cellSize * 0.9
            );

            const material = new THREE.MeshPhongMaterial({
                color: index === 0 ? 0x00ff00 : 0x00cc00,
                emissive: index === 0 ? 0x003300 : 0x002200
            });

            const cube = new THREE.Mesh(geometry, material);
            cube.position.set(seg.x, seg.y + 0.5, seg.z);
            cube.castShadow = true;
            cube.receiveShadow = true;

            this.scene.add(cube);
            this.snakeMeshes.push(cube);
        });
    }

    renderFood(position) {
        if (this.foodMesh) {
            this.scene.remove(this.foodMesh);
        }

        const geometry = new THREE.SphereGeometry(CONFIG.cellSize * 0.4, 32, 32);
        const material = new THREE.MeshPhongMaterial({
            color: 0xff0000,
            emissive: 0x330000
        });

        this.foodMesh = new THREE.Mesh(geometry, material);
        this.foodMesh.position.set(position.x, position.y + 0.5, position.z);
        this.foodMesh.castShadow = true;

        this.scene.add(this.foodMesh);
    }

    onWindowResize() {
        this.camera.aspect = window.innerWidth / window.innerHeight;
        this.camera.updateProjectionMatrix();
        this.renderer.setSize(window.innerWidth, window.innerHeight);
    }

    render() {
        this.renderer.render(this.scene, this.camera);
    }
}

// 主游戏类
class Game {
    constructor() {
        this.state = new GameState();
        this.snake = new Snake();
        this.food = new FoodGenerator();
        this.collision = new CollisionDetector();
        this.renderer = new Renderer();

        this.lastTime = 0;
        this.accumulator = 0;

        this.setupEventListeners();
        this.updateUI();
    }

    setupEventListeners() {
        // 键盘控制
        document.addEventListener('keydown', (e) => this.handleKeyDown(e));

        // 开始按钮
        document.getElementById('start-btn').addEventListener('click', () => this.start());

        // 重新开始按钮
        document.getElementById('restart-btn').addEventListener('click', () => this.restart());
    }

    handleKeyDown(e) {
        if (this.state.isGameOver) return;

        switch(e.key) {
            case 'ArrowUp':
            case 'w':
            case 'W':
                this.snake.setDirection({x: 0, y: 0, z: -1});
                break;
            case 'ArrowDown':
            case 's':
            case 'S':
                this.snake.setDirection({x: 0, y: 0, z: 1});
                break;
            case 'ArrowLeft':
            case 'a':
            case 'A':
                this.snake.setDirection({x: -1, y: 0, z: 0});
                break;
            case 'ArrowRight':
            case 'd':
            case 'D':
                this.snake.setDirection({x: 1, y: 0, z: 0});
                break;
            case ' ':
                this.togglePause();
                break;
        }
    }

    start() {
        document.getElementById('start-btn').style.display = 'none';
        this.food.generate(this.snake.segments);
        this.gameLoop();
    }

    restart() {
        this.state.reset();
        this.snake.reset();
        this.food.generate(this.snake.segments);

        document.getElementById('game-over').style.display = 'none';
        this.updateUI();
        this.gameLoop();
    }

    togglePause() {
        this.state.isPaused = !this.state.isPaused;
        if (!this.state.isPaused) {
            this.lastTime = performance.now();
            this.gameLoop();
        }
    }

    update(currentTime) {
        const deltaTime = currentTime - this.lastTime;
        this.lastTime = currentTime;
        this.accumulator += deltaTime;

        const stepTime = 1000 / this.snake.speed;

        while (this.accumulator >= stepTime) {
            this.snake.move();
            this.checkCollisions();
            this.accumulator -= stepTime;
        }
    }

    checkCollisions() {
        const head = this.snake.segments[0];

        // 撞墙
        if (this.collision.checkWallCollision(head)) {
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
            this.state.updateScore(10);
            this.food.generate(this.snake.segments);

            // 每50分升一级
            if (this.state.score % 50 === 0) {
                this.state.nextLevel();
                if (this.snake.speed < CONFIG.maxSpeed) {
                    this.snake.speed += CONFIG.speedIncrement;
                }
            }

            this.updateUI();
        }
    }

    render() {
        this.renderer.renderSnake(this.snake.segments);
        this.renderer.renderFood(this.food.position);
        this.renderer.render();
    }

    updateUI() {
        document.getElementById('score').textContent = this.state.score;
        document.getElementById('high-score').textContent = this.state.highScore;
        document.getElementById('level').textContent = this.state.level;
    }

    gameOver() {
        this.state.isGameOver = true;
        document.getElementById('final-score').textContent = this.state.score;
        document.getElementById('game-over').style.display = 'block';
    }

    gameLoop(currentTime = 0) {
        if (this.state.isGameOver || this.state.isPaused) return;

        if (currentTime === 0) {
            this.lastTime = performance.now();
        }

        this.update(currentTime);
        this.render();

        requestAnimationFrame((time) => this.gameLoop(time));
    }
}

// 启动游戏
window.addEventListener('DOMContentLoaded', () => {
    new Game();
});
