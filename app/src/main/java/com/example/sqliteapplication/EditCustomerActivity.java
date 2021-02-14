package com.example.sqliteapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class EditCustomerActivity extends AppCompatActivity {

    EditText name, age;
    Switch activeCustomer;
    Button cancel, update;
    int id;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        dataBaseHelper = new DataBaseHelper(getApplicationContext());

        name = findViewById(R.id.et_edit_name);
        age = findViewById(R.id.et_edit_age);
        activeCustomer = findViewById(R.id.switch_edit);
        cancel = findViewById(R.id.btn_edit_cancel);
        update = findViewById(R.id.btn_edit_update);

        id = getIntent().getIntExtra("id", -1);
        String _name = getIntent().getStringExtra("name");
        int _age = getIntent().getIntExtra("age", -1);
        boolean _isActive = getIntent().getBooleanExtra("isActive", false);

        name.setText(_name);
        age.setText(String.valueOf(_age));
        activeCustomer.setChecked(_isActive);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCheckNameExist()) {
                    CustomerModel customerModel = new CustomerModel(id, name.getText().toString(), Integer.parseInt(age.getText().toString()), activeCustomer.isChecked());
                    dataBaseHelper.updateContact(customerModel);
                    Toast.makeText(getApplicationContext(), "Details Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditCustomerActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditCustomerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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