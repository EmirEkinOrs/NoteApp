package ors.emirekin.noteapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> contentArray = new ArrayList<>();
    static ArrayList<String> titleArray = new ArrayList<>();
    static ArrayList<String> printArray = new ArrayList<>();
    static ArrayList<String> printTitle = new ArrayList<>();
    Intent intent;
    ListView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#673AB7")));
        getSupportActionBar().setTitle("All Notes");
        setContentView(R.layout.activity_main);

        intent = new Intent(getApplicationContext(),NoteActivity.class);

        noteList = findViewById(R.id.listView);
        listCheck();

        listItem(titleArray,contentArray);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(),NoteActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("content", contentArray.get(position));
                intent.putExtra("title", titleArray.get(position));
                startActivity(intent);
            }
        });

        noteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("You are going to delete this note.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int pos = position;
                                for(int i = 0;i<contentArray.size();i++){
                                    if(printArray.get(position).equals(contentArray.get(i))) {
                                        pos = i;
                                        break;
                                    }
                                }
                                contentArray.remove(pos);
                                titleArray.remove(pos);

                                printArray.remove(position);

                                listItem(titleArray,printArray);
                                listCheck();
                            }
                        })
                        .setNegativeButton("No",null)
                .show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId() == 2131165210) {
            intent.putExtra("position", -1);
            intent.putExtra("content", "");
            intent.putExtra("title", "");
            startActivity(intent);
        }else{
            this.findViewById(R.id.noteCategory).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);

                    popupMenu.getMenu().add(Menu.NONE,0,0,"All Notes");

                    int flag;
                    for(int i = 0;i < titleArray.size();i++) {
                        flag = 0;
                        for(int j = 0;j < i;j++){
                            if(titleArray.get(i).equals(titleArray.get(j))) {
                                flag = -1;
                                break;
                            }
                        }
                        if(flag != -1)
                            popupMenu.getMenu().add(Menu.NONE,i+1,i+1,titleArray.get(i));
                    }
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            printArray.clear();
                            printTitle.clear();
                            if(item.getItemId() != 0) {
                                String categoryChoose = titleArray.get(item.getItemId() - 1);
                                for (int i = 0; i < titleArray.size(); i++) {
                                    if (categoryChoose.equals(titleArray.get(i))) {
                                        printArray.add(contentArray.get(i));
                                        printTitle.add(titleArray.get(i));
                                    }
                                }
                                getSupportActionBar().setTitle(categoryChoose);
                            }else{
                                for (int i = 0; i < titleArray.size(); i++) {
                                        printArray.add(contentArray.get(i));
                                        printTitle.add(titleArray.get(i));
                                }
                                getSupportActionBar().setTitle("All Notes");
                            }
                            listItem(printTitle,printArray);
                            return false;
                        }
                    });
                }
            });
            this.findViewById(R.id.noteCategory).callOnClick();
        }

        return true;
    }

    void listCheck(){
        ImageView image = findViewById(R.id.imageView);
        if(!contentArray.isEmpty()){
            image.setVisibility(View.INVISIBLE);
            noteList.setVisibility(View.VISIBLE);
        }else{
            image.setVisibility(View.VISIBLE);
            noteList.setVisibility(View.INVISIBLE);
        }
    }

    void listItem(ArrayList<String> arr1,ArrayList<String> arr2){

        ListView resultsListView = findViewById(R.id.listView);

        HashMap<String, String> listContent = new HashMap<>();

        for(int i=0;i < arr2.size();i++) {
            listContent.put("\n" + arr2.get(i), arr1.get(i));
        }

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.text1, R.id.text2});


        Iterator it = listContent.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        resultsListView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("contentArray",contentArray);
        outState.putStringArrayList("titleArray",titleArray);
        outState.putStringArrayList("printTitle",printTitle);
        outState.putStringArrayList("printArray",printArray);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        contentArray = savedInstanceState.getStringArrayList("contentArray");
        titleArray = savedInstanceState.getStringArrayList("titleArray");
        printTitle = savedInstanceState.getStringArrayList("printTitle");
        printArray = savedInstanceState.getStringArrayList("printArray");

        listItem(titleArray,contentArray);
    }


}


























