package com.sinigr.arca;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
	 class GoToMenuListener extends ClickListener {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	sc.setScreen(sc.menuScreen);
	        }
	    }
	private OrthographicCamera camera;
	private SpriteBatch Sb; 
	private static Stage stage;  
	Texture bricks, balls, basket, bonuses, bomb; 
	static TextureRegion[] Bricks ,Balls, Bonuses;
	private Array <Brick> bricksArray;  
	static Array <Ball> ballsArray;
	private Array <Bonus> bonusArray;
	CharSequence str;
	BitmapFont textDrawer;
	static int lifeCounter;
	ScreenController sc;
	static Basket bask; 
	/**
	 * Инициализация игрового экрана
	 * @param batch отрисовщик
	 * @param sc Контроллер экранов
	 */
    public GameScreen(SpriteBatch batch, ScreenController sc) {
    	this.sc = sc;
    	Sb = new SpriteBatch(); 
    	bricksArray = new Array<Brick>(); 
    	ballsArray = new Array<Ball>();  
    	bonusArray = new Array<Bonus>();
    	camera = new OrthographicCamera();
    	camera.setToOrtho(false, 1920, 1080);
    	FitViewport viewp = new FitViewport(1920, 1080, camera);
    	Sb = new SpriteBatch();
    	stage = new Stage(viewp, Sb);
    	TextureInit();
    	lifeCounter = 3;
    	GenerateLvl(1);
    	GenerateBalls(Gdx.graphics.getWidth()/2, 50, Balls[0], 1, "normal" , 600, 255, 285);
    	textDrawer = new BitmapFont();
    	textDrawer.setColor(Color.YELLOW);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
    
    /**
     * Метод отрисовки
     */
	@Override
	public void render(float delta) {
		BricksUpdate();
		BallUpdate();
		BasketUpdate();
		BonusUpdate();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	stage.draw();
    	stage.act(Gdx.graphics.getDeltaTime());
    	str = String.valueOf(Gdx.graphics.getFramesPerSecond()) + " FPS";
    	Sb.begin();
    	textDrawer.draw(Sb, str, 20, 15);
    	textDrawer.draw(Sb, "BRICKS LEFT: " + String.valueOf(bricksArray.size), 20, 30);
    	textDrawer.draw(Sb, "LIFES LEFT: " + String.valueOf(lifeCounter), 20, 45);
    	Sb.end();
     }
	/**
	 * Показать экран
	 */
	@Override
	public void show() {
		FileHandle file = Gdx.files.internal("images/cursor.png");
		Pixmap pixmap = new Pixmap(file);
		BonusController.initInstance();
		Gdx.input.setCursorImage(pixmap, 8, 8);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		
	}
	/**
	 * Кадр для кирпичиков
	 */
	public void BricksUpdate(){
		Iterator<Brick> iter = bricksArray.iterator();
	    while(iter.hasNext()) {
	    	Iterator<Ball> balliter = ballsArray.iterator();
	    	Brick brick = iter.next();
	    	while(balliter.hasNext()) {
	    		Ball ball = balliter.next();
	    		if(BallOfBrickCollision(ball, brick)){
	    			iter.remove();
	    			break;
	    		}
	    	}
	    }
	}
	/**
	 * Кадр для шариков
	 */
	public void BallUpdate(){
		Iterator<Ball> iter = ballsArray.iterator();
	    while(iter.hasNext()) {
	    	Ball ball = iter.next();
	    	TestWallsCollision(ball);
	    	float xSpeed = (float) (ball.speed*Math.sin((Math.PI/180)*(90-ball.angle)));
	    	float ySpeed =(float)  (ball.speed*Math.sin((MathUtils.PI/180)*ball.angle));
	    	ball.setX(ball.getX() + xSpeed*Gdx.graphics.getDeltaTime());
	    	ball.setY(ball.getY() - ySpeed*Gdx.graphics.getDeltaTime());
	    	TestBallCollision(ball);
	    	ball.speed = ball.speed + 5 * Gdx.graphics.getDeltaTime();
	    	if(ball.timer > 0) ball.timer -=1;
	    	if(ball.timer == 0) {
	    		ball.t = Balls[0];
	    		ball.ballState = "normal";
	    	}
	    	if(ball.getY() < 0) {
	    		ball.remove();
	    		iter.remove();
	    		if (ballsArray.size == 0){ 
	    			lifeCounter--;
	    			GenerateBalls(Gdx.graphics.getWidth()/2, 50, Balls[0], 1, "normal" , 400, 255, 285);
	    			bask.remove();
	    			basketInit(basket);
	    		};
	    	}
	    }
	}
	/**
	 * Кадр для корзинки
	 */
	public void BasketUpdate() {
		if(Gdx.app.getType() != ApplicationType.Android){
			Point location = MouseInfo.getPointerInfo().getLocation();
			if(location.getX() < Gdx.graphics.getWidth() - bask.getWidth()) bask.setX((float)location.getX());
			else bask.setX(Gdx.graphics.getWidth()-bask.getWidth());
		} else{
			float aksel = Gdx.input.getAccelerometerY();
			if(Math.abs(aksel)>0.3)
			bask.setX(bask.getX()+aksel*8);
			if(bask.getX()<0) bask.setX(0);
			if(bask.getX()>=Gdx.graphics.getWidth()-bask.getWidth())
			bask.setX(Gdx.graphics.getWidth()-bask.getWidth());
		}
	}
	/**
	 * Кадр для бонусов
	 */
	public void BonusUpdate() {
		Iterator<Bonus> iter = bonusArray.iterator();
	    while(iter.hasNext()) {
	    	Bonus bonus = iter.next();
	    	TestWallsCollisionBonus(bonus);
	    	if(TestBasketCollisionBonus(bonus)) iter.remove();
	    	else {
		    	bonus.Yspeed=bonus.Yspeed - 500 * Gdx.graphics.getDeltaTime();
		    	bonus.setX(bonus.getX() + bonus.Xspeed*Gdx.graphics.getDeltaTime());
		    	bonus.setY(bonus.getY() + bonus.Yspeed*Gdx.graphics.getDeltaTime());
	    	}
	    }
	}
	/**
	 * Генерация уровня
	 * @param number номер уровня
	 */
	public void GenerateLvl(int number) {
		String line = null;
    	FileHandle file = Gdx.files.internal("level/"+ String.valueOf(number) + ".txt");
    	BufferedReader reader = new BufferedReader(file.reader());
    	try {
			line = reader.readLine();
		} catch (IOException e) {}
    	
    	int BrickW = 64; 
    	int BrickH = 40;
    	float CurX = 0,CurY = Gdx.graphics.getHeight()- BrickH;
    	int CurSymbol = -1; 
    	
    	 while(line != null) {
    		for(int j=0; j<32; j++){
    			CurSymbol=Integer.valueOf(line.substring(j, j + 1)) - 1;
    			if(CurSymbol != -1) {
    				BrickGen(CurX, CurY, Bricks[CurSymbol], BrickW, BrickH, CurSymbol + 1);
    			}
    			CurX = CurX + BrickW;
    		}
    		CurX = 0;
    		try {
				line = reader.readLine();
			} catch (IOException e) {}
    		CurY = CurY-BrickH;
    	 }
    }
	/**
	 * Генерация кирпичика
	 * @param x координата x
	 * @param y координата y
	 * @param tr текстурРегион
	 * @param bW ширина
	 * @param bH высота
	 * @param BType тип
	 */
    public void BrickGen(float x, float y, TextureRegion tr, int bW, int bH, int BType) {
    	Brick brick = new Brick(tr, BType);
    	brick.setSize(bW,bH);
    	brick.setPosition(x, y);
    	bricksArray.add(brick);
        stage.addActor(brick);
    }
    /**
     * Генерация шариков
     * @param x координата x
     * @param y координата y
     * @param tr регион
     * @param numbers количество шариков
     * @param type тип шариков
     * @param speed скорость
     * @param Rangle1 начальный возможный угол
     * @param Rangle2 конечный возможный угол
     */
    public static void GenerateBalls(int x, int y, TextureRegion tr, int numbers, String type, float speed, int Rangle1, int Rangle2) {
    	for (int i = 0; i<numbers; i++){
    		int angle = MathUtils.random(Rangle1, Rangle2);
    		if(angle % 90 < 10) angle += 10;
    		Ball ball = new Ball(tr, type, speed, angle);
        	ball.setSize(30,30);
        	ball.setPosition(x, y);
        	ballsArray.add(ball);
            stage.addActor(ball);
    	}
    }
    /**
     * Генерация бонуса
     * @param x координата x
     * @param y координата y
     */
    public void GenerateBonus(float x, float y) {
    	int rand = MathUtils.random(0, 100);
    	Bonus bonus;
        if(rand<=0)
        	bonus = new Bonus(Bonuses[8],9);
        else if(rand <= 15) 
        	bonus = new Bonus(Bonuses[1],2);
        else if(rand <= 35) 
        	bonus = new Bonus(Bonuses[6],7);
        else if(rand <= 55) 
        	bonus = new Bonus(Bonuses[2],3);
        else if(rand <= 58) 
        	bonus = new Bonus(Bonuses[4],5);
        else if(rand <= 60) 
        	bonus = new Bonus(Bonuses[3],4);
        else if(rand <= 75) 
        	bonus = new Bonus(Bonuses[5],6);
        else if(rand <= 90) 
        	bonus = new Bonus(Bonuses[0],1);
        else if(rand <= 95) 
        	bonus = new Bonus(Bonuses[7],8);
        else  
        	bonus = new Bonus(Bonuses[9],10);
        
       bonus.setSize(64, 64);
       bonus.setPosition(x, y);
       bonusArray.add(bonus);
       stage.addActor(bonus);
    }
    /**
     * Проверка пересечения шарика со стенками
     */
    public void TestWallsCollision(Ball ball){
    	if ((ball.getX()<=0)|| (ball.getX()+ball.getWidth()>=Gdx.graphics.getWidth()))
        ball.angle=180-ball.angle;
    	if(ball.getY()+ball.getWidth()>=Gdx.graphics.getHeight()){
    		while(ball.getY()+ball.getWidth()>=Gdx.graphics.getHeight())
    		BallPihPih(ball);
    		ball.angle=360-ball.angle;
    	}
    	
    }
    /**
     * Проверка пересечения бонуса со стенами
     */
    public void TestWallsCollisionBonus(Bonus bonus){
    	if ((bonus.getX()<=0)|| (bonus.getX()+bonus.getWidth()>=Gdx.graphics.getWidth()))
        bonus.Xspeed = -bonus.Xspeed;
    	if(bonus.getY()+ bonus.getWidth()>=Gdx.graphics.getHeight())
    		bonus.Yspeed= - bonus.Yspeed;
    }
   /**
    * Проверка пересечения корзинки с бонусами  
    */
    public boolean TestBasketCollisionBonus(Bonus bonus){
    	Rectangle b1 = new Rectangle(bonus.getX(), bonus.getY(),bonus.getWidth(), bonus.getHeight());
    	Rectangle b2 = new Rectangle(bask.getX(), bask.getY(),bask.getWidth(), bask.getHeight());
    	if (Intersector.overlaps(b1, b2)) {
    		BonusController.getInstance().bonusProc(bonus.bonusType);
    		bonus.remove();
    		return true;
    	}
    		return false;
    }
   /**
    * Инициализация корзинки 
    */
    public void basketInit(Texture tr){
    	bask = new Basket(tr);
    	bask.setSize(tr.getWidth(),tr.getHeight());
    	bask.setPosition(Gdx.graphics.getHeight()/2+tr.getWidth()/2, 0);
        stage.addActor(bask);
    }
    /**
     * Проверка пересечния шарика с корзинкой
     */
    public void TestBallCollision(Ball ball){
    	if(bask.isVisible) {
	    	Rectangle b1 = new Rectangle(ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight());
	    	Rectangle b2 = new Rectangle(bask.getX(), bask.getY(),bask.getWidth(), bask.getHeight());
	    	if (Intersector.overlaps(b1, b2)) {
	    		while(bask.getY() + bask.getHeight() > ball.getY()){
	    			BallPihPih(ball);
	    		}	
	    	ball.angle=(int)(190 + (ball.getX()-bask.getX() + ball.getWidth()/2)*0.9*200/bask.getWidth());
	    	if (ball.angle > 350) ball.angle = 350;
	    	}
    	}
    }
    /**
     * Магический метод выталкивающий шарик из какого-либо объекта, нужен что бы шарик не влетал в объекты, с которыми пересекается
     * @param ball
     */
    public void BallPihPih(Ball ball){
    	float xSpeed = (float) (Math.sin((Math.PI/180)*(90-ball.angle)));
    	float ySpeed =(float) (Math.sin((MathUtils.PI/180)*ball.angle));
    	ball.setX(ball.getX() - xSpeed*Gdx.graphics.getDeltaTime());
    	ball.setY(ball.getY() + ySpeed*Gdx.graphics.getDeltaTime());
    }
    /**
     * Проверка стокновения шарика с кирпичиками
     */
    public boolean BallOfBrickCollision(Ball ball, Brick brick){
    	Rectangle b1 = new Rectangle(ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight());
    	Rectangle b2 = new Rectangle(brick.getX(), brick.getY(),brick.getWidth(), brick.getHeight());
    	if (Intersector.overlaps(b1, b2)) {
    		if("normal".equals(ball.ballState)) {
	    		while(Intersector.overlaps(b1, b2)){
	    			b1 = new Rectangle(ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight());
	    	    	b2 = new Rectangle(brick.getX(), brick.getY(),brick.getWidth(), brick.getHeight());
	    			BallPihPih(ball);
	    		}
	    		if ((ball.getX()>brick.getX()-ball.getWidth()) &&(ball.getX()<brick.getX()+brick.getWidth()) && ((ball.getY()>=brick.getY()+brick.getHeight()) ||(ball.getY()+ball.getHeight()-1<=brick.getY())))
	    			ball.angle=360-ball.angle;
	    		else
	    			ball.angle=540-ball.angle;
    		}
    		return BrickOfBallCollision(brick);
    	}
    	return false;
    }
    /**
     * Проверка стокновения кирпичика с шариками
     */
    public boolean BrickOfBallCollision(Brick brick){
    	if(brick.blockType <= 4) {
    		brick.remove();
    		return true;
    	}
    	if(brick.blockType == 5) {
    		brick.remove();
    		return true;
    	}
    	if(brick.blockType == 6) {
    		GenerateBonus(brick.getX(), brick.getY());
    		brick.remove();
    		return true;
    	}
    	return false;
    }
    /**
     * Инициализация текстур
     */
    public void TextureInit() {
    	bomb = new Texture("images/bomb.png");
    	bricks = new Texture("images/bricks.png"); 
    	balls = new Texture("images/balls.png");
    	basket = new Texture("images/basket.png");
    	bonuses = new Texture("images/bonuses.png");
    	Bricks = new TextureRegion[7];
    	Bonuses = new TextureRegion[10];
    	Balls = new TextureRegion[2];
    	for (int i = 0; i < 7; i++){
    		Bricks[i] = new TextureRegion(bricks, 0, 40*i, 64, 40);	
    	}
    	for (int i = 0; i < 10; i++){
    		Bonuses[i] = new TextureRegion(bonuses, 0, 64*i, 64, 64);	
    	}
    	for (int i = 0; i < 2; i++){
    		Balls[i] = new TextureRegion(balls, 0, 30*i, 30, 30);	
    	}  
    	basketInit(basket);
    }
}
