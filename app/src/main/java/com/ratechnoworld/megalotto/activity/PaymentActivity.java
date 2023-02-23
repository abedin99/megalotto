package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.utils.GenerateRandomString;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    private int currentAmount, bonusAmount, bonusApplied, payableAmount;
    public String feedId, entryFees;

    public TextView ticketNoTv;
    public TextView entryFeeTv;
    public TextView bonusTv;
    public TextView ticketAmountTv;
    public TextView textInputError;
    public Button payBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment");

        context = PaymentActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        ticketNoTv = findViewById(R.id.ticket_no);
        entryFeeTv = findViewById(R.id.entryFee);
        bonusTv = findViewById(R.id.bonus);
        ticketAmountTv = findViewById(R.id.ticket_amount);
        textInputError = findViewById(R.id.textinput_error);
        payBt = findViewById(R.id.pay);

        Intent intent = getIntent();
        if(intent != null) {
            feedId = intent.getStringExtra("FEES_ID");
            entryFees = intent.getStringExtra("ENTREE_FEES");
        }

        if (Function.checkNetworkConnection(context)) {
            ticketNoTv.setText(GenerateRandomString.randomString(10));
            getUserWallet();
        }

        payBt.setOnClickListener(v -> buyTicket());
    }

    @SuppressLint("SetTextI18n")
    public void buyTicket() {
        if (currentAmount >= payableAmount) {
            textInputError.setText("");
            addTicket();
        }else {
            textInputError.setText("You don't have enough deposit balance to buy this ticket, please add balance in your wallet.");
        }
    }

    private void addTicket() {
        progressBarHelper.showProgressDialog();
        Call<CustomerModel> call = api.addTicket(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getString(Preferences.KEY_CONTST_ID), feedId, entryFees, ticketNoTv.getText().toString());
        call.enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            Function.showToast(context, res.get(0).getMsg());
                            Function.fireIntent(context,MyTicketActivity.class);
                        }else {
                            Function.showToast(context, res.get(0).getMsg());
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

    private void getUserWallet() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.getUserWallet(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<CustomerModel>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            currentAmount = Integer.parseInt(res.get(0).getCur_balance())+Integer.parseInt(res.get(0).getWon_balance());
                            bonusAmount = Integer.parseInt(res.get(0).getBonus_balance());
                            bonusApplied = Math.round((AppConstant.TICKET_BONUS_USED * Float.parseFloat(Preferences.getInstance(context).getString(Preferences.KEY_PRICE))) / 100);

                            if (res.get(0).getStatus() != 1 || res.get(0).getIs_block() != 0){
                                Preferences.getInstance(PaymentActivity.this).setlogout();
                            }

                            if (bonusAmount >= bonusApplied) {
                                payableAmount = Integer.parseInt(entryFees) - bonusApplied;
                                bonusTv.setText(String.format("- %s", AppConstant.CURRENCY_SIGN +""+bonusApplied));
                            } else {
                                payableAmount = Integer.parseInt(entryFees) - bonusAmount;
                                bonusTv.setText(String.format("- %s", AppConstant.CURRENCY_SIGN +""+bonusAmount));
                            }
                            ticketAmountTv.setText(String.format("%s%s", AppConstant.CURRENCY_SIGN, Preferences.getInstance(context).getString(Preferences.KEY_PRICE)));
                            entryFeeTv.setText(String.format("%s%d", AppConstant.CURRENCY_SIGN, payableAmount));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
