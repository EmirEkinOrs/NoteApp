package ors.emirekin.noteapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    EditText noteContent;
    AutoCompleteTextView categoryText;
    Intent intent;
    DatabaseHelper myDb;

    static String prevContent = null;

    ArrayList<String> titleShowArray = new ArrayList<>();

    String tag = "kontrolnoktası";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#673AB7")));
        getSupportActionBar().setTitle("Add Note");
        setContentView(R.layout.activity_note);

        myDb = new DatabaseHelper(this);

        intent = getIntent();


        titleShowArray.clear();
        for(int i = 0;i < MainActivity.titleArray.size();i++){
            if(titleShowArray.isEmpty())
                titleShowArray.add(MainActivity.titleArray.get(i));
            else{
                int flag = 0;
                for(int j = 0; j < titleShowArray.size();j++){
                    if(MainActivity.titleArray.get(i).equals(titleShowArray.get(j)))
                        flag = -1;
                }
                if(flag == 0)
                    titleShowArray.add(MainActivity.titleArray.get(i));
            }
        }

        categoryText = findViewById(R.id.categoryText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titleShowArray);
        categoryText.setAdapter(adapter);

        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryText.showDropDown();
            }
        });
        categoryText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    categoryText.callOnClick();
            }
        });
        categoryText.setThreshold(1);
        categoryText.setText(intent.getStringExtra("title"));

        noteContent = findViewById(R.id.noteContent);
        noteContent.setText(intent.getStringExtra("content"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        final int position = intent.getIntExtra("position",-1);


        if(position != -1)
            prevContent = MainActivity.contentArray.get(position);

        String s = "";

        if( !s.equals(categoryText.getText().toString())) {

            if (position != -1) {
                MainActivity.contentArray.set(position, noteContent.getText().toString());
                try {
                    MainActivity.printArray.set(position, noteContent.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

                MainActivity.titleArray.set(position, categoryText.getText().toString().substring(0,1).toUpperCase() + categoryText.getText().toString().substring(1));
                MainActivity.printTitle.set(position, categoryText.getText().toString().substring(0,1).toUpperCase() + categoryText.getText().toString().substring(1));

                updateData(position);
            }else {
                MainActivity.contentArray.add(noteContent.getText().toString());
                MainActivity.printArray.add(noteContent.getText().toString());
                MainActivity.titleArray.add(categoryText.getText().toString().substring(0,1).toUpperCase() + categoryText.getText().toString().substring(1));
                MainActivity.printTitle.add(categoryText.getText().toString().substring(0,1).toUpperCase() + categoryText.getText().toString().substring(1));
                insertData();

            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }else{

            new AlertDialog.Builder(NoteActivity.this)
                    .setTitle("You have not entered a category.")
                    .setMessage("Uncategorized notes fall into the default category. Is it okay for you?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (position != -1) {
                                MainActivity.titleArray.set(position, "Default");
                                MainActivity.printTitle.set(position, "Default");
                            }else {
                                MainActivity.titleArray.add("Default");
                                MainActivity.printTitle.add("Default");
                            }
                            MainActivity.contentArray.add(noteContent.getText().toString());
                            MainActivity.printArray.add(noteContent.getText().toString());
                            insertData();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
        }

        return true;
    }

    public void updateData(int position){
        Log.i(tag,"updateData called.");
        Log.i(tag,"[" + position + "] Title = " + MainActivity.titleArray.get(position) + " | Content = " + MainActivity.contentArray.get(position));
        boolean isUpdate = myDb.updateData(position);
        if(isUpdate)
            Log.i(tag,"Update başarılı");
        else
            Log.i(tag,"Update başarısız");

        showDatabase();
        Log.i(tag,"----------------");
    }

    public void insertData(){
        Log.i(tag,"insertData called.");
        for(int i = 0;i < MainActivity.titleArray.size();i++){
            Log.i(tag,"[" + i + "] Title = " + MainActivity.titleArray.get(i) + " | Content = " + MainActivity.contentArray.get(i));
        }
        boolean isInserted = myDb.insertData();
        if (isInserted)
            Log.i(tag, "InsertData Başarılı");
        else
            Log.i(tag, "InsertData Başarısız");

        showDatabase();
        Log.i(tag,"----------------");
    }

    public void showDatabase(){
        Log.i(tag,"showDatabase called.");
        Cursor res = myDb.getData();
        if(res.getCount() == 0) {
            // show message
            Log.i(tag,"Nothing found. Database is empty.");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Title :"+ res.getString(1)+"\n");
            buffer.append("Content :"+ res.getString(2)+"\n\n");
        }

        // Show all data
        Log.i(tag,"Database -- > \n" + buffer.toString());
        Log.i(tag,"----------------");
    }
}
