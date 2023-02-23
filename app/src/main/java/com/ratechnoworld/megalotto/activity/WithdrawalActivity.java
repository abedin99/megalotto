package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.CustomerModel;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawalActivity extends AppCompatActivity {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    private RadioButton payTmRb, googlePayRb, phonePeRb;
    private TextInputEditText nameEt, numberEt, amountEt;
    public TextView codeTv, signTv, noteTv, alertTv;
    private Button submitBt;

    private String nameSt;
    private String numberSt;
    private String amountSt;
    private String mopSt;
    private int currBalance;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Withdraw");

        context = WithdrawalActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        payTmRb = findViewById(R.id.payTmRb);
        googlePayRb = findViewById(R.id.googlePayRb);
        phonePeRb = findViewById(R.id.phonePeRb);
        nameEt = findViewById(R.id.nameEt);
        numberEt = findViewById(R.id.numberEt);
        amountEt = findViewById(R.id.amountEt);
        noteTv = findViewById(R.id.noteTv);
        alertTv = findViewById(R.id.alertTv);
        codeTv = findViewById(R.id.codeTv);
        signTv = findViewById(R.id.signTv);
        submitBt = findViewById(R.id.submitBt);

        Intent intent = getIntent();
        currBalance = intent.getIntExtra("curr_amount", 0);

        codeTv.setText(AppConstant.COUNTRY_CODE);
        signTv.setText(AppConstant.CURRENCY_SIGN);
        alertTv.setText(String.format("Minimum Redeem Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_WITHDRAW_LIMIT));

        payTmRb.setOnClickListener(v -> {
            nameEt.setHint("Enter Account Holder Name");
            numberEt.setHint("Enter PayTm Number");
            mopSt = "PayTm";
        });

        googlePayRb.setOnClickListener(v -> {
            nameEt.setHint("Enter Account Holder Name");
            numberEt.setHint("Enter GooglePay Number");
            mopSt = "GooglePay";
        });

        phonePeRb.setOnClickListener(v -> {
            nameEt.setHint("Enter Account Holder Name");
            numberEt.setHint("Enter PhonePe Number");
            mopSt = "PhonePe";
        });

        submitBt.setOnClickListener(v -> {
            submitBt.setEnabled(false);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (payTmRb.isChecked()) {
                mopSt = "PayTm";
                alertTv.setText("Enter Valid PayTm Number");
            } else if (googlePayRb.isChecked()) {
                mopSt = "GooglePay";
                alertTv.setText("Enter Valid GooglePay Number");
            } else if (phonePeRb.isChecked()) {
                mopSt = "PhonePe";
                alertTv.setText("Enter Valid PhonePe Number");
            }

            nameSt = Objects.requireNonNull(nameEt.getText()).toString().trim();
            numberSt = Objects.requireNonNull(numberEt.getText()).toString().trim();
            amountSt = Objects.requireNonNull(amountEt.getText()).toString().trim();

            if (!nameSt.isEmpty() && !numberSt.isEmpty() && !amountSt.isEmpty()) {
                int amount = Integer.parseInt(amountEt.getText().toString());

                if (amount < AppConstant.MIN_WITHDRAW_LIMIT) {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText("Minimum Redeem Amount is " + AppConstant.CURRENCY_SIGN + AppConstant.MIN_WITHDRAW_LIMIT);
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                } else if (amount > AppConstant.MAX_WITHDRAW_LIMIT) {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText("Maximum Redeem Amount is " + AppConstant.CURRENCY_SIGN + AppConstant.MAX_WITHDRAW_LIMIT);
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                } else if (currBalance >= amount) {
                    alertTv.setVisibility(View.GONE);
                    if((Function.checkNetworkConnection(context))) {
                        addWithdrawTransaction();
                    }
                } else {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText("You don't have enough won amount to redeem.");
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                }
            }
            else {
                submitBt.setEnabled(true);
                alertTv.setVisibility(View.VISIBLE);
                alertTv.setText("Please enter valid information.");
                alertTv.setTextColor(Color.parseColor("#ff0000"));
            }
        });
    }

    private void addWithdrawTransaction() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.addWithdrawTransaction(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), nameSt, numberSt, amountSt, mopSt);
        call.enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        Function.showToast(context, res.get(0).getMsg());
                        alertTv.setText(res.get(0).getMsg());
                        onBackPressed();
                    }
                    else {
                        alertTv.setText("");
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<CustomerModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });

    }

}
