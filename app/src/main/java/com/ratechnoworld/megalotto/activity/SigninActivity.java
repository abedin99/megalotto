package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Constants;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends FragmentActivity {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;
    private String fcmToken;

    public EditText mobileNoEt;
    public EditText passwordEt;
    public Button signinBt;
    public TextView signupTv;
    public TextView forgotTv;
    public TextView textInputError;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        context = SigninActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        mobileNoEt = findViewById(R.id.mobileno);
        passwordEt = findViewById(R.id.password);
        signinBt = findViewById(R.id.signin);
        signupTv = findViewById(R.id.signup);
        forgotTv = findViewById(R.id.forgot);
        textInputError = findViewById(R.id.textinput_error);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get new FCM registration token
                fcmToken = task.getResult();
            }
        });

        signinBt.setOnClickListener(v -> {
            if (mobileNoEt.getText().toString().equals("") && passwordEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("All fields are mandatory");
            } else if (mobileNoEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Please enter mobile no");
            }else if (passwordEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Please enter password");
            } else {
                if (Function.checkNetworkConnection(context)) {
                    loginUser();
                }
            }
        });

        signupTv.setOnClickListener(v -> Function.fireIntent(getApplicationContext(), SignupActivity.class));

        forgotTv.setOnClickListener(v -> Function.fireIntent(getApplicationContext(), ForgotActivity.class));

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void loginUser() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.loginUser(AppConstant.PURCHASE_KEY, mobileNoEt.getText().toString(), passwordEt.getText().toString());
        call.enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            Preferences.getInstance(context).setString(Preferences.KEY_IS_AUTO_LOGIN, "1");
                            Preferences.getInstance(context).setString(Preferences.KEY_USER_ID, res.get(0).getId());
                            Preferences.getInstance(context).setString(Preferences.KEY_RESORT_IMAGE, res.get(0).getUser_profile());
                            Preferences.getInstance(context).setString(Preferences.KEY_FULL_NAME, res.get(0).getName());
                            Preferences.getInstance(context).setString(Preferences.KEY_USER_NAME, res.get(0).getUsername());
                            Preferences.getInstance(context).setString(Preferences.KEY_EMAIL, res.get(0).getEmail());
                            Preferences.getInstance(context).setString(Preferences.KEY_DOB, res.get(0).getDob());
                            Preferences.getInstance(context).setString(Preferences.KEY_GENDER, res.get(0).getGender());
                            Preferences.getInstance(context).setString(Preferences.KEY_MOBILE, mobileNoEt.getText().toString());
                            Preferences.getInstance(context).setString(Preferences.KEY_PASSWORD, passwordEt.getText().toString());
                            updateUserProfileFCM(res.get(0).getId());
                        } else {
                            progressBarHelper.hideProgressDialog();
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


    private void updateUserProfileFCM(String id) {

        Call<CustomerModel> call = api.updateUserProfileFCM(AppConstant.PURCHASE_KEY, id, fcmToken);
        call.enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            progressBarHelper.hideProgressDialog();
                            Function.fireIntent(getApplicationContext(), MainActivity.class);
                        } else {
                            progressBarHelper.hideProgressDialog();
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
