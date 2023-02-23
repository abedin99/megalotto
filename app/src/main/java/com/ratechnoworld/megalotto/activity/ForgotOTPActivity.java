package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.R;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ForgotOTPActivity extends Activity {

    private Context context;
    private FirebaseAuth mAuth;

    public int counter = 60;
    private String verificationId;

    public Pinview otpView;
    public TextView mobileNoTv;
    public TextView textInputErrorTv;
    public Button resetBt;
    public Button verifyBt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        context = ForgotOTPActivity.this;

        mobileNoTv = findViewById(R.id.mobileno);
        otpView = findViewById(R.id.otp_view);
        textInputErrorTv = findViewById(R.id.textinput_error);
        resetBt = findViewById(R.id.reset);
        verifyBt = findViewById(R.id.verify);

        mobileNoTv.setText(AppConstant.COUNTRY_CODE + Preferences.getInstance(context).getString(Preferences.KEY_MOBILE));
        sendVerificationCode(AppConstant.COUNTRY_CODE + Preferences.getInstance(context).getString(Preferences.KEY_MOBILE));

        new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                resetBt.setText("Resend in " + counter + "s");
                counter--;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                counter = 60;
                resetBt.setText("Resend");
            }
        }.start();

        verifyBt.setOnClickListener(v -> {
            if (otpView.getValue().equals("")) {
                textInputErrorTv.setText("");
                textInputErrorTv.setText("Enter OTP");
            } else {
                verifyCode(otpView.getValue());
            }
        });

        resetBt.setOnClickListener(v -> {
            if (resetBt.getText().toString().equals("Resend")) {

                try {
                    InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sendVerificationCode(AppConstant.COUNTRY_CODE + Preferences.getInstance(context).getString(Preferences.KEY_MOBILE));
                new CountDownTimer(60000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        resetBt.setText("Resend in " + counter + "s");
                        counter--;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFinish() {
                        counter = 60;
                        resetBt.setText("Resend");
                    }
                }.start();
            }
        });

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Function.showToast(ForgotOTPActivity.this, "Otp Is Verified..");
                        Function.fireIntent(ForgotOTPActivity.this, ResetActivity.class);
                        finish();

                    } else {
                        Function.showToast(ForgotOTPActivity.this, "Otp Is Not Verified..");
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otpView.setValue(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ForgotOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

}
