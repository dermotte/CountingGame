/*
 * This project and its source code is licensed under
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * Copyright (c) 2017 Mathias Lux, mathias@juggle.at
 */

package at.juggle.games.counting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;
import java.util.List;

import at.juggle.games.counting.gameobjects.Balloon;

/**
 * Created by mlux on 13.02.2017.
 */

public class CountingGameModel {
    private List<Balloon> balloonList = new LinkedList<Balloon>();
    private boolean dirty = true;

    public enum InputResult {
        Pop, Button, Change, Nothing;
    }

    GlyphLayout layout = new GlyphLayout();
    private final TextureRegion[] spriteAnimRed, spriteAnimBlue, spriteAnimGreen;
    Balloon[] goodBallons;
    Balloon[] badBallons;


    int numberOfSprites = 8;
    int min = 1, max = 12;
    float animationTime = 0f;
    float offset = 128;
    int[] answers = new int[]{1, 2, 3};
    BitmapFont buttonFont = null;
    private int posButtonsY = 192;
    private boolean answerIsGiven = false;

    public CountingGameModel(int min, int max, TextureRegion[] balloonRedSprite, TextureRegion[] balloonBlueSprite, TextureRegion[] balloonGreenSprite, BitmapFont buttonFont) {
        this.min = min;
        this.max = max;
        this.spriteAnimRed = balloonRedSprite;
        this.spriteAnimBlue = balloonBlueSprite;
        this.spriteAnimGreen = balloonGreenSprite;
        this.buttonFont = buttonFont;
        resetGameState();
    }

    public void resetGameState() {
        numberOfSprites = getRandomNumberOfBallons();

        goodBallons = new Balloon[numberOfSprites];
        if (CountingGame.difficulty==0)
            badBallons = new Balloon[0];
        else if (CountingGame.difficulty==1)
            badBallons = new Balloon[getRandomNumberOfBallons() >> 2];
        else if (CountingGame.difficulty==2)
            badBallons = new Balloon[getRandomNumberOfBallons()];

        for (int i = 0; i < goodBallons.length; i++) {
            goodBallons[i] = new Balloon(spriteAnimRed);
            goodBallons[i].setX((float) ((CountingGame.GAME_WIDTH - spriteAnimRed[0].getRegionWidth() - 2 * offset) * Math.random()) + offset);
            goodBallons[i].setY((float) ((CountingGame.GAME_HEIGHT - spriteAnimRed[0].getRegionHeight() - 3 * offset) * Math.random()) + 2 * offset);
        }

        for (int i = 0; i < badBallons.length; i++) {
            badBallons[i] = new Balloon(spriteAnimBlue);
            badBallons[i].setX((float) ((CountingGame.GAME_WIDTH - spriteAnimBlue[0].getRegionWidth() - 2 * offset) * Math.random()) + offset);
            badBallons[i].setY((float) ((CountingGame.GAME_HEIGHT - spriteAnimBlue[0].getRegionHeight() - 3 * offset) * Math.random()) + 2 * offset);
        }
        // random numbers ...
        answers[0] = numberOfSprites;
        answers[1] = getRandomNumberOfBallons();
        while (answers[0] == answers[1]) answers[1] = getRandomNumberOfBallons();
        answers[2] = getRandomNumberOfBallons();
        while (answers[0] == answers[2] || answers[1] == answers[2])
            answers[2] = getRandomNumberOfBallons();
        // shuffling ...
        int count = 50;
        while (count-- > 0) {
            int i1 = (int) (Math.random() * answers.length);
            int i2 = (int) (Math.random() * answers.length);
            int tmp = answers[i1];
            answers[i1] = answers[i2];
            answers[i2] = tmp;
        }

        answerIsGiven = false;
        dirty = true;
    }

    private int getRandomNumberOfBallons() {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public void drawGameState(SpriteBatch batch, float delta, BitmapFont buttonFont) {
        animationTime += delta * 10;
        balloonList = getBalloonList();
        for (int i = 0; i < balloonList.size(); i++) {
            for (int j = 0; j <  balloonList.size(); j++) {
                if (i != j) { // check for collision
                    float distX = (balloonList.get(i).getX() - balloonList.get(j).getX());
                    float distY = (balloonList.get(i).getY() - balloonList.get(j).getY());

                    if (Math.abs(distX) < balloonList.get(i).getWidth() && Math.abs(distY) < balloonList.get(i).getHeight()) { // it's a collision
                        balloonList.get(i).setPosition(balloonList.get(i).getX() + distX * 0.05f, balloonList.get(i).getY() + distY * 0.05f);
                    }

                }
            }
            balloonList.get(i).draw(batch, delta);
            // batch.draw(spriteAnimRed[((int) (animationTime % spriteAnimRed.length))], sprite.getX(), sprite.getY());
        }

        for (int i = 0; i < answers.length; i++) {
            String answer = Integer.toString(answers[i]);
            layout.setText(buttonFont, answer);
            if (answerIsGiven && answers[i] == numberOfSprites) buttonFont.setColor(Color.GREEN);
            else buttonFont.setColor(Color.BLACK);
            float posX = CountingGame.GAME_WIDTH / 4 * (i + 1) - layout.width / 2;
            buttonFont.draw(batch, answer, posX, posButtonsY);
        }

    }

    public InputResult handleTouch(Vector3 p) {
        Vector2 t = new Vector2(p.x, p.y);
        layout.setText(buttonFont, Integer.toString(numberOfSprites));

        if (goodBallons.length < 1) return InputResult.Change;
        if (t.y < posButtonsY + layout.height && !answerIsGiven) { // it's a button press
            // check for the answers:
            for (int i = 0; i < answers.length; i++) {
                String answer = Integer.toString(answers[i]);
                layout.setText(buttonFont, answer);
                float posX = CountingGame.GAME_WIDTH / 4 * (i + 1) - layout.width / 2;
                if (Math.abs(posX - t.x) < layout.width) { // got it!
                    if (answers[i] == numberOfSprites) answerIsGiven = true;
                }
            }
            return answerIsGiven?InputResult.Button:InputResult.Nothing;
        } else if (answerIsGiven) { // check for the balloons ...
            int toRemove = -1;
            float dist = Float.MAX_VALUE;
            for (int i = 0; i < goodBallons.length; i++) {
                Sprite sp = goodBallons[i];
                Vector2 s = new Vector2(sp.getX() + sp.getWidth() / 2, sp.getY() + sp.getHeight() / 2);

                // take the nearest one to remove ...
                if (t.dst(s) < Math.min(goodBallons[i].getWidth(), dist)) { // it's a collision
                    toRemove = i;
                    dist = t.dst(s);
                }
            }
            if (toRemove > -1) {
                Balloon[] temp = new Balloon[goodBallons.length - 1];
                int count = 0;
                for (int i = 0; i < goodBallons.length; i++) {
                    if (i != toRemove) temp[count++] = goodBallons[i];
                }
                goodBallons = temp;
                dirty = true;
                return InputResult.Pop;
            }
        } else {
            return InputResult.Nothing;
        }
        return InputResult.Nothing;
    }

    private List<Balloon> getBalloonList() {
        if (dirty) {
            balloonList.clear();
            for (int i = 0; i < goodBallons.length; i++)
                balloonList.add(goodBallons[i]);

            for (int i = 0; i < badBallons.length; i++)
                balloonList.add(badBallons[i]);
            dirty = false;
        }
        return balloonList;
    }

    public int getNumberOfSprites() {
        return numberOfSprites;
    }
}
