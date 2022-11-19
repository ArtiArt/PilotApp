package com.example.pilotapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateFileKML {

    double lat = 52.237049;
    double lng = 21.017532;

    private String kmlstart;
    private String kmlelement;
    private String kmlend;

    public void createFile(Context c, String fileName){

        File mydir = c. getDir("allRoutes", Context. MODE_PRIVATE);

        FileOutputStream fos = null;
        Toast fileAdded = Toast.makeText(c, "Trasa dodana!", Toast.LENGTH_SHORT);

        kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"+
                "\t<Document>\n";

        kmlelement = "\n" +
                "\t<Placemark>\n" +
                "\t<ExtendedData>\n" +
                "\t</ExtendedData>\n" +
                "\t<Point>\n" +
                "\t\t<coordinates>"+lng+","+lat+"</coordinates>\n" +
                "\t</Point>\n" +
                "\t</Placemark>\n";

        kmlend = "\n" +
                "\t</Document>\n" +
                "</kml>";

        try {
            fos = new FileOutputStream(new File(mydir, fileName+".kml"));
            fos.write(kmlstart.getBytes());
            fos.write(kmlelement.getBytes());
            fos.write(kmlend.getBytes());

            Log.i("test","Saved to " + c.getFilesDir() + "/" + fileName+".kml");
            fileAdded.show();


        }catch(IOException e) {
            e.printStackTrace();
        }



    }
}
