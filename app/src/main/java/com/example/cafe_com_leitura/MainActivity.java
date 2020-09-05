package com.example.cafe_com_leitura;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor favoritesCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listaFavoritos = (ListView)findViewById(R.id.lista_favoritos);
        try {
            SQLiteOpenHelper bebidaBDHelper = new BebidasBDHelper(this);
            db = bebidaBDHelper.getReadableDatabase();
            favoritesCursor = db.query("BEBIDA",
                    new String[] { "_id", "NOME"}, "FAVORITO = 1",
                    null, null, null, null);
            CursorAdapter favoriteAdapter = new SimpleCursorAdapter(MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    favoritesCursor,
                    new String[]{"NOME"},
                    new int[]{android.R.id.text1}, 0);
            listaFavoritos.setAdapter(favoriteAdapter);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Banco de dados indisponível", Toast.LENGTH_SHORT);
            toast.show();
        }

        listaFavoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InfoBebida.class);
                Bundle params = new Bundle();
                params.putInt("id", (int)id);
                intent.putExtras(params);
                startActivity(intent);
            }
        });
    }

    public void onRestart() {
        super.onRestart();
        try {
            BebidasBDHelper bebidaBDHelper = new BebidasBDHelper(this);
            db = bebidaBDHelper.getReadableDatabase();
            Cursor newCursor = db.query("BEBIDA",
                    new String[] { "_id", "NOME"},
                    "FAVORITO = 1",
                    null, null, null, null);
            ListView listaFavoritos = (ListView)findViewById(R.id.lista_favoritos);
            CursorAdapter adapter = (CursorAdapter) listaFavoritos.getAdapter();
            adapter.changeCursor(newCursor);
            favoritesCursor = newCursor;
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Banco de dados indisponível", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        favoritesCursor.close();
        db.close();
    }
    public void onClick(View v){
        Intent it = new Intent(getApplicationContext(), ListaBebidas.class);
        startActivity(it);
        System.out.println("alala");
    }
}