package com.lx.withkids;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */


public class Game1Activity extends AppCompatActivity{
    private androidx.gridlayout.widget.GridLayout gridLayout;
    private List<Integer> listIndex;
    private int gameSize;
    private int iFind;
    private TextView costTime;
    private int iCostTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        iFind = 1;
        gameSize = super.getIntent().getIntExtra("gameSize", 0);
        initListIndex();

        gridLayout = findViewById(R.id.GameGrid);
        gridLayout.setRowCount(gameSize);
        gridLayout.setColumnCount(gameSize);
        gridLayout.setClickable(true);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);

        int screenWidth = point.x;
        int iTextWidth = screenWidth / gameSize;
        for (int iRow = 0; iRow<gameSize;iRow++){
            for(int iCol = 0; iCol < gameSize;iCol++){
                TextView t = new TextView(this);
                t.setText(Integer.toString(getListIndex()));
                t.setWidth(iTextWidth);
                t.setHeight(iTextWidth);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(100/gameSize);
                t.setTextColor(android.graphics.Color.BLACK);
                int color = 0;
                color += (30+Math.random()*(255-30+1));
                color += 0x100*(30+Math.random()*(255-30+1));
                color += 0x10000*(30+Math.random()*(255-30+1));
                color += 0xFF000000;
                t.setBackgroundColor(color);
                t.setClickable(true);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String index = ((TextView)arg0).getText().toString();
                        if(Integer.toString(iFind) == index){
                            ((TextView)arg0).setVisibility(View.INVISIBLE);
                            iFind++;
                            if(iFind>gameSize*gameSize){
                                String record = "";
                                try {
                                    SharedPreferences share = getApplicationContext().getSharedPreferences("records.properties", Context.MODE_PRIVATE);
                                    record = share.getString("schultegrid"+Integer.toString(gameSize), "9999");

                                    if(Integer.parseInt(record) > iCostTime) {
                                        Map<String, String> mqttinfomap = new HashMap<String, String>();
                                        mqttinfomap.put("schultegrid" + Integer.toString(gameSize), Integer.toString(iCostTime));
                                        SharedPreferences.Editor editor = share.edit();//取得编辑器
                                        Set<Map.Entry<String, String>> set = mqttinfomap.entrySet();
                                        // 遍历键值对对象的集合，得到每一个键值对对象
                                        for (Map.Entry<String, String> me : set) {
                                            // 根据键值对对象获取键和值
                                            String key = me.getKey();
                                            String value = me.getValue();
                                            editor.putString(key, value);//存储配置 参数1 是key 参数2 是值
                                        }
                                        editor.commit();//提交刷新数据
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                                Toast toast=Toast.makeText(Game1Activity.this,
                                        "congratulations, cost " + Integer.toString(iCostTime) + " seconds, " +
                                        "best record " + record + " seconds",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 800);
                            }
                        }
                    }
                });
                gridLayout.addView(t);
            }
        }

        costTime = findViewById(R.id.costTime);
        costTime.setVisibility(View.INVISIBLE);
        iCostTime = 0;
        final Handler mHandler = new Handler();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                //do something
                //每隔1s循环执行run方法
                try {
                    costTime.setText(Integer.toString(iCostTime++));
                    mHandler.postDelayed(this, 1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        mHandler.postDelayed(r, 100);//延时100毫秒

    }


    public static Intent toGame1Activity(Context mContext, int gameSize) {
        Intent intent = new Intent(mContext, Game1Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("gameSize", gameSize);
        return intent;
    }

    private void initListIndex(){
        listIndex = new ArrayList<>();
        for (int i = 0;i< gameSize * gameSize; i++){
            listIndex.add(i+1);
        }
    }

    protected Integer getListIndex(){
        int len = listIndex.size();
        int chosen = (int)(Math.random()*(len));
        int index = listIndex.get(chosen);
        listIndex.remove(chosen);
        return index;
    }
}
