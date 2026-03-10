package com.snake3d.game;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL ES渲染器
 * 渲染3D贪吃蛇游戏
 */
public class SnakeRenderer implements GLSurfaceView.Renderer {

    private GameEngine gameEngine;

    // 颜色定义
    private static final float[] SNAKE_HEAD_COLOR = {0.0f, 1.0f, 0.0f, 1.0f}; // 绿色
    private static final float[] SNAKE_BODY_COLOR = {0.0f, 0.8f, 0.0f, 1.0f}; // 深绿色
    private static final float[] FOOD_COLOR = {1.0f, 0.0f, 0.0f, 1.0f}; // 红色
    private static final float[] GROUND_COLOR = {0.18f, 0.2f, 0.21f, 1.0f}; // 深灰色

    // 顶点缓冲
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    // 立方体顶点
    private static final float[] cubeVertices = {
        // 前面
        -0.45f, -0.45f,  0.45f,
         0.45f, -0.45f,  0.45f,
         0.45f,  0.45f,  0.45f,
        -0.45f,  0.45f,  0.45f,
        // 后面
        -0.45f, -0.45f, -0.45f,
        -0.45f,  0.45f, -0.45f,
         0.45f,  0.45f, -0.45f,
         0.45f, -0.45f, -0.45f,
        // 左面
        -0.45f, -0.45f, -0.45f,
        -0.45f, -0.45f,  0.45f,
        -0.45f,  0.45f,  0.45f,
        -0.45f,  0.45f, -0.45f,
        // 右面
         0.45f, -0.45f, -0.45f,
         0.45f,  0.45f, -0.45f,
         0.45f,  0.45f,  0.45f,
         0.45f, -0.45f,  0.45f,
        // 上面
        -0.45f,  0.45f, -0.45f,
        -0.45f,  0.45f,  0.45f,
         0.45f,  0.45f,  0.45f,
         0.45f,  0.45f, -0.45f,
        // 下面
        -0.45f, -0.45f, -0.45f,
         0.45f, -0.45f, -0.45f,
         0.45f, -0.45f,  0.45f,
        -0.45f, -0.45f,  0.45f,
    };

    public SnakeRenderer(GameEngine gameEngine) {
        this.gameEngine = gameEngine;

        // 初始化顶点缓冲
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeVertices);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景颜色
        gl.glClearColor(0.1f, 0.1f, 0.18f, 1.0f);

        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // 启用光照
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);

        // 设置光源参数
        float[] lightPosition = {10.0f, 20.0f, 10.0f, 1.0f};
        float[] lightAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] lightDiffuse = {0.7f, 0.7f, 0.7f, 1.0f};

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);

        // 启用颜色材质
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口
        gl.glViewport(0, 0, width, height);

        // 设置投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float aspect = (float) width / height;
        GLU.gluPerspective(gl, 45.0f, aspect, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 更新游戏状态
        gameEngine.update();

        // 清除缓冲
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // 设置模型视图矩阵
        gl.glLoadIdentity();
        GLU.gluLookAt(gl,
            0.0f, 25.0f, 25.0f,  // 眼睛位置
            0.0f, 0.0f, 0.0f,    // 观察点
            0.0f, 1.0f, 0.0f     // 上方向
        );

        // 绘制地面
        drawGround(gl);

        // 绘制蛇
        drawSnake(gl);

        // 绘制食物
        drawFood(gl);
    }

    private void drawGround(GL10 gl) {
        gl.glPushMatrix();

        gl.glColor4f(GROUND_COLOR[0], GROUND_COLOR[1], GROUND_COLOR[2], GROUND_COLOR[3]);

        // 绘制地面（简单的四边形）
        float size = 10.0f;
        float[] groundVertices = {
            -size, 0.0f, -size,
            size, 0.0f, -size,
            size, 0.0f, size,
            -size, 0.0f, size,
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(groundVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer groundBuffer = bb.asFloatBuffer();
        groundBuffer.put(groundVertices);
        groundBuffer.position(0);

        gl.glDisable(GL10.GL_LIGHTING);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, groundBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnable(GL10.GL_LIGHTING);

        gl.glPopMatrix();
    }

    private void drawSnake(GL10 gl) {
        for (int i = 0; i < gameEngine.getSnake().size(); i++) {
            GameEngine.SnakeSegment seg = gameEngine.getSnake().get(i);

            gl.glPushMatrix();
            gl.glTranslatef(seg.x, 0.5f, seg.z);

            // 设置颜色（头部和身体不同）
            if (i == 0) {
                gl.glColor4f(SNAKE_HEAD_COLOR[0], SNAKE_HEAD_COLOR[1],
                           SNAKE_HEAD_COLOR[2], SNAKE_HEAD_COLOR[3]);
            } else {
                gl.glColor4f(SNAKE_BODY_COLOR[0], SNAKE_BODY_COLOR[1],
                           SNAKE_BODY_COLOR[2], SNAKE_BODY_COLOR[3]);
            }

            drawCube(gl);

            gl.glPopMatrix();
        }
    }

    private void drawFood(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(gameEngine.getFoodX(), 0.5f, gameEngine.getFoodZ());
        gl.glColor4f(FOOD_COLOR[0], FOOD_COLOR[1], FOOD_COLOR[2], FOOD_COLOR[3]);

        // 简化：用立方体表示食物
        drawCube(gl);

        gl.glPopMatrix();
    }

    private void drawCube(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        // 绘制6个面
        for (int i = 0; i < 6; i++) {
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, i * 4, 4);
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
