package com.c1ph3rj.project_sam_user.dashboardpkg.model;

import com.c1ph3rj.project_sam_user.dashboardpkg.Product;

import java.util.ArrayList;

public class OrderDetailsModel {
    public String orderId;
    public String orderDateTime;
    public String totalItems;
    public int paymentStatus;
    public String category;
    public boolean isNewOrder;
    public String totalAmount;
    public ArrayList<Product> listOfProducts;
}
