package com.trackingnevera.app.trackingnevera.trackingnevera;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

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
    public String tracking_number = "";
    public Bundle extras ;
    public int flagAdelante = 0;
    Handler mHandler = new Handler();

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
                                    rutaParseo = rutaParseo + "?cmd=registrarEvento&tracking_number=" + tracking_number + "&notas=loquesea";
                                    new Funciones(ctx, rutaParseo, false, ActivityName);
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

}
