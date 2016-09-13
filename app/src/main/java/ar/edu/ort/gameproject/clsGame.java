package ar.edu.ort.gameproject;

import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

public class clsGame {
    CCGLSurfaceView _GameView;
    CCSize ScreenDevice;
    Sprite Player;
    Sprite Enemy;
    Sprite BackgroundImage;
    Label lblTitulo;

    public clsGame(CCGLSurfaceView GameView)
    {
        _GameView = GameView;
    }
    public void StartGame()
    {
        Director.sharedDirector().attachInView(_GameView);

        ScreenDevice = Director.sharedDirector().displaySize();

        Director.sharedDirector().runWithScene(GameScene());
    }
    private Scene GameScene()
    {
        Scene SceneToReturn;
        SceneToReturn = Scene.node();

        BackgroundLayerClass BackgroundLayer;
        BackgroundLayer = new BackgroundLayerClass();

        TopLayerClass TopLayer;
        TopLayer = new TopLayerClass();

        SceneToReturn.addChild(BackgroundLayer, -10);
        SceneToReturn.addChild(TopLayer, 10);

        return SceneToReturn;
    }

    class TopLayerClass extends Layer{
        public TopLayerClass(){
        SetPlayer();
            SetEnemy();
        }
        private void SetPlayer(){
            Player = Sprite.sprite("rocket_mini.png");

            float PosX,PosY;
            PosX = ScreenDevice.width/2;
            PosY= ScreenDevice.height/2;
            Player.setPosition(PosX,PosY);
            super.addChild(Player);
        }
        private void SetEnemy()
        {
            Enemy = Sprite.sprite("enemigo.gif");

            CCPoint StartPos;
            StartPos = new CCPoint();

            StartPos.y = ScreenDevice.getHeight();
            StartPos.x = ScreenDevice.getWidth()/2;

            Enemy.setPosition(StartPos.x, StartPos.y);

            CCPoint FinalPos;
            FinalPos = new CCPoint();
            FinalPos.x = StartPos.x;
            FinalPos.y = 0;

            Enemy.runAction(MoveTo.action(3,FinalPos.x, FinalPos.y));
            

            super.addChild(Enemy);
        }
    }
    class BackgroundLayerClass extends Layer{
        public BackgroundLayerClass(){
            SetBackground();
            SetTittle();

        }
        private void SetBackground(){
            BackgroundImage = Sprite.sprite("fondo.png");

            float PosX,PosY;
            PosX = ScreenDevice.width/2;
            PosY= ScreenDevice.height/2;
            BackgroundImage.setPosition(PosX,PosY);
            BackgroundImage.runAction(ScaleBy.action(0.01f, 2.0f, 2.0f));
            super.addChild(BackgroundImage);
        }
        private void SetTittle(){
            lblTitulo=Label.label("Lolazo","Calibri",30);

            float TittlePosition;
            TittlePosition=lblTitulo.getHeight();

            lblTitulo.setPosition(ScreenDevice.width/2,ScreenDevice.height - TittlePosition/2);
            super.addChild(lblTitulo);
        }
    }

}
