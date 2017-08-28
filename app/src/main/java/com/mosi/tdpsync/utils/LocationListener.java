package com.mosi.tdpsync.utils;

import android.location.Location;
import android.os.Bundle;

import com.mosi.tdpsync.ui.Toma_De_Pedido;

/**
 * Created by Isaias on 25/11/2016.
 */
public class LocationListener implements android.location.LocationListener {

    Toma_De_Pedido toma_de_pedido;

    public Toma_De_Pedido getToma_de_pedido(){
        return toma_de_pedido;
    }

    public void setToma_de_pedido(Toma_De_Pedido toma_de_pedido){
        this.toma_de_pedido = toma_de_pedido;
    }

    @Override
    public void onLocationChanged(Location location) {

        location.getLatitude();
        location.getLongitude();


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
