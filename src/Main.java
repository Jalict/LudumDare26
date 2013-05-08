import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Main extends BasicGame implements KeyListener {
    // Configuration
    public int NUMBER_OF_POTATOES = 16;
    public float SPEED = 0.10f;

    // Assets
    public SpriteSheet potatoSheet;
    public Image background;
    public Image slicer;
    public Image startScreen;
    public Image conveyor;
    public Image startButtonImg;
    public Image loseScreen;
    public Image tryButtonImg;

    public Sound posPoint;
    public Sound negPoint;


    // Some sort of setup or init.. Maybe.
    public float cx = -128;
    public long t1;
    public long t2 = 0;
    public Random rand;
    public int score;
    public int higestScore = 0;
    public long startTime;
    public long endTime;
    public int state = 0;
    public Rectangle button;
    public Rectangle button2;

    public ArrayList<Potato> potatoList = new ArrayList<Potato>();


    public Main() {
        super("Cutato - Let's peel some Potatoes!");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        // Image Init
        slicer = new Image("res/img/slicer.png");
        background = new Image("res/img/background.png");
        conveyor = new Image("res/img/belt.png");
        startScreen = new Image("res/img/startscreen.png");
        potatoSheet = new SpriteSheet("res/img/potatoSheet.png", 128, 128);
        posPoint = new Sound("res/sfx/peeled.wav");
        negPoint = new Sound("res/sfx/nonpeeled.wav");
        startButtonImg = new Image("res/img/start_button.png");
        button = new Rectangle(gc.getWidth()-228,gc.getHeight()-90,128,32);
        button2 = new Rectangle(gc.getWidth()-228,gc.getHeight()-90,128,32);
        loseScreen = new Image("res/img/loseScreen.png");
        tryButtonImg = new Image("res/img/try_button.png");

        rand = new Random();
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input gameInput = gc.getInput();

        if(state == 1) {
            if(t2 == 0) {
                t2 = System.currentTimeMillis();
            }
            if(t2+1000 <= System.currentTimeMillis() ) {
                SPEED += 0.0025f;
                t2 = System.currentTimeMillis();
            }
            // Checks if mouse clicks within one of the potato's
            if (gameInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
                for (int i = 0; i < potatoList.size(); i++) {
                    if (potatoList.get(i).contains(gameInput.getMouseX() - 10, gameInput.getMouseY() - 30)) {
                        Graphics topImage = potatoList.get(i).topImage.getGraphics();   //TODO Get this done to run one time, instead of for each potato+each update
                        topImage.setDrawMode(Graphics.MODE_COLOR_MULTIPLY);
                        topImage.setColor(new Color(0, 0, 0, 0));
                        topImage.fillOval((gameInput.getMouseX() - potatoList.get(i).getX()) - 10, (gameInput.getMouseY() - potatoList.get(i).getY()) - 30, 20, 10);
                        topImage.flush();
                    }
                }
            }

            for (Potato p : potatoList) {
                float t = p.getX() + (delta * SPEED);
                p.setX(t);
            }
            if (potatoList.size() < NUMBER_OF_POTATOES && t1 + (2500-((gc.getTime()-startTime)/100)) <= System.currentTimeMillis()){

                int ycord = rand.nextInt(potatoSheet.getVerticalCount());
                potatoList.add(new Potato(-200, rand.nextInt(220-120+1)+10, 128, 128, potatoSheet.getSprite(0, ycord), potatoSheet.getSprite(1, ycord)));
                t1 = System.currentTimeMillis();
            }

            if (potatoList.get(0).getMinX() > 700) {
                int s = potatoList.get(0).getScore();
                if(s > 0) {
                    posPoint.play();
                } else if(s < 0) {
                    negPoint.play();
                }
                score += s;
                potatoList.remove(0);
            }

            cx += delta * SPEED;
            if (cx >= 0) {
                cx = -128;
            }
            if(gameInput.isKeyPressed(Input.KEY_P)) {
                if(gc.isPaused()) {
                    gc.resume();
                } else if(!gc.isPaused()) {
                    gc.pause();
                }
            }

            // Goal / End Condition
            if(score < -200) {
                state = 2;
                endTime = gc.getTime()-startTime;
            }
            if(score > higestScore) {
                higestScore = score;
            }
        }
        if (state == 0) {
            if(gameInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && button.contains(gameInput.getMouseX(),gameInput.getMouseY())) {
                startTime = gc.getTime();
                state = 1;
            }
        }
        if(state == 2) {
            if(gameInput.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && button2.contains(gameInput.getMouseX(),gameInput.getMouseY())) {
                // Reset the wholleee game! But I just can't do it properly.
                state = 0;
                score = 0;
                higestScore = 0;
                SPEED = 0.10f;
                t2 = 0;
                startTime = 0;
                for(int i = 0;i < potatoList.size();i++) {
                    potatoList.remove(i);

                }
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if(state == 1) {
            g.drawImage(background, 0, 0);
            g.drawImage(conveyor, cx, 120);

            g.drawString("Score: " + score, gc.getWidth()-140, 10);
            g.drawString("Time: " + ((gc.getTime()-startTime)/1000) + "s",gc.getWidth()-140,30);

            for (Potato p : potatoList) {
                g.drawImage(p.bottomImage, p.getX(), p.getY());
                g.drawImage(p.topImage, p.getX(), p.getY());
            }

            g.drawImage(slicer, gc.getInput().getMouseX() - slicer.getWidth() / 2, gc.getInput().getMouseY() - slicer.getHeight() / 2);
        }
        if(state == 0) {
        	g.drawImage(startScreen,0,0);
            g.drawImage(startButtonImg,478,304);
        }
        if(state == 2) {
            g.drawImage(loseScreen,0,0);
            g.drawString("Time: " + endTime/1000 + "s",gc.getWidth()/2,(gc.getHeight()/2)-20);
            g.drawString("Your highest score: " + higestScore,gc.getWidth()/2,gc.getHeight()/2);
            g.drawImage(tryButtonImg,478,304);
        }
    }

    public static void main(String[] args) throws SlickException {
    	AppGameContainer app = new AppGameContainer(new Main());

        app.setDisplayMode(700, 400, false);
        app.setShowFPS(false);
        app.start();
    }
}