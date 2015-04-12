package com.sinigr.arca;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

	class Brick extends Actor {
		TextureRegion t;
		int blockType;
		public Brick(TextureRegion tr, int block){
			t = tr;
			blockType = block;
		}
    	@Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(t, getX(), getY(), getWidth(), getHeight());
        }
    }