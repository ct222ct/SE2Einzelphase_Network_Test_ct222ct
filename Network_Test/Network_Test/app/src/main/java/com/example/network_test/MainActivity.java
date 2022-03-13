package com.example.network_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    Button verbinden;
    Button sortieren;
    EditText matrikelnummer;
    TextView geordneteZahlen;
    TextView serverAntwort;
    String ausgabe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geordneteZahlen = (TextView) findViewById(R.id.sortierteMatrikelnummer);
        matrikelnummer = (EditText)findViewById(R.id.editTextNumber);
        serverAntwort = (TextView) findViewById(R.id.serverAntwort);
        verbinden = (Button) findViewById(R.id.verbinden);
        sortieren = (Button) findViewById(R.id.sortieren);

        verbinden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TCP_Client().execute();
                System.out.println("Verbunden");
            }
        });

        sortieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int[] zahlenArray = new int[matrikelnummer.length()];

                for(int i=0;i<matrikelnummer.length();i++) {
                    zahlenArray[i] = Character.getNumericValue(matrikelnummer.getText().charAt(i));
                }
                geordneteZahlen.setText(matrikelnummer_sort(zahlenArray));
            }
        });

    }



    public  String matrikelnummer_sort(int[] number){
        ArrayList<Integer> geradeZahlen = new ArrayList<>();
        ArrayList<Integer> ungeradeZahlen = new ArrayList<>();

        String nummern = new String();

        for (int i = 0; i < number.length; i++ ) {
            if (number[i] % 2 == 0) {
                geradeZahlen.add(number[i]);
            }
            if (number[i]%2 == 1){
                ungeradeZahlen.add(number[i]);
            }
        }
        Collections.sort(geradeZahlen);
        Collections.sort(ungeradeZahlen);

        for (int i = 0; i < geradeZahlen.size(); i++ ) {
            nummern = nummern + geradeZahlen.get(i).toString();
        }
        for (int i = 0; i < ungeradeZahlen.size(); i++ ) {
            nummern = nummern + ungeradeZahlen.get(i).toString();
        }
        return nummern;
    }


    class TCP_Client extends AsyncTask<Void, Void, Void> {

        String antwort;

        @Override
        protected Void doInBackground(Void... voids) {

            OutputStreamWriter outputStreamWriter = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bR = null;
            BufferedWriter bW = null;
            matrikelnummer = (EditText) findViewById(R.id.editTextNumber);

            Socket socket = null;

            try {
                socket = new Socket("se2-isys.aau.at",53212);

                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                bR = new BufferedReader(inputStreamReader);
                bW = new BufferedWriter(outputStreamWriter);

                bW.write(matrikelnummer.getText().toString());
                bW.newLine();
                bW.flush();

                antwort = bR.readLine();

                System.out.println("Server-Antwort: " + antwort);

            } catch (IOException e) {
                e.printStackTrace();
                antwort = "UnknownHostException: " + e.toString();

            } finally {
                try {
                    if(socket != null)
                        socket.close();

                    if(inputStreamReader != null)
                        inputStreamReader.close();

                    if(outputStreamWriter != null)
                        outputStreamWriter.close();

                    if(bR != null)
                        bR.close();

                    if(bW != null)
                        bW.close();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //serverAntwort = antwort;

            serverAntwort.setText(antwort);
        }
    }
}
