package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.view.View;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.net.URLEncoder;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
    Handler h;

    final int RECIEVE_MESSAGE = 1;		// Status  for Handler
    private static final String TAG = "bluetooth"; //cabecera del debugeo bluetooth
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "00:14:03:03:02:30";
    public String temperatura = "";
    public String[] temperatura_temp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_principal);
        ctx = this;
        dataBaseName = ctx.getResources().getString(R.string.dataBaseName);
        extras = getIntent().getExtras();
        rutaParseo = extras.getString("rutaParseo");
        servidor1 = extras.getString("servidor1");
        tracking_number = extras.getString("tracking");



        //Agregación de un Listener para el GPS y que nos de las coordenadas
        LocationManager milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener milocListener = new MiLocationListener();
        milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);


        //TO LO SIGUIENTE
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


        //Refresco automatico cada 10 segundos
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                if(flagAdelante == 1) {

                                    //Obtenemos una referencia al LocationManager
                                    locManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
                                    //Obtenemos la última posición conocida
                                    Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    //Si el GPS no está habilitado
                                    if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    //no enviamos nada
                                    rutaParseoFull = rutaParseo + "?cmd=registrarEvento&tracking_number=" + tracking_number + "&notas=unknown_position";
                                    }else{
                                      try {
                                       String latitud = String.valueOf(loc.getLatitude());
                                       String longitud = String.valueOf(loc.getLongitude());
                                       String coordenadas = "" + latitud + "||" + longitud + "||" + temperatura;
                                       String coordenadas_utf8 = URLEncoder.encode(coordenadas, "utf-8");
                                       rutaParseoFull = rutaParseo + "?cmd=registrarEvento&tracking_number=" + tracking_number + "&notas=" + coordenadas_utf8;
                                      }catch (Exception e){  }
                                    }

                                    //Mandamos al CHIP el comando a para decir que la conexion sigue sana
                                    mConnectedThread.write("a");

                                    //Invalidamso el fragment y lo volvemos a cargar para actualizar los
                                    //datos de la pantalla
                                    new Funciones(ctx, rutaParseoFull, false, ActivityName);
                                    mCollectionPagerAdapter.getItem(0);
                                    mCollectionPagerAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();


        //Automastimo BLUETOOTH
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:													// if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                        sb.append(strIncom);												// append string
                        int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
                        if (endOfLineIndex > 0) { 											// if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);				// extract string
                            sb.delete(0, sb.length());										// and clear
                            temperatura =  sbprint; 	        // update TextView
                            //Limpiamos la  cadena llegada del modulo Temp: loquesea
                            temperatura_temp = temperatura.split("\\s+");
                            temperatura = temperatura_temp[1];
                            //Toast.makeText( getApplicationContext(),"-: "+sbprint+" - "+temperatura,Toast.LENGTH_SHORT ).show();
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
        checkBTState();
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


    ////////TOOOOO LO NUEVO/////////////////////////

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
                fragment = new PaginasTabs.TabFragment0(ctx, dataBaseName, ActivityName, rutaParseo);
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


    //LISTENR DEL GPS PARA IR PIDIENDO POSICION DLE GPS
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

    //PARTE DEL BLUETOOTH
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

    /*try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }*/

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

}
