package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastStepActivity extends Activity {

    private Context context;
    public String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String fcmToken;

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private String androidId;

    public EditText emailEt;
    public TextView textInputError;
    public Button finishBt;

    @SuppressLint({"HardwareIds", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_step);

        context = LastStepActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        emailEt = findViewById(R.id.email);
        textInputError = findViewById(R.id.textinput_error);
        finishBt = findViewById(R.id.finish);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get new FCM registration token
                fcmToken = task.getResult();
            }
        });

        finishBt.setOnClickListener(v -> {
            if (emailEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Enter Email Id");
            }
            else if(!emailEt.getText().toString().trim().matches(emailPattern)){
                textInputError.setText("");
                textInputError.setText("Enter valid Email Id");
            }
            else {
                if(Function.checkNetworkConnection(context)) {
                    if (Preferences.getInstance(context).getString(Preferences.KEY_REFER_CODE).equals("")) {
                        customerRegistrationWithoutRefer();
                    } else {
                        customerRegistrationWithRefer();
                    }
                }
            }
        });
    }

    private void customerRegistrationWithRefer() {
        progressBarHelper.showProgressDialog();

        String email = emailEt.getText().toString().trim();
        String[] split = email.split("@");
        String username = split[0];

        Call<CustomerModel> call = api.customerRegistrationWithRefer(AppConstant.PURCHASE_KEY, username, email, androidId, username, Preferences.getInstance(context).getString(Preferences.KEY_PASSWORD), AppConstant.COUNTRY_CODE, Preferences.getInstance(context).getString(Preferences.KEY_MOBILE), Preferences.getInstance(context).getString(Preferences.KEY_REFER_CODE), fcmToken);
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
                            Preferences.getInstance(context).setString(Preferences.KEY_USER_NAME, username);
                            Preferences.getInstance(context).setString(Preferences.KEY_EMAIL, email);
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

    private void customerRegistrationWithoutRefer() {
        progressBarHelper.showProgressDialog();

        String email = emailEt.getText().toString().trim();
        String[] split = email.split("@");
        String username = split[0];

        Call<CustomerModel> call = api.customerRegistrationWithoutRefer(AppConstant.PURCHASE_KEY, username, emailEt.getText().toString(), androidId, username, Preferences.getInstance(context).getString(Preferences.KEY_PASSWORD), AppConstant.COUNTRY_CODE, Preferences.getInstance(context).getString(Preferences.KEY_MOBILE), fcmToken);
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
                            Preferences.getInstance(context).setString(Preferences.KEY_USER_NAME, username);
                            Preferences.getInstance(context).setString(Preferences.KEY_EMAIL, email);
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
