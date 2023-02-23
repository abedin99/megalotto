package com.ratechnoworld.megalotto.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ratechnoworld.megalotto.activity.ResultsActivity;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.helper.Preferences;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    List<Contest> dataArrayList;

    public HistoryAdapter(Context applicationContext, List<Contest> dataArrayList) {
        this.context = applicationContext;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTv.setText(String.format("Contest Id #%s", dataArrayList.get(position).getContest_id()));
        holder.dateTv.setText(dataArrayList.get(position).getDate());

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ResultsActivity.class);
            intent.putExtra("ID",dataArrayList.get(position).getContest_id());
            Preferences.getInstance(context).setString(Preferences.KEY_CONSTANT_ID,dataArrayList.get(position).getContest_id());
            Function.fireIntentWithData(context,intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView dateTv;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}


