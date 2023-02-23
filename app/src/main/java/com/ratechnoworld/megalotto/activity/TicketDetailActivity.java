package com.ratechnoworld.megalotto.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.google.android.material.tabs.TabLayout;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.ui.priceslots.PrizePoolFragment;
import com.ratechnoworld.megalotto.ui.ticketsold.TicketsSoldFragment;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class TicketDetailActivity extends AppCompatActivity {

    public ProgressBarHelper progressBarHelper;
    public ApiCalling api;
    private Context context;

    public TabLayout tabLayout;
    private ViewPager viewPager;

    public ImageView firstPrizeIv;
    public TextView firstPrizeTv;
    public ImageView totalBoughtIv;
    public TextView totalBoughtTv;

    public Bundle bundle;
    public String feedId, entreeFees, firstPrize, totalPrize, totalNoTickets, totalNoWinner, totalNoSold, totalNoBought, status;

    public TextView prizeTv, feeLblTv, feeTv, roomStatusTv, roomSizeTv, totalPrizeTv, joinTv;
    public ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ticket Details");

        bundle = new Bundle();
        context = TicketDetailActivity.this;
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBarHelper = new ProgressBarHelper(context, false);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        feeLblTv = findViewById(R.id.feeLblTv);
        prizeTv = findViewById(R.id.prizeTv);
        feeTv = findViewById(R.id.feeTv);
        roomStatusTv = findViewById(R.id.roomStatusTv);
        roomSizeTv = findViewById(R.id.roomSizeTv);
        firstPrizeTv = findViewById(R.id.firstPrizeTv);
        totalPrizeTv = findViewById(R.id.totalPrizeTv);
        joinTv = findViewById(R.id.joinTv);
        progressBar = findViewById(R.id.progressBar);
        firstPrizeIv = findViewById(R.id.firstPrizeIv);
        firstPrizeTv = findViewById(R.id.firstPrizeTv);
        totalBoughtIv = findViewById(R.id.totalBoughtIv);
        totalBoughtTv = findViewById(R.id.totalBoughtTv);

        Intent intent = getIntent();
        if(Objects.requireNonNull(intent.getStringExtra("TAG")).equals("0")){
            feedId = intent.getStringExtra("FEES_ID");
            entreeFees = intent.getStringExtra("ENTREE_FEES");
            totalPrize = intent.getStringExtra("TOTAL_PRIZE");
            totalNoTickets = intent.getStringExtra("TOTAL_TICKET");
            totalNoWinner = intent.getStringExtra("TOTAL_WINNERS");
            totalNoSold = intent.getStringExtra("TOTAL_SOLD");
            totalNoBought = intent.getStringExtra("TOTAL_BOUGHT");
            status = intent.getStringExtra("STATUS");

            prizeTv.setText(AppConstant.CURRENCY_SIGN +""+totalPrize);
            feeTv.setText(AppConstant.CURRENCY_SIGN +""+entreeFees);
            roomStatusTv.setText(Integer.parseInt(totalNoTickets)-Integer.parseInt(totalNoSold)+" Spots Left");
            roomSizeTv.setText(totalNoTickets+"  Spots");
            totalBoughtTv.setText(totalNoBought+" Bought");
            totalPrizeTv.setText(totalNoWinner+" Winners");
            joinTv.setText(AppConstant.CURRENCY_SIGN +""+entreeFees);

            progressBar.setMax(Integer.parseInt(totalNoTickets));
            progressBar.setProgress(Integer.parseInt(totalNoSold));

            firstPrizeIv.setVisibility(View.GONE);
            firstPrizeTv.setVisibility(View.GONE);

            feeTv.setEnabled(status.equals("2"));
        } else {
            feedId = intent.getStringExtra("FEES_ID");
            entreeFees = intent.getStringExtra("ENTREE_FEES");
            totalPrize = intent.getStringExtra("TOTAL_PRIZE");
            firstPrize = intent.getStringExtra("FIRST_PRIZE");
            totalNoTickets = intent.getStringExtra("TOTAL_TICKET");
            totalNoWinner = intent.getStringExtra("TOTAL_WINNERS");
            totalNoSold = intent.getStringExtra("TOTAL_SOLD");

            prizeTv.setText(AppConstant.CURRENCY_SIGN +""+totalPrize);
            feeTv.setText(AppConstant.CURRENCY_SIGN +""+entreeFees);
            roomStatusTv.setText(Integer.parseInt(totalNoTickets)-Integer.parseInt(totalNoSold)+" Spots Left");
            roomSizeTv.setText(totalNoTickets+"  Spots");
            firstPrizeTv.setText(AppConstant.CURRENCY_SIGN +""+firstPrize);
            totalPrizeTv.setText(totalNoWinner+" Winners");
            joinTv.setText(AppConstant.CURRENCY_SIGN +""+entreeFees);

            progressBar.setMax(Integer.parseInt(totalNoTickets));
            progressBar.setProgress(Integer.parseInt(totalNoSold));

            totalBoughtIv.setVisibility(View.GONE);
            totalBoughtTv.setVisibility(View.GONE);
        }

        Preferences.getInstance(context).setString(Preferences.KEY_PRICE, entreeFees);
        Preferences.getInstance(context).setString(Preferences.KEY_WINNER, totalNoWinner);
        Preferences.getInstance(context).setString(Preferences.KEY_SOLD, totalNoSold);

        PrizePoolFragment priceSlotsFragment = new PrizePoolFragment();
        bundle.putString("FEES_ID", feedId);
        priceSlotsFragment.setArguments(bundle);

        TicketsSoldFragment ticketsSoldFragment = new TicketsSoldFragment();
        bundle.putString("FEES_ID", feedId);
        ticketsSoldFragment.setArguments(bundle);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(priceSlotsFragment,"Winning Breakup");
        pagerAdapter.addFragment(ticketsSoldFragment,"Leaderboard");

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        feeTv.setOnClickListener(v -> buy());
    }

    public void buy(){
        Intent intent = new Intent(getApplicationContext(),PaymentActivity.class);
        intent.putExtra("FEES_ID", feedId);
        intent.putExtra("ENTREE_FEES", entreeFees);
        Function.fireIntentWithData(context,intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

