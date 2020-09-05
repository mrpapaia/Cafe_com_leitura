package com.example.cafe_com_leitura;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InfoBebida extends AppCompatActivity {
    private int indiceBebida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bebida);
        Bundle args = getIntent().getExtras();
        indiceBebida = args.getInt("id");
        try {
            SQLiteOpenHelper bebidaBDHelper = new BebidasBDHelper(this);
            SQLiteDatabase db = bebidaBDHelper.getReadableDatabase();
            Cursor cursor = db.query("BEBIDA",
                    new String[]{"NOME", "DESCRICAO",
                            "IMAGEM_RECURSO_ID", "FAVORITO"},
                    "_id = ?", // busca pela chave primária
                    new String[]{Integer.toString(indiceBebida)},
                    null, null, null);

            if (cursor.moveToFirst()) {

                String nomeText = cursor.getString(0);
                String descricaoText = cursor.getString(1);
                int fotoId = cursor.getInt(2);
                boolean isFavorito = (cursor.getInt(3) == 1);
                //Seta o nome da bebida
                TextView nome = (TextView) findViewById(R.id.name);
                nome.setText(nomeText);
                TextView descricao = (TextView) findViewById(R.id.description);
                descricao.setText(descricaoText);
                ImageView photo = (ImageView) findViewById(R.id.photo);
                photo.setImageResource(fotoId);
                photo.setContentDescription(nomeText);
                CheckBox favorito = (CheckBox) findViewById(R.id.favorito);
                favorito.setChecked(isFavorito);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Banco de dados indisponível",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onFavoritoClicked(View view){
        new UpdateBebidaTarefa().execute(indiceBebida);
    }
    private class UpdateBebidaTarefa extends AsyncTask<Integer, Void, Boolean> {
        ContentValues bebidaValores;
        protected void onPreExecute() {
            CheckBox favorito = (CheckBox)findViewById(R.id.favorito);
            bebidaValores = new ContentValues();
            bebidaValores.put("FAVORITO", favorito.isChecked());
        }
        protected Boolean doInBackground(Integer... bebidas) {
            int drinkNo = bebidas[0];
            SQLiteOpenHelper bebidaBDHelper = new BebidasBDHelper(InfoBebida.this);
            try {
                SQLiteDatabase db = bebidaBDHelper.getWritableDatabase();
                db.update("BEBIDA", bebidaValores,
                        "_id = ?", new String[] {Integer.toString(drinkNo)});
                db.close();
                return true;
            } catch(SQLiteException e) {
                return false;
            }
        }
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(InfoBebida.this,
                        "Banco de dados indisponível:", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}

