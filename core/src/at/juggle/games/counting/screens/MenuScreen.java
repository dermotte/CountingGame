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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import at.juggle.games.counting.CountingGame;
import at.juggle.games.counting.ScreenManager;

/**
 * Created by Mathias Lux, mathias@juggle.at,  on 04.02.2016.
 */
public class MenuScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private CountingGame parentGame;

    Texture backgroundImage;
    BitmapFont menuFont;

    String[] menuStrings = {"Play", "Balloons", "Difficulty", "Exit"};
    int currentMenuItem = 0;

    int numberOfBallonsIndex = 0;
    int[] numberOfBallonsValues = new int[]{10, 8, 6, 4, 16, 14, 12};

    float offsetLeft = CountingGame.GAME_WIDTH / 12, offsetTop = CountingGame.GAME_WIDTH / 12, offsetY = CountingGame.GAME_HEIGHT / 6;


    public MenuScreen(CountingGame game) {
        this.parentGame = game;

        backgroundImage = parentGame.getAssetManager().get("menu/menu_background.jpg");
        menuFont = parentGame.getAssetManager().get("menu/Ravie_72.fnt");
        menuFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Create camera that projects the game onto the actual screen size.
        cam = new OrthographicCamera(CountingGame.GAME_WIDTH, CountingGame.GAME_HEIGHT);

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        handleInput();
        // camera:
        cam.update();
        batch.setProjectionMatrix(cam.combined);


        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // draw bgImage ...
        batch.draw(backgroundImage, 0, 0, CountingGame.GAME_WIDTH, CountingGame.GAME_HEIGHT);
        // draw Strings ...
        for (int i = 0; i < menuStrings.length; i++) {
            if (i == currentMenuItem) menuFont.setColor(0.2f, 0.2f, 0.8f, 1f);
            else menuFont.setColor(0f, 0f, 0f, 1f);
            String menuString = menuStrings[i];
            if (menuString.startsWith("Balloons"))
                menuString += ": " + getNumberOfBallons() + " max.";
            else if (menuString.startsWith("Difficulty"))
                menuString += ": " + ((CountingGame.difficulty == 0) ? "easy" : ((CountingGame.difficulty == 1) ? "medium" : "hard"));
            menuFont.draw(batch, menuString, offsetLeft, CountingGame.GAME_HEIGHT - offsetTop - i * offsetY);
        }
        batch.end();
    }

    private void handleInput() {
        // keys ...
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            currentMenuItem = (currentMenuItem + 1) % menuStrings.length;
            parentGame.getSoundManager().playEvent("blip");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentMenuItem > 0) currentMenuItem = (currentMenuItem - 1);
            else currentMenuItem = menuStrings.length - 1;
            parentGame.getSoundManager().playEvent("blip");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            System.out.println("Next level in music ...");
            parentGame.getSoundManager().addLevel(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.out.println("Previous level in music ...");
            parentGame.getSoundManager().addLevel(-1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (menuStrings[currentMenuItem].equals("Exit")) {
                Gdx.app.exit();
                parentGame.getSoundManager().playEvent("explode");
            } else if (menuStrings[currentMenuItem].equals("Difficulty")) {
                CountingGame.difficulty++;
                CountingGame.difficulty %= 3;
            } else if (menuStrings[currentMenuItem].equals("Play")) {
                parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Game);
            } else if (menuStrings[currentMenuItem].equals("Balloons")) {
                numberOfBallonsIndex++;
                numberOfBallonsIndex = numberOfBallonsIndex % numberOfBallonsValues.length;
                CountingGame.numberOfBalloons = getNumberOfBallons();
            }
        }
        // touch
        if (Gdx.input.justTouched()) {
            Vector3 touchWorldCoords = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1));
            // find the menu item ..
            for (int i = 0; i < menuStrings.length; i++) {
                if (touchWorldCoords.x > offsetLeft) {
                    float pos = CountingGame.GAME_HEIGHT - offsetTop - i * offsetY;
                    if (touchWorldCoords.y < pos && touchWorldCoords.y > pos - menuFont.getLineHeight()) {
                        // it's there
                        if (menuStrings[i].equals("Exit")) {
                            Gdx.app.exit();
                        } else if (menuStrings[i].equals("Difficulty")) {
                            CountingGame.difficulty++;
                            CountingGame.difficulty %= 3;
                        } else if (menuStrings[i].equals("Play")) {
                            parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.Game);
                        } else if (menuStrings[i].equals("Balloons")) {
                            numberOfBallonsIndex++;
                            numberOfBallonsIndex = numberOfBallonsIndex % numberOfBallonsValues.length;
                            CountingGame.numberOfBalloons = getNumberOfBallons();
                        }
                    }
                }

            }
        }
    }

    public int getNumberOfBallons() {
        return numberOfBallonsValues[numberOfBallonsIndex];
    }

}
