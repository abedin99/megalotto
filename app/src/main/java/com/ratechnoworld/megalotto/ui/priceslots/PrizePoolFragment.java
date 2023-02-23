package com.ratechnoworld.megalotto.ui.priceslots;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ratechnoworld.megalotto.adapter.PriceSlotAdapter;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.R;


public class PrizePoolFragment extends Fragment {

    public PrizePoolViewModel mViewModel;

    RecyclerView recyclerView;

    Context context;
    ProgressBarHelper progressBarHelper;
    PriceSlotAdapter contestAdapter;

    public Bundle bundle;
    String feedId;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(PrizePoolViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prize_pool, container, false);

        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);

        recyclerView = root.findViewById(R.id.recyclerView);

        loadBundle();

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            mViewModel.init(feedId);
            mViewModel.getDomesticList().observe(getViewLifecycleOwner(), invoiceModels -> {
                if (invoiceModels != null) {
                    if (invoiceModels.size() > 0) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        contestAdapter = new PriceSlotAdapter(context, invoiceModels);
                        contestAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(contestAdapter);
                    }
                }
                progressBarHelper.hideProgressDialog();
            });
        }

        return root;
    }

    private void loadBundle() {
        bundle = getArguments();
        if (bundle != null){
            feedId = bundle.getString("FEES_ID");
        }
    }
}
