package com.c1ph3rj.project_sam_user.neworderpkg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.c1ph3rj.project_sam_user.R;
import com.c1ph3rj.project_sam_user.commonpkg.Alert;
import com.c1ph3rj.project_sam_user.commonpkg.LoadingDialog;
import com.c1ph3rj.project_sam_user.dashboardpkg.Product;
import com.c1ph3rj.project_sam_user.neworderpkg.model.ItemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NewOrderScreen extends AppCompatActivity {
    LinearLayout addProductsLayout;
    ScrollView productsLayout;
    LoadingDialog loadingDialog;
    FirebaseFirestore fireStoreDb;
    FirebaseAuth firebaseAuth;
    DocumentReference allProductsRef;
    CollectionReference selectedProductRef;
    CollectionReference userOrdersRef;
    Dialog addProductsDialog;
    Alert alert;
    String selectedCategory;
    ArrayList<ItemModel> listOfItems;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_screen);
        selectedCategory = getIntent().getStringExtra("category");

        init();
    }

    void init() {
        try {
            alert = new Alert(this);
            firebaseAuth = FirebaseAuth.getInstance();
            loadingDialog = new LoadingDialog(this);
            addProductsLayout = findViewById(R.id.addProductLayout);
            productsLayout = findViewById(R.id.productsLayout);
            fireStoreDb = FirebaseFirestore.getInstance();

            productsLayout.setVisibility(View.GONE);
            addProductsLayout.setVisibility(View.VISIBLE);

            loadingDialog.show();
            getProductsApi();

            addProductsLayout.setOnClickListener(onClickAddProducts -> addProductsDialog.show());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initAddProductsDialog() {
        try {
            addProductsDialog = new Dialog(this);
            addProductsDialog.setContentView(R.layout.add_product_layout);
            addProductsDialog.setTitle("Add Products Dialog");

            Window window = addProductsDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(null);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;  // Set the desired width
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set the desired height
                window.setAttributes(layoutParams);
            }

            TextInputLayout dateFieldLayout = addProductsDialog.findViewById(R.id.dateFieldLayout);
            TextInputLayout productNameLayout = addProductsDialog.findViewById(R.id.productNameFieldLayout);
            TextInputLayout quantityFieldLayout = addProductsDialog.findViewById(R.id.quantitiyFieldLayout);
            TextInputEditText dateField = addProductsDialog.findViewById(R.id.dateField);
            AutoCompleteTextView productNameField = addProductsDialog.findViewById(R.id.productNameField);
            TextInputEditText quantityField = addProductsDialog.findViewById(R.id.quantitiyField);
            MaterialButton addProductBtn = addProductsDialog.findViewById(R.id.addProductBtn);

            productNameField.setAdapter(getItemNames(this));

            dateField.setOnClickListener(onClickDateField -> showDatePickerDialog(dateField));

            dateField.setText(dateFormat.format(new Date()));

            addProductBtn.setOnClickListener(onClickAddProduct-> {
                try {
                    String quantity = Objects.requireNonNull(quantityField.getText()).toString().trim();
                    String date = Objects.requireNonNull(dateField.getText()).toString().trim();
                    String productName = productNameField.getText().toString().trim();

                    productNameLayout.setErrorEnabled(false);
                    quantityFieldLayout.setErrorEnabled(false);

                    if(productName.isEmpty()){
                        alert.toastAMessage("Please select the product!");
                        productNameLayout.setError("Please choose the product!");
                        return;
                    }

                    if(quantity.isEmpty()){
                        alert.toastAMessage("Please enter the quantity!");
                        quantityFieldLayout.setError("Please enter the quantity!");
                        return;
                    }


                }catch (Exception e){
                    e.printStackTrace();
                    alert.somethingWentWrong();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog(TextInputEditText dateField) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create the date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Validate if selected date is in the future
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);

                        if (selectedCalendar.after(Calendar.getInstance())) {
                            // Format the selected date as desired (e.g., "dd/MM/yyyy")

                            String selectedDate = dateFormat.format(selectedCalendar.getTime());

                            // Set the selected date to the EditText
                            dateField.setText(selectedDate);
                        } else {
                            // Show an error message for selecting past dates
                            Toast.makeText(NewOrderScreen.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                year,
                month,
                dayOfMonth
        );

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private void getProductsApi() {
        try {
            allProductsRef = fireStoreDb.collection("Items")
                    .document("ProductsInStore");
            switch (selectedCategory) {
                case "Milk and Curd":
                    selectedProductRef = allProductsRef.collection("milk");
                    break;
                case "IceCreams":
                    selectedProductRef = allProductsRef.collection("ice_cream");
                    break;
                case "Other Products":
                    selectedProductRef = allProductsRef.collection("other_products");
                    break;
            }
            selectedProductRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listOfItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ItemModel currentItem = document.toObject(ItemModel.class);
                        listOfItems.add(currentItem);
                        // Use the documentId and documentData as needed
                    }
                    loadingDialog.dismiss();
                    initAddProductsDialog();
                } else {
                    loadingDialog.dismiss();
                    alert.withTitleAndMessage("Alert", "There is no product on the Selected Category!")
                            .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                    // Error occurred while fetching documents
                }
            }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                e.printStackTrace();
                alert.somethingWentWrong();
            });
        }catch (Exception e){
            loadingDialog.dismiss();
            e.printStackTrace();
            alert.somethingWentWrong();
        }
    }

    private ArrayAdapter<String> getItemNames(Context context) {
        try {
            ArrayList<String> listOfItemNames = new ArrayList<>();
            for(ItemModel eachItem : listOfItems){
                listOfItemNames.add(eachItem.productName + " | " + eachItem.units);
            }

            // Initializing New Array Adapter with some Dummy Site values And Returning.
            return new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, listOfItemNames);
        } catch (Exception e) {
            e.printStackTrace();
            //    mCrashlytics.recordException(e);
            //    MainActivity.MobileErrorLog( e.getStackTrace()[0].getFileName() + " - " + methodName,  e.getMessage(), e.toString());
            // If Error Happens Return An Empty ArrayAdapter
            return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        }
    }// End Of getSiteNamesAdapter.
}