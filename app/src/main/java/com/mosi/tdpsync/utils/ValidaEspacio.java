package com.mosi.tdpsync.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

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
import java.text.DecimalFormat;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

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
    public int descarga;
    static final DecimalFormat df = new DecimalFormat("0.0");

    public float verificador(final Activity activity){

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
                    utf8usuarios = usuarios.getBytes();
                    utf8companias = companias.getBytes();
                    utf8clientes = clientes.getBytes();
                    utf8tipos = tipos.getBytes();
                    utf8invptdtab = invptdtab.getBytes();
                    utf8invptmtab = invptmtab.getBytes();
                    utf8pr1tab = pr1tab.getBytes();
                    utf8talonarios = talonarios.getBytes();
                    utf8pedidos = pedidos.getBytes();

                    descarga = usuarios.getBytes().length+companias.getBytes().length+clientes.getBytes().length+tipos.getBytes().length+
                            invptdtab.getBytes().length+invptmtab.getBytes().length+pr1tab.getBytes().length+talonarios.getBytes().length+pedidos.getBytes().length;

                    System.out.println( "espacio "+utf8usuarios.length +" "+utf8companias.length+" "+ utf8clientes.length+" "+utf8tipos.length+" "+utf8invptdtab.length+" "+utf8invptmtab.length
                            +" "+utf8pr1tab.length+" "+utf8talonarios.length);

                    Adescargar = utf8usuarios.length+utf8companias.length+utf8clientes.length+utf8tipos.length+
                            utf8invptdtab.length+utf8invptmtab.length+utf8pr1tab.length+utf8talonarios.length;

                    System.out.println("variable descarga " +descarga);
                    descarga =  (descarga/(1024));
                    System.out.println("variable descarga convertido a MB " +descarga);

                    long MEGABYTE = 1024L*1024L;
                    Adescargar = Adescargar/MEGABYTE;

                    System.out.println("Adescargar despues de conversion  "+Adescargar+ " MB" );

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float disponible = getMegabytesAvailable();
                            System.out.println(" disponible "+disponible+" Adescargar "+Adescargar);
                            System.out.println("Adescargar despues de conversion  "+Adescargar+" 2");
                            System.out.println("descarga despues de conversion  "+descarga+" 2");
                            if (disponible<descarga){
                             //  Toast existe = Toast.makeText(activity, "No cuentas con espacio para una sincronizacion", Toast.LENGTH_LONG);
                               // existe.show();
                                System.out.println("No hay espacio disponible para una sincronizacion tu dispones de "+disponible +"MB y la descarga es de "+descarga + "MB N");
                            }else{
                                //Toast existe = Toast.makeText(activity, "Cuentas con espacio disponible", Toast.LENGTH_LONG);
                               // existe.show();
                                System.out.println("Cuentas con espacio disponible para una sincronizacion tu dispones de "+disponible +"MB  y la descarga es de "+descarga + "MB P");
                            }
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error " + e.toString());
                    e.printStackTrace();
                }
            }
        });
        hilo.start();//
        System.out.println("Adescargar final "+Adescargar);
        return Adescargar;
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



    public static long bytesToMeg(long bytes) {
        if (bytes < (1024L * 1024L * 1024L)) {
            System.out.println("valor "+ df.format((double) bytes / (double) (1024L * 1024L)) + "mb");
        }
        long  MEGABYTE = 1024L * 1024L * 1024L;
        System.out.println("Adescargar dentro de bytetoMeg "+bytes / MEGABYTE );
        return bytes / MEGABYTE ;
    }

    public void cargaEspacio(){
        System.out.println("Se esta cargando el espacio disponible en el dispositivo");
        System.out.println("Finzalizando la carga del espacio disponible en el dispositivo");
    }

}



