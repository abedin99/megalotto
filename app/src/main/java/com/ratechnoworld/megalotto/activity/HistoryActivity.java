package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ratechnoworld.megalotto.adapter.HistoryAdapter;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryActivity extends AppCompatActivity {

    private Context context;
    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;

    public RecyclerView recyclerView;
    private HistoryAdapter contestAdapter;

    public TextView noDataTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        context = HistoryActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        recyclerView = findViewById(R.id.recyclerView);
        noDataTv = findViewById(R.id.textView);

        if(Function.checkNetworkConnection(context)) {
            getHistory();
        }

    }

    private void getHistory() {
        progressBarHelper.showProgressDialog();

        Call<List<Contest>> call = api.getHistory(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<List<Contest>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    List<Contest> legalData = response.body();
                    if (legalData != null) {
                        if (legalData.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noDataTv.setVisibility(View.GONE);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            contestAdapter = new HistoryAdapter(context, legalData);
                            contestAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(contestAdapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            noDataTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contest>> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

}
