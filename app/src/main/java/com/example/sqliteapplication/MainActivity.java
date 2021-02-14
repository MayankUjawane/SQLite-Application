package com.example.sqliteapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button add, totalCustomers, viewAll;
    EditText age, name;
    Switch activeCustomer;
    ListView customerList;
    SearchView searchView;
    RelativeLayout relativeLayout;

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
        searchView = findViewById(R.id.search_view);
        relativeLayout = findViewById(R.id.relative_layout);

        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        //displaying list of customers on the list view
        showCustomerOnList(dataBaseHelper);

        //for searching functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            //It searches the query on the submission of content over SearchView editor. It is case dependent.
            public boolean onQueryTextSubmit(String s) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                List<CustomerModel> detailsList = dataBaseHelper.search(s);

                if (detailsList != null) {
                    customerArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.search(s));
                    customerList.setAdapter(customerArrayAdapter);
                } else {
                    List<String> nothingFound = new ArrayList<>();
                    nothingFound.add("Nothing Found");
                    customerArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, nothingFound);
                    customerList.setAdapter(customerArrayAdapter);
                }
                return false;
            }

            @Override
            //It searches the query at the time of text change over SearchView editor.
            public boolean onQueryTextChange(String s) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                List<CustomerModel> detailsList = dataBaseHelper.search(s);

                if (detailsList != null) {
                    customerArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.search(s));
                    customerList.setAdapter(customerArrayAdapter);
                } else {
                    List<String> nothingFound = new ArrayList<>();
                    nothingFound.add("Nothing Found");
                    customerArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, nothingFound);
                    customerList.setAdapter(customerArrayAdapter);
                }
                return false;
            }
        });

        //click listeners for the buttons
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add a new customer to the database whenever the button add is clicked.
                CustomerModel customerModel;
                if (isCheckNameExist()) {
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

        //dialog box of delete and update will appear.
        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Choose the option")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //dialog box for delete will appear.
                                builder.setTitle("Delete")
                                        .setMessage("Are you sure to delete?")
                                        .setIcon(R.drawable.ic_delete_24)
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                CustomerModel clickedCustomer = (CustomerModel) adapterView.getItemAtPosition(position);
                                                dataBaseHelper.deleteCustomer(clickedCustomer);
                                                showCustomerOnList(dataBaseHelper);
                                                Toast.makeText(MainActivity.this, "Deleted " + clickedCustomer.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        })
                        .setNegativeButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CustomerModel clickedCustomer = (CustomerModel) adapterView.getItemAtPosition(position);
                                int id = clickedCustomer.getId();
                                String name = clickedCustomer.getName();
                                int age = clickedCustomer.getAge();
                                boolean isActive = clickedCustomer.getIsActive();

                                Intent intent = new Intent(MainActivity.this, EditCustomerActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("name", name);
                                intent.putExtra("age", age);
                                intent.putExtra("isActive", isActive);
                                startActivity(intent);
                            }
                        }).show();
            }
        });
    }

    private void showCustomerOnList(DataBaseHelper dataBaseHelper) {
        customerArrayAdapter = new ArrayAdapter<CustomerModel>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.selectEveryone());
        customerList.setAdapter(customerArrayAdapter);
    }

    private boolean isCheckNameExist() {
        String _name = name.getText().toString().trim();
        if (_name.length() == 0) {
            name.setError("Invalid Name");
            name.requestFocus();
            return false;
        } else if (dataBaseHelper.getName(_name) != 0){
            name.setError("Name already exists");
            name.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}