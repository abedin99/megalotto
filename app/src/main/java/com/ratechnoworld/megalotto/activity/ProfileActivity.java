package com.ratechnoworld.megalotto.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.EditText;
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
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements PicModeSelectDialogFragment.IPicModeSelectListener {

    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 218;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;

    private Context context;
    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;

    private int year;
    private int month;
    private int day;

    private String uriFile;
    public String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EditText namedEt;
    public EditText genderEt;
    public EditText emailEt;
    public EditText passwordEt;
    public EditText mobileNoEt;
    public EditText dateOfBirthEt;
    public CircleImageView imageView;
    public TextView textImage;
    public TextView textInputError;
    public ImageView imageIv;
    public TextView updateTv;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        context = ProfileActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        namedEt = findViewById(R.id.name);
        emailEt = findViewById(R.id.email);
        mobileNoEt = findViewById(R.id.mobileno);
        passwordEt = findViewById(R.id.password);
        genderEt = findViewById(R.id.gender);
        dateOfBirthEt = findViewById(R.id.dateofbirth);
        imageView = findViewById(R.id.imageView);
        textImage = findViewById(R.id.textImage);
        textInputError = findViewById(R.id.textinput_error);
        imageIv = findViewById(R.id.image);
        updateTv = findViewById(R.id.update);

        mobileNoEt.setText(Preferences.getInstance(context).getString(Preferences.KEY_MOBILE));
        dateOfBirthEt.setText(Preferences.getInstance(context).getString(Preferences.KEY_DOB));

        if (Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME).equals("")) {
            namedEt.setText("");
        } else {
            namedEt.setText(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
        }

        if (Preferences.getInstance(context).getString(Preferences.KEY_EMAIL).equals("")) {
            emailEt.setText("");
        } else {
            emailEt.setText(Preferences.getInstance(context).getString(Preferences.KEY_EMAIL));
        }

        if (Preferences.getInstance(context).getString(Preferences.KEY_GENDER).equals("")) {
            genderEt.setText("");
        } else {
            genderEt.setText(Preferences.getInstance(context).getString(Preferences.KEY_GENDER));
        }

        if (Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE).equals("") || Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE).equals(AppConstant.FILE_URL+"")) {
            textImage.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            try {
                textImage.setText(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else {
            textImage.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext()).load(AppConstant.FILE_URL+Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE))
                    .apply(new RequestOptions().override(120,120))
                    .apply(new RequestOptions().placeholder(R.drawable.app_icon).error(R.drawable.app_icon))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(imageView);
        }

        genderEt.setOnClickListener(v -> {
            final String[] fonts = {"Male", "Female"};

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Select Gender");
            builder.setItems(fonts, (dialog, which) -> {
                if ("Male".equals(fonts[which])) {
                    genderEt.setText("Male");
                }
                else if ("Female".equals(fonts[which])) {
                    genderEt.setText("Female");
                }
            });
            builder.show();
        });

        imageIv.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    showAddProfilePicDialog();
                }
            } else {
                showAddProfilePicDialog();
            }
        });

        updateTv.setOnClickListener(v -> updateProfile());

        dateOfBirthEt.setOnClickListener(v -> setDateOfBirth());
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
                onCaptureImageResultInstrument(result);
            } catch (Exception ex) {
                errorValidation();
            }
        } else if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK && result != null && result.getData() != null) {
                try {
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

    @SuppressLint("SetTextI18n")
    public void updateProfile() {
        if (emailEt.getText().toString().equals("") && dateOfBirthEt.getText().toString().equals("") && passwordEt.getText().toString().equals("")) {
            textInputError.setText("Enter valid input");
        }
        else if(!emailEt.getText().toString().trim().matches(emailPattern)){
            textInputError.setText("Enter valid email id");
        }
        else {
            if (Function.checkNetworkConnection(context)) {
                String name, emailID, dob, gender;

                if (namedEt.getText().toString().equals(""))
                    name = Preferences.getInstance(context).getString(Preferences.KEY_FULL_NAME);
                else
                    name = namedEt.getText().toString();

                if (emailEt.getText().toString().equals(""))
                    emailID = Preferences.getInstance(context).getString(Preferences.KEY_EMAIL);
                else
                    emailID = emailEt.getText().toString();

                if (dateOfBirthEt.getText().toString().equals(""))
                    dob = Preferences.getInstance(context).getString(Preferences.KEY_DOB);
                else
                    dob = dateOfBirthEt.getText().toString();

                if (genderEt.getText().toString().equals(""))
                    gender = Preferences.getInstance(context).getString(Preferences.KEY_GENDER);
                else
                    gender = genderEt.getText().toString();

                if (!Preferences.getInstance(context).getString(Preferences.KEY_EMAIL).equals(emailEt.getText().toString()) || !Preferences.getInstance(context).getString(Preferences.KEY_DOB).equals(dateOfBirthEt.getText().toString()) || !Preferences.getInstance(context).getString(Preferences.KEY_GENDER).equals(genderEt.getText().toString())) {
                    progressBarHelper.showProgressDialog();

                    Call<CustomerModel> call = api.updateUserProfileDOB(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), name, emailID, gender, dob);
                    call.enqueue(new Callback<CustomerModel>() {
                        @Override
                        public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                            progressBarHelper.hideProgressDialog();

                            if (response.isSuccessful()) {
                                CustomerModel legalData = response.body();
                                List<CustomerModel.Result> res;
                                if (legalData != null) {
                                    res = legalData.getResult();
                                    if (res.get(0).getSuccess() == 1) {
                                        Preferences.getInstance(context).setString(Preferences.KEY_EMAIL, emailEt.getText().toString());
                                        Preferences.getInstance(context).setString(Preferences.KEY_DOB, dateOfBirthEt.getText().toString());
                                        Preferences.getInstance(context).setString(Preferences.KEY_FULL_NAME, namedEt.getText().toString());
                                        Preferences.getInstance(context).setString(Preferences.KEY_GENDER, genderEt.getText().toString());
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

                if (passwordEt.getText().toString().equals("")) {
                    textInputError.setText("");
                } else if (passwordEt.getText().toString().length() < 8) {
                    textInputError.setText("Password must be 8 characters");
                } else {
                    progressBarHelper.showProgressDialog();
                    textInputError.setText("");

                    Call<CustomerModel> call1 = api.updateUserProfilePassword(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), passwordEt.getText().toString());
                    call1.enqueue(new Callback<CustomerModel>() {
                        @Override
                        public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                            progressBarHelper.hideProgressDialog();

                            if (response.isSuccessful()) {
                                CustomerModel legalData = response.body();
                                List<CustomerModel.Result> res;
                                if (legalData != null) {
                                    res = legalData.getResult();
                                    if (res.get(0).getSuccess() == 1) {
                                        Preferences.getInstance(context).setString(Preferences.KEY_PASSWORD, passwordEt.getText().toString());
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
        }

    }

    public void updateUserPicture(String uriFile) {
        progressBarHelper.showProgressDialog();

        Call<List<Contest>> profileCall = api.updateUserPicture(AppConstant.PURCHASE_KEY, Preferences.getInstance(context).getString(Preferences.KEY_USER_ID), uriFile);
        profileCall.enqueue(new Callback<List<Contest>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contest>> call, @NonNull Response<List<Contest>> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    List<Contest> legalData = response.body();
                    if (legalData != null) {
                        Preferences.getInstance(context).setString(Preferences.KEY_RESORT_IMAGE, "upload/avatar/"+Preferences.getInstance(context).getString(Preferences.KEY_USER_ID)+".jpg");
                        Function.showToast(context, legalData.get(0).getMsg());
                        Function.fireIntent(context, MainActivity.class);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contest>> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    private void onCaptureImageResultInstrument(Intent data) {

        //Getting the Bitmap from Gallery
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        //Setting the Bitmap to ImageView
        uriFile = getStringImage(bitmap);
        imageView.setImageBitmap(bitmap);

        textImage.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        if (bitmap != null) {
            updateUserPicture(uriFile);
        }

    }

    private void onGalleryImageResultInstrument(Intent data) {
        final Uri saveUri = data.getData();

        try {
            //Getting the Bitmap from Gallery
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);

            //Setting the Bitmap to ImageView
            uriFile = getStringImage(bitmap);
            imageView.setImageBitmap(bitmap);

            textImage.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            if (saveUri != null) {
                updateUserPicture(uriFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setDateOfBirth() {
        final Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, 1980);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(ProfileActivity.this,
                (view, year2, monthOfYear, dayOfMonth) -> {
                    year = year2;
                    month = monthOfYear;
                    day = dayOfMonth;
                    dateOfBirthEt.setText(new StringBuilder().append(pad(month + 1)).append("/").append(pad(day)).append("/").append(year));
                }, year, month, day);

        picker.show();
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }
}
