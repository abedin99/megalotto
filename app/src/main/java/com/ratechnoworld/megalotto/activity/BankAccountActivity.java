package com.ratechnoworld.megalotto.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Constants;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.PicModeSelectDialogFragment;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.BankModel;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankAccountActivity extends AppCompatActivity implements PicModeSelectDialogFragment.IPicModeSelectListener {

    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 218;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;

    private int status = 0;
    private String uriFile;

    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    public TextView accNameTv;
    public TextView accNoTv;
    public TextView ifscCodeTv;
    public TextView panNoTv;
    public ImageView proofCopyTv;
    public ImageView addImageIv;
    public ImageView addImage1Iv;
    public CardView proofCopyCardView;
    public CardView proofCopyCardView2;
    public TextView submitTv;
    public TextView textInputError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bank Account");

        context = BankAccountActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        accNameTv = findViewById(R.id.acc_name);
        accNoTv = findViewById(R.id.acc_no);
        ifscCodeTv = findViewById(R.id.ifsc_code);
        panNoTv = findViewById(R.id.pan_no);
        addImageIv = findViewById(R.id.addImage);
        addImage1Iv = findViewById(R.id.addImage1);
        proofCopyTv = findViewById(R.id.proof_copy);
        proofCopyCardView = findViewById(R.id.proofCopyCardView);
        proofCopyCardView2 = findViewById(R.id.proofCopyCardView2);
        submitTv = findViewById(R.id.submit);
        textInputError = findViewById(R.id.textinput_error);

        if(Function.checkNetworkConnection(context)) {
            getBankInfo();
        }

        submitTv.setOnClickListener(v -> updateBankInfo());

        addImageIv.setOnClickListener(v -> addImage());

        proofCopyCardView.setOnClickListener(v -> proofCopyCardView());

        addImage1Iv.setOnClickListener(v -> addImage1());
    }

    private void getBankInfo() {
        progressBarHelper.showProgressDialog();

        Call<BankModel> call = api.getBankInfo(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<BankModel>() {
            @Override
            public void onResponse(@NonNull Call<BankModel> call, @NonNull Response<BankModel> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    BankModel legalData = response.body();
                    List<BankModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            accNameTv.setText(res.get(0).getAcc_name());
                            accNoTv.setText(res.get(0).getAcc_no());
                            ifscCodeTv.setText(res.get(0).getIfsc_code());
                            panNoTv.setText(res.get(0).getPan_no());
                            if (res.get(0).getProof_copy() != null) {
                                proofCopyCardView.setVisibility(View.GONE);
                                proofCopyCardView2.setVisibility(View.VISIBLE);

                                Glide.with(getApplicationContext()).load(AppConstant.FILE_URL+res.get(0).getProof_copy())
                                        .apply(new RequestOptions().override(720,540))
                                        .apply(new RequestOptions().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder))
                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                        .apply(RequestOptions.skipMemoryCacheOf(true))
                                        .into(proofCopyTv);
                            } else {
                                proofCopyCardView.setVisibility(View.VISIBLE);
                                proofCopyCardView2.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<BankModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    private void addBankDetail() {
        if (status == 1) {
            progressBarHelper.showProgressDialog();

            Call<List<Contest>> call = api.updateBankInfo(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), accNameTv.getText().toString(), accNoTv.getText().toString(), ifscCodeTv.getText().toString(), panNoTv.getText().toString(), uriFile, "0");
            call.enqueue(new Callback<List<Contest>>() {
                @Override
                public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                    progressBarHelper.hideProgressDialog();
                    if (response.isSuccessful()) {
                        List<Contest> legalData = response.body();
                        if (legalData != null) {
                            Function.showToast(context,legalData.get(0).getMsg());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Contest>> call, @NonNull Throwable t) {
                    progressBarHelper.hideProgressDialog();
                }
            });
        } else {
            progressBarHelper.showProgressDialog();
           
            Call<List<Contest>> call = api.updateBankInfoWithoutProofCopy(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), accNameTv.getText().toString(), accNoTv.getText().toString(), ifscCodeTv.getText().toString(), panNoTv.getText().toString(),"0");
            call.enqueue(new Callback<List<Contest>>() {
                @Override
                public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                    progressBarHelper.hideProgressDialog();
                    if (response.isSuccessful()) {
                        List<Contest> legalData = response.body();
                        if (legalData != null) {
                            Function.showToast(context,legalData.get(0).getMsg());
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<Contest>> call, @NonNull Throwable t) {
                    progressBarHelper.hideProgressDialog();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateBankInfo() {
        if (accNameTv.getText().toString().equals("") && accNoTv.getText().toString().equals("") && ifscCodeTv.getText().toString().equals("")) {
            textInputError.setText("");
            textInputError.setText("All fields are mandatory");
        }else if (accNameTv.getText().toString().equals("")) {
            textInputError.setText("");
            textInputError.setText("Please enter account name");
        } else if (accNoTv.getText().toString().equals("")) {
            textInputError.setText("");
            textInputError.setText("Please enter account no");
        }else if (ifscCodeTv.getText().toString().equals("")) {
            textInputError.setText("");
            textInputError.setText("Please enter ifsc code");
        }else if (status == 0) {
            textInputError.setText("");
            textInputError.setText("Attach any bank account proof for verification");
        }else {
            addBankDetail();
        }
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAddProfilePicDialog();
            }
        }
    }

    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void takePic() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        try {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bmp != null){
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            try {
                status = 1;
                onCaptureImageResultInstrument(result);
            } catch (Exception ex) {
                errorValidation();
            }
        } else if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK && result != null && result.getData() != null) {
                try {
                    status = 1;
                    onGalleryImageResultInstrument(result);
                } catch (Exception e) {
                    errorValidation();
                }
            } else {
                errorValidation();
            }

        }
    }

    public void userCancelled() {

    }

    public void errorValidation() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
        finish();
    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        if (action.equals(Constants.IntentExtras.ACTION_CAMERA)) {
            takePic();
        } else {
            pickImage();
        }
    }

    private void onCaptureImageResultInstrument(Intent data) {
        //Getting the Bitmap from Gallery
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        //Setting the Bitmap to ImageView
        uriFile = getStringImage(bitmap);
        proofCopyTv.setImageBitmap(bitmap);

        proofCopyCardView.setVisibility(View.GONE);
        proofCopyCardView2.setVisibility(View.VISIBLE);
    }

    private void onGalleryImageResultInstrument(Intent data) {
        final Uri saveUri = data.getData();

        try {
            //Getting the Bitmap from Gallery
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);

            //Setting the Bitmap to ImageView
            uriFile = getStringImage(bitmap);
            proofCopyTv.setImageBitmap(bitmap);

            proofCopyCardView.setVisibility(View.GONE);
            proofCopyCardView2.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addImage(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                showAddProfilePicDialog();
            }
        } else {
            showAddProfilePicDialog();
        }
    }

    public void proofCopyCardView(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                showAddProfilePicDialog();
            }
        } else {
            showAddProfilePicDialog();
        }
    }

    public void addImage1(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                showAddProfilePicDialog();
            }
        } else {
            showAddProfilePicDialog();
        }
    }

}
