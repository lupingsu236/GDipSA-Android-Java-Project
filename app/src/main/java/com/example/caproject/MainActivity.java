package com.example.caproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    private EditText urlInputField;
    private Button fetchBtn;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView9;
    private List<ImageView> imageViewList = new ArrayList<>();
    private String urlInput;

    private final int NUMBER_OF_IMAGES = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // to enable network calls on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageView1 = findViewById(R.id.imageView1);
        imageViewList.add(imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageViewList.add(imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageViewList.add(imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageViewList.add(imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageViewList.add(imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageViewList.add(imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageViewList.add(imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageViewList.add(imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageViewList.add(imageView9);

        urlInputField = findViewById(R.id.urlInput);
        urlInput = urlInputField.getText().toString();

        fetchBtn = findViewById(R.id.fetchBtn);
        fetchBtn.setOnClickListener(view -> fetchImageLinksIfUrlIsNonEmpty());

    }

    public void fetchImageLinksIfUrlIsNonEmpty()
    {
        urlInput = urlInputField.getText().toString();
        if (!urlInput.isEmpty())
        {
            new ExtractImageLinksFromHTML(urlInput).execute();
        }
        else
            Toast.makeText(this, "Please enter a url", Toast.LENGTH_SHORT).show();
    }

    public class ExtractImageLinksFromHTML extends AsyncTask<Void, Void, Void>
    {
        private String urlInput;

        public ExtractImageLinksFromHTML(String urlInput)
        {
            this.urlInput = urlInput;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            runOnUiThread(() -> {
                List<String> imageDownloadLinks = new ArrayList<>();
                try
                {
                    // parse html page for .jpg links and add to imageDownloadLinks list
                    URL url = new URL(urlInput);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line = null;
                    int counter = 0;
                    while ((line = reader.readLine()) != null)
                    {
                        if (line.contains(".jpg"))
                        {
                            imageDownloadLinks.add(line.substring(line.lastIndexOf("https"),
                                    line.lastIndexOf("jpg") + "jpg".length()));
                            Bitmap image = BitmapFactory.decodeStream((InputStream)new URL
                                    (imageDownloadLinks.get(counter)).getContent());
                            if (image != null)
                                imageViewList.get(counter).setImageBitmap(image);
                            counter = counter + 1;
                        }
                        if (imageDownloadLinks.size() == NUMBER_OF_IMAGES)
                            break;
                    }
                }
                catch (IllegalStateException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            Void v = null;
            return v;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            Toast.makeText(MainActivity.this, "Task done",
                    Toast.LENGTH_SHORT).show();
        }
    }
}