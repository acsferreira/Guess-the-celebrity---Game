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
    String urlCelebrities="https://www.imdb.com/list/ls052283250",htmlText;
    ImageView imageView;
    int celebrityIndex;
    ArrayList<String> names=new ArrayList<String>();
    ArrayList<String> images=new ArrayList<String>();
    Random rand=new Random();
    ArrayList<Integer> options=new ArrayList<Integer>();

    public void downloadImage(View view){
        Log.i("Button tapped","It worked!");
        DownloadImage task=new DownloadImage();
        Bitmap myImage;
        try {
            myImage=task.execute(urlCelebrities).get();
            imageView.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void,String>{

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

    public void setGame(String html){
        names.clear();
        images.clear();
        // get url from the images
        Pattern p=Pattern.compile("src=\"(.*?)jpg\"");
        Matcher m=p.matcher(html);
        while (m.find()) {
            images.add(m.group(1));
        }

        // get name of the celebrities
        p=Pattern.compile("<img alt=\"(.*?)\"");
        m=p.matcher(html);

        while (m.find()){
            names.add(m.group(1));
        }
        //random celebrity and options
        celebrityIndex=rand.nextInt(names.size()+1);
        options.add(celebrityIndex);
        int aux;
        for (int i=1;i<4;i++){
            aux=rand.nextInt(names.size()+1);
            if(!options.contains(aux)){
                options.add(aux);
            }
        }
        Collections.shuffle(options);

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

        Log.i("Celebrity",names.get(celebrityIndex));
        Log.i("Options",names.get(options.get(0))+" "+names.get(options.get(1))+" "+names.get(options.get(2))+" "+names.get(options.get(3)));

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
        Button pressed=(Button) view;
        int answer=(int) pressed.getTag();
        if (answer==celebrityIndex){
            Toast.makeText(this,"Correct!",Toast.LENGTH_SHORT).show();
            setGame(htmlText);
        } else {
            Toast.makeText(this,"Wrong... It was "+names.get(celebrityIndex)+"!",Toast.LENGTH_SHORT).show();
            setGame(htmlText);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView) findViewById(R.id.imageView);
        // take the html from the website
        DownloadTask task=new DownloadTask();
        try {
            htmlText=task.execute(urlCelebrities).get();
        } catch (Exception e){
            Log.i("Error","Didnt work");
            e.printStackTrace();
        }

        htmlText.replace("\n","").replace("  ","");
        setGame(htmlText);
    }
}

