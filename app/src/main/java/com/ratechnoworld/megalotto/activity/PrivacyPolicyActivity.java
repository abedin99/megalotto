package com.ratechnoworld.megalotto.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.ConfigurationModel;
import com.ratechnoworld.megalotto.R;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    public Context context;

    private WebView contentTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.menu_policy));

        context = PrivacyPolicyActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        contentTv = findViewById(R.id.contentTv);

        getPrivacyPolicy();

    }

    private void getPrivacyPolicy() {
        progressBarHelper.showProgressDialog();

        Call<ConfigurationModel> call = api.getPrivacyPolicy(AppConstant.PURCHASE_KEY);
        call.enqueue(new Callback<ConfigurationModel>() {
            @Override
            public void onResponse(@NonNull Call<ConfigurationModel> call, @NonNull Response<ConfigurationModel> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    ConfigurationModel legalData = response.body();
                    List<ConfigurationModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            contentTv.loadDataWithBaseURL(null, res.get(0).getPrivacy(), "text/html", "UTF-8", null);
                        }
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ConfigurationModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });

    }

}

