package com.sinigr.arca;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

	class Basket extends Actor {
		Texture t;
		public Basket(Texture tr){
			t = tr;
		}
    	@Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(t, getX(), getY(), getWidth(), getHeight());
        }
    }