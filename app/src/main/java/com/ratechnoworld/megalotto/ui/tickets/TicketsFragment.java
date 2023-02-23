package com.ratechnoworld.megalotto.ui.tickets;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ratechnoworld.megalotto.adapter.ContestAdapter;
import com.ratechnoworld.megalotto.api.ApiCalling;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.MyApplication;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.R;
import com.ratechnoworld.megalotto.model.CustomerModel;

import java.util.List;

public class TicketsFragment extends Fragment {

    private Context context;
    private ApiCalling api;
    private TicketsViewModel mViewModel;
    public ProgressBarHelper progressBarHelper;
    private ContestAdapter contestAdapter;

    RecyclerView recyclerView;
    TextView textContest;

    public Bundle bundle;
    String id, title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(TicketsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tickets, container, false);

        api = MyApplication.getRetrofit().create(ApiCalling.class);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        textContest = root.findViewById(R.id.textContest);

        bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("PKG_ID");
            title = bundle.getString("PKG_NAME");
        }

        if (Function.checkNetworkConnection(context)) {
            getContestStatus();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_LONG).show();
        }

        return root;
    }


    private void getContestStatus() {
        //progressBarHelper.showProgressDialog();

        Call<CustomerModel> call = api.getContestStatus(AppConstant.PURCHASE_KEY);
        call.enqueue(new Callback<CustomerModel>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<CustomerModel> call, @NonNull Response<CustomerModel> response) {
                //progressBarHelper.hideProgressDialog();

                if (response.isSuccessful()) {
                    CustomerModel legalData = response.body();
                    List<CustomerModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            if (res.get(0).getLive_contest() == 1) {
                                textContest.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                mViewModel.init(Preferences.getInstance(context).getString(Preferences.KEY_CONTST_ID), id);
                                mViewModel.getDomesticList().observe(getViewLifecycleOwner(), invoiceModels -> {
                                    if (invoiceModels != null) {
                                        if (invoiceModels.size() > 0) {
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                            recyclerView.setLayoutManager(linearLayoutManager);
                                            contestAdapter = new ContestAdapter(context, invoiceModels);
                                            contestAdapter.notifyDataSetChanged();
                                            recyclerView.setAdapter(contestAdapter);
                                        }
                                    }
                                });
                            } else if (res.get(0).getUpcoming_contest() == 1) {
                                textContest.setText("Upcoming contest");
                                textContest.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else if (res.get(0).getUpcoming_contest() == 0 && res.get(0).getLive_contest() == 0) {
                                textContest.setText("No Upcoming Contest");
                                textContest.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            textContest.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<CustomerModel> call, @NonNull Throwable t) {
                //progressBarHelper.hideProgressDialog();
            }
        });

    }


}
