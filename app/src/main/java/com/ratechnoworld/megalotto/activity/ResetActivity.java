package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetActivity extends Activity {

    private Context context;
    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;

    public EditText passwordEt;
    public EditText confirmPasswordEt;
    public TextView textInputError;
    public Button resetBt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        context = ResetActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        passwordEt = findViewById(R.id.password);
        confirmPasswordEt = findViewById(R.id.confirmPassword);
        textInputError = findViewById(R.id.textinput_error);
        resetBt = findViewById(R.id.reset);

        resetBt.setOnClickListener(v -> {
            try {
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (passwordEt.getText().toString().equals("") && confirmPasswordEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("All fields are mandatory");
            } else if (passwordEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Please enter password");
            }
            else if (passwordEt.getText().toString().length() < 8) {
                textInputError.setText("");
                textInputError.setText("Password must be 8 characters");
            } else if (!passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())) {
                textInputError.setText("");
                textInputError.setText("Password mismatch");
            }
            else {
                if((Function.checkNetworkConnection(context))) {
                    userForgotPassword();
                }
            }
        });
    }

    private void userForgotPassword() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.userForgotPassword(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_MOBILE), confirmPasswordEt.getText().toString());
        call.enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                progressBarHelper.hideProgressDialog();
                CustomerModel legalData = response.body();
                List<CustomerModel.Result> res;
                if (response.isSuccessful()) {
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            Function.fireIntent(getApplicationContext(), SigninActivity.class);
                        } else {
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

}

