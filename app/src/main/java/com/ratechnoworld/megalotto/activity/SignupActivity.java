package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.R;

import java.util.Objects;

public class SignupActivity extends Activity {

    private Context context;

    public Button signupBt;
    public EditText nameEt;
    public CountryCodePicker cntrNoEt;
    public EditText mobileNoEt;
    public EditText passwordEt;
    public EditText confirmPasswordEt;
    public EditText referEt;
    public TextView textInputError;
    public TextView privacyPolicyTv;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = SignupActivity.this;

        nameEt = findViewById(R.id.name);
        cntrNoEt = findViewById(R.id.cntrno);
        mobileNoEt = findViewById(R.id.mobileno);
        passwordEt = findViewById(R.id.password);
        confirmPasswordEt = findViewById(R.id.confirmPassword);
        referEt = findViewById(R.id.refer);
        textInputError = findViewById(R.id.textinput_error);
        privacyPolicyTv = findViewById(R.id.privacyPolicy);
        signupBt = findViewById(R.id.signup);

        signupBt.setOnClickListener(v -> {
            try {
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mobileNoEt.getText().toString().equals("") && passwordEt.getText().toString().equals("") && confirmPasswordEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("All fields are mandatory");
            } else if (mobileNoEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Please enter mobile no");
            }  else if (mobileNoEt.getText().length() < 6) {
                textInputError.setText("");
                textInputError.setText("Please enter valid mobile no");
            } else if (passwordEt.getText().toString().equals("")) {
                textInputError.setText("");
                textInputError.setText("Please enter password");
            }
            else if (passwordEt.getText().toString().length() < 8) {
                textInputError.setText("");
                textInputError.setText("Password must be 8 characters");
            /*} else if (!passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())) {
                textInputError.setText("");
                textInputError.setText("Password mismatch");*/
            } else {
                textInputError.setText("");
                AppConstant.COUNTRY_CODE = "+"+cntrNoEt.getSelectedCountryCode().trim();
                Preferences.getInstance(context).setString(Preferences.KEY_FULL_NAME, nameEt.getText().toString());
                Preferences.getInstance(context).setString(Preferences.KEY_MOBILE, mobileNoEt.getText().toString());
                Preferences.getInstance(context).setString(Preferences.KEY_PASSWORD, passwordEt.getText().toString());
                Preferences.getInstance(context).setString(Preferences.KEY_REFER_CODE, referEt.getText().toString());
                Function.fireIntent(context, SignupOTPActivity.class);
            }
        });

        privacyPolicyTv.setOnClickListener(v -> Function.fireIntent(context, TermsConditionActivity.class));
    }

}
