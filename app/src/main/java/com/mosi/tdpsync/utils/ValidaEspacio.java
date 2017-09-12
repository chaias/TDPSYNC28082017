package com.mosi.tdpsync.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Isaias on 11/09/2017.
 */

public class ValidaEspacio {
    JSONObject jsonObject;

    static Constantes cons = new Constantes();

    static String usuarios   = cons.GET_URL_USR;
    static String companias  = cons.GET_URL_CIA;
    static String clientes   = cons.GET_URL_CUS;
    static String tipos      = cons.GET_URL_TIP;
    static String invptdtab  = cons.GET_URL_INVPTD;
    static String invptmtab  = cons.GET_URL_INVPTM;
    static String pr1tab     = cons.GET_URL_PR1;
    static String talonarios = cons.GET_URL_TAL;
    static String pedidos    = cons.GET_URL_PED;

    static byte[] utf8usuarios,utf8companias,utf8clientes,utf8tipos,utf8invptdtab,utf8invptmtab,utf8pr1tab,utf8talonarios,utf8pedidos;
    public float Adescargar;



    public void verificador(final Activity activity){

        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {

                 usuarios= httpGetData(usuarios);
                 companias=httpGetData(companias);
                 clientes=httpGetData(clientes);
                 tipos=httpGetData(tipos);
                 invptdtab=httpGetData(invptdtab);
                 invptmtab=httpGetData(invptmtab);
                 pr1tab=httpGetData(pr1tab);
                 talonarios=httpGetData(talonarios);
                 pedidos=httpGetData(pedidos);
                try {

                    utf8usuarios = usuarios.getBytes("UTF-8");
                    utf8companias = companias.getBytes("UTF-8");
                    utf8clientes = clientes.getBytes("UTF-8");
                    utf8tipos = tipos.getBytes("UTF-8");
                    utf8invptdtab = invptdtab.getBytes("UTF-8");
                    utf8invptmtab = invptmtab.getBytes("UTF-8");
                    utf8pr1tab = pr1tab.getBytes("UTF-8");
                    utf8talonarios = talonarios.getBytes("UTF-8");
                    utf8pedidos = pedidos.getBytes("UTF-8");

                    System.out.println( "espacio "+utf8usuarios.length +" "+utf8companias.length+" "+ utf8clientes.length+" "+utf8tipos.length+" "+utf8invptdtab.length+" "+utf8invptmtab.length
                            +" "+utf8pr1tab.length+" "+utf8talonarios.length);

                     Adescargar = utf8usuarios.length+utf8companias.length+utf8clientes.length+utf8tipos.length+
                            utf8invptdtab.length+utf8invptmtab.length+utf8pr1tab.length+utf8talonarios.length;


                    Adescargar = Adescargar/1024.f;
                    System.out.println("Adescargar "+Adescargar);

                    float disponible = getMegabytesAvailable();
                    if (disponible<Adescargar){
                        Toast existe = Toast.makeText(activity, "No cuentas con espacio para una sincronizacion", Toast.LENGTH_LONG);
                        existe.show();

                    }else{
                        Toast existe = Toast.makeText(activity, "Cuentas con espacio disponible", Toast.LENGTH_LONG);
                        existe.show();

                    }

                } catch (Exception e) {
                    System.out.println("Error " + e.toString());

                    e.printStackTrace();

                }
            }
        });
        hilo.start();//

    }


    public String httpGetData(String mURL){

        mURL= mURL.replace(" ", "%20");

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(mURL);
        HttpContext contexto = new BasicHttpContext();
        String resultado = null;

        try {
            HttpResponse response = httpclient.execute(httpget,contexto);
            HttpEntity entity = response.getEntity();
            resultado = EntityUtils.toString(entity, "UTF-8");

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return resultado;
    }

    public  float getMegabytesAvailable()
    {
        long bytesAvailable;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (Build.VERSION.SDK_INT < 18)
        {
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }
        else{
            bytesAvailable = (long) stat.getBlockSizeLong() * (long) stat.getAvailableBlocksLong();
        }

        return bytesAvailable / (1024.f * 1024.f);
    }

}



