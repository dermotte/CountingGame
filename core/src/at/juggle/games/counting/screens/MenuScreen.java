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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import at.juggle.games.counting.CountingGame;
import at.juggle.games.counting.ScreenManager;
import at.juggle.games.counting.gameobjects.Balloon;

/**
 * Created by Mathias Lux, mathias@juggle.at,  on 04.02.2016.
 */
public class MenuScreen extends ScreenAdapter {
    private final SpriteBatch batch;
    private final OrthographicCamera cam;
    private CountingGame parentGame;

    Texture backgroundImage;
    BitmapFont menuFont;

    private final String difficulty = "Herausforderung";
    private final String ende = "Ende";
    private final String ballons = "Ballons";
    private final String zahlen = "Mengen";
    private final String reihen = "Folgen";

    String[] menuStrings = {zahlen, reihen, ballons, difficulty};//, ende};
    int currentMenuItem = 0;

    int numberOfBallonsIndex = 0;
    int[] numberOfBallonsValues = new int[]{10, 8, 6, 4, 16, 14, 12};

    float offsetLeft = CountingGame.GAME_WIDTH / 12, offsetTop = CountingGame.GAME_WIDTH / 12, offsetY = CountingGame.GAME_HEIGHT / 6;

    Sprite startZaehlen,startMengen;
    Balloon[] background = new Balloon[7];
    float buttonSize = 256f;


    public MenuScreen(CountingGame game) {
        this.parentGame = game;

        backgroundImage = parentGame.getAssetManager().get("menu/menu_background.jpg");
        menuFont = parentGame.getAssetManager().get("menu/Ravie_72.fnt");

        startZaehlen = new Sprite(parentGame.getAssetManager().get("sprites/button_count.png", Texture.class));
        startZaehlen.setPosition(offsetLeft, CountingGame.GAME_HEIGHT-offsetTop-buttonSize);
        startZaehlen.setScale(buttonSize / startZaehlen.getWidth());
        startMengen = new Sprite(parentGame.getAssetManager().get("sprites/button_question.png", Texture.class));
        startMengen.setPosition(offsetLeft+3f*buttonSize/2f, CountingGame.GAME_HEIGHT-offsetTop-buttonSize);
        startMengen.setScale(buttonSize / startMengen.getWidth());

        for (int i = 0; i < background.length; i++) {
            String[] fileName = {"sprites/balloon.png", "sprites/balloonblue.png", "sprites/balloongreen.png"};
            background[i] = new Balloon(TextureRegion.split(parentGame.getAssetManager().get(fileName[(int) (Math.random()*3)], Texture.class), 128, 192)[0]);
            background[i].setPosition(CountingGame.GAME_WIDTH/2f + ((float) (Math.random() * CountingGame.GAME_WIDTH/2f)), -((float) (Math.random() * CountingGame.GAME_HEIGHT)));
            background[i].setSpeed((float) Math.random() + 0.5f);
        }


        menuFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // menuFont.getData().setScale(1f);
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

        for (int i = 0; i < background.length; i++) {
            for (int j = 0; j < background.length; j++) {
                if (i!=j && background[i]!=null && background[j]!=null) {
                    Vector2 dist = background[j].dist(background[i]);
                    if (dist.len() <background[j].getHeight()) {
                        float d = (background[i].getHeight()-dist.len())/background[i].getHeight();
                        background[j].setPosition(background[j].getX() + dist.x*d, background[j].getY() + dist.y*d);
                        background[i].setPosition(background[i].getX() - dist.x*d, background[i].getY() - dist.y*d);
                    }
                }

            }
            background[i].setY(background[i].getY()+delta*100f*background[i].getSpeed());
            background[i].draw(batch, delta);
            if (background[i].getY()>CountingGame.GAME_HEIGHT*1.1f)
                background[i].setPosition(CountingGame.GAME_WIDTH/2f + ((float) (Math.random() * CountingGame.GAME_WIDTH/2f)), -((float) (Math.random() * CountingGame.GAME_HEIGHT)));
        }

        // draw buttons ...
        startMengen.draw(batch);
        startZaehlen.draw(batch);

        // draw Strings ...
        for (int i = 2; i < menuStrings.length; i++) {

            if (i == currentMenuItem) menuFont.setColor(0.2f, 0.2f, 0.8f, 1f);
            else menuFont.setColor(0f, 0f, 0f, 1f);
            String menuString = menuStrings[i];
            if (menuString.startsWith(ballons))
                menuString += ": " + getNumberOfBallons() + " max.";
            else if (menuString.startsWith(difficulty))
                menuString += ": " + ((CountingGame.difficulty == 0) ? "einfach" : ((CountingGame.difficulty == 1) ? "mittel" : "schwer"));
            menuFont.draw(batch, menuString, offsetLeft, CountingGame.GAME_HEIGHT - offsetTop - i * offsetY);
        }
        batch.end();
    }

    private void handleInput() {
        // touch
        if (Gdx.input.justTouched()) {
            Vector3 touchWorldCoords = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 1));
            // find the menu item ..
            if (startMengen.getBoundingRectangle().contains(touchWorldCoords.x, touchWorldCoords.y)) {
                parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.CountingGame);
            } else if (startZaehlen.getBoundingRectangle().contains(touchWorldCoords.x, touchWorldCoords.y)) {
                parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.SortingGame);
            }
            for (int i = 2; i < menuStrings.length; i++) {
                if (touchWorldCoords.x > offsetLeft) {
                    float pos = CountingGame.GAME_HEIGHT - offsetTop - i * offsetY;
                    if (touchWorldCoords.y < pos && touchWorldCoords.y > pos - menuFont.getLineHeight()) {
                        // it's there
                        if (menuStrings[i].equals(ende)) {
                            Gdx.app.exit();
                        } else if (menuStrings[i].equals(difficulty)) {
                            CountingGame.difficulty++;
                            CountingGame.difficulty %= 3;
                        } else if (menuStrings[i].equals(zahlen)) {
                            // parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.CountingGame);
                        } else if (menuStrings[i].equals(reihen)) {
                            // parentGame.getScreenManager().setCurrentState(ScreenManager.ScreenState.SortingGame);
                        } else if (menuStrings[i].equals(ballons)) {
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
