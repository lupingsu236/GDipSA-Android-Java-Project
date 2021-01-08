package com.example.caproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private int noOfImages;
    private String difficulty;
    private boolean inactive = true;
    private EditText urlInputField;
    private Button fetchBtn;
    public List<String> imageDownloadLinks = new ArrayList<>();
    private List<ImageView> imageViewList = new ArrayList<>();
    private List<ImageView> imageSelected = new ArrayList<>();
    private ArrayList<String> imgSelecttoSend = new ArrayList<>();
    private String urlInput;
    private TextView selectText;
    private Button startGameBtn;
    private ProgressBar progressBar;
    private TextView progressText;
    private ExtractImageLinksFromHTML currentTask;

    private static final String IMGURL_REG = "<img.*src=\"(.*?)\"";
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get number of images based on difficulty from previous activity
        noOfImages = getIntent().getIntExtra("noOfImages", 0);

        // display difficulty in this activity
        difficulty = getIntent().getStringExtra("difficulty");
        TextView difficultyTextView = findViewById(R.id.difficulty);
        difficultyTextView.setText(String.format("%s%s %s", difficulty.substring(0,1).toUpperCase(),
                difficulty.substring(1), getString(R.string.difficulty)));


        // to enable network calls on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        for (int i = 1; i <= 20; i++) {
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

        selectText = findViewById(R.id.selectText);
        startGameBtn = findViewById(R.id.startGameBtn);
        for (int i = 0; i < 20; i++) {
            ImageView selected = imageViewList.get(i);
            int number = i;
            selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inactive) {
                        return;
                    }

                    selectText.setVisibility(View.VISIBLE);
                    startGameBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    if (number < imageDownloadLinks.size()) {
                        if (selected.getTag() != null){
                            selected.setForeground(getDrawable(R.drawable.image_border));
                            selected.setTag(null);
                            imageSelected.remove(selected);
                            imgSelecttoSend.remove(imageDownloadLinks.get(number));
                            selectText.setText(imageSelected.size() + "/" + noOfImages + " images selected");
                        } else {
                            if (imageSelected.size() < noOfImages) {
                                selected.setForeground((getDrawable(R.drawable.image_border_selected)));
                                selected.setTag("selected");
                                imageSelected.add(selected);
                                imgSelecttoSend.add(imageDownloadLinks.get(number));
                                selectText.setText(imageSelected.size() + "/" + noOfImages + " images selected");
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Unable to select",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


//      Send url list to gameactivity
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected.size() == noOfImages) {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("noOfImages", noOfImages);
                    bundle.putStringArrayList("urlSelectedtoSend", imgSelecttoSend);
                    bundle.putString("difficulty", difficulty);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Insufficient images selected!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchImageLinksIfUrlIsNonEmpty() {
        inactive = false;
        //if no change in url, return
        if (!urlInput.isEmpty() && urlInput.equals(urlInputField.getText().toString()))
            return;
        //else set new url to urlInput
        urlInput = urlInputField.getText().toString();

        if (!urlInput.isEmpty()) {
            hideSoftKeyboard(MainActivity.this);
            //check the url's start
            String urlInputNew = null;
            if (urlInput.startsWith("http:")) {
                urlInputNew = "https:" + urlInput.substring(6);
            } else if (!urlInput.startsWith("https://")) {
                urlInputNew = "https://" + urlInput;
            } else {
                urlInputNew = urlInput;
            }

            if (currentTask != null) {
                //cancel current task if running
                currentTask.cancel(true);
            }
            //start new task

            currentTask = new ExtractImageLinksFromHTML(urlInputNew);
            currentTask.execute();
            selectText.setVisibility(View.GONE);
            startGameBtn.setVisibility((View.GONE));
            imageSelected.clear();
            imgSelecttoSend.clear();
            selectText.setText("Select " + imageSelected.size() + "/" + noOfImages + " images");
            for (int i = 0; i < 20; i++) {
                ImageView selected = imageViewList.get(i);
                selected.setForeground(getDrawable(R.drawable.image_border));
                selected.setTag(null);
            }
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
            imageDownloadLinks.clear();
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
                    progressText.setVisibility(View.VISIBLE);
                    progressText.setText("Fetching images");
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
                        if (image != null) {
                            final int threadCounter1 = counter;
                            runOnUiThread(() -> {
                                imageViewList.get(threadCounter1).setImageBitmap(image);
                                progressBar.incrementProgressBy(5);
                                progressText.setText("Downloading " + (threadCounter1 + 1) +
                                        " of 20 images");
                            });
                            counter += 1;
                        }
                    }
                    if (imageDownloadLinks.size() == imageViewList.size()) {
                        break;
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Void v = null;
            return v;
        }

        @Override
        protected void onPostExecute(Void v) {
            //set progress to 100 to cater for url with less than 20 images
            progressBar.setProgress(100);
            progressText.setText("Download completed! Please select images");
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}