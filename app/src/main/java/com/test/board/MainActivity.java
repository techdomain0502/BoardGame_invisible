package com.test.board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends ActionBarActivity {

    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        board = (com.test.board.Board)findViewById(R.id.board);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle  i = getIntent().getExtras();
        String grid = i.get("grid").toString();
        if(grid != null)
        Toast.makeText(this,grid,Toast.LENGTH_SHORT).show();
        board.initBoard(3);
    }

}
