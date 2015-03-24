package tw.com.s11113153.gif_animationdrawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by xuyouren on 15/3/23.
 */
public class GifAnimationDrawable extends AnimationDrawable {
  private final String TAG = GifAnimationDrawable.class.getSimpleName();

  // We need to keep our own frame count because mCurFrame in AnimationDrawable is private
  private int mCurFrameHack = -1;

  // This is the Runnable that we will call when the animation finishes
  private Runnable mCallback = null;

//  public GifAnimationDrawable(AnimationDrawable aniDrawable) {
//    for(int i=0;i<aniDrawable.getNumberOfFrames();i++){
//      this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
//    }
//  }

//  public GifAnimationDrawable(Context context, boolean isOneShot, int duration) {
//    this.mContext = context;
//    addFrame(mContext.getResources().getDrawable(R.mipmap.ic_launcher), duration);
//    addFrame(mContext.getResources().getDrawable(R.drawable.abc_btn_check_material), duration);
//    addFrame(mContext.getResources().getDrawable(R.drawable.abc_btn_radio_to_on_mtrl_000), duration);
//    addFrame(mContext.getResources().getDrawable(R.drawable.abc_list_focused_holo), duration);
//    this.setOneShot(isOneShot);
//  }

  private final Context mContext;

  private GifAnimationDrawable(Builder builder) {
    mContext = builder.context;
    switch (builder.constructor) {
      case DRAWABLE:
            builder.duration = builder.duration < 0 ? 1000 : builder.duration;
            for (int i : builder.drawable)
                    addFrame(mContext.getResources().getDrawable(i), builder.duration);
        break;

      case DRAWABLE_AND_DURATION:
            int count = 0;
            // i[0] : drawableID, i[1] : duration
            for (int i[] : builder.drawableArray) {
              try {
                Drawable d = mContext.getResources().getDrawable(i[0]);
                i[1] = i[1] < 0 ? 1000 : i[1];
                addFrame(d, i[1]);
              } catch (Resources.NotFoundException e) {
                Log.d(TAG, "drawable[" + count + "][] is error");
              }
              count ++;
            }
        break;

      default:
        throw new IllegalAccessError("build error");
    }
  }

  public static IBuilder builder(Context context) {
    return new Builder(context);
  }

  /**
   * @return total of drawable in AnimationDrawable
   */
  public int getFrameCount() {
    return getNumberOfFrames();
  }

  /**
   * We override the run method in order to increment the frame count the same
   * way the AnimationDrawable class does. Also, when we are on the last frame we
   * schedule our callback to be called after the duration of the last frame.
   */

  private int mRunningTimes = 0;
  @Override
  public void run() {
    super.run();
    if (mHandler != null) {
      Message message = Message.obtain();
      message.obj = mRunningTimes++;
      mHandler.sendMessage(message);
    }

    mCurFrameHack += 1;
    if (mCurFrameHack == (getNumberOfFrames() - 1) && mCallback != null && isOneShot()) {
      // 當讀取到最後Drawable時，把 mCallback 以及 Delay Time 丟給 Drawable 去呼叫結尾工作
      scheduleSelf(mCallback, SystemClock.uptimeMillis() + getDuration(mCurFrameHack));
      stop();
    }
  }

  @Override
  public void stop() {
    super.stop();
    mRunningTimes = 0;
  }

  private Handler mHandler;
  public GifAnimationDrawable getRunningTimes(Handler handler) {
    mHandler = handler;
    return this;
  }


  /**
   * We override this method simply to reset the frame count just as is done in AnimationDrawable.
   */
  @Override
  public void unscheduleSelf(Runnable what) {
    super.unscheduleSelf(what);
    mCurFrameHack = -1;
  }

  public void setOnFinishCallback(Runnable callback) {
    mCallback = callback;
  }

  public Runnable getOnFinishCallback() {
    return mCallback;
  }

  private static class Builder implements IBuilder {
    private int[][] drawableArray = null;
    private int[] drawable = null;
    private int duration = -1;
    private Constructor constructor = Constructor.INIT;

    private enum Constructor {
      INIT, DRAWABLE, DRAWABLE_AND_DURATION
    }

    private Context context;
    public Builder(Context context) {
      this.context = context;
    }

    @Override
    public GifAnimationDrawable build(int[] drawable, int duration) {
      this.constructor = Constructor.DRAWABLE;
      this.drawable = drawable;
      this.duration = duration;
      return new GifAnimationDrawable(this);
    }

    @Override
    public GifAnimationDrawable build(int[][] drawableArray) {
      this.constructor = Constructor.DRAWABLE_AND_DURATION;
      this.drawableArray = drawableArray;
      return new GifAnimationDrawable(this);
    }
  }

  public interface IBuilder {
    GifAnimationDrawable build(int[] drawable, int duration);
    GifAnimationDrawable build(int[][] drawableArray);
  }
}