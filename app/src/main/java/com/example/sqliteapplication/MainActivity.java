package com.example.sqliteapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button add, totalCustomers, viewAll;
    EditText age, name;
    Switch activeCustomer;
    ListView customerList;

    DataBaseHelper dataBaseHelper;
    ArrayAdapter customerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.btn_add);
        totalCustomers = findViewById(R.id.btn_total_customers);
        viewAll = findViewById(R.id.btn_viewAll);
        activeCustomer = findViewById(R.id.switch_activeCustomer);
        customerList = findViewById(R.id.lv_customer_list);
        age = findViewById(R.id.et_age);
        name = findViewById(R.id.et_name);


        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        //displaying list of customers on the list view
        showCustomerOnList(dataBaseHelper);

        //click listeners for the buttons
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add a new customer to the database whenever the button add is clicked.
                CustomerModel customerModel;
                try {
                    customerModel = new CustomerModel(-1, name.getText().toString(), Integer.parseInt(age.getText().toString()), activeCustomer.isChecked());
                    name.setText("");
                    age.setText("");
                    activeCustomer.setChecked(false);
                    Toast.makeText(getApplicationContext(), customerModel.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Please give valid information", Toast.LENGTH_SHORT).show();
                    customerModel = new CustomerModel(-1, "error", 0, false);
                }

                dataBaseHelper = new DataBaseHelper(getApplicationContext());
                dataBaseHelper.addOne(customerModel);
                //displaying list of customers on the list view
                showCustomerOnList(dataBaseHelper);
            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //displaying list of customers on the list view
                showCustomerOnList(dataBaseHelper);
            }
        });

        totalCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBaseHelper = new DataBaseHelper(MainActivity.this);
                int customersCount = dataBaseHelper.getCustomersCount();
                Toast.makeText(MainActivity.this, "Total Customers " + customersCount, Toast.LENGTH_LONG).show();
            }
        });

        //deleting the clicked item in list view.
        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerModel clickedCustomer = (CustomerModel) adapterView.getItemAtPosition(i);
                dataBaseHelper.deleteCustomer(clickedCustomer);
                showCustomerOnList(dataBaseHelper);
                Toast.makeText(MainActivity.this, "Deleted " + clickedCustomer.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCustomerOnList(DataBaseHelper dataBaseHelper) {
        customerArrayAdapter = new ArrayAdapter<CustomerModel>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.selectEveryone());
        customerList.setAdapter(customerArrayAdapter);
    }
}