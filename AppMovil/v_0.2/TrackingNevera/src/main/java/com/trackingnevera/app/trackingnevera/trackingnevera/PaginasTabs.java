package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

/**
 * Created by GlobalMarduk on 24/01/14.
 */
public class PaginasTabs{


    public static class TabFragment0 extends Fragment {
        public static final String ARG_OBJECT = "object";

        public Context ctx;
        public String ActivityName;
        public String dataBaseName;
        public String rutaParseo;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.layout_fragment_servidor, container, false);
            //rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            return rootView;
        }


        public TabFragment0(Context ctxllegado, String dataBaseNamellegado, String ActivityNamellegado, String rutaParseollegado){
            rutaParseo = rutaParseollegado;
            ctx = ctxllegado;
            dataBaseName = dataBaseNamellegado;
            ActivityName = ActivityNamellegado;
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
