package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.ratechnoworld.megalotto.adapter.NotificationAdapter;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.NotificationModel;
import com.ratechnoworld.megalotto.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    private final List<NotificationModel> dataArrayList = new ArrayList<>();
    private NotificationAdapter dataAdapter;
    private NotificationModel notificationModel;

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        context = NotificationActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        recyclerView = findViewById(R.id.recyclerView);

        if(Function.checkNetworkConnection(context)) {
            getNotification();
        }
    }

    private void getNotification() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        progressBarHelper.showProgressDialog();

        Call<List<NotificationModel>> call = api.getNotification(AppConstant.PURCHASE_KEY);
        call.enqueue(new Callback<List<NotificationModel>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<NotificationModel>> call, @NonNull Response<List<NotificationModel>> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    List<NotificationModel> legalData = response.body();
                    if (legalData != null && legalData.size() > 0) {
                        for (NotificationModel notificationModel1 : legalData) {
                            notificationModel = new NotificationModel();
                            notificationModel.setTitle(notificationModel1.getTitle());
                            notificationModel.setMessage(notificationModel1.getMessage());
                            notificationModel.setImage(notificationModel1.getImage());
                            notificationModel.setCreated(findDifference(notificationModel1.getCreated(), currentDateAndTime));
                            notificationModel.setImage(notificationModel1.getImage());
                            notificationModel.setUrl(notificationModel1.getUrl());
                            dataArrayList.add(notificationModel);
                        }
                        if (!dataArrayList.isEmpty()) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            dataAdapter = new NotificationAdapter(getApplicationContext(), dataArrayList);
                            dataAdapter.notifyDataSetChanged();
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(dataAdapter);
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<NotificationModel>> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public String findDifference(String start_date, String end_date) {
        // SimpleDateFormat converts the
        // string format to date object
        String ans = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Try Class
        try {
            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time = Objects.requireNonNull(d2).getTime() - Objects.requireNonNull(d1).getTime();

            // Calucalte time difference in seconds,
            // minutes, hours, years, and days
            long difference_In_Seconds
                    = TimeUnit.MILLISECONDS
                    .toSeconds(difference_In_Time)
                    % 60;

            long difference_In_Minutes = TimeUnit.MILLISECONDS.toMinutes(difference_In_Time);

            long difference_In_Hours
                    = TimeUnit
                    .MILLISECONDS
                    .toHours(difference_In_Time)
                    % 24;

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;

            if (difference_In_Minutes == 0) {
                ans = difference_In_Seconds + " seconds";
            } else if (difference_In_Hours == 0) {
                ans = difference_In_Minutes + " minutes";
            } else if (difference_In_Days == 0) {
                ans = difference_In_Hours + " hours";
            } else if (difference_In_Days > 0 && difference_In_Days < 7) {
                ans = difference_In_Days + " days";
            } else if (difference_In_Days > 6) {
                ans = start_date;
            }

            return ans;
        } catch (ParseException e) {
            return "";
        }
    }

}
