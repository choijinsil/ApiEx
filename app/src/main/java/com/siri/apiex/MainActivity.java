package com.siri.apiex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_join, btn_list;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_join= findViewById(R.id.btn_join);
        btn_list= findViewById(R.id.btn_list);

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(MainActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }
}
