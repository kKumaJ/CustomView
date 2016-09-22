package com.kumaj.customview.widget.OctoCatProgress;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.kumaj.customview.R;

public class DemoActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octo_cat_dialog_demo);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setBackgroundDrawable(new OctoCatDrawable.Builder(this).build());
    }
}
