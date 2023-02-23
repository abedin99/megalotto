package com.ratechnoworld.megalotto.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ratechnoworld.megalotto.activity.ResultDetailsActivity;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.helper.Function;
import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyResultAdapter extends RecyclerView.Adapter<MyResultAdapter.ViewHolder> {

    Context context;
    List<Contest> dataArrayList;

    public MyResultAdapter(Context applicationContext, List<Contest> dataArrayList) {
        this.context = applicationContext;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_winner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.currencyPrizeTv.setText(String.format("Won : %s", AppConstant.CURRENCY_SIGN));
        holder.currencyFeeTv.setText(AppConstant.CURRENCY_SIGN);

        holder.winPrice.setText(dataArrayList.get(position).getWin_prize());
        holder.entryFee.setText(dataArrayList.get(position).getEntry_fee());

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ResultDetailsActivity.class);
            intent.putExtra("winPrice", dataArrayList.get(position).getWin_prize());
            intent.putExtra("entryFee", dataArrayList.get(position).getEntry_fee());
            intent.putExtra("fees_id", dataArrayList.get(position).getFees_id());
            intent.putExtra("ticket_no", dataArrayList.get(position).getTicket_no());
            Function.fireIntentWithData(context, intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView currencyFeeTv;
        TextView currencyPrizeTv;
        TextView winPrice;
        TextView entryFee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyFeeTv = itemView.findViewById(R.id.currencyFeeTv);
            currencyPrizeTv = itemView.findViewById(R.id.currencyPrizeTv);
            winPrice = itemView.findViewById(R.id.winPrice);
            entryFee = itemView.findViewById(R.id.entryFee);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}
