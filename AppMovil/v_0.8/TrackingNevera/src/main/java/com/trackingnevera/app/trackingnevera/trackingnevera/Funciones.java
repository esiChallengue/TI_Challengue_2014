package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


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
    public LocationManager locManager;
    public LocationListener locListener;
    public BluetoothAdapter bAdapter;
    BluetoothAdapter btAdapter;

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
            //borradoApunto(ctx);

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

    public void OpcionesBD(View inflatedV, String Opcion, Context ctx, String dataBaseName){

        EditText Server1NameEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Name);
        EditText Server1IpEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Ip);
        EditText Server1PuertoEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Puerto);
        EditText Server1ParametrosEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Parametros);
        EditText Server1PassphraseEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Passphrase);
        EditText Server1TrackingNumberEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1TrackingNumber);
        FrameLayout GuardarFrameLayout   = (FrameLayout)inflatedV.findViewById(R.id.FrameLayoutOpcionesLineaSave);


        String nameServer1 = Server1NameEditText.getText().toString();
        String ipServer1 = Server1IpEditText.getText().toString();
        String puertoServer1 = Server1PuertoEditText.getText().toString();
        String parametroServer1 = Server1ParametrosEditText.getText().toString();
        String Server1Passphrase = Server1PassphraseEditText.getText().toString();
        String Server1TrackingNumber = Server1TrackingNumberEditText.getText().toString();

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
                    valuesOpciones.put("ipServer1", ipServer1);
                    valuesOpciones.put("puertoServer1", puertoServer1);
                    valuesOpciones.put("parametroServer1", parametroServer1);
                    valuesOpciones.put("Server1Passphrase", Server1Passphrase);
                    valuesOpciones.put("Server1TrackingNumber", Server1TrackingNumber);
                    db.insert(DBHelper.TABLAOpciones, null, valuesOpciones);
              //  }
            } else {
                db.execSQL("UPDATE opciones SET nameServer1='"+nameServer1+"', ipServer1='"+ipServer1+"'"
                        + ", puertoServer1='"+puertoServer1+"', parametroServer1='"+parametroServer1+"'"
                        + ", Server1Passphrase='"+Server1Passphrase+"', Server1TrackingNumber='"+Server1TrackingNumber+"' WHERE id_opcion='1'");
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
                    Server1IpEditText.setText(fila.getString(2));
                    Server1PuertoEditText.setText(fila.getString(3));
                    Server1ParametrosEditText.setText(fila.getString(4));
                    Server1PassphraseEditText .setText(fila.getString(5));
                    Server1TrackingNumberEditText .setText(fila.getString(6));
                } while(fila.moveToNext()); }
            fila.close();
        }
        db.close();
        dbHelper.close();
    }

    public void actualizarLayer(View inflatedV, String Opcion, Context ctx){

        TextView textViewServer1StatusGPS  = (TextView)inflatedV.findViewById(R.id.textViewServer1StatusGPS);
        TextView textViewServerLatitudNumber   = (TextView)inflatedV.findViewById(R.id.textViewServerLatitudNumber);
        TextView textViewServerLongitudNumber   = (TextView)inflatedV.findViewById(R.id.textViewServerLongitudNumber);
        TextView textViewServerBTEstado   = (TextView)inflatedV.findViewById(R.id.textViewServerBTEstado);
        TextView textViewServerNombreEnlaceValue   = (TextView)inflatedV.findViewById(R.id.textViewServerNombreEnlaceValue);
        String estadoChip = "desconocido";
        String latitud = "";
        String longitud = "";
        String estadoBT = "desconocido";
        String estadoEnlace = "Indefinido";
        String nombreEnlace = "";

        //Obtenemos una referencia al LocationManager
        locManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        //Obtenemos la última posición conocida
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Si el GPS no está habilitado
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            estadoChip = "GPS desactivado, ¡actívelo!";
            latitud = "0.00";
            longitud = "0.00";
        }else{
            estadoChip = "GPS activado";
            //Metemos un try a la hora de solicituar la longitud y latitud para silenciar errores
            try {
                latitud = String.valueOf(loc.getLatitude());
                longitud = String.valueOf(loc.getLongitude());
            }catch (Exception e){  }
        }

        //Recureamos el nombre del enlace, que será el del trackingNumber
        DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor fila = db.rawQuery("SELECT * FROM opciones ORDER BY id_opcion DESC LIMIT 1",null);
        //Nos aseguramos de que existe al menos un registro
        if (fila.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
            do {
                // displaying all data in textview
                nombreEnlace = fila.getString(6);
            } while(fila.moveToNext()); }
        fila.close();
        db.close();
        dbHelper.close();


        // Obtenemos el adaptador Bluetooth. Si es NULL, significara que el
        // dispositivo no posee Bluetooth, por lo que deshabilitamos el boton
        // encargado de activar/desactivar esta caracteristica.
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bAdapter == null) { return; }

        // Comprobamos si el Bluetooth esta activo y cambiamos el texto del
         // boton dependiendo del estado.
        if(bAdapter.isEnabled()) {
            estadoBT = "activado";
            estadoEnlace = nombreEnlace;
        }else {
            estadoBT = "desactivado";
            estadoEnlace = "Empareje el dispositivo";
        }



        textViewServer1StatusGPS.setText(estadoChip);
        textViewServerLatitudNumber.setText(latitud);
        textViewServerLongitudNumber.setText(longitud);
        textViewServerBTEstado.setText(estadoBT);
        textViewServerNombreEnlaceValue .setText(estadoEnlace);

    }

}