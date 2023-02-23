package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ratechnoworld.megalotto.adapter.TransactionAdapter;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends AppCompatActivity {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    private Contest contest;
    private TransactionAdapter contestAdapter;
    private final List<Contest> legalDataFinal = new ArrayList<>();

    private int amount, bonus, won;

    RecyclerView recyclerView;
    LinearLayout withdrawLl;
    LinearLayout depositLl;
    CardView bankAccountCv;
    TextView wonAmtTv;
    TextView bonusAmtTv;
    TextView amtTv;
    TextView noDataTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wallet");
        getSupportActionBar().setElevation(0);

        context = WalletActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        wonAmtTv = findViewById(R.id.wonAmt);
        bonusAmtTv = findViewById(R.id.bonusAmt);
        amtTv = findViewById(R.id.amt);
        recyclerView = findViewById(R.id.recyclerView);
        withdrawLl = findViewById(R.id.withdraw);
        depositLl = findViewById(R.id.deposit);
        bankAccountCv = findViewById(R.id.bankAccount);
        noDataTv = findViewById(R.id.textView);

        withdrawLl.setOnClickListener(v -> withdrawBalance());

        depositLl.setOnClickListener(v -> depositBalance());

        bankAccountCv.setOnClickListener(v -> bankAccount());

        if (AppConstant.WALLET_MODE == 0) {
            withdrawLl.setVisibility(View.VISIBLE);
        }
        else {
            withdrawLl.setVisibility(View.GONE);
        }
    }

    private void getUserWallet() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.getUserWallet(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<CustomerModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            amount = Integer.parseInt(res.get(0).getCur_balance())+Integer.parseInt(res.get(0).getWon_balance());
                            won = Integer.parseInt(res.get(0).getWon_balance());
                            bonus = Integer.parseInt(res.get(0).getBonus_balance());

                            amtTv.setText(AppConstant.CURRENCY_SIGN+""+(amount));
                            wonAmtTv.setText(String.format("Won: %s", AppConstant.CURRENCY_SIGN+""+won));
                            bonusAmtTv.setText(String.format("Bonus Balance: %s", AppConstant.CURRENCY_SIGN+""+bonus));

                            if (res.get(0).getStatus() != 1 || res.get(0).getIs_block() != 0){
                                Preferences.getInstance(WalletActivity.this).setlogout();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    private void getTransactions() {
        progressBarHelper.showProgressDialog();

        Call<List<Contest>> call = api.getTransactions(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<List<Contest>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    List<Contest> legalData = response.body();
                    if (legalData != null) {
                        if (legalData.size() > 0) {
                            legalDataFinal.clear();
                            for (Contest contestfinal : legalData) {
                                contest = new Contest();
                                contest.setDate(contestfinal.getDate());
                                contest.setReq_amount(contestfinal.getReq_amount());
                                contest.setOrder_id(contestfinal.getOrder_id());
                                contest.setRemark(contestfinal.getRemark());
                                contest.setStatus(contestfinal.getStatus());
                                legalDataFinal.add(contest);
                            }

                            recyclerView.setVisibility(View.VISIBLE);
                            noDataTv.setVisibility(View.GONE);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            contestAdapter = new TransactionAdapter(context, legalDataFinal);
                            contestAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(contestAdapter);
                        }
                        else {
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

    private AlertDialog alertDialog() {
        return new AlertDialog.Builder(this)
                // Set Dialog Message
                .setTitle("Withdraw limit")
                .setMessage("Minimum withdraw amount is limited to "+AppConstant.CURRENCY_SIGN +""+AppConstant.MIN_WITHDRAW_LIMIT)
                // negative button
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss()).create();
    }

    public void withdrawBalance() {
        try {
            if (amount < AppConstant.MIN_WITHDRAW_LIMIT) {
                alertDialog().show();
            } else {
                Intent intent = new Intent(context, WithdrawalActivity.class);
                intent.putExtra("curr_amount", amount);
                Function.fireIntentWithData(context, intent);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void depositBalance() {
        try {
            Intent intent = new Intent(context, DepositActivity.class);
            Function.fireIntentWithData(context, intent);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void bankAccount() {
        Function.fireIntent(context, BankAccountActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Function.checkNetworkConnection(context)) {
            getUserWallet();
            getTransactions();
        }
    }

}
