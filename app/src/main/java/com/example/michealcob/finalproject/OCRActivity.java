package com.example.michealcob.finalproject;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCRActivity extends AppCompatActivity {

    public Uri takenPhoto;
    private Bitmap mBitmap;
    EditText OCRTextView;
    String datapath = "";
    private TessBaseAPI mTess;

    private static String LOG_TAG = OCRActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        OCRTextView = (EditText) findViewById(R.id.ocr_result);

        takenPhoto = (Uri) getIntent().getExtras().get("URI");

        Log.v("takenPhoto", "is " + takenPhoto);
        ImageView imageView = (ImageView) findViewById(R.id.snappedImage);
        mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                takenPhoto, getContentResolver());
        if (mBitmap != null) {
            // Show the image on screen.
            imageView.setImageBitmap(mBitmap);
            //doOCR();
        }
        datapath = getFilesDir() +"/";  //"/tesseract/"

        checkFile(new File(datapath + "tessdata/"));

        //initialize Tesseract Api
        String lang = "yor";
        mTess = new TessBaseAPI();

        //this is where the language is been set for the tessaract api
        mTess.init(datapath, lang);
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/yor.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/yor.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/yor.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    public void processImage(View view){
        String OCRresult = null;
        mTess.setImage(mBitmap);
        OCRresult = mTess.getUTF8Text();
        Log.v("OCR_text", OCRresult + " ");
        OCRTextView.setText(OCRresult);
    }


    /*protected void ocr(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(, options);

        try {
            ExifInterface exif = new ExifInterface(takenPhoto.toString());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Log.v(LOG_TAG, "Orient: " + exifOrientation);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(LOG_TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract req. ARGB_8888
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Rotate or coversion failed: " + e.toString());
        }


        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        String LANG = "fin";
        baseApi.init(DATA_PATH, LANG);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        Log.v(LOG_TAG, "OCR Result: " + recognizedText);

        // clean up and show
        if (LANG.equalsIgnoreCase("fin")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
        if (recognizedText.length() != 0) {
            ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
        }
    }*/

    /*class OCRTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(final String... urls) {
            String response = "";
            for (String url : urls) {
                try {
                    response = executeMultipartPost(url, mBA);
                    Log.v(LOG_TAG, "Response:" + response);
                    break;
                } catch (Throwable ex) {
                    Log.e(LOG_TAG, "error: " + ex.getMessage());
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String result) {
            mProgressBar.setVisibility(View.GONE);
            setResult(Activity.RESULT_OK, getIntent().putExtra("OCR_TEXT", result));
            finish();
        }
    }*/

   /* private void doOCR() {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this)
                .build();

        if (!textRecognizer.isOperational()) {
            new AlertDialog.Builder(this)
                    .setMessage("Text recognizer could not be set up on your device :(")
                    .show();
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(mBitmap).build();
        SparseArray<TextBlock> text = textRecognizer.detect(frame);

        for (int i = 0; i < text.size(); ++i) {
            TextBlock item = text.valueAt(i);
            if (item != null && item.getValue() != null) {
                ocrResult.setText(item.getValue());
            }
        }
    }*//*
   private String executeMultipartPost(final String stringUrl, final byte[] bm) throws Exception {
       HttpClient httpClient = new DefaultHttpClient();
       HttpPost postRequest = new HttpPost(stringUrl);
       ByteArrayBody bab = new ByteArrayBody(bm, "the_image.jpg");
       MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
       reqEntity.addPart("uploaded", bab);
       reqEntity.addPart("name", new StringBody("the_file"));
       postRequest.setEntity(reqEntity);
       HttpResponse response = httpClient.execute(postRequest);
       BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
       String sResponse;
       StringBuilder s = new StringBuilder();

       while ((sResponse = reader.readLine()) != null) {
           s = s.append(sResponse).append('\n');
       }
       int i = s.indexOf("body");
       int j = s.lastIndexOf("body");
       return s.substring(i + 5, j - 2);
   }*/
}
