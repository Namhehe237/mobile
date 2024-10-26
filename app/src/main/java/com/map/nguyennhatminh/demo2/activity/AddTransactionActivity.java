package com.map.nguyennhatminh.demo2.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.map.nguyennhatminh.demo2.R;
import com.map.nguyennhatminh.demo2.activity.AddCategoryActivity;
import com.map.nguyennhatminh.demo2.dao.CatInOutDAO;
import com.map.nguyennhatminh.demo2.dao.CategoryDAO;
import com.map.nguyennhatminh.demo2.dao.TransactionDAO;
import com.map.nguyennhatminh.demo2.model.CatInOut;
import com.map.nguyennhatminh.demo2.model.Category;
import com.map.nguyennhatminh.demo2.model.CategoryAdapter;
import com.map.nguyennhatminh.demo2.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private RadioGroup rgInOut;
    private RadioButton rbIncome, rbExpense;
    private Spinner spCategory;
    private EditText etName, etAmount, etNote;
    private TextView tvDateTime;
    private Button btnReset;
    private Button btnAdd;
    private ImageButton btnCategoryMenu;

    private CatInOutDAO catInOutDAO;
    private CategoryDAO categoryDAO;
    private TransactionDAO transactionDAO;
    private List<Category> categories;
    private Calendar selectedDateTime;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    private ActivityResultLauncher<Intent> addCategoryLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction_activity);

        initializeViews();
        setupListeners();
        loadCategories();

        addCategoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Category newCategory = (Category) data.getSerializableExtra("newCategory");
                            if (newCategory != null) {
                                loadCategories();
                                for (int i = 0; i < categories.size(); i++) {
                                    if (categories.get(i).getId() == newCategory.getId()) {
                                        spCategory.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void showCategoryMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_add_category) {
                Intent addCat = new Intent(AddTransactionActivity.this, AddCategoryActivity.class);
                boolean isIncome = rbIncome.isChecked();
                addCat.putExtra("isIncome", isIncome); // Pass the state
                addCategoryLauncher.launch(addCat);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void initializeViews() {
        rgInOut = findViewById(R.id.rgInOut);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        spCategory = findViewById(R.id.spCategory);
        etName = findViewById(R.id.etName);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        tvDateTime = findViewById(R.id.tvDateTime);
        btnReset = findViewById(R.id.btnReset);

        btnAdd = findViewById(R.id.btnAdd);
        btnCategoryMenu = findViewById(R.id.btnCategoryMenu);

        categoryDAO = new CategoryDAO(this);
        if (!categoryDAO.isSampleDataInitialized()) {
            categoryDAO.initSampleData();
        }
        transactionDAO = new TransactionDAO(this);
        catInOutDAO = new CatInOutDAO(this);
        selectedDateTime = Calendar.getInstance();
        updateDateTimeDisplay();

    }

    private void setupListeners() {
        tvDateTime.setOnClickListener(v -> showDateTimePicker());
        btnReset.setOnClickListener(v -> clearInputs());
        btnAdd.setOnClickListener(v -> addTransaction());
        rgInOut.setOnCheckedChangeListener((group, checkedId) -> loadCategories());
        btnCategoryMenu.setOnClickListener(this::showCategoryMenu);
    }

    private void loadCategories() {
        boolean isIncome = rbIncome.isChecked();
        categories = categoryDAO.getAll(isIncome);
        if(isIncome == true) {
            etName.setHint("FROM");
        }else {
            etName.setHint("TO");
        }
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        spCategory.setAdapter(adapter);
    }

    private void addTransaction() {
        int amount;
        try {
            amount = Integer.parseInt(etAmount.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        String note = etNote.getText().toString();

        boolean isIncome = rbIncome.isChecked();
        int inoutId = isIncome ? 1 : 2;
        int catId = ((Category) spCategory.getSelectedItem()).getId();

        // Get the CatInOut by inoutId and catId
        CatInOut catInOut = catInOutDAO.getCatInOutBy(catId, inoutId);

        // Check if the CatInOut is null
        if (catInOut == null) {
            String text = ((Category) spCategory.getSelectedItem()).getId() + " ";
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the Transaction object
        Transaction transaction = new Transaction(etName.getText().toString(), amount, selectedDateTime.getTime(), note, catInOut);

        // Add the transaction
        if (transactionDAO.add(transaction)) {
            Toast.makeText(this, "Thêm giao dịch thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm giao dịch thất bại", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    selectedDateTime.set(Calendar.SECOND, 0);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        tvDateTime.setText(dateTimeFormat.format(selectedDateTime.getTime()));
    }

    private void clearInputs() {
        etAmount.setText("");
        etNote.setText("");
        selectedDateTime = Calendar.getInstance();
        updateDateTimeDisplay();
        spCategory.setSelection(0);
    }
}