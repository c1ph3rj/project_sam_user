package com.c1ph3rj.project_sam_user.dashboardpkg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.dashboardpkg.model.OrderDetailsModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CurrentOrdersAdapter extends RecyclerView.Adapter<CurrentOrdersAdapter.ViewHolder> {
    ArrayList<OrderDetailsModel> listOfOrders;
    Context context;

    public CurrentOrdersAdapter(ArrayList<OrderDetailsModel> listOfOrders, Context context) {
        this.context = context;
        this.listOfOrders = listOfOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_order_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (listOfOrders.size() == 0) {
                holder.addNewOrderLayout.setVisibility(View.VISIBLE);
                holder.orderDetailsLayout.setVisibility(View.GONE);
            }else {
                if(position == 0){
                    holder.addNewOrderLayout.setVisibility(View.VISIBLE);
                    holder.orderDetailsLayout.setVisibility(View.GONE);
                    return;
                }else {
                    holder.addNewOrderLayout.setVisibility(View.GONE);
                    holder.orderDetailsLayout.setVisibility(View.VISIBLE);
                }
                OrderDetailsModel currentOrderDetails = listOfOrders.get(position - 1);
                holder.orderIdView.setText(currentOrderDetails.orderId);
                holder.orderedAtView.setText(currentOrderDetails.orderDateTime);
                holder.categoryView.setText(currentOrderDetails.category);
                holder.totalItemsView.setText(currentOrderDetails.totalItems);
                switch (currentOrderDetails.paymentStatus) {
                    case 0:
                        holder.paymentStatusView.setText("Success");
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
                holder.totalAmountView.setText(currentOrderDetails.totalAmount);
                holder.viewDetailsBtn.setOnClickListener(onClickViewDetailsBtn -> {

                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (listOfOrders.size() == 0) ? 1 : (listOfOrders.size() > 3)? 3 + 1: listOfOrders.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdView;
        TextView orderedAtView;
        TextView categoryView;
        TextView totalItemsView;
        TextView paymentStatusView;
        TextView totalAmountView;
        MaterialButton viewDetailsBtn;
        CardView addNewOrderLayout;
        CardView orderDetailsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdView = itemView.findViewById(R.id.orderIdView);
            orderedAtView = itemView.findViewById(R.id.dateTimeView);
            categoryView = itemView.findViewById(R.id.categoryView);
            totalItemsView = itemView.findViewById(R.id.totalItemsView);
            paymentStatusView = itemView.findViewById(R.id.paymentStatusView);
            totalAmountView = itemView.findViewById(R.id.totalAmountView);
            viewDetailsBtn = itemView.findViewById(R.id.viewMoreDetailsBtn);
            addNewOrderLayout = itemView.findViewById(R.id.addNewOrderLayout);
            orderDetailsLayout = itemView.findViewById(R.id.orderDetailsLayout);
        }
    }
}
