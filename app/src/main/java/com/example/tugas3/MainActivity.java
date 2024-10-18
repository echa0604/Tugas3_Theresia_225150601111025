package com.example.recyclerviewsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    SQLiteOpenHelper openHelper;
    RecyclerView recyclerView;
    EditText editTextName;
    Button buttonAdd;
    DataAdapter adapter;
    List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen UI
        recyclerView = findViewById(R.id.recyclerView);
        editTextName = findViewById(R.id.editTextName);
        buttonAdd = findViewById(R.id.buttonAdd);

        // Inisialisasi database
        openHelper = new SQLiteOpenHelper(this, "mydatabase.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS names (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS names");
                onCreate(db);
            }
        };
        db = openHelper.getWritableDatabase();

        // Load data dari database dan tampilkan dalam RecyclerView
        dataList = new ArrayList<>();
        loadData();

        // Set RecyclerView
        adapter = new DataAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Button untuk menambah data baru
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                if (!name.isEmpty()) {
                    addData(name);
                    editTextName.setText("");
                    loadData();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Fungsi untuk menambah data ke dalam database
    private void addData(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        db.insert("names", null, contentValues);
    }

    // Fungsi untuk memuat data dari database ke dalam ArrayList
    private void loadData() {
        dataList.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM names", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                dataList.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    // Adapter untuk RecyclerView
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

        private List<String> dataList;

        public DataAdapter(List<String> dataList) {
            this.dataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            public void bind(String name) {
                textView.setText(name);
            }
        }
    }
}
