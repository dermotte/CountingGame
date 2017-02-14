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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by mlux on 14.02.2017.
 */

public class Balloon extends Sprite {
    float animationTime = 0;
    TextureRegion[] spriteAnim;

    public Balloon(TextureRegion[] spriteAnim) {
        super(spriteAnim[0]);
        this.spriteAnim = spriteAnim;
        animationTime = (float) (Math.random()*100);

    }

    public void draw(SpriteBatch batch, float delta) {
        animationTime += delta * 10;
        batch.draw(spriteAnim[((int) (animationTime % spriteAnim.length))], getX(), getY());
    }
}
