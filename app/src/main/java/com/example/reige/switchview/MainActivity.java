package com.example.reige.switchview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.reige.switchview.view.SwitchView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwitchView switchView = (SwitchView) findViewById(R.id.switch_view);
/*
        switchView.setButton(R.mipmap.switch_button);
        switchView.setSliderBackground(R.mipmap.slider_background);
        switchView.setSwitchBackground(R.mipmap.switch_background);*/
        switchView.setOnStateChangeListener(new SwitchView.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean state) {
                Toast.makeText(getApplicationContext(),"开关"+state,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
