package com.kevinselvaprasanna.flashfetch.shareactivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kevinselvaprasanna.flashfetch.Network.PostRequest;
import com.kevinselvaprasanna.flashfetch.Objects.PostParam;
import com.kevinselvaprasanna.flashfetch.Objects.UserProfile;
import com.kevinselvaprasanna.flashfetch.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity {
    JSONObject ResponseJSON;
    ArrayList<PostParam> iPostParams;
    String price,name,url;
    TextView tv,tvp;
    ImageView iv;
    String text;
    private View mProgressView;
    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent receivedintent = getIntent();
        String receivedaction = receivedintent.getAction();
        String receivedtype = receivedintent.getType();
        if(receivedaction.equals(Intent.ACTION_SEND)){
            if(receivedtype.startsWith("text/")){
                text = receivedintent.getStringExtra(Intent.EXTRA_TEXT);
                //tv.setText(text);
                //wv.loadUrl(text);
            }
        }else if(receivedaction.equals(Intent.ACTION_MAIN)){

        }
        tv = (TextView)findViewById(R.id.tv);
        tvp = (TextView)findViewById(R.id.tvp);
        iv = (ImageView)findViewById(R.id.iv);
        mProgressView = findViewById(R.id.progress);
        tv.setText(text);
        //WebView wv = (WebView)findViewById(R.id.wv);
        GetTask gt = new GetTask();
        gt.execute();




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                final CharSequence colors[] = new CharSequence[]{"laptops", "mobiles", "tablets"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);
                builder.setTitle("Pick a category");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        // the user clicked on colors[which]
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ShareActivity.this);
                        builder2.setTitle("Price");
                        builder2.setMessage("Enter price that you want to bargain");
                        final EditText price = new EditText(ShareActivity.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        price.setLayoutParams(lp);
                        builder2.setView(price);
                        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which2) {
                                iPostParams = new ArrayList<PostParam>();
                                PostParam post = new PostParam("category", (String) colors[which]);
                                PostParam postprice = new PostParam("price", price.getText().toString());
                                PostParam postemail = new PostParam("email", UserProfile.getEmail(ShareActivity.this));
                                iPostParams.add(post);
                                iPostParams.add(postprice);
                                iPostParams.add(postemail);
                                BargainTask bt = new BargainTask();
                                bt.execute();
                            }
                        });
                        builder2.show();
                    }
                });
                builder.show();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

           /* mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class BargainTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            ResponseJSON = PostRequest.execute("http://192.168.43.66/bargain_push.php", iPostParams, null);
            Log.d("RESPONSE", ResponseJSON.toString());
            return null;
        }
    }
    public class GetTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            iPostParams = new ArrayList<PostParam>();
            PostParam post = new PostParam("name", text);
            iPostParams.add(post);
            ResponseJSON = PostRequest.execute("http://www.flashfetch.in/temp/flipkart.php", iPostParams, null);

            try {
                JSONObject data = ResponseJSON.getJSONObject("data");
                name = data.getString("name");
                price = data.getString("price");
                url = data.getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("RESPONSE", ResponseJSON.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            tv.setText(name);
            tvp.setText("Price: " + price);
            Glide
                    .with(ShareActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(iv);
            showProgress(false);

        }
    }




}
