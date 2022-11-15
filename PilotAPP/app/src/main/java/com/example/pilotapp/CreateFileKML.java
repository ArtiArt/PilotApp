package com.example.pilotapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class CreateFileKML {




    public void createFile(Context c, String fileName){
        FileOutputStream fos = null;
        Toast fileAdded = Toast.makeText(c, "Trasa dodana!", Toast.LENGTH_SHORT);

        try {
            fos = c.openFileOutput(fileName+".kml", Context.MODE_PRIVATE);
            fos.write("Fourth message".getBytes());

            Log.i("test","Saved to " + c.getFilesDir() + "/" + fileName+".kml");
            fileAdded.show();


        }catch(IOException e) {
            e.printStackTrace();
        }



    }
}
