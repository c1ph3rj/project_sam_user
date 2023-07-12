package com.c1ph3rj.project_sam_user.dashboardpkg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.commonpkg.AgentDetailModel;
import com.c1ph3rj.project_sam_user.commonpkg.DatabaseHelper;
import com.c1ph3rj.project_sam_user.dashboardpkg.adapter.CurrentOrdersAdapter;
import com.c1ph3rj.project_sam_user.dashboardpkg.adapter.ShortOrderHistoryAdapter;
import com.c1ph3rj.project_sam_user.dashboardpkg.model.OrderDetailsModel;
import com.c1ph3rj.project_sam_user.neworderpkg.NewOrderScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
    BottomNavigationView bottomNavMenu;

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
            bottomNavMenu = findViewById(R.id.bottomNavMenu);

            currentAgentDetails = localDbHelper.getAgentDetail();
            localDbHelper.close();

            noHistoryView.setVisibility(View.GONE);
            viewAllHistoryBtn.setVisibility(View.GONE);
            viewAllCurrentOrdersBtn.setVisibility(View.GONE);

            String greetings = "HELLO,\n" + currentAgentDetails.agentName.toUpperCase(Locale.ROOT) + "!";
            userNameView.setText(greetings);

            bottomNavMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == R.id.addNewOrder){
                        showCustomDialog();
                    }
                    return false;
                }
            });

            ArrayList<OrderDetailsModel> listOfCurrentOrders = new ArrayList<>();

//            // Order 1 - Success
//            OrderDetailsModel order1 = createOrder("#20230709195301", "2023-07-09 10:30 AM", "3", 0, "Electronics", true, "₹ 1000");
//            listOfCurrentOrders.add(order1);
//
//            // Order 2 - Failed
//            OrderDetailsModel order2 = createOrder("#20230709195302", "2023-07-09 11:00 AM", "2", 1, "Clothing", false, "₹ 500");
//            listOfCurrentOrders.add(order2);
//
//            // Order 3 - Canceled
//            OrderDetailsModel order3 = createOrder("#20230709195303", "2023-07-09 11:30 AM", "1", 2, "Home Decor", true, "₹ 200");
//            listOfCurrentOrders.add(order3);
//
//            // Order 4 - Processing
//            OrderDetailsModel order4 = createOrder("#20230709195304", "2023-07-09 12:00 PM", "4", 3, "Furniture", true, "₹ 1500");
//            listOfCurrentOrders.add(order4);

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
    public void showCustomDialog() {
        // Create a dialog instance
        Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.choose_category_layout);
        customDialog.setTitle("Custom Dialog");

        Window window = customDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(null);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;  // Set the desired width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set the desired height
            window.setAttributes(layoutParams);
        }

        // Find views within the custom dialog layout
        ImageView closeButton = customDialog.findViewById(R.id.backBtn);
        ListView categoriesView = customDialog.findViewById(R.id.categoriesView);

        // Set custom dialog message
        closeButton.setOnClickListener(onClickClose -> {
            customDialog.dismiss();
        });
        String[] listOfCategories = new String[]{"Milk and Curd", "IceCreams", "Other Products"};
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(DashboardScreen.this, android.R.layout.simple_list_item_1, listOfCategories);
        categoriesView.setAdapter(categoriesAdapter);
        categoriesView.setOnItemClickListener((adapterView, view, i, l) -> {
            customDialog.dismiss();
            Intent newOrderScreenIntent = new Intent(DashboardScreen.this, NewOrderScreen.class);
            newOrderScreenIntent.putExtra("category", listOfCategories[i]);
            startActivity(newOrderScreenIntent);
        });

        // Show the custom dialog
        customDialog.show();
    }


}