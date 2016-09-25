package com.kumaj.customview.widget.OctoCatView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.kumaj.customview.R;

public class DemoActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octo_cat_dialog_demo);

        OctoCatView octoCatView = (OctoCatView) findViewById(R.id.octoCatView);
        Button btnStart = (Button) findViewById(R.id.btn_start);
        Button btnStop = (Button) findViewById(R.id.btn_stop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                octoCatView.startAnimation();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                octoCatView.stopAnimation();
            }
        });
    }
}