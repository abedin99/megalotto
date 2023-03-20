package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.model.Token;
import com.ratechnoworld.megalotto.payu.ServiceWrapper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity implements PaymentResultListener {

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    public RadioGroup radioGroup;
    public RadioButton payTmRb, payuRb, RazorPayRb;
    private TextInputEditText amountEt;
    public TextView signTv, noteTv, alertTv;
    private Button submitBt;

    public String amountSt;
    public String orderIdSt;
    public String paymentIdSt;
    public String checksumSt;
    public String tokenSt;
    private String mopSt = "PayTm";

    private final Integer activityRequestCode = 2;
    private static final String TAG = PaymentActivity.class.getSimpleName();

    private final PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    private PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Deposit");

        context = DepositActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        radioGroup = findViewById(R.id.radioGroup);
        payTmRb = findViewById(R.id.payTmRb);
        payuRb = findViewById(R.id.payuRb);
        RazorPayRb = findViewById(R.id.RazorPayRb);
        amountEt = findViewById(R.id.amountEt);
        noteTv = findViewById(R.id.noteTv);
        alertTv = findViewById(R.id.alertTv);
        signTv = findViewById(R.id.signTv);
        submitBt = findViewById(R.id.submitBt);

        signTv.setText(AppConstant.CURRENCY_SIGN);
        alertTv.setText(String.format("Minimum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_DEPOSIT_LIMIT));

        payTmRb.setOnClickListener(v -> mopSt = "PayTm");

        payuRb.setOnClickListener(v -> mopSt = "PayUMoney");

        RazorPayRb.setOnClickListener(v -> mopSt = "RazorPay");

        switch (AppConstant.MODE_OF_PAYMENT) {
            case 1:
                radioGroup.setVisibility(View.GONE);
                payTmRb.setVisibility(View.VISIBLE);
                payuRb.setVisibility(View.GONE);
                RazorPayRb.setVisibility(View.GONE);
                mopSt = "PayTm";
                break;
            case 2:
                radioGroup.setVisibility(View.GONE);
                payTmRb.setVisibility(View.GONE);
                payuRb.setVisibility(View.VISIBLE);
                RazorPayRb.setVisibility(View.GONE);
                mopSt = "PayUMoney";
                break;
            case 3:
                radioGroup.setVisibility(View.GONE);
                payTmRb.setVisibility(View.GONE);
                payuRb.setVisibility(View.GONE);
                RazorPayRb.setVisibility(View.VISIBLE);
                mopSt = "RazorPay";
                break;
            case 4:
                radioGroup.setVisibility(View.GONE);
                payTmRb.setVisibility(View.GONE);
                payuRb.setVisibility(View.GONE);
                RazorPayRb.setVisibility(View.VISIBLE);
                mopSt = "ManualPay";
                break;
            default:
                radioGroup.setVisibility(View.VISIBLE);
                payTmRb.setVisibility(View.VISIBLE);
                payuRb.setVisibility(View.VISIBLE);
                RazorPayRb.setVisibility(View.VISIBLE);
                mopSt = "PayTm";
                break;
        }

        submitBt.setOnClickListener(v -> {
            submitBt.setEnabled(false);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Random rand = new Random();
            int min =1000, max= 9999;

            // nextInt as provided by Random is exclusive of the top value so you need to add 1
            int randomNum = rand.nextInt((max - min) + 1) + min;
            orderIdSt = System.currentTimeMillis() + randomNum + Preferences.getInstance(this).getString(Preferences.KEY_USER_ID);
            paymentIdSt = orderIdSt;

            amountSt = Objects.requireNonNull(amountEt.getText()).toString();
            if (!amountSt.isEmpty()) {
                double payout = Integer.parseInt(amountEt.getText().toString());

                if (payout < AppConstant.MIN_DEPOSIT_LIMIT) {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText(String.format("Minimum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_DEPOSIT_LIMIT));
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                } else if (payout > AppConstant.MAX_DEPOSIT_LIMIT) {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText(String.format("Maximum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MAX_DEPOSIT_LIMIT));
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    alertTv.setVisibility(View.GONE);
                    Preferences.getInstance(context).setInt(Preferences.KEY_DEPOSITE_AMOUNT, Integer.parseInt(amountSt));
                    try {
                        submitBt.setEnabled(false);
                        switch (mopSt) {
                            case "PayTm":
                                getPayTmToken(amountSt);
                                break;
                            case "PayUMoney":
                                startPayUMoney();
                                break;
                            case "RazorPay":
                                startRazorPay(amountSt);
                                break;
                            case "ManualPay":
                                startManualPay(amountSt);
                                break;
                        }
                    } catch (NullPointerException e) {
                        submitBt.setEnabled(true);
                    }
                }
            } else {
                submitBt.setEnabled(true);
                alertTv.setVisibility(View.VISIBLE);
                alertTv.setText(String.format("Minimum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_DEPOSIT_LIMIT));
                alertTv.setTextColor(Color.parseColor("#ff0000"));
            }
        });

    }

    private void addDepositTransaction(String paymentID) {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.addDepositTransaction(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), paymentID, String.valueOf(Preferences.getInstance(context).getInt(Preferences.KEY_DEPOSITE_AMOUNT)),mopSt);
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
                        onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });

    }



    private void startRazorPay(String amount) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;
        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", Preferences.getInstance(context).getString(Preferences.KEY_USER));
            options.put("description", "Added Using RazorPay");

            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(amount) * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", Preferences.getInstance(context).getString(Preferences.KEY_EMAIL));
            preFill.put("contact", Preferences.getInstance(context).getString(Preferences.KEY_MOBILE));
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void startManualPay(String amount) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;
        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", Preferences.getInstance(context).getString(Preferences.KEY_USER));
            options.put("description", "Added Using RazorPay");

            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(amount) * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", Preferences.getInstance(context).getString(Preferences.KEY_EMAIL));
            preFill.put("contact", Preferences.getInstance(context).getString(Preferences.KEY_MOBILE));
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String paymentID) {
        try {
            // Loading jsonarray in Background Thread
            addDepositTransaction(paymentID);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toast.makeText(getApplicationContext(), "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }



    private  void getPayTmToken(String amount){
        Log.e(TAG, " get token start");
        if (Function.checkNetworkConnection(DepositActivity.this)) {
            Call<Token> callToken = api.generateTokenCall("12345", AppConstant.PAYTM_M_ID, orderIdSt, amount);
            callToken.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                    Log.e(TAG, " respo "+ response.isSuccessful() );
                    try {
                        if (response.isSuccessful() && response.body()!=null){
                            if (!response.body().getBody().getTxnToken().equals("")) {
                                Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                                startPaytmPayment(response.body().getBody().getTxnToken());
                            }else {
                                Log.e(TAG, " Token status false");
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG, " error in Token Res "+e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                    Log.e(TAG, " response error "+t.getMessage());
                }
            });
        }
    }

    public void startPaytmPayment (String token){
        tokenSt = token;
        // for test mode use it
        //String host = "https://securegw-stage.paytm.in/";

        // for production mode use it
        String host = "https://securegw.paytm.in/";

        String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdSt;
        Log.e(TAG, " callback URL "+callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdSt, AppConstant.PAYTM_M_ID, tokenSt, amountSt, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());

                orderIdSt = bundle.getString("ORDERID");
                String status = bundle.getString("STATUS");
                paymentIdSt = bundle.getString("TXNID");
                checksumSt = bundle.getString("CHECKSUMHASH");

                if(Objects.requireNonNull(status).equalsIgnoreCase("TXN_SUCCESS")) {
                    addDepositTransaction(paymentIdSt);
                }
            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+ s);
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, activityRequestCode);
    }



    private void startPayUMoney() {
        builder.setAmount(amountSt)                                                                 // Payment amount
                .setTxnId(paymentIdSt)                                                              // Transaction ID
                .setPhone(Preferences.getInstance(this).getString(Preferences.KEY_MOBILE))          // User Phone number
                .setProductName("Wallet Balance")                                                   // Product Name or description
                .setFirstName(Preferences.getInstance(this).getString(Preferences.KEY_FULL_NAME))   // User First name
                .setEmail(Preferences.getInstance(this).getString(Preferences.KEY_EMAIL))           // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")               // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")               // Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                                                                  // Integration environment - true (Debug)/ false(Production)
                .setKey(AppConstant.PAYU_M_KEY)                                                     // Merchant key
                .setMerchantId(AppConstant.PAYU_M_ID);
        try {
            paymentParam = builder.build();
            getHashkey();

        } catch (Exception e) {
            Log.e(TAG, " error s "+e.getMessage());
        }
    }

    public void getHashkey(){
        ServiceWrapper service = new ServiceWrapper(null);
        Call<String> call = service.newHashCall(AppConstant.PAYU_M_KEY, paymentIdSt, amountSt, "Wallet Balance", Preferences.getInstance(this).getString(Preferences.KEY_FULL_NAME), Preferences.getInstance(this).getString(Preferences.KEY_EMAIL));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                Log.e(TAG, "hash res "+response.body());
                String merchantHash= response.body();
                if (Objects.requireNonNull(merchantHash).isEmpty()) {
                    Toast.makeText(DepositActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "hash empty");
                } else {
                    // mPaymentParams.setMerchantHash(merchantHash);
                    paymentParam.setMerchantHash(merchantHash);
                    // Invoke the following function to open the checkout page.
                    // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, DepositActivity.this, R.style.AppTheme_default, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e(TAG, "hash error "+ t.getMessage());
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG ," result code "+resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
                    //Success Transaction
                    checksumSt = "123";
                    addDepositTransaction(paymentIdSt);
                } else{
                    //Failure Transaction
                    Toast.makeText(DepositActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e(TAG, "tran "+payuResponse+"---"+ merchantResponse);
            }
        }
        else if (requestCode == activityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(data.getStringExtra("response")));
                String status = jsonObject.getString("STATUS");

                if(status.equalsIgnoreCase("TXN_SUCCESS")) {
                    paymentIdSt = jsonObject.getString("TXNID");
                    checksumSt = jsonObject.getString("CHECKSUMHASH");
                    orderIdSt = jsonObject.getString("ORDERID");

                    addDepositTransaction(paymentIdSt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e(TAG, " TXNID "+  paymentIdSt);
            Log.e(TAG, " CHECKSUMHASH "+  checksumSt);
            Log.e(TAG, " ORDERID "+  orderIdSt);

            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));
        } else {
            Log.e(TAG, " payment failed");
        }
    }
}
