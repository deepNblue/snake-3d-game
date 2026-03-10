package com.snake3d.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * 游戏引擎
 * 处理游戏逻辑
 */
public class GameEngine {

    // 游戏配置
    private static final int GRID_SIZE = 20;
    private static final int INITIAL_SPEED = 5;

    // 蛇的段
    private ArrayList<SnakeSegment> snake;
    private int directionX = 1;
    private int directionZ = 0;

    // 食物位置
    private int foodX;
    private int foodZ;

    // 游戏状态
    private int score = 0;
    private int level = 1;
    private float speed = INITIAL_SPEED;
    private boolean isGameOver = false;

    // 随机数生成器
    private Random random;

    public GameEngine() {
        random = new Random();
        reset();
    }

    public void reset() {
        snake = new ArrayList<>();
        snake.add(new SnakeSegment(0, 0));

        directionX = 1;
        directionZ = 0;

        score = 0;
        level = 1;
        speed = INITIAL_SPEED;
        isGameOver = false;

        spawnFood();
    }

    public void setDirection(int dx, int dz) {
        // 防止反向移动
        if (dx != -directionX || dz != -directionZ) {
            directionX = dx;
            directionZ = dz;
        }
    }

    public void update() {
        if (isGameOver) return;

        // 移动蛇
        SnakeSegment head = snake.get(0);
        SnakeSegment newHead = new SnakeSegment(
            head.x + directionX,
            head.z + directionZ
        );

        // 检查墙壁碰撞
        if (Math.abs(newHead.x) >= GRID_SIZE / 2 ||
            Math.abs(newHead.z) >= GRID_SIZE / 2) {
            isGameOver = true;
            return;
        }

        // 检查自身碰撞
        for (int i = 1; i < snake.size(); i++) {
            SnakeSegment seg = snake.get(i);
            if (seg.x == newHead.x && seg.z == newHead.z) {
                isGameOver = true;
                return;
            }
        }

        snake.add(0, newHead);

        // 检查食物碰撞
        if (newHead.x == foodX && newHead.z == foodZ) {
            score += 10;
            spawnFood();

            // 升级
            if (score % 50 == 0) {
                level++;
                speed = Math.min(speed + 0.5f, 15);
            }
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void spawnFood() {
        boolean valid;
        do {
            valid = true;
            foodX = random.nextInt(GRID_SIZE) - GRID_SIZE / 2;
            foodZ = random.nextInt(GRID_SIZE) - GRID_SIZE / 2;

            // 确保食物不在蛇身上
            for (SnakeSegment seg : snake) {
                if (seg.x == foodX && seg.z == foodZ) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
    }

    public ArrayList<SnakeSegment> getSnake() {
        return snake;
    }

    public int getFoodX() {
        return foodX;
    }

    public int getFoodZ() {
        return foodZ;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * 蛇的段
     */
    public static class SnakeSegment {
        public int x;
        public int z;

        public SnakeSegment(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}
