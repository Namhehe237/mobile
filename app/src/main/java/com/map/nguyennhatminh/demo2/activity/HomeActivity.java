package com.map.nguyennhatminh.demo2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.map.nguyennhatminh.demo2.R;
import com.map.nguyennhatminh.demo2.dao.CategoryDAO;
import com.map.nguyennhatminh.demo2.dao.TransactionDAO;
import com.map.nguyennhatminh.demo2.model.Category;
import com.map.nguyennhatminh.demo2.model.Transaction;
import com.map.nguyennhatminh.demo2.model.TransactionAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView tvToday, tvTotalIncome, tvTotalExpense;
    private ListView lvTransactions;
    private Button btnAddTransaction;
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> transactionList;
    private ArrayList<Transaction>transactionIn;
    private ArrayList<Transaction>transactionOut;
    private TransactionDAO transactionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        tvToday = findViewById(R.id.date);
        tvTotalIncome = findViewById(R.id.totalIncome);
        tvTotalExpense = findViewById(R.id.totalExpense);
        lvTransactions = findViewById(R.id.listView);
        btnAddTransaction = findViewById(R.id.AddBtn);

        transactionDAO = new TransactionDAO(this);

        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvToday.setText(today);

        loadTransactionData();

        btnAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactionData();
    }

    private void loadTransactionData() {
        try {
            Date today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(tvToday.getText().toString());
            transactionList = new ArrayList<>(transactionDAO.getTransactionsByDay(today));
            int totalIncome = 0;
            int totalExpense = 0;

            for (Transaction transaction : transactionList) {
                if (transactionDAO.isIncomeCategory(transaction.getCatInOut().getCategory())) {
                    totalIncome += transaction.getAmount();
                } else {
                    totalExpense += transaction.getAmount();
                    transaction.setAmount(transaction.getAmount() * -1);
                }
            }

            if (!transactionList.isEmpty()) {
                Collections.sort(transactionList);
            }

            tvTotalIncome.setText(" Thu : " + formatMoney(totalIncome));
            tvTotalExpense.setText(" Chi : " + formatMoney(totalExpense));

            transactionAdapter = new TransactionAdapter(this, transactionList);
            lvTransactions.setAdapter(transactionAdapter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String formatMoney(int a){

        if(a / 1000000000 > 1 ) return (a / 1000000000) + "B";
        else if(a / 1000000 > 1) return (a / 1000000 ) + "M";
        else if(a / 1000 > 1) return (a / 1000 ) + "K";
        else return a + "D";

    }

}