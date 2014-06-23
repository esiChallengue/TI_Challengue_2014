package com.trackingnevera.app.trackingnevera.trackingnevera;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.AsyncTask;
import android.text.format.Time;
import java.io.File;


public class PrecargadorActivity extends Activity{

    public String dataBaseName = "";
    public String directorioINT = "";
    public Context ctx;
    public String ActivityName = "PrecargadorActivity";
    public String rutaParseo = "";
    public String servidor1;
    public String tracking_number = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_precargador);
        ctx = this;

        dataBaseName = getResources().getString(R.string.dataBaseName);
        directorioINT = getResources().getString(R.string.directorioINT);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {


                    //Creamos el directorio donde descargamos los ficheros temporales
                    crearDirectorioINT();

                    //Leemos las opciones de BD
                    DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    Cursor fila = db.rawQuery("SELECT * FROM opciones ORDER BY id_opcion DESC LIMIT 1",null);
                    //Nos aseguramos de que existe al menos un registro
                    if (fila.moveToFirst()) {
                        //Recorremos el cursor hasta que no haya m�s registros
                        do {
                            // Metemos en la string la IP o servidor al que conectarnos IP:PUERTO-PARAMETRO
                            rutaParseo = "http://"+fila.getString(2)+":"+fila.getString(3)+fila.getString(4);
                            if(fila.getString(1).equals("")){ servidor1 = "servidor 1"; }else{ servidor1 = fila.getString(1); tracking_number = fila.getString(6);}
                        } while(fila.moveToNext());
                    }else{
                        servidor1 = "servidor 1";
                    }
                    fila.close();

                    //Close the Database and the Helper
                    db.close();
                    dbHelper.close();

                    //LLamamos a la función para que nos haga un parseo de las ultimas actividades
                    new Funciones(ctx, rutaParseo, false, ActivityName);

                    //Hacemos un Sleep un rato
                    Thread.sleep(2000);


                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                }
                return null;
            }

            //Funcion que nos genera la hora actual para meterla en la BD
            private String getDatePhone(){
                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();
                return today.format("%k:%M:%S");
            }

            @Override
            protected void onPostExecute(Void result) {
                Intent ArranquePrincipal = new Intent(PrecargadorActivity.this, PrincipalActivity.class);
                ArranquePrincipal.putExtra("rutaParseo", rutaParseo);
                ArranquePrincipal.putExtra("servidor1", servidor1);
                ArranquePrincipal.putExtra("tracking", tracking_number);
                ArranquePrincipal.putExtra("firstBoot", "true");
                startActivity(ArranquePrincipal);
                finish();
            }
        };
        task.execute((Void[])null);


    }


    public void crearDirectorioINT(){
        File folder = new File(directorioINT);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }

    }


}//Fin de la clase