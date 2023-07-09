package com.c1ph3rj.project_sam_user.dashboardpkg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.commonpkg.AgentDetailModel;
import com.c1ph3rj.project_sam_user.commonpkg.DatabaseHelper;
import com.c1ph3rj.project_sam_user.dashboardpkg.adapter.CurrentOrdersAdapter;
import com.c1ph3rj.project_sam_user.dashboardpkg.adapter.ShortOrderHistoryAdapter;
import com.c1ph3rj.project_sam_user.dashboardpkg.model.OrderDetailsModel;

import java.util.ArrayList;
import java.util.Locale;

public class DashboardScreen extends AppCompatActivity {
    TextView userNameView;
    CardView settingsBtn;
    DatabaseHelper localDbHelper;
    AgentDetailModel currentAgentDetails;
    RecyclerView currentOrdersView;
    TextView viewAllCurrentOrdersBtn;
    RecyclerView shortOrderHistoryView;
    ImageView noHistoryView;
    TextView viewAllHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);
        getWindow().setStatusBarColor(getColor(R.color.color2));

        init();
    }

    void init(){
        try {
            localDbHelper = new DatabaseHelper(this);
            userNameView = findViewById(R.id.userNameView);
            settingsBtn = findViewById(R.id.settingsBtn);
            currentOrdersView = findViewById(R.id.currentOrdersView);
            viewAllCurrentOrdersBtn = findViewById(R.id.viewAllCurrentOrders);
            shortOrderHistoryView = findViewById(R.id.orderHistoryView);
            noHistoryView = findViewById(R.id.noRecordsView);
            viewAllHistoryBtn = findViewById(R.id.viewAllOrderHistory);

            currentAgentDetails = localDbHelper.getAgentDetail();
            localDbHelper.close();

            noHistoryView.setVisibility(View.GONE);
            viewAllHistoryBtn.setVisibility(View.GONE);
            viewAllCurrentOrdersBtn.setVisibility(View.GONE);

            String greetings = "HELLO,\n" + currentAgentDetails.agentName.toUpperCase(Locale.ROOT) + "!";
            userNameView.setText(greetings);


            ArrayList<OrderDetailsModel> listOfCurrentOrders = new ArrayList<>();

            // Order 1 - Success
            OrderDetailsModel order1 = createOrder("#20230709195301", "2023-07-09 10:30 AM", "3", 0, "Electronics", true, "₹ 1000");
            listOfCurrentOrders.add(order1);

            // Order 2 - Failed
            OrderDetailsModel order2 = createOrder("#20230709195302", "2023-07-09 11:00 AM", "2", 1, "Clothing", false, "₹ 500");
            listOfCurrentOrders.add(order2);

            // Order 3 - Canceled
            OrderDetailsModel order3 = createOrder("#20230709195303", "2023-07-09 11:30 AM", "1", 2, "Home Decor", true, "₹ 200");
            listOfCurrentOrders.add(order3);

            // Order 4 - Processing
            OrderDetailsModel order4 = createOrder("#20230709195304", "2023-07-09 12:00 PM", "4", 3, "Furniture", true, "₹ 1500");
            listOfCurrentOrders.add(order4);

            initCurrentOrdersView(listOfCurrentOrders);

            initShortOrderHistoryView(listOfCurrentOrders);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initShortOrderHistoryView(ArrayList<OrderDetailsModel> listOfCurrentOrders) {
        if(listOfCurrentOrders.size() == 0){
            noHistoryView.setVisibility(View.VISIBLE);
            return;
        }else {
            noHistoryView.setVisibility(View.GONE);
        }
        if(listOfCurrentOrders.size() > 3){
            viewAllHistoryBtn.setVisibility(View.VISIBLE);
        }else {
            viewAllHistoryBtn.setVisibility(View.GONE);
        }
        ShortOrderHistoryAdapter shortOrderHistoryAdapter = new ShortOrderHistoryAdapter(this, listOfCurrentOrders);
        shortOrderHistoryView.setAdapter(shortOrderHistoryAdapter);
        shortOrderHistoryView.setLayoutManager(new LinearLayoutManager(this));
        setRecyclerViewHeightBasedOnChildren(shortOrderHistoryView);
    }

    private void initCurrentOrdersView(ArrayList<OrderDetailsModel> listOfCurrentOrders) {
        if(listOfCurrentOrders.size() > 3){
            viewAllCurrentOrdersBtn.setVisibility(View.VISIBLE);
        }else {
            viewAllCurrentOrdersBtn.setVisibility(View.GONE);
        }

        CurrentOrdersAdapter currentOrdersAdapter = new CurrentOrdersAdapter(listOfCurrentOrders, this);
        currentOrdersView.setAdapter(currentOrdersAdapter);
        currentOrdersView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private static OrderDetailsModel createOrder(String orderId, String orderDateTime, String totalItems,
                                                 int paymentStatus, String category, boolean isNewOrder,
                                                 String totalAmount) {
        OrderDetailsModel order = new OrderDetailsModel();
        order.orderId = orderId;
        order.orderDateTime = orderDateTime;
        order.totalItems = totalItems;
        order.paymentStatus = paymentStatus;
        order.category = category;
        order.isNewOrder = isNewOrder;
        order.totalAmount = totalAmount;
        order.listOfProducts = new ArrayList<>();
        // Add products to the order if needed
        return order;
    }

    public void setRecyclerViewHeightBasedOnChildren(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            View listItem = recyclerView.getChildAt(i);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = totalHeight + (recyclerView.getPaddingTop() + recyclerView.getPaddingBottom());
        recyclerView.setLayoutParams(layoutParams);
    }

}