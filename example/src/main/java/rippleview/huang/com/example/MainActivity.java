package rippleview.huang.com.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import rippleview.huang.com.RippleStyle;
import rippleview.huang.com.RippleView;
import rippleview.huang.com.example.view.bubbleSeekBar.BubbleSeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RippleView mRippleView;
    private TextView mTvStyle;
    private ImageView mIbToggle;

    private int[] mColors = {R.color.gitstar, R.color.blue, R.color.green,
            R.color.purple, R.color.red, R.color.yellow};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRippleView = (RippleView) findViewById(R.id.ripple_player);
        changeViewSize();
        changeStyle();

        mIbToggle = (ImageView) findViewById(R.id.ib_toggle);
        mIbToggle.setOnClickListener(this);
        findViewById(R.id.btn_amount).setOnClickListener(this);
        findViewById(R.id.btn_change_color).setOnClickListener(this);
        findViewById(R.id.btn_scale).setOnClickListener(this);

        changeStrokeWidth();
        changeDuration();
    }

    /**
     * 改变view大小
     */
    private void changeViewSize() {
        final BubbleSeekBar bsbViewSize = (BubbleSeekBar) findViewById(R.id.bsb_view_size);
        mRippleView.setOnGetViewSizeListener(new RippleView.OnGetViewSizeListener() {
            @Override
            public void onGetSize(int size) {
                bsbViewSize.setProgressRange(50, size);
                mRippleView.setOnGetViewSizeListener(null);

            }
        });

        bsbViewSize.setOnProgressChangedListener(
                new BubbleSeekBar.OnProgressChangedListenerAdapter() {

                    @Override
                    public void getProgressOnActionUp(int progress) {

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRippleView.getLayoutParams();
                        params.width = progress;
                        params.gravity = Gravity.CENTER;
                        mRippleView.setLayoutParams(params);
                        if (mRippleView.getOnGetViewSizeListener() == null) {
                            mRippleView.setOnGetViewSizeListener(new RippleView.OnGetViewSizeListener() {
                                @Override
                                public void onGetSize(int size) {
                                    mRippleView.rippleReset();
                                    mRippleView.setOnGetViewSizeListener(null);
                                }
                            });
                        }
                    }
                });
    }

    /**
     * 修改动画时间
     */
    private void changeDuration() {
        ((BubbleSeekBar) findViewById(R.id.bsb_duration))
                .setOnProgressChangedListener(
                        new BubbleSeekBar.OnProgressChangedListenerAdapter() {

                            @Override
                            public void getProgressOnActionUp(int progress) {
                                mRippleView.setRippleDuration(progress);
                            }
                        });
    }

    /**
     * 修改线条粗细
     */
    private void changeStrokeWidth() {
        ((BubbleSeekBar) findViewById(R.id.bsb_stroke_width)).setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {

            @Override
            public void getProgressOnActionUp(float progress) {
                mRippleView.setRippleStrokeWidth(progress);
            }
        });
    }

    /**
     * style
     */
    private void changeStyle() {
        mTvStyle = (TextView) findViewById(R.id.tv_style);
        mTvStyle.setText(mRippleView.getRippleStyle().name());
        ((SwitchCompat) findViewById(R.id.sc_style)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mTvStyle.setText(RippleStyle.STROKE.name());
                    mRippleView.setRippleStyle(RippleStyle.STROKE);
                    findViewById(R.id.ll_stroke_width).setVisibility(View.VISIBLE);
                } else {
                    mTvStyle.setText(RippleStyle.FILL.name());
                    mRippleView.setRippleStyle(RippleStyle.FILL);
                    findViewById(R.id.ll_stroke_width).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_toggle://开关

                mRippleView.toggle();

                if (mRippleView.isRippleRunning()) {
                    mIbToggle.setImageResource(R.mipmap.ic_start);
                } else {
                    mIbToggle.setImageResource(R.mipmap.ic_stop);
                }
                break;
            case R.id.btn_change_color://改变颜色

                int index = (int) (Math.random() * 10 % mColors.length);
                mRippleView.setRippleColor(mColors[index]);
                break;
            case R.id.btn_amount://改变波纹数量

                String amount = String.valueOf(((EditText) findViewById(R.id.et_amount)).getText());
                if (!TextUtils.isEmpty(amount)) {
                    mRippleView.setRippleAmount(Integer.parseInt(amount));
                }
                break;
            case R.id.btn_scale://改变缩放比
                String scale = String.valueOf(((EditText) findViewById(R.id.et_scale)).getText());
                if (!TextUtils.isEmpty(scale)) {
                    mRippleView.setRippleScale(Float.parseFloat(scale));
                }
                break;
        }
    }
}
