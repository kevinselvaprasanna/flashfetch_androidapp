package com.kevinselvaprasanna.flashfetch.Network;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ahammad on 17/06/15.
 */
public class ImageUploader {

    public static String LOG_TAG = "ImageUploader";

    public static JSONObject execute(String urlString, String sourceFileUri, String token){

        String fileName = sourceFileUri;
        Log.d(LOG_TAG, fileName);

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "------";
        int bytesRead, bytesAvailable, bufferSize;
        int serverResponseCode = 999;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        int status = 999;
        JSONObject json = new JSONObject();
        try {
            json.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!sourceFile.isFile()) {
            Log.d(LOG_TAG, "Source File not exist ");
            return json;
        }
        else {
            try {

                Long time = System.currentTimeMillis();
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(urlString);
                Log.d(LOG_TAG, "Url " + url.toString());
                Log.d(LOG_TAG, fileName);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setConnectTimeout(30000);
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                Log.d(LOG_TAG, fileName);


                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.d(LOG_TAG, "HTTP Response is : "
                        + serverResponseCode + " " + serverResponseMessage + " in " + (System.currentTimeMillis() - time) + "ms");
                json.put("status", serverResponseCode);

                if(serverResponseCode == 200){
                    //Get Response
                    InputStream is = conn.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    json.put("data", new JSONObject(response.toString()));
                    Log.d(LOG_TAG, response.toString());
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            }  catch (Exception e) {
                e.printStackTrace();

            }
            return json;

        }
    }

    public static void execute2(String urlString, Bitmap bitmap){
        try {

            // Static stuff:
            String attachmentName = "bitmap";
            String attachmentFileName = "bitmap.bmp";
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            // Setup the request:
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL("http://10.42.0.77:9000/api/uploads");
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" +  boundary);

            // Start content wrapper:
            DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens +  boundary +  crlf);
            request.writeBytes("Content-Disposition: form-data; file=\"" +  attachmentName + "\";"+  crlf);
            request.writeBytes( crlf);
            // Convert Bitmap to ByteBuffer

            //I want to send only 8 bit black & white bitmaps
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }

            request.write(pixels);
            // End content wrapper:

            request.writeBytes( crlf);
            request.writeBytes( twoHyphens +  boundary +  twoHyphens +  crlf);
            // Flush output buffer:

            request.flush();
            request.close();
            // Get response:
            Log.d(LOG_TAG, "status " + httpUrlConnection.getResponseCode() + httpUrlConnection.getResponseMessage());

            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();
            //Close response stream:

            responseStream.close();
            // Close the connection:

            httpUrlConnection.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
