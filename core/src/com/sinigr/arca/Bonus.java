package com.sinigr.arca;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

	class Bonus extends Actor {
		TextureRegion t;
		int bonusType;
		float Xspeed;
		float Yspeed;
		// 1)fast 2)death 3)big 4)x8 5) x3 6) small 7)slow 8)rocket 9)life 10)fireball
		public Bonus(TextureRegion tr, int block){
			t = tr;
			bonusType = block;
			Xspeed = MathUtils.random(-300, 300);
			Yspeed = MathUtils.random(300, 600);
		}
    	@Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(t, getX(), getY(), getWidth(), getHeight());
        }
    }