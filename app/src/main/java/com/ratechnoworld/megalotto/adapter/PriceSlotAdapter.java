package com.ratechnoworld.megalotto.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PriceSlotAdapter extends RecyclerView.Adapter<PriceSlotAdapter.ViewHolder> {

    Context context;
    List<Contest> dataArrayList;

    public PriceSlotAdapter(Context applicationContext, List<Contest> dataArrayList) {
        this.context = applicationContext;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_prize_pool, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.amount.setText(AppConstant.CURRENCY_SIGN +""+dataArrayList.get(position).getPrize());
        holder.rank.setText(dataArrayList.get(position).getRank());
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount;
        TextView rank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            rank = itemView.findViewById(R.id.rank);
        }
    }

}
