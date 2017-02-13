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

/**
 * Created by mlux on 13.02.2017.
 */

public class GameModel {
    public enum InputResult {
        Pop, Button, Change, Nothing;
    }

    GlyphLayout layout = new GlyphLayout();
    private final TextureRegion[] spriteAnim;
    Sprite[] sprites;
    int numberOfSprites = 8;
    int min = 1, max = 12;
    float animationTime = 0f;
    float offset = 128;
    int[] answers = new int[]{1, 2, 3};
    BitmapFont buttonFont = null;
    private int posButtonsY = 128;
    private boolean answerIsGiven = false;

    public GameModel(int min, int max, TextureRegion[] spriteAnim, BitmapFont buttonFont) {
        this.min = min;
        this.max = max;
        this.spriteAnim = spriteAnim;
        this.buttonFont = buttonFont;
        resetGameState();
    }

    public void resetGameState() {
        numberOfSprites = (int) (Math.random() * (max - min)) + min;
        sprites = new Sprite[numberOfSprites];
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(spriteAnim[0]);
            sprites[i].setX((float) ((CountingGame.GAME_WIDTH - spriteAnim[0].getRegionWidth() - 2 * offset) * Math.random()) + offset);
            sprites[i].setY((float) ((CountingGame.GAME_HEIGHT - spriteAnim[0].getRegionHeight() - 3 * offset) * Math.random()) + 2 * offset);
        }
        // random numbers ...
        answers[0] = numberOfSprites;
        answers[1] = (int) (Math.random() * (max - min)) + min;
        while (answers[0] == answers[1]) answers[1] = (int) (Math.random() * (max - min)) + min;
        answers[2] = (int) (Math.random() * (max - min)) + min;
        while (answers[0] == answers[2] || answers[1] == answers[2])
            answers[2] = (int) (Math.random() * (max - min)) + min;
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
    }

    public void drawGameState(SpriteBatch batch, float delta, BitmapFont buttonFont) {
        animationTime += delta * 10;
        for (int i = 0; i < sprites.length; i++) {
            for (int j = 0; j < sprites.length; j++) {
                if (i != j) { // check for collision
                    float distX = (sprites[i].getX() - sprites[j].getX());
                    float distY = (sprites[i].getY() - sprites[j].getY());

                    if (Math.abs(distX) < sprites[i].getWidth() && Math.abs(distY) < sprites[i].getHeight()) { // it's a collision
                        sprites[i].setPosition(sprites[i].getX() + distX * 0.05f, sprites[i].getY() + distY * 0.05f);
                    }

                }
            }
            Sprite sprite = sprites[i];
            batch.draw(spriteAnim[((int) (animationTime % spriteAnim.length))], sprite.getX(), sprite.getY());
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

        if (sprites.length < 1) return InputResult.Change;
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
            return InputResult.Button;
        } else if (answerIsGiven) { // check for the balloons ...
            int toRemove = -1;
            float dist = Float.MAX_VALUE;
            for (int i = 0; i < sprites.length; i++) {
                Sprite sp = sprites[i];
                Vector2 s = new Vector2(sp.getX() + sp.getWidth() / 2, sp.getY() + sp.getHeight() / 2);

                // take the nearest one to remove ...
                if (t.dst(s) < Math.min(sprites[i].getWidth(), dist)) { // it's a collision
                    toRemove = i;
                    dist = t.dst(s);
                }
            }
            if (toRemove > -1) {
                Sprite[] temp = new Sprite[sprites.length - 1];
                int count = 0;
                for (int i = 0; i < sprites.length; i++) {
                    if (i != toRemove) temp[count++] = sprites[i];
                }
                sprites = temp;
                return InputResult.Pop;
            }
        } else {
            return InputResult.Change;
        }
        return InputResult.Nothing;
    }
}
