package com.ratechnoworld.megalotto.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ratechnoworld.megalotto.activity.NotificationDetailsActivity;
import com.ratechnoworld.megalotto.helper.AppConstant;
import com.ratechnoworld.megalotto.model.NotificationModel;
import com.ratechnoworld.megalotto.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<NotificationModel> dataArrayList;

    public NotificationAdapter(Context applicationContext, List<NotificationModel> dataArrayList) {
        this.context = applicationContext;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTv.setText(dataArrayList.get(position).getTitle());
        holder.timeTv.setText(dataArrayList.get(position).getCreated());

        if(dataArrayList.get(position).getImage() != null) {
            Glide.with(context).load(AppConstant.FILE_URL+dataArrayList.get(position).getImage())
                    .apply(new RequestOptions().override(60,60))
                    .apply(new RequestOptions().placeholder(R.drawable.app_icon).error(R.drawable.app_icon))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(holder.iconTv);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotificationDetailsActivity.class);
            intent.putExtra("title", dataArrayList.get(position).getTitle());
            intent.putExtra("description", dataArrayList.get(position).getMessage());
            intent.putExtra("image", dataArrayList.get(position).getImage());
            intent.putExtra("url", dataArrayList.get(position).getUrl());
            intent.putExtra("created", dataArrayList.get(position).getCreated());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconTv;
        TextView titleTv;
        TextView timeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconTv = itemView.findViewById(R.id.iconTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }

}
