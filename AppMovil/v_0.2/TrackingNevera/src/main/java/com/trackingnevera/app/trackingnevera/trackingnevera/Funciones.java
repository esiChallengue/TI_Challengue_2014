package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Funciones
{

    public String carpetaDestinoFichero = "";
    public String dataBaseName = "";
    public Boolean OpmostrarDialogo;
    public String OpzonaActivity;
    public Context ctx;
    public String versionCode="";
    public String versionName="";
    public String buildDate="";
    public String developer = "";
    public String contactEmail = "";

    public Funciones(Context context,String rutaParseo, boolean mostrarDialogo, String zonaActivity) {
        ctx = context;
        OpmostrarDialogo = mostrarDialogo;
        OpzonaActivity = zonaActivity;
        carpetaDestinoFichero = ctx.getResources().getString(R.string.carpetaDestinoFichero);
        versionCode = ctx.getResources().getString(R.string.app_versionCode);
        versionName = ctx.getResources().getString(R.string.app_versionName);
        buildDate = ctx.getResources().getString(R.string.app_buildDate);
        dataBaseName = ctx.getResources().getString(R.string.dataBaseName);
        developer = ctx.getResources().getString(R.string.app_developer);;
        contactEmail = ctx.getResources().getString(R.string.app_contactEmail);;

        if(rutaParseo.equals("")){}else{
        new DownloadFileFromURL().execute(rutaParseo);
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... sUrl) {
            int count;
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),8192);
                OutputStream output = new FileOutputStream(carpetaDestinoFichero);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            refreshActionIcon();
        }

        @Override
        public void onPostExecute(String file_url) {

            //Borramos BD antigua, parseamos el fichero, y lo cargamos en BD
            //leerYparsear(ctx);

            if(OpmostrarDialogo == true){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
            alertDialogBuilder.setTitle("Estado");
            alertDialogBuilder
                    .setMessage("Se ha enviado el comando correctamente!")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            }


        }
    }

    public void refreshActionIcon() {

    }



    //Funcion que nos genera la hora actual para meterla en la BD
    private String getDatePhone(){
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        return today.format("%k:%M:%S");
    }

    //Funcion para mostrar ventana de Información
    public void AcercaDe(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle("Acerca de");
        alertDialogBuilder
                .setMessage("Desarrollador: "+ developer +"\n\nVersion: "+ versionCode +"\n\nNombre: "+ versionName +"\n\nBuild date: "+ buildDate +"\n\nEmail: "+ contactEmail +"")
                .setCancelable(false)
                .setPositiveButton("Cerrar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Funcion paracambiar COlor de lso Botones
    public void cambiarColor(View inflatedV, int resID){
        ImageView botonCambioColor = (ImageView)inflatedV.findViewById(resID);

        AlphaAnimation transparency = new AlphaAnimation(1.00f, 0.5f);
        transparency.setDuration(300);

        botonCambioColor.startAnimation(transparency);
    }

    public void OpcionesBD(View inflatedV, String Opcion, Context ctx, String dataBaseName){

        EditText Server1NameEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Name);
        EditText Server2NameEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer2Name);
        EditText Server1IpEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Ip);
        EditText Server2IpEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer2Ip);
        EditText Server1PuertoEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Puerto);
        EditText Server2PuertoEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer2Puerto);
        EditText Server1ParametrosEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Parametros);
        EditText Server2ParametrosEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer2Parametros);
        EditText Server1PassphraseEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Passphrase);
        EditText Server2PassphraseEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer2Passphrase);
        FrameLayout GuardarFrameLayout   = (FrameLayout)inflatedV.findViewById(R.id.FrameLayoutOpcionesLineaSave);

        String nameServer1 = Server1NameEditText.getText().toString();
        String nameServer2 = Server2NameEditText.getText().toString();
        String ipServer1 = Server1IpEditText.getText().toString();
        String ipServer2 = Server2IpEditText.getText().toString();
        String puertoServer1 = Server1PuertoEditText.getText().toString();
        String puertoServer2 = Server2PuertoEditText.getText().toString();
        String parametroServer1 = Server1ParametrosEditText.getText().toString();
        String parametroServer2 = Server2ParametrosEditText.getText().toString();
        String Server1Passphrase = Server1PassphraseEditText.getText().toString();
        String Server2Passphrase = Server2PassphraseEditText.getText().toString();

        DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(Opcion.equals("guardarOpcionesBD")){
            //Cursor cursor = db.rawQuery("SELECT id_opcion FROM opciones ORDER BY id_opcion DESC LIMIT 1", null);
            Cursor cur = db.rawQuery("SELECT * FROM opciones ORDER BY id_opcion DESC", null);
            if (cur.moveToFirst() == false) {
               // cur.moveToFirst();
               // if (cur.getInt(0) == 0) {
                    //Insertamos las opciones por defecto
                    ContentValues valuesOpciones = new ContentValues();
                    valuesOpciones.put("nameServer1", nameServer1);
                    valuesOpciones.put("nameServer2", nameServer2);
                    valuesOpciones.put("ipServer1", ipServer1);
                    valuesOpciones.put("ipServer2", ipServer2);
                    valuesOpciones.put("puertoServer1", puertoServer1);
                    valuesOpciones.put("puertoServer2", puertoServer2);
                    valuesOpciones.put("parametroServer1", parametroServer1);
                    valuesOpciones.put("parametroServer2", parametroServer2);
                    valuesOpciones.put("Server1Passphrase", Server1Passphrase);
                    valuesOpciones.put("Server2Passphrase", Server2Passphrase);
                    db.insert(DBHelper.TABLAOpciones, null, valuesOpciones);
              //  }
            } else {
                db.execSQL("UPDATE opciones SET nameServer1='"+nameServer1+"' , nameServer2='"+nameServer2+"', ipServer1='"+ipServer1+"' , "
                        + "ipServer2='"+ipServer2+"', puertoServer1='"+puertoServer1+"', puertoServer2='"+puertoServer2+"', parametroServer1='"+parametroServer1+"', "
                        + "parametroServer2='"+parametroServer2+"', Server1Passphrase='"+Server1Passphrase+"', Server2Passphrase='"+Server2Passphrase+"' WHERE id_opcion='1'");
            }
          GuardarFrameLayout.setVisibility(View.VISIBLE); // Hacemos visible el layout de guardado
        }else if(Opcion.equals("leerOpcionesBD")){
            Cursor fila = db.rawQuery("SELECT * FROM opciones ORDER BY id_opcion DESC LIMIT 1",null);
            //Nos aseguramos de que existe al menos un registro
            if (fila.moveToFirst()) {
                //Recorremos el cursor hasta que no haya m�s registros
                do {
                    // displaying all data in textview
                    Server1NameEditText.setText(fila.getString(1));
                    Server2NameEditText.setText(fila.getString(2));
                    Server1IpEditText.setText(fila.getString(3));
                    Server2IpEditText.setText(fila.getString(4));
                    Server1PuertoEditText.setText(fila.getString(5));
                    Server2PuertoEditText.setText(fila.getString(6));
                    Server1ParametrosEditText.setText(fila.getString(7));
                    Server2ParametrosEditText.setText(fila.getString(8));
                    Server1PassphraseEditText .setText(fila.getString(9));
                    Server2PassphraseEditText .setText(fila.getString(10));
                } while(fila.moveToNext()); }
            fila.close();
        }
        db.close();
        dbHelper.close();
    }

    public void LogBD(View inflatedV, Context ctx, String dataBaseName){

        //TableLayout tableLayoutLogEventos   = (TableLayout)inflatedV.findViewById(R.id.tableLayoutLogEventos);

        DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int counter = 0;

        Cursor fila = db.rawQuery("SELECT id_evento,evento,fecha,status FROM logeventos ORDER BY id_evento ASC",null);
        //Nos aseguramos de que existe al menos un registro
        if (fila.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
            do {
                // create a new TableRow
                TableRow row = new TableRow(ctx);

                TextView id = new TextView(ctx);
                id.setText(fila.getString(0));
                id.setGravity(Gravity.CENTER);
                id.setBackgroundResource(R.drawable.bordes_cuadrados_2);

                TextView cmd = new TextView(ctx);
                cmd.setText(fila.getString(1));
                cmd.setGravity(Gravity.CENTER);
                cmd.setBackgroundResource(R.drawable.bordes_cuadrados_2);

                TextView status = new TextView(ctx);
                status.setText(fila.getString(3));
                status.setGravity(Gravity.CENTER);
                status.setBackgroundResource(R.drawable.bordes_cuadrados_2);

                TextView fecha = new TextView(ctx);
                fecha.setText(fila.getString(2));
                fecha.setGravity(Gravity.CENTER);
                fecha.setBackgroundResource(R.drawable.bordes_cuadrados_2);

                row.addView(id);
                row.addView(cmd);
                row.addView(status);
                row.addView(fecha);

                // add the TableRow to the TableLayout
                //tableLayoutLogEventos.addView(row,new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                counter++;
            } while(fila.moveToNext()); }
        fila.close();

        Cursor fila2 = db.rawQuery("SELECT id_evento,evento,fecha,status FROM loginfo ORDER BY id_evento ASC",null);
        //Nos aseguramos de que existe al menos un registro
        if (fila2.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
            do {
                // create a new TableRow
                TableRow row2 = new TableRow(ctx);

                TextView cmd = new TextView(ctx);
                cmd.setText(fila2.getString(1));
                cmd.setGravity(Gravity.CENTER);
                cmd.setBackgroundResource(R.drawable.bordes_cuadrados_2);

                TextView status = new TextView(ctx);
                status.setText(fila2.getString(3));
                status.setGravity(Gravity.CENTER);
                status.setBackgroundResource(R.drawable.bordes_cuadrados_2);


                row2.addView(cmd);
                row2.addView(status);

                // add the TableRow to the TableLayout
                //tableLayoutLogEventosInfo.addView(row2,new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                counter++;
            } while(fila2.moveToNext()); }
        fila2.close();

        //Close the Database and the Helper
        db.close();
        dbHelper.close();

    }


}