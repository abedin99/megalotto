package com.ratechnoworld.megalotto;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ratechnoworld.megalotto.helper.AppConstant;

import java.util.concurrent.TimeUnit;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import io.customerly.Customerly;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyApplication extends MultiDexApplication {

    private static Retrofit retrofit;
    public static Gson gson;

    public static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "DreamJob";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Customerly.configure(this, getResources().getString(R.string.customerly_app_id));
        initRetrofit();
        initGson();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void saveIsNotification(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsNotification", flag);
        editor.apply();
    }

    public boolean getNotification() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsNotification", true);
    }

    private void initRetrofit() {
        Interceptor interceptor = chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder().addHeader("Cache-Control", "no-cache");
            request = builder.build();
            return chain.proceed(request);
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .cache(null)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void initGson() {
        gson = new GsonBuilder()
                .setLenient()
                .create();
    }


    public static Retrofit getRetrofit() {
        return retrofit;
    }

}
