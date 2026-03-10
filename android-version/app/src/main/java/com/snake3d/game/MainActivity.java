package com.snake3d.game;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * 3D贪吃蛇游戏 - Android版本
 * 主Activity
 */
public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private SnakeRenderer renderer;
    private GameEngine gameEngine;

    private float touchStartX;
    private float touchStartY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建OpenGL ES视图
        glSurfaceView = new GLSurfaceView(this);

        // 创建游戏引擎
        gameEngine = new GameEngine();

        // 创建渲染器
        renderer = new SnakeRenderer(gameEngine);

        // 设置渲染器
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // 设置触摸监听
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchStartX = event.getX();
                        touchStartY = event.getY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        float touchEndX = event.getX();
                        float touchEndY = event.getY();

                        float deltaX = touchEndX - touchStartX;
                        float deltaY = touchEndY - touchStartY;

                        // 判断滑动方向
                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            // 水平滑动
                            if (deltaX > 0) {
                                gameEngine.setDirection(1, 0); // 右
                            } else {
                                gameEngine.setDirection(-1, 0); // 左
                            }
                        } else {
                            // 垂直滑动
                            if (deltaY > 0) {
                                gameEngine.setDirection(0, 1); // 下
                            } else {
                                gameEngine.setDirection(0, -1); // 上
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}
