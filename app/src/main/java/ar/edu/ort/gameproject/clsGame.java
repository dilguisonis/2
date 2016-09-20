package ar.edu.ort.gameproject;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.renderscript.ScriptIntrinsicResize;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.Scheduler;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.RotateTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCPointSprite;
import org.cocos2d.types.CCSize;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class clsGame {
    Context _ContextoDelJuego;
    CCGLSurfaceView _GameView;
    CCSize ScreenDevice;
    Sprite Player;
    Sprite Enemy;
    Sprite BackgroundImage;
    Label lblTitulo;
    ArrayList<Sprite> arrayEnemies;

    public clsGame(CCGLSurfaceView GameView) {
        _GameView = GameView;
        arrayEnemies = new ArrayList<Sprite>();
    }

    public void StartGame(CCGLSurfaceView VistaDelJuego, Context ContextoDelJuego) {
        _ContextoDelJuego = ContextoDelJuego;

        Director.sharedDirector().attachInView(_GameView);

        ScreenDevice = Director.sharedDirector().displaySize();

        Director.sharedDirector().runWithScene(GameScene());
    }

    private Scene GameScene() {
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


    class TopLayerClass extends Layer {
        public TopLayerClass() {
            MediaPlayer mMusicaDeFondo;
            mMusicaDeFondo = MediaPlayer.create(_ContextoDelJuego, R.raw.dark_fallout);
            mMusicaDeFondo.start();
            mMusicaDeFondo.setVolume(0.5f,0.5f);
            mMusicaDeFondo.setLooping(true);

            this.setIsTouchEnabled(true);
            SetPlayer();
            putButton();
            TimerTask setEnemyTask;
            setEnemyTask = new TimerTask() {
                @Override
                public void run() {
                    SetEnemy();
                    detectCollisions();
                }
            };
            Timer enemyClock;
            enemyClock = new Timer();
            enemyClock.schedule(setEnemyTask, 0, 1000);
        }

        private void SetPlayer() {
            Player = Sprite.sprite("rocket_mini.png");
            Player.runAction(RotateTo.action(0.01f, 312f));
            float PosX, PosY;
            PosX = ScreenDevice.width / 2;
            PosY = ScreenDevice.height / 2;
            Player.setPosition(PosX, PosY);
            super.addChild(Player);
        }

        private void SetEnemy() {
            Enemy = Sprite.sprite("enemigo.gif");

            CCPoint StartPos;
            StartPos = new CCPoint();

            float enemyHeight;
            enemyHeight = Enemy.getHeight();
            StartPos.y = ScreenDevice.getHeight() + enemyHeight / 2;

            Random Randomizer;
            Randomizer = new Random();

            float enemyWidth;
            enemyWidth = Enemy.getWidth();
            StartPos.x = Randomizer.nextInt((int) ScreenDevice.width - (int) enemyWidth) + enemyWidth / 2;

            Enemy.setPosition(StartPos.x, StartPos.y);
            Enemy.runAction(RotateTo.action(0.01f, 180f));

            CCPoint FinalPos;
            FinalPos = new CCPoint();
            FinalPos.x = StartPos.x;
            FinalPos.y = -1*(Enemy.getHeight() + Enemy.getHeight()/2);

            //Enemy.runAction(MoveTo.action(3, FinalPos.x, FinalPos.y));
            ZigZag();
            arrayEnemies.add(Enemy);

            super.addChild(Enemy);
        }

        public boolean ccTouchesMoved(MotionEvent event) {
            MovePlayerShip(event.getX(), ScreenDevice.getHeight() - event.getY());
            return true;
        }

        void MovePlayerShip(float X, float Y) {
            float MovimientoHorizontal, MovimientoVertical, SuavizadorDeMovimiento;
            MovimientoHorizontal = X - ScreenDevice.getWidth() / 2;
            MovimientoVertical = Y - ScreenDevice.getHeight() / 2;

            SuavizadorDeMovimiento = 20;
            MovimientoHorizontal = MovimientoHorizontal / SuavizadorDeMovimiento;
            MovimientoVertical = MovimientoVertical / SuavizadorDeMovimiento;

            float PosFinalX, PosFinalY;

            PosFinalX = Player.getPositionX() + MovimientoHorizontal;
            PosFinalY = Player.getPositionY() + MovimientoVertical;

            if (PosFinalX < Player.getWidth() / 2) {
                PosFinalX = Player.getWidth() / 2;
            }
            if (PosFinalX > ScreenDevice.getWidth() - Player.getWidth() / 2) {
                PosFinalX = ScreenDevice.getWidth() - Player.getWidth() / 2;
            }
            if (PosFinalY < Player.getHeight() / 2) {
                PosFinalY = Player.getHeight() / 2;
            }
            if (PosFinalY > ScreenDevice.getHeight() - Player.getHeight() / 2) {
                PosFinalY = ScreenDevice.getHeight() - Player.getHeight() / 2;
            }

            Player.setPosition(PosFinalX, PosFinalY);
        }

        void putButton(){
            MenuItemImage ShootButton, PauseButton;
            ShootButton = MenuItemImage.item("harambe.png","smiley_face_thumb_small.jpg",this, "PresionaBotondispado");
            PauseButton = MenuItemImage.item("harambe.png","smiley_face_thumb_small.jpg",this, "Pausa");

            float PosBtnX, PosBtnY;
            PosBtnX = ShootButton.getWidth()/2;
            PosBtnY = ShootButton.getHeight()/2;
            ShootButton.setPosition(PosBtnX, PosBtnY);
            PosBtnX = ScreenDevice.width-PauseButton.getWidth()/2;
            PosBtnY = ScreenDevice.width-PauseButton.getHeight()/2;
            PauseButton.setPosition(PosBtnX,PosBtnY);

            Menu MenuDeBotones;
            MenuDeBotones = Menu.menu(ShootButton,PauseButton);
            MenuDeBotones.setPosition(0,0);
            super.addChild(MenuDeBotones);
        }
        public void Shoot(){

        }
        public void Pause(){

        }

        void ZigZag()
        {
            MoveBy irHaciaAbajo, irHaciaArriba, irHaciaDerecha;
            irHaciaAbajo = MoveBy.action(1,0,-300);
            irHaciaArriba = MoveBy.action(1,0,300);
            irHaciaDerecha = MoveBy.action(1,300,0);

            CallFunc FinDelMovimiento;
            FinDelMovimiento = CallFunc.action(this, "FinDelTrayecto");

            IntervalAction secuencia;
            secuencia = Sequence.actions(irHaciaAbajo, irHaciaDerecha, irHaciaArriba, FinDelMovimiento);
            Enemy.runAction(secuencia);
            super.addChild(Enemy);
        }
        public void FinDelTrayecto(CocosNode cn)
        {
            super.removeChild(cn, true);
            arrayEnemies.remove(cn);

        }
    }

    class BackgroundLayerClass extends Layer {
        public BackgroundLayerClass() {
            SetBackground();
            SetTittle();
        }

        private void SetBackground() {
            BackgroundImage = Sprite.sprite("fondo.png");

            float PosX, PosY;
            PosX = ScreenDevice.width / 2;
            PosY = ScreenDevice.height / 2;
            BackgroundImage.setPosition(PosX, PosY);
            BackgroundImage.runAction(ScaleBy.action(0.01f, 2.0f, 2.0f));
            super.addChild(BackgroundImage);
        }

        private void SetTittle() {
            lblTitulo = Label.label("Lolazo", "Calibri", 30);

            float TittlePosition;
            TittlePosition = lblTitulo.getHeight();

            lblTitulo.setPosition(ScreenDevice.width / 2, ScreenDevice.height - TittlePosition / 2);
            super.addChild(lblTitulo);
        }
    }

    boolean estaEntre(int numeroComparar, int numeroMenor, int numeroMayor) {
        boolean devolver;
        if (numeroMenor > numeroMayor) {
            int aux;
            aux = numeroMayor;
            numeroMayor = numeroMenor;
            numeroMenor = aux;
        }
        if (numeroComparar >= numeroMenor && numeroComparar <= numeroMayor) {
            devolver = true;
        } else {
            devolver = false;
        }
        return devolver;
    }

    boolean InterseccionEntreSprites(Sprite Sprite1, Sprite Sprite2) {

        boolean Devolver;

        Devolver = false;

        int Sprite1Izquierda, Sprite1Derecha, Sprite1Abajo, Sprite1Arriba;

        int Sprite2Izquierda, Sprite2Derecha, Sprite2Abajo, Sprite2Arriba;

        Sprite1Izquierda = (int) (Sprite1.getPositionX() - Sprite1.getWidth() / 2);

        Sprite1Derecha = (int) (Sprite1.getPositionX() + Sprite1.getWidth() / 2);

        Sprite1Abajo = (int) (Sprite1.getPositionY() - Sprite1.getHeight() / 2);

        Sprite1Arriba = (int) (Sprite1.getPositionY() + Sprite1.getHeight() / 2);

        Sprite2Izquierda = (int) (Sprite2.getPositionX() - Sprite2.getWidth() / 2);

        Sprite2Derecha = (int) (Sprite2.getPositionX() + Sprite2.getWidth() / 2);

        Sprite2Abajo = (int) (Sprite2.getPositionY() - Sprite2.getHeight() / 2);

        Sprite2Arriba = (int) (Sprite2.getPositionY() + Sprite2.getHeight() / 2);

//Borde izq y borde inf de Sprite 1 está dentro de Sprite 2

        if (estaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&

                estaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {

            Log.d("Interseccion", "1");

            Devolver = true;

        }

//Borde izq y borde sup de Sprite 1 está dentro de Sprite 2

        if (estaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&

                estaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

            Log.d("Interseccion", "2");

            Devolver = true;

        }

//Borde der y borde sup de Sprite 1 está dentro de Sprite 2

        if (estaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) &&

                estaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

            Log.d("Interseccion", "3");

            Devolver = true;

        }

//Borde der y borde inf de Sprite 1 está dentro de Sprite 2

        if (estaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) &&

                estaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {

            Log.d("Interseccion", "4");

            Devolver = true;

        }

//Borde izq y borde inf de Sprite 2 está dentro de Sprite 1

        if (estaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) &&

                estaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {

            Log.d("Interseccion", "5");

            Devolver = true;

        }

//Borde izq y borde sup de Sprite 1 está dentro de Sprite 1

        if (estaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) &&

                estaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {

            Log.d("Interseccion", "6");
            ;

            Devolver = true;

        }

//Borde der y borde sup de Sprite 2 está dentro de Sprite 1

        if (estaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) &&

                estaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {

            Log.d("Interseccion", "7");

            Devolver = true;

        }

//Borde der y borde inf de Sprite 2 está dentro de Sprite 1

        if (estaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) &&

                estaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {

            Log.d("Interseccion", "8");

            Devolver = true;

        }

        return Devolver;

    }

    void detectCollisions() {
        boolean wasCollision;
        wasCollision = false;

        for (Sprite aux : arrayEnemies) {
            if (InterseccionEntreSprites(Player, aux)) {
                wasCollision = true;
            }
            if (wasCollision) {
                //Sacar vida
                Log.d("lolazo", "si");
            } else { //No hacer nada
                Log.d("lolazo", "no");
            }
        }
    }


}
