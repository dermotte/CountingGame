/*
 * This project and its source code is licensed under
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * Copyright (c) 2017 Mathias Lux, mathias@juggle.at
 */

package at.juggle.games.counting.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by mlux on 14.02.2017.
 */

public class Balloon extends Sprite {
    float animationTime = 0;
    TextureRegion[] spriteAnim;
    private int number = -1;
    GlyphLayout layout = new GlyphLayout();

    public Balloon(TextureRegion[] spriteAnim) {
        super(spriteAnim[0]);
        this.spriteAnim = spriteAnim;
        animationTime = (float) (Math.random()*100);
    }

    public Balloon(TextureRegion[] spriteAnim, int number) {
        super(spriteAnim[0]);
        this.spriteAnim = spriteAnim;
        animationTime = (float) (Math.random()*100);
        this.number = number;
    }

    public void draw(SpriteBatch batch, float delta, BitmapFont font) {
        draw(batch, delta);
        if (number >0) {
            // find center of the baloon ...
            String str = "" + number;
            layout.setText(font, str);
            font.draw(batch, str, getX() + getWidth()/2-layout.width/2, getY() + getHeight()/2+layout.height/2+16);
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        animationTime += delta * 10;
        batch.draw(spriteAnim[((int) (animationTime % spriteAnim.length))], getX(), getY());
    }

    public int getNumber() {
        return number;
    }
}
