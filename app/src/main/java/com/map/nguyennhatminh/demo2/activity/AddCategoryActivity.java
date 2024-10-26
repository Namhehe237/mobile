package com.map.nguyennhatminh.demo2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.map.nguyennhatminh.demo2.R;
import com.map.nguyennhatminh.demo2.dao.CategoryDAO;
import com.map.nguyennhatminh.demo2.model.Category;
import com.map.nguyennhatminh.demo2.model.CategoryAdapter;
import com.map.nguyennhatminh.demo2.model.IconAdapter;
import com.map.nguyennhatminh.demo2.model.InOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {

    private Spinner spnInOut, spnIcon, spnParentCategory;
    private EditText etName;
    private Button btnReset, btnAdd;

    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory_activity);

        spnInOut = findViewById(R.id.spnInOut);
        spnIcon = findViewById(R.id.spnIcon);
        spnParentCategory = findViewById(R.id.spnParentCategory);
        etName = findViewById(R.id.etName);
        btnReset = findViewById(R.id.btnReset);
        btnAdd = findViewById(R.id.btnAdd);

        int[] iconIds = {
                R.drawable.book,
                R.drawable.home,
                R.drawable.school,
                R.drawable.cart,
                R.drawable.gift,
                R.drawable.hang_out,
                R.drawable.salary
        };

        categoryDAO = new CategoryDAO(this);

        IconAdapter iconAdapter = new IconAdapter(this, iconIds);
        spnIcon.setAdapter(iconAdapter);

        spnInOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateParentCategorySpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        boolean isIncome = getIntent().getBooleanExtra("isIncome", true);
        spnInOut.setSelection(isIncome ? 0 : 1); // Set the initial state

        updateParentCategorySpinner(isIncome ? 0 : 1);

        btnReset.setOnClickListener(v -> resetFields());

        btnAdd.setOnClickListener(v -> addCategory());
    }

    private void updateParentCategorySpinner(int inOutType) {
        List<Category> parentCategories = new ArrayList<>();

        if (inOutType == 0) {
            parentCategories.addAll(categoryDAO.getAll(true));
        } else if (inOutType == 1) {
            parentCategories.addAll(categoryDAO.getAll(false));
        } else {
            parentCategories.addAll(categoryDAO.getAll(true));
            parentCategories.addAll(categoryDAO.getAll(false));
        }

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, parentCategories);
        spnParentCategory.setAdapter(categoryAdapter);
        spnParentCategory.setSelection(AdapterView.INVALID_POSITION);
    }

    private List<String> getAvailableIcons() {
        return Arrays.asList("icon1", "icon2", "icon3");
    }

    private void resetFields() {
        etName.setText("");
        spnInOut.setSelection(0);
        spnIcon.setSelection(0);
        spnParentCategory.setSelection(AdapterView.INVALID_POSITION);
    }

    private void addCategory() {
        String name = etName.getText().toString().trim();
        int iconIndex = spnIcon.getSelectedItemPosition();
        int iconId =(int) ((IconAdapter) spnIcon.getAdapter()).getItem(iconIndex);
        Category parentCategory = (Category) spnParentCategory.getSelectedItem();
        int inOutType = spnInOut.getSelectedItemPosition();

        if (spnParentCategory.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            parentCategory = null;
        }

        boolean isAdded = false;
        Category newCategory = null;

        if (inOutType == 2) {
            InOut inOutIncome = new InOut(1,"Thu nhập", 1);
            InOut inOutExpense = new InOut(2, "Chi tiêu",2);

            Category newCategoryIncome = new Category(name, iconId, null, parentCategory);
            Category newCategoryExpense = new Category(name, iconId, null, parentCategory);

            boolean isAddedIncome = categoryDAO.add(newCategoryIncome, inOutIncome);
            boolean isAddedExpense = categoryDAO.add(newCategoryExpense, inOutExpense);

            isAdded = isAddedIncome && isAddedExpense;
            newCategory = isAddedIncome ? newCategoryIncome : newCategoryExpense;
        } else {
            boolean isIncome = inOutType == 0;
            InOut inOut = new InOut(isIncome ? 1 : 2, isIncome ? "Thu nhập" : "Chi tiêu", isIncome ? 1 : 2);

            newCategory = new Category(name, iconId, null, parentCategory);
            isAdded = categoryDAO.add(newCategory, inOut);
        }

        if (isAdded) {
            int newCategoryId = categoryDAO.getLastInsertedCategoryId();
            newCategory.setId(newCategoryId);

            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newCategory", newCategory);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
        }
    }
}
