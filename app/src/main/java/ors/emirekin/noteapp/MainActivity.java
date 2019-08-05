package ors.emirekin.noteapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;

    static ArrayList<String> contentArray = new ArrayList<>(); //Bütün notları tutuyor
    static ArrayList<String> titleArray = new ArrayList<>(); //Bütün başlıkları tutuyor
    static ArrayList<String> printArray = new ArrayList<>(); //Sadece printlenecek notları tutuyor
    static ArrayList<String> printTitle = new ArrayList<>(); //Sadece printlenecek başlıkları tutuyor
    Intent intent;
    ListView noteList;

    String tag = "KontrolNoktası";

    static int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#673AB7")));
        getSupportActionBar().setTitle("All Notes");
        setContentView(R.layout.activity_main);

        Log.i(tag,"onCreate called.");
        Log.i(tag,"----------------");

        myDb = new DatabaseHelper(this);


        //Sadece uygulama ilk açıldığında buraya girsin
        if(flag++ == 1) {
            Cursor res = myDb.getData();
            if (res.getCount() == 0) {
                Log.i(tag, "Empty Database");
            } else {
                res.moveToFirst();
                do{
                    //Eğer database boş değilse bütün arraylistleri doldur
                    titleArray.add(res.getString(1));
                    printTitle.add(res.getString(1));
                    contentArray.add(res.getString(2));
                    printArray.add(res.getString(2));
                }while (res.moveToNext());
            }
        }


        intent = new Intent(getApplicationContext(),NoteActivity.class);

        noteList = findViewById(R.id.listView);

        listCheck(); // Eğer notlar yani contentArray boşsa "boş kutu resmini" göster, değilse notları göster.

        listItem(titleArray,contentArray); // ListView'e bu iki arrayi gönderiyoruz.

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(),NoteActivity.class);
                int pos = position;
                String str1 = "";
                String str2 = "";

                for(int i = parent.getItemAtPosition(position).toString().length() - 2;i > 0;i--){
                    //List viewde title "Second Line=" şeklinde tutulurken content "First Line=" şeklinde tutuluyor
                    //Böylece sondan geriye doğru test bir şekilde str1 i dolduruyoruz ta ki "=" işaretini görene kadar
                    //Örnek: {Second Line=Kitap, First Line=Dostoyevski - Ecinniler}
                    if(parent.getItemAtPosition(position).toString().charAt(i) != '=')
                        str1 += parent.getItemAtPosition(position).toString().charAt(i);
                    else {
                        if ((parent.getItemAtPosition(position).toString().charAt(i-1) == 'e') && (parent.getItemAtPosition(position).toString().charAt(i-2) == 'n')) //Her "=" işaretini breaklememesi için "Line" kelimesindeki 'e' ve 'n' yi de kontrol ettim
                            break;
                        else
                            str1 += parent.getItemAtPosition(position).toString().charAt(i);
                    }
                }


                //Burda ters olan str1 i str2 ye tekrardan ters bir şekilde atıyoruz, böylece content düzeliyor
                for(int i = str1.length() - 2;i >= 0; i--){
                    str2 += str1.charAt(i);
                }


                for(int i = 0;i<contentArray.size();i++){
                    //Her bir contenti istenilen(str2) contentle karşılaştırıyoruz ve bulduğumuzda pozisyonu "pos" a kaydediyoruz
                    if(str2.equals(contentArray.get(i))) {
                        pos = i;
                        break;
                    }
                }
                //İstenilen pozisyonu ve o pozisyondaki content ile title ı NoteActivitye gönderiyoruz
                intent.putExtra("position",pos);
                intent.putExtra("content", contentArray.get(pos));
                intent.putExtra("title", titleArray.get(pos));
                startActivity(intent);
            }
        });

        noteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("You are going to delete this note.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int pos = position;
                               String str1 = "";
                               String str2 = "";

                                for(int i = parent.getItemAtPosition(position).toString().length() - 2;i > 0;i--){
                                    if(parent.getItemAtPosition(position).toString().charAt(i) != '=')
                                        str1 += parent.getItemAtPosition(position).toString().charAt(i);
                                    else {
                                        if ((parent.getItemAtPosition(position).toString().charAt(i-1) == 'e') && (parent.getItemAtPosition(position).toString().charAt(i-2) == 'n'))
                                            break;
                                        else
                                            str1 += parent.getItemAtPosition(position).toString().charAt(i);
                                    }
                                }

                                for(int i = str1.length() - 2;i >= 0; i--){
                                    str2 += str1.charAt(i);
                                }

                                Log.i(tag,"Item --> " + str2);

                                for(int i = 0;i<contentArray.size();i++){
                                    if(str2.equals(contentArray.get(i))) {
                                        pos = i;
                                        break;
                                    }
                                }//Buraya kadar onClickListener da ne yaptıysak aynı

                                deleteData(pos); //Databesen sildik

                                contentArray.remove(pos);
                                titleArray.remove(pos);

                                for(int i = 0;i<printArray.size();i++){
                                    if(str2.equals(printArray.get(i))) {
                                        pos = i;
                                        break;
                                    }
                                }

                                printArray.remove(pos);
                                printTitle.remove(pos);

                                showDatabase();

                                listItem(printTitle,printArray);
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


        if(item.getItemId() == 2131165210) { // Eğer seçilen tuş not eklemeyse bu id ye sahip
            intent.putExtra("position", -1); // Yeni bir not ekleneceği için gönderdiğimiz pozisyon -1
            intent.putExtra("content", "");
            intent.putExtra("title", "");
            startActivity(intent);
        }else{ // Burası kategori seçimi
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
                    } //Burda iki tane aynı title ın gösterilmesini engelledik
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            printArray.clear();
                            printTitle.clear();
                            //Printlenecek arrayleri tekrar doldurmak için boşalttık
                            if(item.getItemId() != 0) {
                                String categoryChoose = titleArray.get(item.getItemId() - 1); //istediğim kategori
                                for (int i = 0; i < titleArray.size(); i++) {
                                    if (categoryChoose.equals(titleArray.get(i))) { //Sadece istediğim kategorideki notları göstermek için print arrayleri doldurduk
                                        printArray.add(contentArray.get(i));
                                        printTitle.add(titleArray.get(i));
                                    }
                                }
                                getSupportActionBar().setTitle(categoryChoose);
                            }else{ //Eğer id 0'a eşitse All Notes seçilmiş demektir. Bu yüzden contentArraydeki bütün notlar printArrayde de gösterilecek
                                for (int i = 0; i < titleArray.size(); i++) {
                                        printArray.add(contentArray.get(i));
                                        printTitle.add(titleArray.get(i));
                                }
                                getSupportActionBar().setTitle("All Notes");
                            }
                            listItem(printTitle,printArray); //ListView e gönderilen arrayler
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

    public void deleteData(int position){
        Integer deletedRows = myDb.deleteData(position);
        if(deletedRows > 0)
            Log.i(tag,"Data deleted.");
        else
            Log.i(tag,"Data not deleted.");
    }

    public void showDatabase(){
        Cursor res = myDb.getData();
        if(res.getCount() == 0) {
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

    @Override
    public void onBackPressed() {
        //Nothing
    }
}