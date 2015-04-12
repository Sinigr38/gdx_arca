package com.sinigr.arca;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture;

	class MenuButton extends Actor {
		TextureRegion tr;
		Texture texture;
		public MenuButton(TextureRegion tr){
			this.tr = tr;
		}
		public MenuButton(Texture tr){
			texture = tr;
		}
    	@Override
        public void draw(Batch batch, float parentAlpha) {
    		if(tr != null)
    			batch.draw(tr, getX(), getY(), getWidth(), getHeight());
    		else
    			batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }