package com.example.pilotapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class SetRouteName extends AppCompatActivity {

    EditText setNameTextView;
    MaterialCardView startRecordingButton;
    String routeName;

    Context con = null;

    protected static final String Extra_name = "routName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_route_name);

        setNameTextView = findViewById(R.id.setNameTV);
        startRecordingButton = findViewById(R.id.startRecordingBtn);
        con = getApplicationContext();



        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               routeName = setNameTextView.getText().toString();

                if(routeName.isEmpty()) {
                    Toast.makeText(SetRouteName.this, "Wprowadź nazwę trasy!", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(SetRouteName.this, RecordingRoute.class);
                    intent.putExtra(Extra_name, routeName);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){

        startActivity(new Intent(SetRouteName.this, MainActivity.class));
        finish();
    }
}