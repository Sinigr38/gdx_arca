package com.sinigr.arca;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenController extends Game {
    private SpriteBatch batch; //ќтрисовщик спрайтов. ќн один на все экраны, больше и не нужно
    public MenuScreen menuScreen; // экран меню
    public GameScreen gameScreen; //игровой экран
    
    public int CAMERA_WIDTH = 1920;
    public int CAMERA_HEIGHT = 1080;
    

    @Override
    public void create() {
        batch = new SpriteBatch();
        menuScreen = new MenuScreen(batch, this);
        gameScreen = new GameScreen(batch, this);
        setScreen(menuScreen);
    }  
}