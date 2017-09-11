package com.mosi.tdpsync.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Isaias on 11/09/2017.
 */

public class ValidaEspacio {
    JSONObject jsonObject;

    Constantes cons = new Constantes();

    String usuarios   = cons.GET_URL_USR;
    String companias  = cons.GET_URL_CIA;
    String clientes   = cons.GET_URL_CUS;
    String tipos      = cons.GET_URL_TIP;
    String invptdtab  = cons.GET_URL_INVPTD;
    String invptmtab  = cons.GET_URL_INVPTM;
    String pr1tab     = cons.GET_URL_PR1;
    String talonarios = cons.GET_URL_TAL;
    String pedidos    = cons.GET_URL_PED;



    public void verificador(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                String data=httpGetData(usuarios);
                try {
                    System.out.println(usuarios);
                    System.out.println(data  + " data");
                    JSONArray ja = new JSONArray(data);
                    for (int i = 0; i < ja.length(); i++) {
                        jsonObject = ja.getJSONObject(i);

                    }
                      //jsonObject.toString().getBytes(UTF-8).length;

                } catch (JSONException e) {
                    System.out.println("Error " + e.toString());

                    e.printStackTrace();
                }


            }}).start();//

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


}



