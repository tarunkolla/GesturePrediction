package com.example.tkolla.guesturepredction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static android.app.PendingIntent.getActivity;
import static java.lang.Integer.parseInt;


public class MainActivity extends AppCompatActivity {


    public int PERMISSION_CODE = 1;

    public String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStart() {


        super.onStart();
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        }


        else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }

        else {
            //The app can do this all day till you allow access.
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public void onClick(View view) {
        switch(view.getId()){



            case R.id.button_Select:
                Toast.makeText(getApplicationContext(), "Select file", Toast.LENGTH_SHORT).show();

                Intent docfile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docfile.addCategory(Intent.CATEGORY_OPENABLE);
                docfile.setType("*/*");
                startActivityForResult(Intent.createChooser(docfile, "Open CSV"), 1);

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1: {

                if (resultCode == RESULT_OK) {
                    try {

                        Uri uri = data.getData();
                        File myFile = new File(uri.toString());
                        String path = myFile.getAbsolutePath();
                        String displayName = null;
                        String uriString = uri.toString();

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                        }



                        parseCSV(displayName);
                    } catch (IOException e) {
                        String message = e.getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    }

                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
    public int About = 0;
    public int Father = 0;
    public int Error = 0;

    public void parseCSV(String filename) throws IOException {
        File csvfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename );
        CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()) , CSVParser.DEFAULT_SEPARATOR,
                CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
        long start = System.currentTimeMillis();

        String [] eachRow;

        while((eachRow = reader.readNext()) != null){
            int result = decisionTree(eachRow);
            switch(result) {
                case 0:
                    About++;
                    break;
                case 1:
                    Father++;
                    break;
                case 2:
                    Error++;
                    break;
                default:
                    break;

            }


        }
        long runTime = (System.currentTimeMillis() - start);
        TextView mTextView = (TextView) findViewById(R.id.textView_Mobile);

        if(About > Father){

            mTextView.setText("The actions is classified as About\n" + "Run Time: "+ runTime + " Milliseconds");
        }
        else if(Father > About){

            mTextView.setText("The actions is classified as Father\n" + "Run Time: " + runTime  + " Milliseconds");

        }
        else if(Error > 0){
            mTextView.setText("");
            Toast.makeText(getApplicationContext(), "Error with selected file", Toast.LENGTH_SHORT).show();

        }
        else {

            mTextView.setText("The action cannot be classified \n"  + "Run Time: " + runTime  + " Milliseconds");
        }


    }


    public int decisionTree(String[] row) {

        if (row.length == 53) {
            if (Float.parseFloat(row[34]) < 161.525) {
                if (Float.parseFloat(row[32]) < 0.940401) {
                    if (Float.parseFloat(row[25]) < 172.152) {
                        return 0;
                    } else {
                        if (Float.parseFloat(row[17]) < 0.970375) {
                            return 0;
                        } else {
                            if (Float.parseFloat(row[51]) < -9.9028) {
                                return 0;
                            } else {
                                if (Float.parseFloat(row[25]) < 206.946) {
                                    if (Float.parseFloat(row[31]) < 210.386) {
                                        return 0;
                                    } else return 1;
                                } else {
                                    return 1;
                                }
                            }
                        }
                    }

                } else {
                    return 0;
                }

            } else {

                if (Float.parseFloat(row[31]) < 284.645) {
                    if (Float.parseFloat(row[16]) < 175.874) {
                        return 0;
                    } else return 1;
                } else {

                    if (Float.parseFloat(row[26]) < 0.931994) {
                        return 0;
                    } else {
                        if (Float.parseFloat(row[27]) < 21.6408) {
                            return 0;
                        } else return 1;
                    }
                }

            }

        }
     else return 2;
}

}

