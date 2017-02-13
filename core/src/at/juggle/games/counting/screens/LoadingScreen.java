/*
 * This project and its source code is licensed under
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * Copyright (c) 2017 Mathias Lux, mathias@juggle.at
 */

package at.juggle.games.counting.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import at.juggle.games.counting.CountingGame;
import at.juggle.games.counting.ScreenManager;

/**
 * Created by Mathias Lux, mathias@juggle.at,  on 04.02.2016.
 */
public class LoadingScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private CountingGame parentGame;
    private Texture loadingSheet;
    private TextureRegion[] loadingFrames;

    private int animationFrame = 0;
    private float animationFrameShownFor = 0.05f; // how long is each frame shown ..
    private float animationFrameShownAlready = 0f;

    public LoadingScreen(CountingGame game) {
        this.parentGame = game;
        // this is the only asset not loaded by the AssetManager.
        loadingSheet = new Texture(Gdx.files.internal("loading/preloader_180x40.png"));
        loadingFrames = TextureRegion.split(loadingSheet, 180, 40)[0];

        // Create camera taht projects the game onto the actual screen size.
        cam = new OrthographicCamera(CountingGame.GAME_WIDTH, CountingGame.GAME_HEIGHT);

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        if (parentGame.getAssetManager().update()) {
            parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Menu);
        }
        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // determine the current frame:
        animationFrameShownAlready += delta;
        if (animationFrameShownAlready > animationFrameShownFor) {
            animationFrame = (animationFrame + 1) % loadingFrames.length;
            animationFrameShownAlready = 0f;
        }

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(loadingFrames[animationFrame], CountingGame.GAME_WIDTH/2 - loadingFrames[animationFrame].getRegionWidth()/2, CountingGame.GAME_HEIGHT/2-loadingFrames[animationFrame].getRegionHeight()/2);
        batch.end();
    }


}
