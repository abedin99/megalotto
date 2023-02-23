package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ratechnoworld.megalotto.adapter.ViewPagerAdapter;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Constants;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.model.CustomerModel;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.model.Packages;
import com.ratechnoworld.megalotto.ui.tickets.TicketsFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.customerly.Customerly;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawer;
    public NavigationView navigationView;
    public Toolbar toolbar;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public TextView timer;
    public TextView contest;

    public ActionBarDrawerToggle mDrawerToggle;
    private ProgressBarHelper progressBarHelper;
    private ApiCalling api;
    private Context context;

    public TextView navUsername,emailId,imageText;
    private CircleImageView imageView;
    public FloatingActionButton chatBt;

    public SwitchCompat switchNoti;
    public MyApplication MyApp;
    public FirebaseAnalytics mFirebaseAnalytics;

    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private long mMilliSeconds = 0;

    public TimerListener mListener;
    private CountDownTimer mCountDownTimer;

    private long backPressed;

    public interface TimerListener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        MyApp = MyApplication.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        timer = findViewById(R.id.timer);
        contest = findViewById(R.id.contest);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }
                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            drawer.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        navUsername = headerView.findViewById(R.id.username);
        navUsername.setText(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));

        emailId = headerView.findViewById(R.id.emailId);
        emailId.setText(Preferences.getInstance(context).getString(Preferences.KEY_EMAIL));

        imageView = headerView.findViewById(R.id.imageView);
        imageText = headerView.findViewById(R.id.imageText);

        if (Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE).equals("") || Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE).equals(AppConstant.FILE_URL+"")) {
            imageText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            try {
                imageText.setText(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageText.setVisibility(View.GONE);
            try {
                Glide.with(getApplicationContext()).load(AppConstant.FILE_URL+Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE))
                        .apply(new RequestOptions().override(60,60))
                        .apply(new RequestOptions().placeholder(R.drawable.app_icon).error(R.drawable.app_icon))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .into(imageView);
             }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }

        chatBt = findViewById(R.id.chatBt);
        chatBt.setOnClickListener(v -> Customerly.openSupport(MainActivity.this));

        switchNoti = findViewById(R.id.switch_noti);
        switchNoti.setChecked(MyApp.getNotification());

        switchNoti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.e("aaa-noti", "" + isChecked);
            MyApp.saveIsNotification(isChecked);
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
        });

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this, viewPager, tabLayout);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                Log.d("Selected", "Selected " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });

        if(Function.checkNetworkConnection(context)){
            getLiveContest();
        }
    }

    public void addPage(String id, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("PKG_ID", id);
        bundle.putString("PKG_NAME", title);
        TicketsFragment dynamicFragment = new TicketsFragment();
        dynamicFragment.setArguments(bundle);

        Preferences.getInstance(context).setString(Preferences.KEY_PKG_ID,id);

        adapter.addFrag(dynamicFragment, title);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(adapter.getCount() - 1);
    }

    private void setDynamicFragmentToTabLayout() {
        progressBarHelper.showProgressDialog();

        Call<List<Packages>> call = api.getPackages(AppConstant.PURCHASE_KEY);
        call.enqueue(new Callback<List<Packages>>() {
            @Override
            public void onResponse(@NonNull Call<List<Packages>> call, @NonNull Response<List<Packages>> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    List<Packages> legalData = response.body();
                    if (legalData != null) {
                        if (legalData.size() > 0) {
                            for (int i = 0; i < legalData.size(); i++) {
                                String id = legalData.get(i).getId();
                                String title = legalData.get(i).getPkg_name();
                                addPage(id, title);
                                viewPager.setCurrentItem(0);
                            }
                        }
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Packages>> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                Function.fireIntent(this, NotificationActivity.class);
                return true;
            case R.id.menu_profile:
                Function.fireIntent(this, ProfileActivity.class);
                return false;
            case R.id.action_wallet:
            case R.id.menu_wallet:
                Function.fireIntent(this, WalletActivity.class);
                return true;
            case R.id.menu_my_ticket:
                Function.fireIntent(this, MyTicketActivity.class);
                return true;
            case R.id.menu_history:
                Function.fireIntent(this, HistoryActivity.class);
                return true;
            case R.id.menu_refer_earn:
                Function.fireIntent(this, ReferActivity.class);
                return true;
            case R.id.menu_policy:
                Function.fireIntent(this, PrivacyPolicyActivity.class);
                return true;
            case R.id.menu_terms:
                Function.fireIntent(this, TermsConditionActivity.class);
                return true;
            case R.id.menu_contact:
                Function.fireIntent(this, ContactUsActivity.class);
                return true;
            case R.id.menu_about:
                Function.fireIntent(this, AboutUsActivity.class);
                return true;
            case R.id.logout:
                alertDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog alertDialog() {
        return new AlertDialog.Builder(this)
                // set dialog icon
                .setIcon(R.drawable.logout)
                // Set Dialog Message
                .setMessage("Are You Sure You Want to Logout?")
                // positive button
                .setPositiveButton("Confirm", (dialog, which) -> Preferences.getInstance(MainActivity.this).setlogout())
                // negative button
                .setNegativeButton("Cancel", (dialog, which) -> {
                }).create();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Function.fireIntent(this, ProfileActivity.class);
                break;
            case R.id.menu_wallet:
                Function.fireIntent(this, WalletActivity.class);
                break;
            case R.id.menu_my_ticket:
                Function.fireIntent(this, MyTicketActivity.class);
                break;
            case R.id.menu_history:
                Function.fireIntent(this, HistoryActivity.class);
                break;
            case R.id.menu_refer_earn:
                Function.fireIntent(this, ReferActivity.class);
                break;
            case R.id.menu_policy:
                Function.fireIntent(this, PrivacyPolicyActivity.class);
                break;
            case R.id.menu_terms:
                Function.fireIntent(this, TermsConditionActivity.class);
                break;
            case R.id.menu_contact:
                Function.fireIntent(this, ContactUsActivity.class);
                break;
            case R.id.menu_about:
                Function.fireIntent(this, AboutUsActivity.class);
                break;
            case R.id.logout:
                alertDialog().show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLiveContest() {
        progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.getLiveContest(AppConstant.PURCHASE_KEY);
        call.enqueue(new Callback<CustomerModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            if(res.get(0).getEnd_time() != null && res.get(0).getCurrent_time() != null){
                                Preferences.getInstance(context).setString(Preferences.KEY_CONTST_ID,res.get(0).getId());
                                int time = Integer.parseInt(res.get(0).getEnd_time()) - Integer.parseInt(res.get(0).getCurrent_time());

                                if (time > 0) {
                                    setTime(time * 1000L);
                                    startCountDown();
                                }
                                else {
                                    progressBarHelper.hideProgressDialog();
                                    contest.setText("Result will announce soon");
                                }
                            }
                        }else {
                            getUpcomingContest();
                        }
                    }

                    if(Function.checkNetworkConnection(context)){
                        setDynamicFragmentToTabLayout();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });

    }

    private void getUpcomingContest() {
        Call<CustomerModel> call = api.getUpcomingContest(AppConstant.PURCHASE_KEY);

        call.enqueue(new Callback<CustomerModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            if(res.get(0).getStart_time() != null && res.get(0).getCurrent_time() != null){
                                contest.setText("Next contest starts in: ");
                                int time = Integer.parseInt(res.get(0).getStart_time()) - Integer.parseInt(res.get(0).getCurrent_time());

                                if (time > 0) {
                                    setTime(time * 1000L);
                                    startCountDown();
                                }
                                else {
                                    progressBarHelper.hideProgressDialog();
                                    contest.setText("Contest will start soon");
                                }
                            }
                        }
                        else {
                            contest.setText("No Upcoming Contest");
                        }
                    }
                    else {
                        contest.setText("No Upcoming Contest");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerModel> call, @NonNull Throwable t) {
            }
        });

    }

    private void initCounter() {
        mCountDownTimer = new CountDownTimer(mMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                calculateTime(millisUntilFinished,timer);
                if (mListener != null) {
                    mListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                calculateTime(0,timer);
                if (mListener != null) {
                    mListener.onFinish();
                }
                Function.fireIntent(context,MainActivity.class);
            }
        };
    }

    public void startCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    public void setTime(long milliSeconds) {
        mMilliSeconds = milliSeconds;
        initCounter();
        calculateTime(milliSeconds,timer);
    }

    private void calculateTime(long milliSeconds, TextView timeText) {
        mSeconds = (milliSeconds / 1000) % 60;
        mMinutes = (milliSeconds / (1000 * 60)) % 60;
        mHours = (milliSeconds / (1000 * 60 * 60));

        displayText(timeText);
    }

    private void displayText(TextView timeText) {
        try{
            String stringBuilder = getTwoDigitNumber(mHours) + ":" + getTwoDigitNumber(mMinutes) + ":" + getTwoDigitNumber(mSeconds) + "";
            timeText.setText(stringBuilder);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE).equals("") || Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE).equals(AppConstant.FILE_URL+"")) {
            imageText.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            try {
                String a = String.valueOf(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME).charAt(0));
                imageText.setText(a);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageText.setVisibility(View.GONE);
            try {

                Glide.with(getApplicationContext()).load(AppConstant.FILE_URL+Preferences.getInstance(context).getString(Preferences.KEY_RESORT_IMAGE))
                        .apply(new RequestOptions().override(60,60))
                        .apply(new RequestOptions().placeholder(R.drawable.app_icon).error(R.drawable.app_icon))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .into(imageView);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }

        //getUserProfile();
    }

}
