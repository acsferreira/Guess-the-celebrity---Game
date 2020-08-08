package com.example.and.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    // webside where we take the image and name of some celebrities
    String urlCelebrities="https://www.imdb.com/list/ls052283250",htmlText=null;
    ImageView imageView;
    TextView textView;
    int celebrityIndex,games,score;
    ArrayList<String> names=new ArrayList<String>();
    ArrayList<String> images=new ArrayList<String>();
    Random rand=new Random();
    ArrayList<Integer> options=new ArrayList<Integer>();

    public class DownloadTask extends AsyncTask<String, Void,String>{
        // download the html source code and save it in a string
        @Override
        protected String doInBackground(String... urls) {
            Log.i("URL",urls[0]);
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data=reader.read();
                char current;
                while (data!=-1){
                    current = (char) data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            } catch (Exception e) {
                //MalformedURL
                e.printStackTrace();
                return "Failed!";
            }

        }
    }

    public class DownloadImage extends AsyncTask<String,Void,Bitmap>{
        // download the image from the url and keep in a variable
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void restart(View view){
        score=0;
        games=0;
        textView.setText(String.format("%02d/%02d",score,games));
        setGame(htmlText);
    }

    public void setGame(String html){
        options.clear();
        //randomly generate the chosen celebrity and some more options
        celebrityIndex=rand.nextInt(names.size());
        options.add(celebrityIndex);
        int aux;
        while (options.size()<4){
            aux=rand.nextInt(names.size());
            if(!options.contains(aux)){
                options.add(aux);
            }
        }
        // shuffle the array to make the options random
        Collections.shuffle(options);

        // set all the butons with the options
        Button option1=(Button) findViewById(R.id.button1);
        option1.setTag(options.get(0));
        option1.setText(names.get(options.get(0)));

        Button option2=(Button) findViewById(R.id.button2);
        option2.setTag(options.get(1));
        option2.setText(names.get(options.get(1)));

        Button option3=(Button) findViewById(R.id.button3);
        option3.setTag(options.get(2));
        option3.setText(names.get(options.get(2)));

        Button option4=(Button) findViewById(R.id.button4);
        option4.setTag(options.get(3));
        option4.setText(names.get(options.get(3)));

        // print some important info to check
        Log.i("Celebrity",celebrityIndex+" "+names.get(celebrityIndex));
        Log.i("Image",images.get(celebrityIndex));
        Log.i("Options",options.get(0)+" "+names.get(options.get(0))+" "+options.get(1)+" "+names.get(options.get(1))+" "+options.get(2)+" "+names.get(options.get(2))+" "+options.get(3)+" "+names.get(options.get(3)));

        // set the image in the image View
        DownloadImage task=new DownloadImage();
        try {
            Bitmap celebrityImage=task.execute(images.get(celebrityIndex)).get();
            imageView.setImageBitmap(celebrityImage);
        } catch (Exception e){
            Log.i("Error","Didnt work");
            e.printStackTrace();
        }

    }

    public void checkAnswer(View view){
        // check if the option chosen it is the correct
        Button pressed=(Button) view;
        int answer=(int) pressed.getTag();
        // the tags are the index of the celebrities in the names and images arrays
        if (answer==celebrityIndex){
            Toast.makeText(this,"Correct!",Toast.LENGTH_SHORT).show();
            score++;
            setGame(htmlText);
        } else {
            Toast.makeText(this,"Wrong... It was "+names.get(celebrityIndex)+"!",Toast.LENGTH_SHORT).show();
            setGame(htmlText);
        }
        games++;
        textView.setText(String.format("%02d/%02d",score,games));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView) findViewById(R.id.imageView);
        textView=(TextView) findViewById(R.id.textView);
        // take the html from the website
        DownloadTask task=new DownloadTask();
        try {
            htmlText=task.execute(urlCelebrities).get();
            htmlText.replace("\n","").replace("  ","");
            // generate the names of the celebrities and set the image of the chosen one
            names.clear();
            images.clear();
            // get urls for the images in the big string of html
            Pattern p=Pattern.compile("src=\"(.*?)jpg\"");
            Matcher m=p.matcher(htmlText);
            while (m.find()) {
                images.add(m.group(1));
            }
            // get the names of the celebrities
            p=Pattern.compile("<img alt=\"(.*?)\"");
            m=p.matcher(htmlText);
            while (m.find()){
                names.add(m.group(1));
            }
            setGame(htmlText);
        } catch (Exception e){
            Log.i("Error","Didnt work");
            e.printStackTrace();
        }
    }
}

