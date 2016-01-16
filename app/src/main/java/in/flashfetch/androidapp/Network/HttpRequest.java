package in.flashfetch.androidapp.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ahammad on 22/08/15.
 */
public class HttpRequest {

    private static String LOG_TAG = "HttpRequest";

    public static JSONObject execute(String method, String urlString, String token, JSONObject json){
        URL url;
        int status = 989;
        HttpURLConnection connection = null;

        //Dummy response
        JSONObject jsonResponse = new JSONObject();
        try {

            jsonResponse.put("status", status);

            //Create connection
            url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            //Connection properties
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("Content-Type", "application/json");


            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(15000);



            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(json.toString());
            out.close();

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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if(connection != null) {
                connection.disconnect();
            }
            return jsonResponse;
        }
    }

}
