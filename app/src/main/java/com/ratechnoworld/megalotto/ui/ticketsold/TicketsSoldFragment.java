package com.ratechnoworld.megalotto.ui.ticketsold;

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
import android.widget.TextView;
import android.widget.Toast;

import com.ratechnoworld.megalotto.adapter.TicketSoldAdapter;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.helper.ProgressBarHelper;
import com.ratechnoworld.megalotto.R;


public class TicketsSoldFragment extends Fragment {

    private Context context;
    public TicketsSoldViewModel mViewModel;
    private ProgressBarHelper progressBarHelper;
    private TicketSoldAdapter contestAdapter;

    RecyclerView recyclerView;
    TextView textView;

    public Bundle bundle;
    String feedId;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(TicketsSoldViewModel.class);
        View root  = inflater.inflate(R.layout.fragment_tickets_sold, container, false);

        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        textView = root.findViewById(R.id.textView);

        loadBundle();

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            mViewModel.init(Preferences.getInstance(context).getString(Preferences.KEY_CONTST_ID), feedId);
            mViewModel.getDomesticList().observe(getViewLifecycleOwner(), invoiceModels -> {
                if (invoiceModels != null) {
                    if (invoiceModels.size() > 0) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        contestAdapter = new TicketSoldAdapter(context, invoiceModels);
                        contestAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(contestAdapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
                progressBarHelper.hideProgressDialog();
            });
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_LONG).show();
        }

        return  root;
    }

    private void loadBundle() {
        bundle = getArguments();
        if (bundle != null){
            feedId = bundle.getString("FEES_ID");
        }
    }

}
