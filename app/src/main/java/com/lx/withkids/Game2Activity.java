package com.lx.withkids;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Game2Activity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private FrameLayout layoutCalc;
    private FrameLayout layoutResult;
    private DrawView drawView;
    private TextView tvl1;
    private TextView tvl2;
    private TextView tvl3;
    private TextView inputText;
    private androidx.gridlayout.widget.GridLayout inputGridLayout;
    private int iResult;
    private Button btnClearDraw;
    private int iType;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game2);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        drawView = findViewById(R.id.myDrawView);
        tvl1 = findViewById(R.id.textView1);
        tvl2 = findViewById(R.id.textView2);
        tvl3 = findViewById(R.id.textView3);
        inputText = findViewById(R.id.resultInput);
        inputGridLayout = findViewById(R.id.inputGrid);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        btnClearDraw = findViewById(R.id.buttonClear);
        btnClearDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clear();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        iType = super.getIntent().getIntExtra("Type", 0);

        initCalc();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //获取屏幕的宽度
    public int getScreenWidth() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return point.x;
    }

    //获取屏幕的高度
    public int getScreenHeight() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }

    private void initCalc() {
        tvl1.setTextSize(50);
        tvl2.setTextSize(50);
        tvl3.setTextSize(20);


        int iCalc, iFront, iBack;
        iCalc = iType; // 1加法 2减法 3乘法 4除法
        while (true) {
            if (1 == iCalc || 2 == iCalc) {
                iFront = (int) (1 + Math.random() * 100);
                iBack = (int) (1 + Math.random() * 100);
                if (1 == iCalc) {
                    if (iFront + iBack <= 100) {
                        break;
                    }
                } else {
                    if (iFront < iBack) {
                        int temp = iFront;
                        iFront = iBack;
                        iBack = temp;
                        break;
                    }
                }
            } else {
                iFront = (int) (1 + Math.random() * 9);
                iBack = (int) (1 + Math.random() * 9);
                break;
            }
        }
        if (1 == iCalc) {
            iResult = iFront + iBack;
            tvl2.setText("+       " + Integer.toString(iBack) + "      ");
        } else if (2 == iCalc) {
            iResult = iFront - iBack;
            tvl2.setText("-       " + Integer.toString(iBack) + "      ");
        } else if (3 == iCalc) {
            iResult = iFront * iBack;
            tvl2.setText("×       " + Integer.toString(iBack) + "      ");
        } else {
            int temp = iFront;
            iFront = iFront * iBack;
            iResult = temp;
            tvl2.setText("÷       " + Integer.toString(iBack) + "      ");
        }

        tvl1.setText(Integer.toString(iFront) + "      ");
        tvl3.setText("_________________________________    ");

        inputText.setText("");
        inputText.setTextSize(30);
        inputGridLayout.setRowCount(4);
        inputGridLayout.setColumnCount(3);
        inputGridLayout.setClickable(true);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);

        //注意此时是横屏的
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) findViewById(R.id.resultInerLayout).getLayoutParams();
        int screenWidth = point.y;
        int screenHeight = point.x;
        int iButtonWidth = ((screenWidth / 2) - lp.leftMargin - lp.rightMargin) / 3;
        int iButtonHeight = (screenHeight - lp.topMargin - lp.bottomMargin - 100) / 4;
        for (int iRow = 0; iRow < 3; iRow++) {
            for (int iCol = 0; iCol < 4; iCol++) {
                Button t = new Button(this);
                final int index = iRow * 4 + iCol;
                if (0 == index) {
                    t.setText(Integer.toString(7));
                } else if (1 == index) {
                    t.setText(Integer.toString(8));
                } else if (2 == index) {
                    t.setText(Integer.toString(9));
                } else if (3 == index) {
                    t.setText(Integer.toString(4));
                } else if (4 == index) {
                    t.setText(Integer.toString(5));
                } else if (5 == index) {
                    t.setText(Integer.toString(6));
                } else if (6 == index) {
                    t.setText(Integer.toString(1));
                } else if (7 == index) {
                    t.setText(Integer.toString(2));
                } else if (8 == index) {
                    t.setText(Integer.toString(3));
                } else if (9 == index) {
                    t.setText("C");
                } else if (10 == index) {
                    t.setText(Integer.toString(0));
                } else if (11 == index) {
                    t.setText("OK");
                }
                t.setWidth(iButtonWidth);
                t.setHeight(iButtonHeight);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(40);
                t.setTextColor(android.graphics.Color.BLACK);
                int color = 0;
                color += (30 + Math.random() * (255 - 30 + 1));
                color += 0x100 * (30 + Math.random() * (255 - 30 + 1));
                color += 0x10000 * (30 + Math.random() * (255 - 30 + 1));
                color += 0xFF000000;
                t.setBackgroundColor(color);
                t.setClickable(true);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String btnText = ((TextView) arg0).getText().toString();
                        if ("C" == btnText) {
                            inputText.setText("");
                        } else if ("OK" == btnText) {
                            String strResult = inputText.getText().toString();
                            if (strResult.equals(Integer.toString(iResult))) {
                                Toast toast = Toast.makeText(Game2Activity.this, "congratulations", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 800);
                            } else {
                                Toast toast = Toast.makeText(Game2Activity.this, "calculation error", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            String temp = inputText.getText().toString();
                            inputText.setText(temp + btnText);
                        }
                    }
                });
                inputGridLayout.addView(t);
            }
        }
    }


}
