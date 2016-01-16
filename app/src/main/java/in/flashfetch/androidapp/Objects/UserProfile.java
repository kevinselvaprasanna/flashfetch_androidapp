package in.flashfetch.androidapp.Objects;

import android.content.Context;
import android.content.SharedPreferences;


public class UserProfile {

    public static String name,email;

    public static void setName(String name, Context context){
        SharedPreferences preferences = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name",name);
        editor.commit();
    }

    public static void setEmail(String email,Context context){
        SharedPreferences preferences = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email",email);
        editor.commit();
    }


    public static String getName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        return pref.getString("name","");
    }

    public static String getEmail(Context context) {
        SharedPreferences pref = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        return pref.getString("email", "");
    }


}


