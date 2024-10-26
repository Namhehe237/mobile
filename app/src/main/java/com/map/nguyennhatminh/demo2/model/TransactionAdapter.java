package com.map.nguyennhatminh.demo2.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.map.nguyennhatminh.demo2.R;
import com.map.nguyennhatminh.demo2.dao.TransactionDAO;
import com.map.nguyennhatminh.demo2.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private final Context context;
    private final List<Transaction> transactions;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private TransactionDAO transactionDAO;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, R.layout.list_item, transactions);
        this.context = context;
        this.transactions = transactions;

        this.transactionDAO = new TransactionDAO(context);
        this.transactionDAO.open();  // Open the database connection
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        Transaction transaction = transactions.get(position);

        TextView text = convertView.findViewById(R.id.paid);
        TextView source = convertView.findViewById(R.id.source);

        if (transaction.getCatInOut().getInOut().getType() == 1) {
            text.setText("+ " + transaction.getAmount() + " VND ");
            text.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark)); // Màu xanh
            source.setText("FROM " + transaction.getName().toUpperCase() + "  |  " + dateFormat.format(transaction.getDay()) + "  |  " + (transaction.getNote() != null ? transaction.getNote() : ""));
        } else {
            String str = transaction.getAmount() + "";
            text.setText("- " + str.substring(1) + " VND");
            text.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark)); // Màu đỏ
            source.setText("TO " + transaction.getName().toUpperCase() + "  |  " + dateFormat.format(transaction.getDay()) + "  |  " + (transaction.getNote() != null ? transaction.getNote() : ""));
        }

        return convertView;
    }
}
