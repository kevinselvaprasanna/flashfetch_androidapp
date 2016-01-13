package com.kevinselvaprasanna.flashfetch.Network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import com.kevinselvaprasanna.flashfetch.Objects.PostParam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Ahammad on 16/06/15.
 */
public class PostRequest {

    public static String LOG_TAG = "PostReqeust";

    public static JSONObject execute(String urlString,ArrayList<PostParam> params, String token){

        URL url;
        int status = 989;
        HttpURLConnection connection = null;
        Log.d(LOG_TAG, urlString);
        //Dummy response
        JSONObject jsonResponse = new JSONObject();
        try {

            jsonResponse.put("status", status);

            //Create connection
            url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            //Setting url parameters for post request
            String urlParameters = null;
            if (params.size() > 0){
                urlParameters = urlEncode(params);
                Log.d(LOG_TAG, urlParameters);
                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
            }
            Log.d("post   req ", urlParameters);
            //Connection properties
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Check if response code is 200 OK
            status = connection.getResponseCode();
            jsonResponse.put("status", status);

            if(status/100 != 2){
                return jsonResponse;
            }

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            Log.d(LOG_TAG, response.toString());
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            try {
                jsonResponse.put("data", new JSONObject(response.toString()));
            } catch (JSONException e){
                jsonResponse.put("data", new JSONObject("{ \"response\":" + response.toString() + "}"));
            }
            return jsonResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return jsonResponse;
        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String urlEncode(ArrayList<PostParam> params) {
        String urlParameters = null;
        try {
            urlParameters =  params.get(0).getKey()+ "=" + URLEncoder.encode(params.get(0).getValue(), "UTF-8");
            for (int i=1; i< params.size(); i++ ){
                urlParameters += "&" + params.get(i).getKey() + "=" + URLEncoder.encode(params.get(i).getValue(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlParameters;

    }


}
