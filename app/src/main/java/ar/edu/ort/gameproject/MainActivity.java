package ar.edu.ort.gameproject;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.cocos2d.opengl.CCGLSurfaceView;

public class MainActivity extends Activity {
    CCGLSurfaceView MainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     //   setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN);
        MainView = new CCGLSurfaceView(this);
        setContentView(MainView);
    }
    @Override
    protected void onStart() {
        super.onStart();
        //   setContentView(R.layout.activity_main);
        clsGame game;
        game = new clsGame(MainView);
        game.StartGame(MainView, this);

    }

    public void run(){

    }
}
