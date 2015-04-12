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
	private Stage stage;  
	Texture bricks, balls,basket,bonuses;  
	static TextureRegion[] Bricks ,Balls, Bonuses;
	private Array <Brick> BricksArray;  
	private Array <Ball> BallsArray;
	private Array <Bonus> BonusArray;
	CharSequence str;
	BitmapFont TextDrawer;
	int brickCounter, lifeCounter;
	ScreenController sc;
	Basket bask; 
	
    public GameScreen(SpriteBatch batch, ScreenController sc) {
    	this.sc = sc;
    	Sb = new SpriteBatch(); 
    	BricksArray = new Array<Brick>(); 
    	BallsArray = new Array<Ball>();  
    	BonusArray = new Array<Bonus>();
    	camera = new OrthographicCamera();
    	camera.setToOrtho(false, 1920, 1080);
    	FitViewport viewp = new FitViewport(1920, 1080, camera);
    	Sb = new SpriteBatch();
    	stage = new Stage(viewp, Sb);
    	TextureInit();
    	brickCounter = 0;
    	lifeCounter = 3;
    	GenerateLvl(1);
    	GenerateBalls(Gdx.graphics.getWidth()/2, 50, Balls[0], 1, "normal" , 300, 255, 285);
    	TextDrawer = new BitmapFont();
    	TextDrawer.setColor(Color.YELLOW);
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
    	TextDrawer.draw(Sb, str, 20, 15);
    	TextDrawer.draw(Sb, "BRICKS LEFT: " + String.valueOf(brickCounter), 20, 30);
    	TextDrawer.draw(Sb, "LIFES LEFT: " + String.valueOf(lifeCounter), 20, 45);
    	Sb.end();
     }
	
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
	
	public void BricksUpdate(){
		Iterator<Brick> iter = BricksArray.iterator();
	    while(iter.hasNext()) {
	    	Iterator<Ball> balliter = BallsArray.iterator();
	    	Brick brick = iter.next();
	    	while(balliter.hasNext()) {
	    		Ball ball = balliter.next();
	    		if(BallOfBrickCollision(ball, brick)){
	    			iter.remove();
	    			brickCounter--;
	    			break;
	    		}
	    	}
	    }
	}
	
	public void BallUpdate(){
		Iterator<Ball> iter = BallsArray.iterator();
	    while(iter.hasNext()) {
	    	Ball ball = iter.next();
	    	TestWallsCollision(ball);
	    	float xSpeed = (float) (ball.speed*Math.sin((Math.PI/180)*(90-ball.angle)));
	    	float ySpeed =(float)  (ball.speed*Math.sin((MathUtils.PI/180)*ball.angle));
	    	ball.setX(ball.getX() + xSpeed*Gdx.graphics.getDeltaTime());
	    	ball.setY(ball.getY() - ySpeed*Gdx.graphics.getDeltaTime());
	    	TestBallCollision(ball);
	    	ball.speed = ball.speed+Gdx.graphics.getDeltaTime();
	    	if(ball.getY() < 0) {
	    		ball.remove();
	    		iter.remove();
	    		if (BallsArray.size==0){ lifeCounter--;
	    		GenerateBalls(Gdx.graphics.getWidth()/2, 50, Balls[0], 1, "normal" , 300, 255, 285);
	    		}
	    	}
	    }
	}
	
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
	
	public void BonusUpdate() {
		Iterator<Bonus> iter = BonusArray.iterator();
	    while(iter.hasNext()) {
	    	Bonus bonus = iter.next();
	    	TestWallsCollisionBonus(bonus);
	    	TestBasketCollisionBonus(bonus);
	    	bonus.Yspeed=bonus.Yspeed-500*Gdx.graphics.getDeltaTime();
	    	bonus.setX(bonus.getX() + bonus.Xspeed*Gdx.graphics.getDeltaTime());
	    	bonus.setY(bonus.getY() + bonus.Yspeed*Gdx.graphics.getDeltaTime());
	    }
	}
	
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
    				brickCounter++;
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
	
    public void BrickGen(float x, float y, TextureRegion tr, int bW, int bH, int BType) {
    	Brick brick = new Brick(tr, BType);
    	brick.setSize(bW,bH);
    	brick.setPosition(x, y);
    	BricksArray.add(brick);
        stage.addActor(brick);
    }
    
    public void GenerateBalls(int x, int y, TextureRegion tr, int numbers, String type, float speed, int Rangle1, int Rangle2) {
    	for (int i = 0; i<numbers; i++){
    		int angle = MathUtils.random(Rangle1, Rangle2);
    		Ball ball = new Ball(tr, type, speed, angle);
        	ball.setSize(30,30);
        	ball.setPosition(x, y);
        	BallsArray.add(ball);
            stage.addActor(ball);
    	}
    }
    
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
       BonusArray.add(bonus);
       stage.addActor(bonus);
    }
    
    public void TestWallsCollision(Ball ball){
    	//������� ������
    	if ((ball.getX()<=0)|| (ball.getX()+ball.getWidth()>=Gdx.graphics.getWidth()))
        ball.angle=180-ball.angle;
        //������� ������
    	if(ball.getY()+ball.getWidth()>=Gdx.graphics.getHeight()){
    		while(ball.getY()+ball.getWidth()>=Gdx.graphics.getHeight())
    		BallPihPih(ball);
    		ball.angle=360-ball.angle;
    	}
    	
    }
    public void TestWallsCollisionBonus(Bonus bonus){
    	
    	if ((bonus.getX()<=0)|| (bonus.getX()+bonus.getWidth()>=Gdx.graphics.getWidth()))
        bonus.Xspeed = -bonus.Xspeed;
       
    	if(bonus.getY()+ bonus.getWidth()>=Gdx.graphics.getHeight())
    		bonus.Yspeed= - bonus.Yspeed;
    	
    }
    
    public void TestBasketCollisionBonus(Bonus bonus){
    	Rectangle b1 = new Rectangle(bonus.getX(), bonus.getY(),bonus.getWidth(), bonus.getHeight());
    	Rectangle b2 = new Rectangle(bask.getX(), bask.getY(),bask.getWidth(), bask.getHeight());
    	if (Intersector.overlaps(b1, b2)) {
    		BonusController.getInstance().bonusProc(bonus.bonusType);
    	}
    }
    
    public void BasketInit(Texture tr){
    	bask = new Basket(tr);
    	bask.setSize(tr.getWidth(),tr.getHeight());
    	bask.setPosition(Gdx.graphics.getHeight()/2+tr.getWidth()/2, 0);
        stage.addActor(bask);
    }
    public void TestBallCollision(Ball ball){
    	Rectangle b1 = new Rectangle(ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight());
    	Rectangle b2 = new Rectangle(bask.getX(), bask.getY(),bask.getWidth(), bask.getHeight());
    	if (Intersector.overlaps(b1, b2)) {
    		while(bask.getY() + bask.getHeight() > ball.getY()){
    			BallPihPih(ball);
    		}	
    	ball.angle=(int)(190+(ball.getX()-bask.getX()+ball.getWidth()/2)*150/basket.getWidth()); 
    	}
    }
    public void BallPihPih(Ball ball){
    	float xSpeed = (float) (Math.sin((Math.PI/180)*(90-ball.angle)));
    	float ySpeed =(float) (Math.sin((MathUtils.PI/180)*ball.angle));
    	ball.setX(ball.getX() - xSpeed*Gdx.graphics.getDeltaTime());
    	ball.setY(ball.getY() + ySpeed*Gdx.graphics.getDeltaTime());
    }
    
    public boolean BallOfBrickCollision(Ball ball, Brick brick){
    	Rectangle b1 = new Rectangle(ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight());
    	Rectangle b2 = new Rectangle(brick.getX(), brick.getY(),brick.getWidth(), brick.getHeight());
    	if (Intersector.overlaps(b1, b2)) {
    		while(Intersector.overlaps(b1, b2)){
    			b1 = new Rectangle(ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight());
    	    	b2 = new Rectangle(brick.getX(), brick.getY(),brick.getWidth(), brick.getHeight());
    			BallPihPih(ball);
    		}
    		if ((ball.getX()>brick.getX()-ball.getWidth()) &&(ball.getX()<brick.getX()+brick.getWidth()) && ((ball.getY()>=brick.getY()+brick.getHeight()) ||(ball.getY()+ball.getHeight()-1<=brick.getY())))
    			ball.angle=360-ball.angle;
    		else
    			ball.angle=540-ball.angle;
    		return BrickOfBallCollision(brick);
    	}
    	return false;
    }
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
    public void TextureInit() {
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
    	BasketInit(basket);
    }
}
