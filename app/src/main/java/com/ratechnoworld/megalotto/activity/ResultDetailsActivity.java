package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ratechnoworld.megalotto.adapter.TopWinnerAdapter;
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

public class ResultDetailsActivity extends AppCompatActivity {

    private String feesId;
    private Context context;
    private ApiCalling api;
    private ProgressBarHelper progressBarHelper;

    private TopWinnerAdapter contestAdapter;
    private RecyclerView recyclerView;

    public LinearLayout linearCard;
    public TextView winPrice;
    public TextView entryFee;
    public TextView ticketNo;
    public TextView winStatus;
    public TextView currencyFeeTv;
    public TextView currencyPrizeTv;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Result Details");

        recyclerView = findViewById(R.id.recyclerView);
        winPrice = findViewById(R.id.winPrice);
        entryFee = findViewById(R.id.entryFee);
        ticketNo = findViewById(R.id.ticketNo);
        linearCard = findViewById(R.id.linearCard);
        winStatus = findViewById(R.id.winStatus);
        currencyFeeTv = findViewById(R.id.currencyFeeTv);
        currencyPrizeTv = findViewById(R.id.currencyPrizeTv);

        currencyPrizeTv.setText(AppConstant.CURRENCY_SIGN);
        currencyFeeTv.setText(AppConstant.CURRENCY_SIGN);

        Intent intent = getIntent();
        winPrice.setText(intent.getStringExtra("winPrice"));
        entryFee.setText(intent.getStringExtra("entryFee"));
        feesId = intent.getStringExtra("fees_id");
        ticketNo.setText(intent.getStringExtra("ticket_no"));

        context = ResultDetailsActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        if(Objects.equals(intent.getStringExtra("winPrice"), "0")){
            winStatus.setText("Unlucky ");
        }else {
            winStatus.setText("You won ");
        }

        if(Function.checkNetworkConnection(context)) {
            getTopWinners();
        }
    }

    private void getTopWinners() {
        progressBarHelper.showProgressDialog();

        Call<List<Contest>> call = api.getTopWinners(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_CONSTANT_ID), feesId);
        call.enqueue(new Callback<List<Contest>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    List<Contest> legalData = response.body();
                    if (legalData != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        contestAdapter = new TopWinnerAdapter(context, legalData);
                        contestAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(contestAdapter);
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
