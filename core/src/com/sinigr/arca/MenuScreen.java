package com.sinigr.arca;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {
	
	private Texture Buttons, btnPlay, Background;
	private SpriteBatch Sb;
	private Stage stage;
	private OrthographicCamera camera;
	static TextureRegion btnShop, btnLvl, btnSettings, btnHelp;
	ScreenController sc;
	
	 class GoToGameListener extends ClickListener {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	            sc.setScreen(sc.gameScreen);
	        }
	    }
	 class GoToLvlListener extends ClickListener {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	           
	        }
	    }
	 class GoToHelpListener extends ClickListener {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	           
	        }
	    }
	 class GoToShopListener extends ClickListener {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	Gdx.app.exit();
	        }
	    }
	 class GoToSettingsListener extends ClickListener {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	Gdx.app.exit();
	        }
	    }
    public MenuScreen(SpriteBatch batch, ScreenController sc) {
    	this.sc = sc;
    	btnPlay = new Texture(Gdx.files.internal("images/play.png"), true);
    	btnPlay.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
    	Buttons = new Texture(Gdx.files.internal("images/buttons.png") ,true);
    	Buttons.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
    	Background = new Texture("images/background.jpg");
    	camera = new OrthographicCamera();
    	camera.setToOrtho(false, 1920, 1080);
    	FitViewport viewp = new FitViewport(1920, 1080, camera);
    	Sb = new SpriteBatch();
    	stage = new Stage(viewp, Sb);
    	btnLvl = new TextureRegion(Buttons, 0, 0, 639, 147);
    	btnSettings = new TextureRegion(Buttons, 0, 147, 639, 148);
    	btnShop = new TextureRegion(Buttons, 0, 295, 639, 147);
    	btnHelp = new TextureRegion(Buttons, 0, 442, 639, 148);
    	BackgroundInit();
    	ButtonPlayInit();
    	ButtonInit(0, 260, btnLvl);
    	ButtonInit(0, 50, btnSettings);
    	ButtonInit(1281, 260, btnShop);
    	ButtonInit(1281, 50, btnHelp);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
    public void BackgroundInit(){
    	MenuButton mButton = new MenuButton(Background);
    	mButton.setSize((Background.getWidth()), (Background.getHeight()));
    	mButton.setPosition(0, 0);
        stage.addActor(mButton);	
    }
    public void ButtonInit(int x, int y, TextureRegion tr){
    	MenuButton mButton = new MenuButton(tr);
    	mButton.setSize((int)(Buttons.getWidth()), (int)(Buttons.getHeight()/4));
    	mButton.setPosition(x, y);
    	if (tr==btnLvl) mButton.addListener(new GoToLvlListener());	
    	if (tr==btnShop)mButton.addListener(new GoToShopListener());
    	if (tr==btnSettings)mButton.addListener(new GoToSettingsListener());
    	if (tr==btnHelp)mButton.addListener(new GoToHelpListener());
        stage.addActor(mButton);
    }
    
    public void ButtonPlayInit(){
    	MenuButton buttonPlay = new MenuButton(btnPlay);
    	buttonPlay.setSize((btnPlay.getWidth()), (btnPlay.getHeight()));
    	buttonPlay.setPosition(634, 427);
    	buttonPlay.addListener(new GoToGameListener());
        stage.addActor(buttonPlay);
    }

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	stage.draw();
    	stage.act(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		stage.dispose();
		
	}
}
