package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.ratechnoworld.megalotto.BuildConfig;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.model.AppModel;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Activity {

    public Context context;
    private ApiCalling api;
    private TextView statusTv;
    private String forceUpdate, whatsNew, updateDate, latestVersionName, latestVersionCode, updateUrl;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = SplashScreen.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);

        statusTv = findViewById(R.id.statusTv);

        if(Function.checkNetworkConnection(SplashScreen.this)) {
            getAppDetails();
        }
        else {
            statusTv.setText("No internet Connection, please try again later.");
        }

    }

    private void getAppDetails() {
        Call<AppModel> call = api.getAppDetails(AppConstant.PURCHASE_KEY);
        call.enqueue(new Callback<AppModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AppModel> call, @NonNull Response<AppModel> response) {
                if (response.isSuccessful()) {
                    AppModel legalData = response.body();
                    List<AppModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            AppConstant.CURRENCY_SIGN = res.get(0).getCurrency_sign();
                            AppConstant.COUNTRY_CODE = res.get(0).getCountry_code();
                            AppConstant.PAYTM_M_ID = res.get(0).getPaytm_mer_id();
                            AppConstant.PAYU_M_ID = res.get(0).getPayu_id();
                            AppConstant.PAYU_M_KEY = res.get(0).getPayu_key();
                            AppConstant.MODE_OF_PAYMENT = res.get(0).getMop();
                            AppConstant.WALLET_MODE = res.get(0).getWallet_mode();
                            AppConstant.MAINTENANCE_MODE = res.get(0).getMaintenance_mode();
                            AppConstant.TICKET_BONUS_USED = res.get(0).getBonus_used();
                            AppConstant.APP_SHARE_PRIZE = res.get(0).getShare_prize();
                            AppConstant.APP_DOWNLOAD_PRIZE = res.get(0).getDownload_prize();
                            AppConstant.MIN_WITHDRAW_LIMIT = res.get(0).getMin_withdraw();
                            AppConstant.MAX_WITHDRAW_LIMIT = res.get(0).getMax_withdraw();
                            AppConstant.MIN_DEPOSIT_LIMIT = res.get(0).getMin_deposit();
                            AppConstant.MAX_DEPOSIT_LIMIT = res.get(0).getMax_deposit();

                            forceUpdate = res.get(0).getForce_update();
                            whatsNew = res.get(0).getWhats_new();
                            updateDate = res.get(0).getUpdate_date();
                            latestVersionName = res.get(0).getLatest_version_name();
                            latestVersionCode = res.get(0).getLatest_version_code();
                            updateUrl = res.get(0).getUpdate_url();

                            try {
                                if (BuildConfig.VERSION_CODE < Integer.parseInt(latestVersionCode)) {
                                    if (forceUpdate.equals("1")) {
                                        Intent intent = new Intent(SplashScreen.this, UpdateAppActivity.class);
                                        intent.putExtra("forceUpdate", forceUpdate);
                                        intent.putExtra("whatsNew", whatsNew);
                                        intent.putExtra("updateDate", updateDate);
                                        intent.putExtra("latestVersionName", latestVersionName);
                                        intent.putExtra("updateURL", updateUrl);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else if (forceUpdate.equals("0")) {
                                        Intent intent = new Intent(SplashScreen.this, UpdateAppActivity.class);
                                        intent.putExtra("forceUpdate", forceUpdate);
                                        intent.putExtra("whatsNew", whatsNew);
                                        intent.putExtra("updateDate", updateDate);
                                        intent.putExtra("latestVersionName", latestVersionName);
                                        intent.putExtra("updateURL", updateUrl);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                                else if (AppConstant.MAINTENANCE_MODE == 0) {
                                    new Handler().postDelayed(() -> {
                                        if (Preferences.getInstance(SplashScreen.this).getString(Preferences.KEY_IS_AUTO_LOGIN).equals("1")) {
                                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                            intent.putExtra("finish", true);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(SplashScreen.this, SigninActivity.class);
                                            intent.putExtra("finish", true);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        finish();
                                    },1000);
                                }
                                else {
                                    statusTv.setText("App is under maintenance, please try again later.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            statusTv.setText("App is under maintenance, please try again later.");
                        }
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<AppModel> call, @NonNull Throwable t) {
                statusTv.setText("App is under maintenance, please try again later.");
            }
        });
    }

}
