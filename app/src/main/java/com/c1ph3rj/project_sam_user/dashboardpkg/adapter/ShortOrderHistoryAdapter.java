package com.c1ph3rj.project_sam_user.dashboardpkg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.dashboardpkg.model.OrderDetailsModel;

import java.util.ArrayList;

public class ShortOrderHistoryAdapter extends RecyclerView.Adapter<ShortOrderHistoryAdapter.ViewHolder> {
    private ArrayList<OrderDetailsModel> listOfOrders;
    private Context context;

    public ShortOrderHistoryAdapter(Context context, ArrayList<OrderDetailsModel> listOfOrders) {
        this.context = context;
        this.listOfOrders = listOfOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.short_order_history_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailsModel currentOrderDetail = listOfOrders.get(position);

        holder.orderIdView.setText(currentOrderDetail.orderId);
        holder.orderedAtView.setText(currentOrderDetail.orderDateTime);
        holder.categoryView.setText(currentOrderDetail.category);
        holder.totalAmountView.setText(currentOrderDetail.totalAmount);

        switch (currentOrderDetail.paymentStatus) {
            case 0:
                holder.paymentStatusView.setText("Paid");
                holder.paymentStatusView.setTextColor(context.getColor(R.color.payment_success));
                break;
            case 1:
                holder.paymentStatusView.setText("Failed");
                holder.paymentStatusView.setTextColor(context.getColor(R.color.payment_failed));
                break;
            case 2:
                holder.paymentStatusView.setText("Canceled");
                holder.paymentStatusView.setTextColor(context.getColor(R.color.payment_cancelled));
                break;
            case 3:
                holder.paymentStatusView.setText("Processing");
                holder.paymentStatusView.setTextColor(context.getColor(R.color.payment_processing));
                break;
            default:
                holder.paymentStatusView.setText("Unknown");
                holder.paymentStatusView.setTextColor(context.getColor(R.color.payment_partially_paid));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(listOfOrders.size(), 3);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdView;
        TextView categoryView;
        TextView orderedAtView;
        TextView paymentStatusView;
        TextView totalAmountView;

        public ViewHolder(View itemView) {
            super(itemView);
            orderIdView = itemView.findViewById(R.id.orderIdView);
            orderedAtView = itemView.findViewById(R.id.dateTimeView);
            categoryView = itemView.findViewById(R.id.categoryView);
            totalAmountView = itemView.findViewById(R.id.totalAmountView);
            paymentStatusView = itemView.findViewById(R.id.paymentStatusView);
        }
    }
}
