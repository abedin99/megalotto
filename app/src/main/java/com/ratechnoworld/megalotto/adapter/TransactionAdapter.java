package com.ratechnoworld.megalotto.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    Context context;
    List<Contest> dataArrayList;

    public TransactionAdapter(Context applicationContext, List<Contest> dataArrayList) {
        this.context = applicationContext;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.currencyTv.setText(AppConstant.CURRENCY_SIGN);

        holder.date.setText(dataArrayList.get(position).getDate());
        holder.reqAmount.setText(dataArrayList.get(position).getReq_amount());
        holder.orderID.setText(dataArrayList.get(position).getOrder_id());
        holder.statusTv.setText(dataArrayList.get(position).getRemark());

        switch (dataArrayList.get(position).getStatus()) {
            case "0":
                holder.statusIv.setImageResource(R.drawable.ic_pending);
                break;
            case "1":
                holder.statusIv.setImageResource(R.drawable.ic_accept);
                break;
            case "2":
                holder.statusIv.setImageResource(R.drawable.ic_reject);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView date;
        TextView currencyTv;
        TextView reqAmount;
        TextView orderID;
        TextView statusTv;
        ImageView statusIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            currencyTv = itemView.findViewById(R.id.currencyTv);
            reqAmount = itemView.findViewById(R.id.reqAmount);
            orderID = itemView.findViewById(R.id.orderID);
            cardView = itemView.findViewById(R.id.cardView);
            statusTv = itemView.findViewById(R.id.statusTv);
            statusIv = itemView.findViewById(R.id.statusIv);
        }
    }

}

