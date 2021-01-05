package com.example.caproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private EditText urlInputField;
    private Button fetchBtn;
    private List<ImageView> imageViewList = new ArrayList<>();
    private String urlInput;
    private ProgressBar progressBar;
    private TextView progressText;
    private ExtractImageLinksFromHTML currentTask;

    private static final String IMGURL_REG = "<img.*src=\"(.*?)\"";
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // to enable network calls on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        for (int i=1; i<=20; i++) {
            String imageID = "image" + i;
            int resID = getResources().getIdentifier(imageID, "id", getPackageName());
            imageViewList.add(findViewById(resID));
        }

        urlInputField = findViewById(R.id.urlInput);
        urlInput = urlInputField.getText().toString();

        fetchBtn = findViewById(R.id.fetchBtn);
        fetchBtn.setOnClickListener(view -> fetchImageLinksIfUrlIsNonEmpty());

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

    }

    public void fetchImageLinksIfUrlIsNonEmpty() {
        //if no change in url, return
        if (!urlInput.isEmpty() && urlInput.equals(urlInputField.getText().toString()))
            return;
        //else set new url to urlInput
        urlInput = urlInputField.getText().toString();

        if (!urlInput.isEmpty()) {
            if (currentTask!=null) {
                //cancel current task if running
                currentTask.cancel(true);
            }
            //start new task
            currentTask = new ExtractImageLinksFromHTML(urlInput);
            currentTask.execute();
        } else
            Toast.makeText(this, "Please enter url", Toast.LENGTH_SHORT).show();
    }

    public class ExtractImageLinksFromHTML extends AsyncTask<Void, Void, Void> {
        private String urlInput;

        public ExtractImageLinksFromHTML(String urlInput) {
            this.urlInput = urlInput;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<String> imageDownloadLinks = new ArrayList<>();
            try {
                URL url = new URL(urlInput);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36)");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    sb.append(line, 0, line.length());
                    sb.append('\n');
                }
                reader.close();
                inputStream.close();
                Matcher matcher = Pattern.compile(IMGURL_REG).matcher(sb.toString());
                List<String> listimgurl = new ArrayList<>();
                while (matcher.find()) {
                    listimgurl.add(matcher.group());
                }
                int counter = 0;
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    progressBar.setMax(Math.min(20, listimgurl.size()));
                    progressText.setText("Downloading 1 of 20 images");
                    //reset all images to placeholder
                    for (ImageView image : imageViewList) {
                        image.setImageResource(R.drawable.image_placeholder);
                    }
                });
                for (String imgurl : listimgurl) {
                    Matcher matc = Pattern.compile(IMGSRC_REG).matcher(imgurl);
                    while (matc.find()) {
                        imageDownloadLinks.add(matc.group().substring(0, matc.group().length() - 1));
                        Bitmap image = BitmapFactory.decodeStream((InputStream) new URL(imageDownloadLinks.get(counter)).getContent());
                        if (image != null)
                        {
                            final int threadCounter1 = counter;
                            runOnUiThread(() -> {
                                imageViewList.get(threadCounter1).setImageBitmap(image);
                                progressBar.incrementProgressBy(1);
                                progressText.setText("Downloading "+ (threadCounter1+1) +
                                        " of 20 images");
                            });
                            counter += 1;
                        }
                    }
                    if (imageDownloadLinks.size() == imageViewList.size()) break;
                }
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Void v = null;
            return v;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressText.setText("Download completed!");
        }
    }
}