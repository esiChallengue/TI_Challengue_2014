package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by GlobalMarduk on 24/01/14.
 */

public class PaginasTabs{
    private static final String TAG = "bluetooth"; //cabecera del debugeo bluetooth



    public static class TabFragment0 extends Fragment {
        public static final String ARG_OBJECT = "object";

        public Context ctx;
        public String ActivityName;
        public String dataBaseName;
        public String rutaParseo;
        public String temperatura;
        public Thread idThread2;
        public BluetoothSocket btSocket2;
        public String xAxis = "";
        public String yAxis = "";
        public String zAxis = "";
        public Handler handler = new Handler();
        public View viewFinal;
        public refreshFrame  refrescor = null;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_fragment_servidor, container, false);
            viewFinal = rootView;
            //rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            //Listener del Boton de INICIar sesion
            final Button button= (Button)rootView.findViewById(R.id.buttonNuevaSesion);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 try {
                     idThread2.interrupt();
                     //new PrincipalActivity().cerrarConexion(btSocket2);
                     //new PrincipalActivity().checkBTState();
                     button.setText("ConexionReiniciada");
                 }catch(Exception e){ Log.e("petada", "...Oopps ha petado", e); }
                 }
            });

            handler.postDelayed(runnable, 3000);
            return rootView;
        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new Funciones(ctx,"",false, ActivityName).actualizarLayer(viewFinal, "refresh", ctx, temperatura, xAxis, yAxis, zAxis);
                handler.postDelayed(this, 3000);
            }
        };

        public TabFragment0(Context ctxllegado, String dataBaseNamellegado, String ActivityNamellegado, String rutaParseollegado,
                            String temperaturaRecv, String xAxisllegado,  String yAxisllegado, String zAxisllegado,
                            Thread idThread, BluetoothSocket btSocket)
        {
            rutaParseo = rutaParseollegado;
            ctx = ctxllegado;
            dataBaseName = dataBaseNamellegado;
            ActivityName = ActivityNamellegado;
            temperatura = temperaturaRecv;
            idThread2 = idThread;
            btSocket2 = btSocket;
            xAxis = xAxisllegado;
            yAxis = yAxisllegado;
            zAxis = zAxisllegado;
        }

        class refreshFrame extends AsyncTask<Integer, Void, Integer> {

            @Override
            protected Integer doInBackground(Integer... n) {
                try {
                    new Funciones(ctx,"",false, ActivityName).actualizarLayer(viewFinal, "refresh", ctx, temperatura, xAxis, yAxis, zAxis);
                }catch(Exception e){
                    Log.d(TAG, "...No refresco Frame Background..."+e.toString());
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer res) {
                refrescor = null;
                Log.d(TAG, "...Frame refrescando correctamente :D");
            }

        }


    }


    public static class TabFragment1 extends Fragment {
        public static final String ARG_OBJECT = "object";

        public Context ctx;
        public String ActivityName;
        public String dataBaseName;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.layout_fragment_opciones, container, false);
            rootView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            new Funciones(ctx,"",false, ActivityName).OpcionesBD(rootView, "leerOpcionesBD", ctx, dataBaseName);

            return rootView;

        }

        public TabFragment1(Context ctxllegado, String dataBaseNamellegado, String ActivityNamellegado){
            ctx = ctxllegado;
            dataBaseName = dataBaseNamellegado;
            ActivityName = ActivityNamellegado;

        }

      }

}
