package com.map.nguyennhatminh.demo2.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.map.nguyennhatminh.demo2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = buildHierarchicalList(categories);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_category, parent, false);
        }

        Category category = categories.get(position);

        ImageView ivIcon = convertView.findViewById(R.id.ivCategoryIcon);
        TextView tvName = convertView.findViewById(R.id.tvCategoryName);

        ivIcon.setImageResource(category.getIcon());
        tvName.setText(category.getName());

        int level = calculateLevel(category);
        int leftPadding = level * 50;
        convertView.setPadding(leftPadding, 0, 0, 0);
        return convertView;
    }

    private List<Category> buildHierarchicalList(List<Category> categories) {
        List<Category> hierarchicalList = new ArrayList<>();
        Map<Integer, List<Category>> categoryMap = new HashMap<>();

        for (Category category : categories) {
            int parentId = category.getCategory() != null ? category.getCategory().getId() : -1;
            if (!categoryMap.containsKey(parentId)) {
                categoryMap.put(parentId, new ArrayList<>());
            }
            categoryMap.get(parentId).add(category);
        }

        addCategoriesToList(hierarchicalList, categoryMap, -1);
        return hierarchicalList;
    }

    private void addCategoriesToList(List<Category> hierarchicalList, Map<Integer, List<Category>> categoryMap, int parentId) {
        if (categoryMap.containsKey(parentId)) {
            for (Category category : categoryMap.get(parentId)) {
                hierarchicalList.add(category);
                addCategoriesToList(hierarchicalList, categoryMap, category.getId());
            }
        }
    }

    private int calculateLevel(Category category) {
        int level = 0;
        Category parent = category.getCategory();
        while (parent != null) {
            level++;
            parent = parent.getCategory();
        }
        return level;
    }
}
