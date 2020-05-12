package com.vokasi.tangkapbola;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //buat framenya
    private FrameLayout frame;
    private int frameHeight, frameWidth, initialFramewidth;
    private LinearLayout startLayout;

    //BUAT IMAGENYA
    private ImageView box, tinja, orange, bonus;
    private Drawable imageBoxRight, imageBoxLeft;

    //UKURAN CHARACTERNYA
    private int boxSize;

    //POSITION CHARACTERNYA dan KOMPONENNYA
    private float boxX,boxY;
    private float tinjaX,tinjaY;
    private float orangeX,orangeY;
    private float bonusX, bonusY;

    //BUAT SCORENYA
    private TextView skoranda, skortinggi;
    private int score, highscore, timeCount;
    private SharedPreferences settings;

    //CLASS
    private Timer timer;
    private Handler handler = new Handler();
    private musikgame musikGame;

    //STATUS
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean bonus_flg = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musikGame = new musikgame(this);

        frame = findViewById(R.id.frame);
        startLayout = findViewById(R.id.startlayout);
        box = findViewById(R.id.box);
        tinja = findViewById(R.id.tinja);
        orange = findViewById(R.id.orange);
        bonus = findViewById(R.id.bonus);
        skoranda = findViewById(R.id.skoranda);
        skortinggi = findViewById(R.id.skortinggi);

        imageBoxLeft = getResources().getDrawable(R.drawable.box_left);
        imageBoxRight = getResources().getDrawable(R.drawable.box_right);

        //SKOR TERTINGGINYA
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highscore = settings.getInt("HIGH_SCORE",0);
        skortinggi.setText("SKOR TERTINGGIMU :" + highscore);
    }

    public void changePos(){

        //TAMBAH PENGITUNG WAKTUNYA
        timeCount += 20;

        //Orange

        orangeY += 12;

        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if (hitCheck(orangeCenterX, orangeCenterY)){
            orangeY = frameHeight + 100;
            score += 10;
            musikGame.playHitOrangeSound();
        }

        if (orangeY > frameHeight){
            orangeY = -100;
            orangeX = (float) Math.floor(Math.random() * (frameWidth - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //BUAT BONUS
        if (!bonus_flg && timeCount % 10000 == 0){
            bonus_flg = true;
            bonusY = -20;
            bonusX = (float) Math.floor(Math.random() * (frameWidth - bonus.getWidth()));
        }

        if(bonus_flg){
            bonusY += 20;

            float bonusCenterX = bonusX + bonus.getWidth() / 2;
            float bonusCenterY = bonusY + bonus.getHeight() / 2;

            if(hitCheck(bonusCenterX, bonusCenterY)){
                bonusY = frameHeight + 50;
                score += 50;
                //MENGUBAH LEBAR FRAME
                if (initialFramewidth > frameWidth * 110 / 100){
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }
                musikGame.playHitBonusSound();
            }

            if (bonusY > frameHeight) bonus_flg = false;
            bonus.setX(bonusX);
            bonus.setY(bonusY);
        }

        //RINTANGAN
        tinjaY += 18;

        float tinjaCenterX = tinjaX + tinja.getWidth() / 2;
        float tinjaCenterY = tinjaY + tinja.getHeight() / 2;

        if(hitCheck(tinjaCenterX, tinjaCenterY)){
            tinjaY = frameHeight + 100;

            //Mengurangi dinding
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            musikGame.playHitTinjaSound();
            if(frameWidth <= boxSize){
                gameOver();
            }
        }

        if(tinjaY > frameHeight) {
            tinjaY = -100;
            tinjaX = (float)Math.floor(Math.random() * (frameWidth - tinja.getWidth()));
        }
        tinja.setX(tinjaX);
        tinja.setY(tinjaY);


        //MENGGERAKAN POSISI
        if (action_flg){
            //SENTUHAN
            boxX += 14;
            box.setImageDrawable(imageBoxRight);
        } else {
            //LEPAS
            boxX -= 14;
            box.setImageDrawable(imageBoxLeft);
        }

        // CHECK BOX POSITION
        if (boxX < 0){
            boxX = 0;
            box.setImageDrawable(imageBoxRight);
        }
        if (frameWidth - boxSize < boxX){
            boxX = frameWidth - boxSize;
            box.setImageDrawable(imageBoxLeft);
        }

        box.setX(boxX);

        skoranda.setText("Skor Kamu : " + score);
    }

    public boolean hitCheck(float x, float y){
        if (boxX <= x && x <= boxX + boxSize &&
                boxY <= y && y <= frameHeight){
            return true;
        }
        return false;
    }

    //Menyepitkan Tembok Setelah Terkena Tinja

    public void changeFrameWidth(int frameWidth){
        ViewGroup.LayoutParams params = frame.getLayoutParams();
        params.width = frameWidth;
        frame.setLayoutParams(params);
    }

    public void gameOver(){
        // Stop Timer.
        timer.cancel();
        timer = null;
        start_flg = false;

        //SEBELUM MUNCUL LAYOUT STARTNYA , NGEFREEZE DULU 1 DETIK
        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFramewidth);

        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        tinja.setVisibility(View.INVISIBLE);
        orange.setVisibility(View.INVISIBLE);
        bonus.setVisibility(View.INVISIBLE);

        // Update SKOR TERTINGGI
        if (score > highscore){
            highscore = score;
            skortinggi.setText("SKOR TERTINGGIMU : " + highscore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highscore);
            editor.commit();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP){
                action_flg = false;
            }
        }
        return true;
    }

    // Mulai Game
    public void mulaiGame (View view) {
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight == 0) {
            frameHeight = frame.getHeight();
            frameWidth  = frame.getWidth();
            initialFramewidth = frameWidth;

            boxSize = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidth = initialFramewidth;

        box.setX(0.0f);
        tinja.setY(3000.0f);
        orange.setY(3000.0f);
        bonus.setY(3000.0f);

        tinjaY = tinja.getY();
        orangeY = orange.getY();
        bonusY = bonus.getY();

        box.setVisibility(View.VISIBLE);
        tinja.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        bonus.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        skoranda.setText("Skor Kamu : 0");


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }


    //Tombol Keluar
    public void quitGame(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

}