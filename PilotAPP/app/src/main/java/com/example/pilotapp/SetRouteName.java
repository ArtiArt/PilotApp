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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_route_name);

        setNameTextView = findViewById(R.id.setNameTV);
        startRecordingButton = findViewById(R.id.startRecordingBtn);
        CreateFileKML fileCreator = new CreateFileKML();
        con = getApplicationContext();
        Toast emptyRout = Toast.makeText(this, "Wprowadź nazwę trasy!", Toast.LENGTH_SHORT);



        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               routeName = setNameTextView.getText().toString();

                if(routeName.isEmpty()) {
                   emptyRout.show();
                } else{
                    fileCreator.createFile(con, routeName);
                }
                startActivity(new Intent(SetRouteName.this, RecordingRoute.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SetRouteName.this, MainActivity.class));
        finish();
    }
}