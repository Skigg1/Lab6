package com.example.lab6;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

        private DBHelper dbHelper;
        private LinearLayout categoriesContainer;
        private ListView productsListView;
        private Button btnUpdateDB;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            categoriesContainer = findViewById(R.id.categoriesContainer);
            productsListView = findViewById(R.id.productsListView);
            btnUpdateDB = findViewById(R.id.btnUpdateDB);

            dbHelper = new DBHelper(this);

            // Обработчик кнопки обновления БД
            btnUpdateDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.updateDB();
                    loadCategories();
                    Toast.makeText(MainActivity.this, "База данных обновлена", Toast.LENGTH_SHORT).show();
                }
            });

            loadCategories();
        }



        private void loadCategories() {
            ArrayList<HashMap<String, String>> categories = dbHelper.getCategories();

            // Очищаем контейнер перед добавлением новых кнопок
            categoriesContainer.removeAllViews();

            for (HashMap<String, String> category : categories) {
                Button button = new Button(this);
                button.setText(category.get("name"));
                button.setTag(category.get("id")); // Сохраняем ID категории в тег

                // Настройка внешнего вида кнопки
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 0, 8);
                button.setLayoutParams(params);

                // Обработчик нажатия на кнопку категории
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int categoryId = Integer.parseInt(v.getTag().toString());
                        loadProducts(categoryId);
                    }
                });

                categoriesContainer.addView(button);
            }
        }

        private void loadProducts(int categoryId) {
            ArrayList<HashMap<String, String>> products = dbHelper.getProductsByCategory(categoryId);

            // Используем СУЩЕСТВУЮЩИЙ listview_item.xml
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    products,
                    R.layout.list_item_product,
                    new String[]{"name", "description", "price"},
                    new int[]{R.id.textPerson, R.id.textAchievement, R.id.textPrice}
            );

            productsListView.setAdapter(adapter);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (dbHelper != null) {
                dbHelper.closeDB();
            }
        }
    }