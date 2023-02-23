package com.ratechnoworld.megalotto.activity;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.helper.AppConstant;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class NotificationDetailsActivity extends AppCompatActivity {

    public TextView titleTv, dateTv;
    public WebView webView;
    public Button viewMoreBt;
    public ImageView imageIv;

    public String title,description,image,created,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification Details");

        titleTv = findViewById(R.id.titleTv);
        dateTv = findViewById(R.id.dateTv);
        viewMoreBt = findViewById(R.id.viewMoreBt);
        imageIv = findViewById(R.id.imageIv);
        webView = findViewById(R.id.webView);

        if (getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString("title");
            description = getIntent().getExtras().getString("description");
            image = getIntent().getExtras().getString("image");
            url = getIntent().getExtras().getString("url");
            created = getIntent().getExtras().getString("created");
        }

        titleTv.setText(title);
        dateTv.setText(created);
        webView.setBackgroundColor(0);
        webView.loadData(description,"text/html", "UTF-8");

        try {
            if (!image.equals("null")) {
                Glide.with(getApplicationContext()).load(AppConstant.FILE_URL+image)
                        .apply(new RequestOptions().override(720,540))
                        .apply(new RequestOptions().placeholder(R.drawable.app_icon).error(R.drawable.app_icon))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .into(imageIv);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        try {
            if (!url.isEmpty()) {
                if (!url.equals("false")) {
                    viewMoreBt.setVisibility(View.VISIBLE);
                }
                else {
                    viewMoreBt.setVisibility(View.GONE);
                }
            }
            else {
                viewMoreBt.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            viewMoreBt.setVisibility(View.GONE);
        }


        viewMoreBt.setOnClickListener(v -> openWebPage(url));

    }

    public void openWebPage(String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install link web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}