package in.flashfetch.androidapp.Objects;

import android.content.Context;
import android.database.Cursor;

//import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.flashfetch.androidapp.Helper.DatabaseHelper;

/**
 * Created by kevinselvaprasanna on 3/1/16.
 */
public class Notification {
    public String email, price, category;
    public long time;
    public long id;

    public Notification(JSONObject not) {
        try {
            this.email = not.getString("email");
            this.price = not.getString("price");
            this.category = not.getString("category");
            this.time = not.getLong("time");
            this.id = not.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String[] columns = {"id", "email", "category", "price", "time"};

    public Notification(String email, String category, String price, Long time) {
        this.email = email;
        this.category = category;
        this.price = price;
        this.time = time;
    }

    public static ArrayList<Notification> getArrayList(Cursor c) {
        ArrayList<Notification> arrayList = new ArrayList<>();
        //Gson gson = new Gson();
        while (c.moveToNext()) {
            arrayList.add(parseEvent(c));
        }
        return arrayList;
    }

    public static Notification parseEvent(Cursor c) {
        //Gson gson = new Gson();
        Notification not = new Notification(c.getString(1), c.getString(2), c.getString(3), c.getLong(4));
        return not;
    }

    public static ArrayList<Notification> getAllNotifications(Context context){
        DatabaseHelper data = new DatabaseHelper(context);
        return data.getAllNotifications();
    }
}
