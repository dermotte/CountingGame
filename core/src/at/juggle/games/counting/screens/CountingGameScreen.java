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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import at.juggle.games.counting.CountingGame;
import at.juggle.games.counting.CountingGameModel;
import at.juggle.games.counting.ScreenManager;

/**
 * Created by Mathias Lux, mathias@juggle.at,  on 04.02.2016.
 */
public class CountingGameScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private CountingGame parentGame;
    private CountingGameModel model;

    Texture backgroundImage, gradientTop, gradientBottom;
    BitmapFont buttonFont;

    private float moveY, animationTime;
    private final TextureRegion[] balloonRedSprite, balloonBlueSprite, balloonGreenSprite;
    private final ParticleEffect xplode;


    public CountingGameScreen(CountingGame game) {
        this.parentGame = game;
        Texture balloonRedSheet = parentGame.getAssetManager().get("sprites/balloon.png");
        Texture balloonBlueSheet = parentGame.getAssetManager().get("sprites/balloonblue.png");
        Texture balloonGreenSheet = parentGame.getAssetManager().get("sprites/balloongreen.png");
        balloonRedSprite = TextureRegion.split(balloonRedSheet, 128, 192)[0];
        balloonBlueSprite = TextureRegion.split(balloonBlueSheet, 128, 192)[0];
        balloonGreenSprite = TextureRegion.split(balloonGreenSheet, 128, 192)[0];

        buttonFont = parentGame.getAssetManager().get("menu/Ravie_72.fnt");
        backgroundImage = parentGame.getAssetManager().get("menu/menu_background.jpg");
        xplode = parentGame.getAssetManager().get("sprites/xplode");

        // Create camera that projects the game onto the actual screen size.
        cam = new OrthographicCamera(CountingGame.GAME_WIDTH, CountingGame.GAME_HEIGHT);

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
        model = new CountingGameModel(1, CountingGame.numberOfBalloons, balloonRedSprite, balloonBlueSprite, balloonGreenSprite, buttonFont);
        model.resetGameState();
    }

    @Override
    public void render(float delta) {
        moveY += delta * 12;
        animationTime += delta * 8;
        handleInput();
        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);


        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // draw bgImage
        batch.draw(backgroundImage, 0, 0, CountingGame.GAME_WIDTH, CountingGame.GAME_HEIGHT);

        model.drawGameState(batch, delta, buttonFont);
        xplode.draw(batch, delta);

        // draw moving text:
//        for (int i = 0; i < credits.length; i++) {
//            creditsFont.draw(batch, credits[i], CountingGame.GAME_WIDTH/8, moveY - i*creditsFont.getLineHeight()*1.5f);
//        }


        // draw gradient

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ) {
            parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Menu);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            // model.resetGameState();
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchWorldCoords = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1));
            CountingGameModel.InputResult inputResult = model.handleTouch(touchWorldCoords);
            if (inputResult == CountingGameModel.InputResult.Change) {
                model.resetGameState();
            } else if (inputResult == CountingGameModel.InputResult.Button) {
                // Todo .. play sound
                parentGame.getSoundManager().playEvent(model.getNumberOfSprites()+"");
            } else if (inputResult == CountingGameModel.InputResult.Pop) {
                xplode.setPosition(touchWorldCoords.x, touchWorldCoords.y);
                xplode.reset();
                String event = "blob0" + ((int) (Math.random() * 4) + 1);
                parentGame.getSoundManager().playEvent(event);
            }
        }

    }


}
