package tw.com.s11113153.gif_animationdrawable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity {
  private final String TAG = MainActivity.class.getSimpleName();

  private static class AnimationHandler extends Handler {
    private final WeakReference<GifAnimationDrawable> mWeakReference;

    public AnimationHandler(GifAnimationDrawable drawable) {
      mWeakReference = new WeakReference(drawable);
    }

    @Override
    public void handleMessage(Message msg) {
      GifAnimationDrawable gif = mWeakReference.get();
      if (gif != null) {
        Log.d("running times =  ", String.valueOf(msg.obj));
        if ((int)msg.obj == 2) gif.stop();
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ImageView imageView = (ImageView) findViewById(R.id.image);
    int[][] drawableList = new int[][] {
      { R.drawable.abc_btn_check_material, 1000 },
      { R.drawable.abc_btn_radio_material, 500 },
      { R.drawable.abc_ic_go_search_api_mtrl_alpha, 500},
      { R.mipmap.ic_launcher, 3500},
    };

    GifAnimationDrawable gif = GifAnimationDrawable.builder(this).build(drawableList);
    imageView.setBackground(gif);
    gif.setOneShot(false);
    gif.getRunningTimes(new AnimationHandler(gif)).start();
  }
}
