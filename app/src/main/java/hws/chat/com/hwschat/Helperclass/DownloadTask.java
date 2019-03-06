package hws.chat.com.hwschat.Helperclass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "", downloadFileName = "";

    public DownloadTask(Context context, String downloadUrl) {
        this.context = context;
        this.downloadUrl = downloadUrl;

        StringTokenizer tokenizer = new StringTokenizer(downloadUrl, "http://liwa.biz/public/assets/chat_assets/");
       String exten="";
        if(downloadUrl.contains(".pdf"))
        {
            exten=".pdf";
        }
        else if(downloadUrl.contains(".doc")||downloadUrl.contains(".docx"))
        {
            exten=".docx";
        }
        else if(downloadUrl.contains(".xls")||downloadUrl.contains(".xlsx"))
        {
            exten=".xlsx";
        }
        else if(downloadUrl.contains(".txt")||downloadUrl.contains(".txt"))
        {
            exten=".txt";
        }
        downloadFileName = tokenizer.nextToken()+exten;
        //Start Downloading Task
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "/HWS/Media/document/"+downloadFileName);
        if (!directory.exists()) {
            new DownloadingTask().execute();
        }
        else
        {
            Toast.makeText(context,"Already file downloaded",Toast.LENGTH_LONG).show();
        }

    }

    public class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "File Downloading...", Toast.LENGTH_LONG).show();
            //Set Button Text when download started
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    Toast.makeText(context, "File Downloading Complete", Toast.LENGTH_LONG).show();
                    //If Download completed then change button text
                }
            } catch (Exception e) {
                e.printStackTrace();


            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }

                File dir = new File(createFolder() +"/");
                if (!dir.exists()) {
                    dir.mkdir();
                }

                outputFile = new File(dir, downloadFileName);
                //Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }

        public File createFolder() {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "/HWS/Media/document");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            return directory;
        }

        private void openDownloadedFolder() {
            //Get Download Directory File
            File dir = new File(createFolder()+"/" );
            if (!dir.exists())
                Toast.makeText(context, "Right now there is no directory. Please download some file first.", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(dir.getAbsolutePath());
                intent.setDataAndType(uri, "*/*");
                context.startActivity(Intent.createChooser(intent, "Open Download Folder"));
            }

        }
    }



}
