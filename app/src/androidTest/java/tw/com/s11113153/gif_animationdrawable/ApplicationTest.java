package tw.com.s11113153.gif_animationdrawable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Application;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.widget.ImageView;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {

  private MainActivity mActivity;
  public ApplicationTest() {
    super(MainActivity.class);
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    mActivity = getActivity();
  }

  @Test
  public void test1() throws Exception {
    Espresso.onView(withId(R.id.title)).perform(typeText("ready to run"));
    int[][] drawableList = new int[][] {
      { R.drawable.abc_btn_check_material, 100 },
      { R.drawable.abc_btn_radio_material, 100 },
      { R.drawable.abc_ic_go_search_api_mtrl_alpha, 100},
      { R.mipmap.ic_launcher, 100},
    };

    final GifAnimationDrawable gif = GifAnimationDrawable.builder(mActivity).build(drawableList);
    final ImageView imageView = (ImageView) mActivity.findViewById(R.id.image);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        imageView.setBackground(gif);
        gif.setOneShot(false);
        gif.start();
      }
    });
    Thread.sleep(7000);
    gif.stop();
    Espresso.onView(withId(R.id.title)).perform(clearText());
    Espresso.onView(withId(R.id.title)).perform(typeText("ready to stop"));
    Thread.sleep(1000);
  }
}