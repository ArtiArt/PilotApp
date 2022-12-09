package com.example.pilotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.google.android.material.card.MaterialCardView;

<<<<<<< HEAD
public class ChooseRoute extends AppCompatActivity {
    private MaterialCardView chooseRouteButton;
=======
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    ListView listView;

    @SuppressLint("MissingInflatedId")
>>>>>>> ArtBranch
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_route);
<<<<<<< HEAD
        chooseRouteButton = findViewById(R.id.chooseRouteBtn);
        chooseRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseRoute.this, Racing.class));
                finish();
            }
        });

=======

        listView = findViewById(R.id.listOfTraces);

        File trace = new File("/data/user/0/com.example.pilotapp/app_allRoutes/");

        String[] arr = trace.list();

        list = new ArrayList<>();

        assert arr != null;
        list = getFiles(arr);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String chosen = ((TextView) view).getText().toString() + ".kml";
            Intent startTrace = new Intent(getApplicationContext(), Racing.class);
            startTrace.putExtra("chosenTrace", chosen);
            startActivity(startTrace);
        });
    }
    private ArrayList<String> getFiles(String[] arr){
        list = new ArrayList<>();
        for(String s : arr){
            if(s.endsWith(".kml")){
                s = s.substring(0, s.length() - 4);
                list.add(s);
            }
        }
        return list;
>>>>>>> ArtBranch
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(ChooseRoute.this, MainActivity.class));
        finish();
    }

}