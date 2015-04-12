package com.sinigr.arca;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

	class Ball extends Actor {
		TextureRegion t; //Текстурка
		String ballState; //Состояние шарика
		float speed;  //скорость
		int angle;  //угол
		float timer; //Внутренний таймер для бонусов и не только
		public Ball(TextureRegion tr, String state, float speed, int angle){
			t = tr;
			ballState = state;
			this.speed = speed;
			this.angle = angle;
			timer = 0;
		}
    	@Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(t, getX(), getY(), getWidth(), getHeight());
        }
    }