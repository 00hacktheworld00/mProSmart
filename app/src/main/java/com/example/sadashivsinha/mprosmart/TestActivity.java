package com.example.sadashivsinha.mprosmart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example);

        main();
    }

    List items;

    public void Person(String name, Date dob)
    {
        this.name = name;
        this.dob = dob;
    }

    public void BankAccount(String accountNo, String bankName, Double balance)
    {
        this.accountNo = accountNo;
        this.bankName = bankName;
        this.balance = balance;
    }


    public void Customer(String customerId, String billingAddress)
    {
        this.customerId = customerId;
        this.billingAddress = billingAddress;
    }


//    public void bankingSystem()
//    {
//        createCustomer("1", "India", "101", "ABC Bank", 40000.00);
//    }

    //---

    String name;
    Date dob;

    String accountNo, bankName;
    Double balance;

    String customerId, billingAddress;
    List customer = new ArrayList();

    public void createCustomer(String customerId, String name, Date dob, String billingAddress, String accountNo, String bankName, Double balance )
    {
        this.name = name;
        this.dob = dob;
        this.customerId = customerId;
        this.billingAddress = billingAddress;
        this.accountNo = accountNo;
        this.bankName = bankName;
        this.balance = balance;

        Toast.makeText(this, "Created cus : " + name + dob + customerId + billingAddress + accountNo + bankName + balance, Toast.LENGTH_SHORT).show();
    }

    public void listCustomer()
    {
        for(int i=0; i<customer.size(); i++)
        {
            String val = customer.get(i).toString();
            System.out.println(val);
            Toast.makeText(this, val, Toast.LENGTH_SHORT).show();
        }
    }

    public void findCustomer(String customer)
    {
        for(int i=0; i<customer.length(); i++)
        {
            if(customer.equals(customerId))
                System.out.println(customer);
            Toast.makeText(this, "FOUND" + customer, Toast.LENGTH_SHORT).show();
        }
    }

    public void depositAmount(String customer, Double amount)
    {
        if(customer.equals(customerId))
        {
            balance = balance + amount;
            System.out.println(balance);
            Toast.makeText(this, String.valueOf(balance), Toast.LENGTH_SHORT).show();
        }
    }

    public Double withdrawAmount(Double amount)
    {
        if(balance<amount)
        {
            System.out.print("Amount is more than Balance Amount");
            Toast.makeText(this, "Amount is more than Balance Amount", Toast.LENGTH_SHORT).show();
        }
        else
        {
            balance = balance - amount;
            Toast.makeText(this, "BALANCE" + balance, Toast.LENGTH_SHORT).show();
        }
        return balance;
    }



    public void main()
    {
        Toast.makeText(this, "Reached here", Toast.LENGTH_SHORT).show();
        createCustomer("1", "Sady", new Date(19-07-1992), "India", "101", "ABC Bank", 40000.00);
        createCustomer("2", "Daddy", new Date(01-02-2014), "India", "102", "XYZ Bank", 20000.00);
        listCustomer();
        findCustomer("1");
        depositAmount("Sady", 4000.00);
        withdrawAmount(2000.00);
        Toast.makeText(this, "Reached end", Toast.LENGTH_SHORT).show();
    }
}
