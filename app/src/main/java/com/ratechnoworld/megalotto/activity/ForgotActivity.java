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

public class ForgotActivity extends Activity {

    private Context context;
    private ApiCalling api;
    private ProgressBarHelper progressBarHelper;

    public EditText mobileNoEt;
    public TextView textInputError;
    public Button nextBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        context = ForgotActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        mobileNoEt = findViewById(R.id.mobileno);
        textInputError = findViewById(R.id.textinput_error);
        nextBt = findViewById(R.id.next);

        nextBt.setOnClickListener(v -> {
            try {
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mobileNoEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Please enter mobile no");
            } else if(mobileNoEt.getText().length() < 6){
                textInputError.setText("");
                textInputError.setText("Please enter valid mobile no");
            }else {
                textInputError.setText("");
                verifyUserMobile();
            }
        });
    }

    private void verifyUserMobile() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.verifyUserMobile(AppConstant.PURCHASE_KEY, mobileNoEt.getText().toString());
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
                            Preferences.getInstance(context).setString(Preferences.KEY_MOBILE, mobileNoEt.getText().toString());
                            Function.fireIntent(context, ForgotOTPActivity.class);
                        }else {
                            textInputError.setText("");
                            textInputError.setText("Please check your mobile no");
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
