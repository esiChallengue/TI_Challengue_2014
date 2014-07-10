package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.net.URLEncoder;
import java.util.Set;
import java.util.UUID;
import java.lang.reflect.Method;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PrincipalActivity extends FragmentActivity implements ActionBar.TabListener {

    CollectionPagerAdapter mCollectionPagerAdapter;
    ViewPager mViewPager;
    public Context ctx;
    public int valorMenu;
    public String dataBaseName = "";
    public Fragment fragment;
    public String ActivityName = "PrincipalActivity";
    public String servidor1 = "";
    public String rutaParseo = "";
    public String rutaParseoFull = "";
    public String tracking_number = "";
    public Bundle extras ;
    public int flagAdelante = 0;
    public LocationManager locManager;
    public LocationListener locListener;
    Handler mHandler = new Handler();
    Handler mHandler2 = new Handler();
    Handler h;

    final int RECIEVE_MESSAGE = 1;		// Status  for Handler
    private static final String TAG = "bluetooth"; //cabecera del debugeo bluetooth
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "00:14:03:03:02:30";
    public String phoneNumber = "";
    public String nombreOperario = "";
    public String apellidosOperario = "";
    public String dniOperario = "";
    public String firtBoot;
    public Thread idThread;
    public int flagVistaCreada = 0;
    public int flagEnviarAlive = 1;

    public String mensajeRecv = "";
    public String[] mensajeRecv_temp = null;
    public String xAxis = "";
    public String yAxis = "";
    public String zAxis = "";
    public String temperatura = "";
    public String notas = "";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    public sendAlivePacket conexionViva = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_principal);

        extras = getIntent().getExtras();
        rutaParseo = extras.getString("rutaParseo");
        servidor1 = extras.getString("servidor1");
        tracking_number = extras.getString("tracking");
        firtBoot = extras.getString("firstBoot");

        if(firtBoot.equals("true")) {

            ctx = this;
            dataBaseName = ctx.getResources().getString(R.string.dataBaseName);

            //cargamos la configuracion
            cargarConfiguracion();

            //Cargamos los fragments y las paginas
            mCollectionPagerAdapter = new CollectionPagerAdapter(getFragmentManager());

            final ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mCollectionPagerAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                    valorMenu = position;
                    invalidateOptionsMenu();
                }

            });
            for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
                actionBar.addTab(actionBar.newTab()
                        .setText(mCollectionPagerAdapter.getPageTitle(i))
                        .setTabListener(this));
            }
            mViewPager.setCurrentItem(0);

            //cargamos automatismos
            //cargarAutomatismos();

            //cargamos el bluetooth
            try  {
                openBT();
            }
            catch (IOException ex) { }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(valorMenu == 1){
            getMenuInflater().inflate(R.menu.opciones, menu);
            flagAdelante = 0;
        }else{
            getMenuInflater().inflate(R.menu.principal, menu);
            flagAdelante = 1;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       int id = item.getItemId();
       if (id == R.id.action_about) {
         new Funciones(ctx,"",false,"").AcercaDe();
        }else if(id == R.id.action_save){
           View fragmentV = fragment.getView();
           new Funciones(ctx,"",false, ActivityName).OpcionesBD(fragmentV, "guardarOpcionesBD", ctx, dataBaseName);
       }else if(id == R.id.action_reload){
          new Funciones(ctx,rutaParseo,false, ActivityName);
          mCollectionPagerAdapter.getItem(0);
          mCollectionPagerAdapter.notifyDataSetChanged();
         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try  {  closeBT();   } catch (IOException ex) { }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        flagVistaCreada = 1;
    }

    //////////////////////////////////////////////////////////////////////
    /////////////// EVENTOS FRAGMENTS NO TOCAR   ////////////////////////
    ////////////////////////////////////////////////////////////////////

    ///////////////////////INICIO EVENTOS TABS/////////////////////////////////
    public void onTabUnselected(ActionBar.Tab tab,  FragmentTransaction fragmentTransaction) {  }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabReselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {  }
    /////////////////////////FIN EVENTOS TABS//////////////////////////////////

    /////////////////////////INICIO EVENTOS DE FRAGMENTS////////////////////////////////7
    public class CollectionPagerAdapter extends FragmentStatePagerAdapter {

        public CollectionPagerAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            //Devuelve el fragment nuevo que se crea
            if(i == 0){
                fragment = new PaginasTabs.TabFragment0(ctx, dataBaseName, ActivityName, rutaParseo, temperatura, xAxis, yAxis, zAxis, idThread, btSocket);
            }
            if(i == 1){
                fragment = new PaginasTabs.TabFragment1(ctx, dataBaseName, ActivityName);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
                  return POSITION_NONE;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            String tabLabel = null;
            switch (position) {
                case 0:
                    tabLabel = servidor1;
                    break;
                case 1:
                    tabLabel = getString(R.string.title_section2);
                    break;

            }
            return tabLabel;
        }
    }
    ///////////FIN EVENTOS FRAGMENTS///////////////////////////////////////

    //////////////////////////////////////////////////////////////////////
    /////////////// CARGA DE LOS PARAMETROS INICIALES ///////////////////
    ////////////////////////////////////////////////////////////////////

    public void cargarConfiguracion(){

        //Leemos las opciones de BD para obtener la mac guardada
        DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor fila = db.rawQuery("SELECT * FROM opciones ORDER BY id_opcion DESC LIMIT 1", null);
        //Nos aseguramos de que existe al menos un registro
        if (fila.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
            do {
                // Metemos en la string la IP o servidor al que conectarnos IP:PUERTO-PARAMETRO
                address = fila.getString(7);
                nombreOperario = fila.getString(9);
                apellidosOperario = fila.getString(10);
                dniOperario = fila.getString(11);
            } while (fila.moveToNext());
        }
        fila.close();

        //Close the Database and the Helper
        db.close();
        dbHelper.close();

        //Convertimos los datos estaticos a UTF-8 PAra luego enviarlos
        try {
            phoneNumber = URLEncoder.encode(phoneNumber, "utf-8");
            nombreOperario = URLEncoder.encode(nombreOperario, "utf-8");
            apellidosOperario = URLEncoder.encode(apellidosOperario, "utf-8");
            dniOperario = URLEncoder.encode(dniOperario, "utf-8");
        }catch(Exception e){  }

        //Prenveción de pausa de la app al bloquear la pantalla
        PowerManager mgr = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"TrackingNeveraLock");
        wakeLock.acquire();
        //do nothing
        wakeLock.release();

        //Adquisición del numero de telefono
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        phoneNumber = tm.getLine1Number();

        //Agregación de un Listener para el GPS y que nos de las coordenadas
        LocationManager milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener milocListener = new MiLocationListener();
        milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);

    }

    public void obtenerNotas(){

        //Leemos las opciones de BD para obtener la mac guardada
        DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor fila = db.rawQuery("SELECT * FROM opciones ORDER BY id_opcion DESC LIMIT 1", null);
        //Nos aseguramos de que existe al menos un registro
        if (fila.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
            do {
                // Metemos en la string las notas cmo enfermedades
                notas = fila.getString(12);
            } while (fila.moveToNext());
        }
        fila.close();

        //Close the Database and the Helper
        db.close();
        dbHelper.close();

    }

    public void guardarDatos(){
        DBHelper dbHelper = new DBHelper(ctx, dataBaseName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cur = db.rawQuery("SELECT * FROM datos ORDER BY id_dato DESC", null);
            if (cur.moveToFirst() == false) {
                //Insertamos las opciones por defecto
                ContentValues valuesDatos = new ContentValues();
                valuesDatos.put("temperatura", temperatura);
                valuesDatos.put("xaxis", xAxis);
                valuesDatos.put("yaxis", yAxis);
                valuesDatos.put("zaxis", zAxis);
                db.insert(DBHelper.TABLADatos, null, valuesDatos);
                //  }
            } else {
                db.execSQL("UPDATE datos SET temperatura='"+temperatura+"', xaxis='"+xAxis+"'"
                        + ", yaxis='"+yAxis+"', zaxis='"+zAxis+"' WHERE id_dato='1'");
            }

        db.close();
        dbHelper.close();
    }
    //////////////////////////////////////////////////////////////////////
    /////////////// CARGA DE LOS AUTOMATISMOS ///////////////////////////
    ////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////
    /////////////// CARGA DE FUNCIONES GPS LISTENER /////////////////////
    ////////////////////////////////////////////////////////////////////
    public class MiLocationListener implements LocationListener
    {
        public void onLocationChanged(Location loc)
        {
            loc.getLatitude();
            loc.getLongitude();
            //String coordenadas = "Mis coordenadas son: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
            //Toast.makeText( getApplicationContext(),coordenadas,Toast.LENGTH_LONG).show();
        }
        public void onProviderDisabled(String provider)
        {
            //Toast.makeText( getApplicationContext(),"Gps Desactivado", Toast.LENGTH_SHORT ).show();
        }
        public void onProviderEnabled(String provider)
        {
            //Toast.makeText( getApplicationContext(),"Gps Activo",Toast.LENGTH_SHORT ).show();
        }
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }


    //////////////////////////////////////////////////////////////////////
    /////////////// CARGA DE FUNCIONES BLUETOOTH ////////////////////////
    ////////////////////////////////////////////////////////////////////

    //PARTE DEL BLUETOOTH


    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
           // myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("MattsBlueTooth"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        //myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mmDevice = btAdapter.getRemoteDevice(address);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

       // myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {

                                            try {
                                                mensajeRecv_temp = data.split("\\s+");
                                                temperatura = mensajeRecv_temp[1];
                                                xAxis = mensajeRecv_temp[3];
                                                yAxis = mensajeRecv_temp[5];
                                                zAxis = mensajeRecv_temp[7];
                                                //Toast.makeText( getApplicationContext(),"-: "+sbprint+"",Toast.LENGTH_SHORT ).show();
                                                guardarDatos();
                                            }catch(Exception e){
                                                Log.e(TAG, "Fallo en la transmision entre Bluetooth y APP..String: "+ data );
                                                temperatura = "desconocida";
                                                xAxis = "desconocida";
                                                yAxis = "desconocida";
                                                zAxis = "desconocida";
                                            }

                                                //Obtenemos una referencia al LocationManager
                                                locManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
                                                //Obtenemos la última posición conocida
                                                Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                                //Si el GPS no está habilitado
                                                if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                                    //no enviamos nada
                                                    rutaParseoFull = rutaParseo + "?zona=registrarEvento&tracking_number=" + tracking_number + "&notas=unknown_position" + "||" + phoneNumber;
                                                }else{
                                                    try {
                                                        obtenerNotas();
                                                        String latitud = String.valueOf(loc.getLatitude());
                                                        String longitud = String.valueOf(loc.getLongitude());
                                                        latitud = URLEncoder.encode(latitud, "utf-8");
                                                        longitud = URLEncoder.encode(longitud, "utf-8");
                                                        temperatura = URLEncoder.encode(temperatura, "utf-8");
                                                        xAxis = URLEncoder.encode(xAxis, "utf-8");
                                                        yAxis = URLEncoder.encode(yAxis, "utf-8");
                                                        zAxis = URLEncoder.encode(zAxis, "utf-8");
                                                        notas = URLEncoder.encode(notas, "utf-8");

                                                        String datosExtras = "&tracking_number=" + tracking_number + "&latitud=" + latitud + "&longitud=" + longitud + "&telefono=" + phoneNumber +
                                                                "&nombreOperario=" + nombreOperario + "&apellidosOperario=" + apellidosOperario + "&dniOperario=" + dniOperario + "&temperatura=" + temperatura +
                                                                "&xAxis=" + xAxis + "&yAxis=" + yAxis + "&zAxis=" + zAxis + "&notas=" + notas;

                                                        rutaParseoFull = rutaParseo + "?zona=registrarEvento" + datosExtras;
                                                    }catch (Exception e){
                                                        Log.d(TAG, "Fallo en la creación de la rutaParseo", e);
                                                    }
                                                }

                                            if((flagEnviarAlive == 1) && (flagVistaCreada == 1)) {
                                                try {
                                                    conexionViva = new sendAlivePacket();
                                                    conexionViva.execute(1);
                                                } catch (Exception e) {
                                                    Log.d(TAG, "Fallo en el sendData", e);
                                                }
                                            }


                                            //Toast.makeText(ctx,data,Toast.LENGTH_SHORT).show();
                                            //myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    try{
                                        readBuffer[readBufferPosition++] = b;
                                    }catch(Exception oa){
                                        Log.d(TAG, "Error al leer el buffer", oa);
                                    }
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        String msg = "a";
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
       // myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException
    {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        }catch (IOException mon){
            Log.d(TAG, "no se ha cerrado bien la conexion Onclose...",mon);
        }
     //   myLabel.setText("Bluetooth Closed");
    }

    class sendAlivePacket extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... n) {
            try {
                flagEnviarAlive = 0;
                sendData();
            }catch(Exception e){
                Log.d(TAG, "...Petada en el sendAlive..."+e.toString());
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer res) {
            flagEnviarAlive = 1;
            new Funciones(ctx, rutaParseoFull, false, ActivityName);
            conexionViva = null;
            Log.d(TAG, "...Paquete ALIVE enviado correctamente :D");
        }

    }

    //FIN DEL TEMA
}
