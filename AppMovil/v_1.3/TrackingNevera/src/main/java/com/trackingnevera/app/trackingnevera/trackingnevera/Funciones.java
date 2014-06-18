package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Set;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


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
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
    private BluetoothSocket clientSocket;
    private BroadcastReceiver discoveryMonitor;

    private ArrayAdapter<String> mArrayAdapter;

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
        final Context ctx2 = ctx;
        final TelephonyManager tm = (TelephonyManager)ctx2.getSystemService(ctx2.TELEPHONY_SERVICE);
        String phoneNumber = tm.getLine1Number();

        EditText Server1NameEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Name);
        EditText Server1IpEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Ip);
        EditText Server1PuertoEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Puerto);
        EditText Server1ParametrosEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Parametros);
        EditText Server1PassphraseEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1Passphrase);
        EditText Server1TrackingNumberEditText   = (EditText)inflatedV.findViewById(R.id.editTextServer1TrackingNumber);
        FrameLayout GuardarFrameLayout   = (FrameLayout)inflatedV.findViewById(R.id.FrameLayoutOpcionesLineaSave);
        final ListView listaDispositivos   = (ListView)inflatedV.findViewById(R.id.listViewBtDeviceListValues);
        final EditText BtMacAddressValueEditText   = (EditText)inflatedV.findViewById(R.id.editTextBtMacAddressValue);
        final EditText BtNameValueEditText   = (EditText)inflatedV.findViewById(R.id.editTextBtNameValue);
        final EditText Server1TrackingNumberEditText2   = (EditText)inflatedV.findViewById(R.id.editTextServer1TrackingNumber);
        EditText BtMacAddressValueEditText2   = (EditText)inflatedV.findViewById(R.id.editTextBtMacAddressValue);
        EditText BtNameValueEditText2   = (EditText)inflatedV.findViewById(R.id.editTextBtNameValue);
        EditText NombreOperarioEditText   = (EditText)inflatedV.findViewById(R.id.editTextNombreOperario);
        EditText ApellidosOperarioEditText   = (EditText)inflatedV.findViewById(R.id.editTextApellidosOperario);
        EditText DniOperarioEditText   = (EditText)inflatedV.findViewById(R.id.editTextDniOperario);
        EditText NumeroTelefonoOperarioEditText   = (EditText)inflatedV.findViewById(R.id.editTextNumeroTelefono);

        String nameServer1 = Server1NameEditText.getText().toString();
        String ipServer1 = Server1IpEditText.getText().toString();
        String puertoServer1 = Server1PuertoEditText.getText().toString();
        String parametroServer1 = Server1ParametrosEditText.getText().toString();
        String Server1Passphrase = Server1PassphraseEditText.getText().toString();
        String Server1TrackingNumber = Server1TrackingNumberEditText.getText().toString();
        String Server1TrackingNumber2 = Server1TrackingNumberEditText2.getText().toString();
        String BtMacAddressValue = BtMacAddressValueEditText2.getText().toString();
        String BtNameValue = BtNameValueEditText2.getText().toString();
        String NombreOperarioValue = NombreOperarioEditText.getText().toString();
        String ApellidosOperarioValue = ApellidosOperarioEditText.getText().toString();
        String DniOperarioValue = DniOperarioEditText.getText().toString();
        String NumeroTelefonoOperarioValue = phoneNumber;
        NumeroTelefonoOperarioEditText.setText(NumeroTelefonoOperarioValue);


        // TODO: Get Bluetooth Adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

         mArrayAdapter  = new ArrayAdapter<String>(ctx,  android.R.layout.simple_list_item_1);

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        listaDispositivos.setAdapter(mArrayAdapter);

        listaDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = listaDispositivos.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                //Toast.makeText(ctx2,str,Toast.LENGTH_SHORT).show();
                String partes[] = str.split("\\r?\\n");
                BtMacAddressValueEditText.setText(partes[1]);
                BtNameValueEditText.setText(partes[0]);
                Server1TrackingNumberEditText2.setText(partes[0]);

            }
        });


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
                    valuesOpciones.put("BtMacAddressValue", BtMacAddressValue);
                    valuesOpciones.put("BtNameValue", BtNameValue);
                    valuesOpciones.put("nombreOperario", NombreOperarioValue);
                    valuesOpciones.put("apellidosOperario", ApellidosOperarioValue);
                    valuesOpciones.put("dniOperario" ,DniOperarioValue);
                    valuesOpciones.put("numeroTelefono", NumeroTelefonoOperarioValue);
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
                    Server1PassphraseEditText.setText(fila.getString(5));
                    Server1TrackingNumberEditText.setText(fila.getString(6));
                    BtMacAddressValueEditText2.setText(fila.getString(7));
                    BtNameValueEditText2.setText(fila.getString(8));
                    NombreOperarioEditText.setText(fila.getString(9));
                    ApellidosOperarioEditText.setText(fila.getString(10));
                    DniOperarioEditText.setText(fila.getString(11));
                } while(fila.moveToNext()); }
            fila.close();
        }
        db.close();
        dbHelper.close();
    }

    public void actualizarLayer(View inflatedV, String Opcion, Context ctx, String temperatura){

        TextView textViewServer1StatusGPS  = (TextView)inflatedV.findViewById(R.id.textViewServer1StatusGPS);
        TextView textViewServerLatitudNumber   = (TextView)inflatedV.findViewById(R.id.textViewServerLatitudNumber);
        TextView textViewServerLongitudNumber   = (TextView)inflatedV.findViewById(R.id.textViewServerLongitudNumber);
        TextView textViewServerBTEstado   = (TextView)inflatedV.findViewById(R.id.textViewServerBTEstado);
        TextView textViewServerNombreEnlaceValue   = (TextView)inflatedV.findViewById(R.id.textViewServerNombreEnlaceValue);
        TextView textViewChipTemperaturaValue   = (TextView)inflatedV.findViewById(R.id.textViewChipTemperaturaValue);
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
        textViewChipTemperaturaValue.setText(temperatura);

    }

}