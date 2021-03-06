package ar.edu.ort.gameproject;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.renderscript.ScriptIntrinsicResize;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.Scheduler;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.instant.CallFuncN;
import org.cocos2d.actions.interval.Animate;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.JumpBy;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.RotateTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.Animation;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCColor3B;
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

    Animate REDAnimacionAccion;
    Animation REDAnimacionSecuencia;
    CallFunc RedFuncNFinDeLaAnimacion;
    IntervalAction REDsecuencia;

    Animate NAnimacionAccion;
    Animation NAnimacionSecuencia;
    CallFunc NFuncNFinDeLaAnimacion;
    IntervalAction Nsecuencia;

    int countdown = 1;
    int puntos = 0;
    int countdownaux = 0;
    int vidas = 3;
    Boolean canfall = true;
    boolean hadjump = false;
    Boolean BOOLLeft = false;
    Boolean BOOLRight = false;
    Boolean BOOLEND = false;
    Boolean isnotmoving = true;
    Sprite Life1, Life2, Life3;
    MenuItemImage btn1,btn2,btn3,btn4;
    Menu MenuDeBotones1;
    Menu MenuDeBotones2;
    Menu MenuDeBotones3;
    Menu MenuDeBotones4;

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

           /* MediaPlayer mMusicaDeFondo;
            mMusicaDeFondo = MediaPlayer.create(_ContextoDelJuego, R.raw.dark_fallout);
            mMusicaDeFondo.start();
            mMusicaDeFondo.setVolume(0.5f,0.5f);
            mMusicaDeFondo.setLooping(true);
            MediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);*/

            this.setIsTouchEnabled(true);
            SetPlayer();
            SetLifes();
           // SetButton();

            TimerTask setEnemyTask;
            setEnemyTask = new TimerTask() {
                @Override
                public void run() {
                    SetEnemy();
                    if (hadjump)
                    {
                     countdown--;
                    }
                    if (countdown < 0)
                    {
                        hadjump = false;
                        canfall = true;
                        countdown = 1;
                    }

                    if(countdownaux > 0) {
                        countdownaux--;
                    }

                    detectCollisions();
                }
            };
            Timer enemyClock;
            enemyClock = new Timer();
            enemyClock.schedule(setEnemyTask, 0, 1000);


            TimerTask MoveTask;
            MoveTask = new TimerTask() {
                @Override
                public void run() {
                    Bounce();
                    if  (canfall) {
                        if (!BOOLEND) {
                            if (BOOLLeft) {
                                BOOLRight = false;
                                Player.runAction(MoveTo.action(0.1f, Player.getPositionX() - 25, Player.getPositionY()));
                                isnotmoving = false;
                            }
                            if (BOOLRight) {
                                BOOLLeft = false;
                                Player.runAction(MoveTo.action(0.1f, Player.getPositionX() + 25, Player.getPositionY()));
                                isnotmoving = false;
                            }
                        } else {
                            isnotmoving = true;
                            BOOLRight = false;
                            BOOLLeft = false;
                            BOOLEND = false;
                        }
                    }

                }
            };
            Timer MoveClock;
            MoveClock= new Timer();
            MoveClock.schedule(MoveTask, 0, 110);
        }

        private void Bounce()
        {
            if (Player.getPositionX() < ScreenDevice.getWidth() - ScreenDevice.getWidth())
            {
                isnotmoving = false;
                MoveRight();
                Log.d("123","menor a width");
            }
            if (Player.getPositionX() > ScreenDevice.getWidth())
            {
                isnotmoving = false;
                MoveLeft();
                Log.d("123","mayor a width");
            }
            super.addChild(Player);
        }

        private void SetPlayer() {
            Player = Sprite.sprite("1.png");
            float PosX, PosY;
            PosX = ScreenDevice.width / 2;
            PosY = ScreenDevice.height / 8;
            Player.setPosition(PosX, PosY);
            Run();
            super.addChild(Player);
        }
        private void SetLifes() {
            float PosX, PosY;

            Life1 = Sprite.sprite("life2.png");
            PosX = ScreenDevice.width / 8;
            PosY = ScreenDevice.height;
            Life1.setPosition(PosX, PosY);
            super.addChild(Life1);

            Life2 = Sprite.sprite("life2.png");
            PosX = ScreenDevice.width / 6;
            PosY = ScreenDevice.height;
            Life2.setPosition(PosX, PosY);
            super.addChild(Life2);

            Life3 = Sprite.sprite("life2.png");
            PosX = ScreenDevice.width / 4;
            PosY = ScreenDevice.height;
            Life3.setPosition(PosX, PosY);
            super.addChild(Life3);
        }
        private void RemoveLifes(int number)
        {
            if (number == 1)
            {
                super.removeChild(Life1, true);
            }
            if (number == 2)
            {
                super.removeChild(Life2, true);
            }
            if (number == 3)
            {
                super.removeChild(Life3, true);
            }

        }
        private void SetEnemy() {
            Enemy = Sprite.sprite("dollar.png");

            CCPoint StartPos;
            StartPos = new CCPoint();

            float enemyWidth;
            enemyWidth = Enemy.getWidth();
            StartPos.x = ScreenDevice.getWidth() + enemyWidth / 2;

            Random Randomizer;
            Randomizer = new Random();


            float enemyHeight;
            enemyHeight = Enemy.getHeight();

            StartPos.y = ScreenDevice.height;

            StartPos.x = Randomizer.nextInt((int) ScreenDevice.width- (int) enemyWidth) + enemyWidth/ 2;

            Enemy.setPosition(StartPos.x, StartPos.y);

            CCPoint FinalPos;
            FinalPos = new CCPoint();
            FinalPos.y = -1*(Enemy.getHeight() + Enemy.getHeight()/2);
            FinalPos.x = StartPos.x;

            ZigZag(FinalPos.x, FinalPos.y);
           //Enemy.runAction(MoveTo.action(8, FinalPos.x, FinalPos.y));

        }

        public boolean ccTouchesMoved(MotionEvent event) {
           //MovePlayerShip(event.getX(), ScreenDevice.getHeight() - event.getY());
           /*
            if (isnotmoving)
            {
                Jump();
            }
           */
            return true;
        }
        public boolean ccTouchesBegan(MotionEvent event) {
            if (event.getX() > ScreenDevice.width/3 && event.getX() > ScreenDevice.width/3 && event.getY() > ScreenDevice.height/4
                    && Player.getPositionX() > 0
                    && Player.getPositionX() < ScreenDevice.getWidth())
            {
                isnotmoving = false;
                MoveRight();
            }
            if(event.getX() < ScreenDevice.width/6 && event.getY() > ScreenDevice.height/4
                    && Player.getPositionX() > 0
                    && Player.getPositionX() < ScreenDevice.getWidth())
            {
                isnotmoving = false;
                MoveLeft();
            }
            return true;
        }

        public boolean ccTouchesEnded(MotionEvent event) {
            /*
            BOOLLeft = false;
            BOOLRight = false;
            BOOLEND = true;
            */
            return true;

        }
        void Run()
        {
            REDAnimacionSecuencia = new Animation("Anim", 0.1f, "red1.png", "red2.png", "red3.png", "red4.png");
            REDAnimacionAccion = Animate.action(REDAnimacionSecuencia, false);
            RedFuncNFinDeLaAnimacion = CallFuncN.action(this, "RedFinDeLaAnimacion");
            REDsecuencia = Sequence.actions(REDAnimacionAccion, RedFuncNFinDeLaAnimacion);
            Player.runAction(REDsecuencia);
            super.addChild(Player);
        }
        void MoveLeft()
        {
            BOOLLeft = true;
            BOOLRight = false;
            super.addChild(Player);
        }
        void MoveRight()
        {
            BOOLRight = true;
            BOOLLeft = false;
            super.addChild(Player);
        }
        public void RedFinDeLaAnimacion(CocosNode ObjetoLlamador)
        {
            REDsecuencia = Sequence.actions(REDAnimacionAccion, RedFuncNFinDeLaAnimacion);
            ObjetoLlamador.runAction(REDsecuencia);
        }
        void ZigZag(float aux1, float aux2)
        {

            MoveTo FallDown;
            FallDown = (MoveTo.action(8, aux1, aux2));

            CallFuncN FinDelMovimiento;
            FinDelMovimiento = CallFuncN.action(this, "FinDelTrayecto");

            IntervalAction secuencia;
            secuencia = Sequence.actions(FallDown, FinDelMovimiento);
            Enemy.runAction(secuencia);
            arrayEnemies.add(Enemy);
            super.addChild(Enemy);
            Log.d("123", "uno mas");
        }
        public void FinDelTrayecto(CocosNode CN)
        {
            super.removeChild(CN, true);
            arrayEnemies.remove(CN);
            Log.d("123", "uno menos de " + arrayEnemies.size());
            lblTitulo.setString("Puntos " +  puntos++);
            CCColor3B color = new CCColor3B(128,100,200);
            lblTitulo.setColor(color);
            super.addChild(lblTitulo);

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

            Player.setPosition(Player.getPositionX(), Player.getPositionY() + 5);
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

            if (arrayEnemies != null)
            {
                for (Sprite aux : arrayEnemies) {
                    if (InterseccionEntreSprites(Player, aux)) {
                        wasCollision = true;
                    }
                    if (wasCollision) {
                        if(countdownaux==0)
                        {
                        //Sacar vida
                        vidas--;
                            countdownaux = 1;
                        Log.d("lolazo", "perdiste una vida te quedan " + vidas);
                        if (vidas == 2)
                        {
                            RemoveLifes(1);
                        }
                        if (vidas == 1)
                        {
                            RemoveLifes(2);
                        }
                        if (vidas <= 0) {
                            RemoveLifes(3);
                            Log.d("lolazo", "perdiste");
                        }
                        Log.d("lolazo", "si");
                        }
                    } else { //No hacer nada
                        Log.d("lolazo", "no");
                    }
                }
            }
        }
    }

    class BackgroundLayerClass extends Layer {
        public BackgroundLayerClass() {
            SetBackground();
            SetTittle();
            BackGroundRun();
            TimerTask setEnemyTask;
            setEnemyTask = new TimerTask() {
                @Override
                public void run() {

                }
            };
            Timer enemyClock;
            enemyClock = new Timer();
            enemyClock.schedule(setEnemyTask, 0, 1000);
        }
        private void SetBackground() {
            BackgroundImage = Sprite.sprite("n1.png");
            float PosX, PosY;
            PosX = ScreenDevice.width / 2;
            PosY = ScreenDevice.height / 2;
            BackgroundImage.setPosition(PosX, PosY);
            BackgroundImage.runAction(ScaleBy.action(0.01f, 2.0f, 2.0f));
            super.addChild(BackgroundImage);
        }
        void BackGroundRun()
        {
            NAnimacionSecuencia = new Animation("Anim2", 0.2f, "n1.png", "n2.png", "n3.png", "n4.png", "n5.png", "n6.png", "n7.png", "n8.png", "n9.png", "n10.png");
            NAnimacionAccion = Animate.action(NAnimacionSecuencia, false);
            NFuncNFinDeLaAnimacion = CallFuncN.action(this, "NFinDeLaAnimacion");
            Nsecuencia = Sequence.actions(NAnimacionAccion, NFuncNFinDeLaAnimacion);
            BackgroundImage.runAction(Nsecuencia);
            super.addChild(BackgroundImage);
        }
        public void NFinDeLaAnimacion(CocosNode ObjetoLlamador)
        {
            Nsecuencia = Sequence.actions(NAnimacionAccion, NFuncNFinDeLaAnimacion);
            ObjetoLlamador.runAction(Nsecuencia);
        }
        private void SetTittle() {
            lblTitulo = Label.label("Puntos", "Calibri", 30);

            float TittlePosition;
            TittlePosition = lblTitulo.getHeight();

            lblTitulo.setPosition(ScreenDevice.width / 2, ScreenDevice.height - TittlePosition / 2);
            CCColor3B color = new CCColor3B(128,100,200);
            lblTitulo.setColor(color);

            super.addChild(lblTitulo);
        }
    }




}
