package com.example.pilotapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateFileKML {

    private String kmlstart;
    private String kmlelement;
    private String kmlend;

    private String routName;

    private FileOutputStream fos = null;
    private int point = 1;

    private File currentFile;

    public CreateFileKML(Context c, String fileName) {
        //Add new directory
        File mydir = c. getDir("allRoutes", Context.MODE_APPEND);
        currentFile = new File(mydir, fileName + ".kml");

        try {
            setFos(new FileOutputStream(currentFile));
        } catch (IOException e){
            e.printStackTrace();
        }
        this.routName = fileName;
    }

//    Write start element to new kml file
    public void writeStartElementToFile() {
        kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"+
                "\t<Document>\n";

        try {
            getFos().write(kmlstart.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
//    Write current localisation to kml file
    public void writeElementsToFile(double lat, double lng) {
        kmlelement = "\n" +
                "\t<Placemark>\n" +
                "\t\t<name>Punkt "+ point +"</name>\n" +
                "\t<Point>\n" +
                "\t\t<coordinates>"+lat+","+lng+"</coordinates>\n" +
                "\t</Point>\n" +
                "\t</Placemark>\n";

        point++;

        try{
            getFos().write(kmlelement.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

//    Write ending element to kml file
    public void writeEndElementsToFile(){
        kmlend = "\n" +
                "\t</Document>\n" +
                "</kml>";
        try {
            getFos().write(kmlend.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void deleteFile(){
        currentFile.delete();
    }

    private FileOutputStream getFos() {
        return fos;
    }

    private void setFos(FileOutputStream fos) {
        this.fos = fos;
    }
}
