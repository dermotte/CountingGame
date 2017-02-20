/*
 * This project and its source code is licensed under
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * Copyright (c) 2017 Mathias Lux, mathias@juggle.at
 */

package at.juggle.games.counting;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class CountingGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private AssetManager assMan;
	private ScreenManager screenManager;
	private SoundManager soundManager;

	// gives the original size for all screen working with the scaling orthographic camera
	// set in DesktopLauncher to any resolution and it will be scaled automatically.
	public static final int GAME_WIDTH = 1920;
	public static final int GAME_HEIGHT = 1080;

	public static int numberOfBalloons = 10;
    public static int difficulty = 0; // 0 is easy, 1 is medium, 2 is hard.


	@Override
	public void create() {
		screenManager = new ScreenManager(this);
		soundManager = new SoundManager(this);

		// LOAD ASSETS HERE ...
		// Loading screen will last until the last one is loaded.
		assMan = new AssetManager();
		// for the menu
		assMan.load("menu/Ravie_42.fnt", BitmapFont.class);
		assMan.load("menu/Ravie_72.fnt", BitmapFont.class);
		assMan.load("menu/menu_background.jpg", Texture.class);
		// for the credits
		assMan.load("credits/gradient_top.png", Texture.class);
		assMan.load("credits/gradient_bottom.png", Texture.class);
		assMan.load("sprites/balloon.png", Texture.class);
		assMan.load("sprites/balloonblue.png", Texture.class);
		assMan.load("sprites/balloongreen.png", Texture.class);
		assMan.load("sprites/balloonpart.png", Texture.class);
		assMan.load("sprites/xplode", ParticleEffect.class);
		assMan.load("sprites/button.png", Texture.class);
		assMan.load("sprites/button_count.png", Texture.class);
		assMan.load("sprites/button_question.png", Texture.class);
		// for the sounds
		soundManager.preload(assMan);

        Gdx.input.setInputProcessor(new GestureDetector(new GestureDetector.GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                screenManager.setCurrentState(ScreenManager.ScreenState.Menu);
                return true;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                return false;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                return false;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {

            }
        }));

	}

	@Override
	public void render() {
		// make sure the sound stays in sync.
		soundManager.handle(Gdx.graphics.getDeltaTime());
		// render the screen.
		screenManager.getCurrentScreen().render(Gdx.graphics.getDeltaTime());
	}

	public AssetManager getAssetManager() {
		return assMan;
	}

	public ScreenManager getScreenManager() {
		return screenManager;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}
}
