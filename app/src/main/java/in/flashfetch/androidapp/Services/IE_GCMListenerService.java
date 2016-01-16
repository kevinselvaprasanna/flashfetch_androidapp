package in.flashfetch.androidapp.Services;


/**
 * Created by kevin selva prasanna on 24-Aug-15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

import in.flashfetch.androidapp.Helper.DatabaseHelper;
import in.flashfetch.androidapp.R;


public class IE_GCMListenerService extends GcmListenerService{

    private static final String TAG = "GcmListenerService";
    String message=" ";
    String data="";
    String type=" ",category,scoreBoardId,title=" ";



    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        JSONObject notjs;
        Log.i("GCM-message",data.toString());
        //try {
            //notjs = new JSONObject(String.valueOf(data));
            //Notification not = new Notification(notjs);
            ContentValues cv = new ContentValues();
            cv.put("email", data.getString("email"));
            cv.put("category", data.getString("category"));
            cv.put("price", Integer.parseInt(data.getString("price")));
            cv.put("time", Long.getLong(data.getString("time")));
            cv.put("id", Long.getLong(data.getString("id")));
            DatabaseHelper dh = new DatabaseHelper(this);
            dh.addNot(cv);
        //} catch (JSONException e) {
        //    e.printStackTrace();
        //}

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */


        sendNotification(data.getString("email"), data.getString("category"), data.getString("price"));
    }
    // [END receive_message]

   /* *//**
     * Create and show a simple notification containing the received GCM message.
     *
     * message GCM message received.
     */
    private void sendNotification(String email, String category, String price){
        PendingIntent pendingIntent;
        Uri defaultSoundUri;
        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager;
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_ONE_SHOT);

        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FlashFetch")
                .setContentText(email + " wants " + category + " at price Rs." + price)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int)(Math.random()*1000) , notificationBuilder.build());


    }

}
