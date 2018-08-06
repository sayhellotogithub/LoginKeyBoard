package iblogstreet.com.loginkeyboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends Activity {
    private ScrollView mScrollView;
    private int screenHeight = 0;//屏幕高度
    private int keyHeight = 0; //软件盘弹起后所占高度
    private boolean isPop = false;
    private static final String TAG = MainActivity.class.getName();
    private LinearLayout llAccount;
    private TextView mTvLoginTitle;
    private LinearLayout mLLMain;
    private TextView mTvCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getSupportActionBar().hide();
        // 这里不能设置，如果设置的话，oldBottom和bottom永远不会变
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        /**
         * 禁止键盘弹起的时候可以滚动
         */
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mScrollView.addOnLayoutChangeListener(new ViewGroup.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
      /* old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
      现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起*/
                Log.e(TAG, "Math.abs(oldBottom - bottom)" + Math.abs(oldBottom - bottom) + ",keyHeight" + keyHeight + "mLLMain.getBottom()" + mLLMain.getBottom());

                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom) > 0 && !isPop) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llAccount.getLayoutParams();
                    layoutParams.topMargin = DensityUtil.dip2px(MainActivity.this, 0);
                    llAccount.setLayoutParams(layoutParams);

                    int dist = mLLMain.getBottom() - bottom;
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(mLLMain, "translationY", 0.0f, -Math.abs(dist));
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
                    zoomIn(mTvLoginTitle, 0.6f, mTvLoginTitle.getTop() - mTvLoginTitle.getHeight());
                    mTvCopyright.setVisibility(View.INVISIBLE);
                    isPop = true;

                } else if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom) < 0 && isPop) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llAccount.getLayoutParams();
                    layoutParams.topMargin = DensityUtil.dip2px(MainActivity.this, 42);
                    llAccount.setLayoutParams(layoutParams);
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(mLLMain, "translationY", mLLMain.getTranslationY(), 0);
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
                    //键盘收回后，logo恢复原来大小，位置同样回到初始位置
                    zoomOut(mTvLoginTitle, 0.6f);
                    isPop = false;
                    mTvCopyright.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    private void initData() {
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        keyHeight = screenHeight / 3;//弹起高度为屏幕高度的1/3
    }


    private void initView() {
        mScrollView = findViewById(R.id.scrollView);
        llAccount = findViewById(R.id.ll_account);
        mTvLoginTitle = findViewById(R.id.tv_login_title);
        mLLMain = findViewById(R.id.ll_main);
        mTvCopyright = findViewById(R.id.tv_copyright);
    }

    /**
     * f放大
     *
     * @param view
     */
    public static void zoomOut(final View view, float scale) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }

    /**
     * 缩小
     *
     * @param view
     */
    public static void zoomIn(final View view, float scale, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();

    }
}
