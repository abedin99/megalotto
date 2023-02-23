package com.ratechnoworld.megalotto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ratechnoworld.megalotto.model.Contest;
import com.ratechnoworld.megalotto.R;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TicketSoldAdapter extends RecyclerView.Adapter<TicketSoldAdapter.ViewHolder> {

    Context context;
    List<Contest> dataArrayList;

    public TicketSoldAdapter(Context applicationContext, List<Contest> dataArrayList) {
        this.context = applicationContext;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ticket_sold, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ticketNo.setText(dataArrayList.get(position).getTicket_no());
        holder.username.setText(dataArrayList.get(position).getUsername());
        try {
            holder.userImage.setText(dataArrayList.get(position).getUsername());
        }catch (Exception e ){
            e.printStackTrace();
        }
        int[] androidColors = context.getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        holder.userImage.setBackgroundColor(randomAndroidColor);
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ticketNo;
        TextView username;
        TextView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketNo = itemView.findViewById(R.id.ticket_no);
            username = itemView.findViewById(R.id.username);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }

}
