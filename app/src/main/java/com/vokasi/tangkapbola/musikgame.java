package com.vokasi.tangkapbola;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class musikgame {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;

    private static SoundPool soundPool;
    private static int hitOrangeSound;
    private static int hitBonusSound;
    private static int hitTinjaSound;

    public musikgame(Context context) {

        // SoundPool is deprecated in API level 21. (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        hitOrangeSound = soundPool.load(context, R.raw.orange, 1);
        hitBonusSound = soundPool.load(context, R.raw.bonus, 1);
        hitTinjaSound = soundPool.load(context, R.raw.tinja, 1);
    }

    public void playHitOrangeSound() {
        soundPool.play(hitOrangeSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitBonusSound() {
        soundPool.play(hitBonusSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitTinjaSound() {
        soundPool.play(hitTinjaSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}


