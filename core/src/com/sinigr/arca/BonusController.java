package com.sinigr.arca;

import java.util.Iterator;

public class BonusController {
	private static BonusController mInstance;

	public static void initInstance() { if (mInstance == null) mInstance = new BonusController(); }
	
	public static BonusController getInstance() { return mInstance; }
	// 1)fast 2)death 3)big 4)x8 5) x3 6) small 7)slow 8)rocket 9)life 10)fireball
	/**
	 * Применение бонуса в зависимости от его типа
	 * @param type тип бонуса
	 */
	public void bonusProc(int type){
		switch(type){
			case 1:{
				Iterator<Ball> iter = GameScreen.ballsArray.iterator();
			    while(iter.hasNext()) {
			    	Ball ball = iter.next();
			    	if(ball.speed < 1000) ball.speed += 150;
			    }
			}break;
			case 2:{
				GameScreen.bask.remove();
				GameScreen.bask.isVisible = false;
			}break;
			case 3:{
				if(GameScreen.bask.getWidth() <= 300) GameScreen.bask.setWidth(GameScreen.bask.getWidth() + 50);
			}break;
			case 4:{
				GameScreen.GenerateBalls((int)GameScreen.ballsArray.get(0).getX(), (int)GameScreen.ballsArray.get(0).getY(),
						GameScreen.Balls[0], 7, "normal" , GameScreen.ballsArray.get(0).speed, 0, 360);
			}break;
			case 5:{
				GameScreen.GenerateBalls((int)GameScreen.ballsArray.get(0).getX(), (int)GameScreen.ballsArray.get(0).getY(),
						GameScreen.Balls[0], 3, "normal" , GameScreen.ballsArray.get(0).speed, 0, 360);
			}break;
			case 6:{
				if(GameScreen.bask.getWidth() >= 100) GameScreen.bask.setWidth(GameScreen.bask.getWidth() - 50);
			}break;
			case 7:{
				Iterator<Ball> iter = GameScreen.ballsArray.iterator();
			    while(iter.hasNext()) {
			    	Ball ball = iter.next();
			    	if(ball.speed > 250) ball.speed -= 150;
			    }
			}break;
			case 8:{
		
			}break;
			case 9:{
				GameScreen.lifeCounter += 1;
			}break;
			case 10:{
				Iterator<Ball> iter = GameScreen.ballsArray.iterator();
			    while(iter.hasNext()) {
			    	Ball ball = iter.next();
			    	ball.ballState = "fire";
			    	ball.timer = 600;
			    	ball.t = GameScreen.Balls[1];
			    }
			}

		}
	}
}
