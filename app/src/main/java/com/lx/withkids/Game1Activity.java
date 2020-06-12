package com.lx.withkids;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Game1Activity extends AppCompatActivity {

    private androidx.gridlayout.widget.GridLayout gridLayout;
    private List<Integer> listIndex;
    private int gameSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        gameSize = super.getIntent().getIntExtra("gameSize", 0);
        initListIndex();

        gridLayout = findViewById(R.id.GameGrid);
        gridLayout.setRowCount(gameSize);
        gridLayout.setColumnCount(gameSize);

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
                gridLayout.addView(t);
            }
        }
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
