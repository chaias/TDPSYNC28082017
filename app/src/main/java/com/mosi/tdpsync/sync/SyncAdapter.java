package com.mosi.tdpsync.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mosi.tdpsync.utils.Utilidades;
import com.mosi.tdpsync.utils.Constantes;
import com.mosi.tdpsync.R;
import com.mosi.tdpsync.web.Ciatab;
import com.mosi.tdpsync.web.Custab;
import com.mosi.tdpsync.web.Gasto;
import com.mosi.tdpsync.web.Invptdtab;
import com.mosi.tdpsync.web.Invptmtab;
import com.mosi.tdpsync.web.Pedidos;
import com.mosi.tdpsync.web.Pr1tab;
import com.mosi.tdpsync.web.Talonarios;
import com.mosi.tdpsync.web.Tiptab;
import com.mosi.tdpsync.web.Usuario;
import com.mosi.tdpsync.web.VolleySingleton;
import com.mosi.tdpsync.sqlite.ContratoPedidos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Maneja la transferencia de datos entre el servidor y el cliente
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    ContentResolver resolver;
    private Gson gson = new Gson();
    private Gson gson2 = new Gson();

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
            ContratoPedidos.Columnas._ID,
            ContratoPedidos.Columnas.ID_REMOTA,
            ContratoPedidos.Columnas.MONTO,
            ContratoPedidos.Columnas.ETIQUETA,
            ContratoPedidos.Columnas.FECHA,
            ContratoPedidos.Columnas.DESCRIPCION
    };

    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;
    public static final int COLUMNA_MONTO = 2;
    public static final int COLUMNA_ETIQUETA = 3;
    public static final int COLUMNA_FECHA = 4;
    public static final int COLUMNA_DESCRIPCION = 5;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    public static void inicializarSyncAdapter(Context context) {
        obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {

        Log.i(TAG, "onPerformSync() Toma De Pedido...");

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida) {
            realizarSincronizacionLocal(syncResult);
            realizarSincronizacionLocalInvptmtab(syncResult);
            realizarSincronizacionLocalPr1tab(syncResult);
            realizarSincronizacionLocalUsuarios(syncResult);
            realizarSincronizacionLocalCiatab(syncResult);
            realizarSincronizacionLocalInvptdtab(syncResult);
            realizarSincronizacionLocalCustab(syncResult);
           // realizarSincronizacionLocalTalonarios(syncResult);
           // realizarSincronizacionLocalPedidos(syncResult);
            realizarSincronizacionLocalTiptab(syncResult);
        } else {
            realizarSincronizacionRemota();
            /*realizarSincronizacionRemotaInvptmtab();
            realizarSincronizacionRemotaPr1tab();
            realizarSincronizacionRemotaUsuarios();
            realizarSincronizacionRemotaCiatab();
            realizarSincronizacionRemotaInvptdtab();
            realizarSincronizacionRemotaCustab();
            realizarSincronizacionRemotaTalonarios();*/
            realizarSincronizacionRemotaPedidos();
            //realizarSincronizacionRemotaTiptab();
        }
    }

    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG, "Actualizando el cliente." + syncResult);

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGet(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    Log.d(TAG, "Error de red");
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los gastos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGet(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);

            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocales(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemota() {
        Log.i(TAG, "Actualizando el servidor...");

        iniciarActualizacion();

        Cursor c = obtenerRegistrosSucios();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL,
                                Utilidades.deCursorAJSONObject(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsert(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
        c.close();
    }

    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     *
     * @return Cursor con el registro.
     */
    private Cursor obtenerRegistrosSucios() {
        Uri uri = ContratoPedidos.CONTENT_URI_GASTO;
        String selection = ContratoPedidos.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoPedidos.ESTADO_SYNC + ""};

        return resolver.query(uri, PROJECTION, selection, selectionArgs, null);
    }

    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacion() {
        Uri uri = ContratoPedidos.CONTENT_URI_GASTO;
        String selection = ContratoPedidos.Columnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.Columnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", ContratoPedidos.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.Columnas.ESTADO, ContratoPedidos.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción:" + results);
    }

    /**
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor
     *
     * @param //idRemota id remota
     */
    private void finalizarActualizacion(String idRemota, int idLocal) {
        Uri uri = ContratoPedidos.CONTENT_URI_GASTO;
        String selection = ContratoPedidos.Columnas._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.Columnas.PENDIENTE_INSERCION, "0");
        v.put(ContratoPedidos.Columnas.ESTADO, ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.Columnas.ID_REMOTA, idRemota);

        resolver.update(uri, v, selection, selectionArgs);
    }

    /**
     * Procesa los diferentes tipos de respuesta obtenidos del servidor
     *
     * @param response Respuesta en formato Json
     */
    public void procesarRespuestaInsert(JSONObject response, int idLocal) {

        try {
            // Obtener estado
            String estado = response.getString(Constantes.ESTADO);
            // Obtener mensaje
            String mensaje = response.getString(Constantes.MENSAJE);
            // Obtener identificador del nuevo registro creado en el servidor
            String idRemota = response.getString(Constantes.ID_GASTO);

            switch (estado) {
                case Constantes.SUCCESS:
                    Log.i(TAG, mensaje);
                    finalizarActualizacion(idRemota, idLocal);
                    break;

                case Constantes.FAILED:
                    Log.i(TAG, mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocales(JSONObject response, SyncResult syncResult) {

        JSONArray gastos = null;

        try {
            // Obtener array "gastos"
            gastos = response.getJSONArray(Constantes.GASTOS);
            System.out.println("JSN GASTOS " +gastos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Gasto[] res = gson2.fromJson(gastos != null ? gastos.toString() : null, Gasto[].class);
        List<Gasto> data = Arrays.asList(res);
        System.out.println("data "+ data);
        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir las entradas entrantes
        HashMap<String, Gasto> expenseMap = new HashMap<String, Gasto>();
        System.out.println("ExpenseMap antes de for "+ expenseMap);
        System.out.println("data data "+ data);


        for (Gasto e : data) {
            expenseMap.put(e.idGasto, e);
            System.out.println("expenseMap.put(e.idGasto, e); "+ expenseMap.put(e.idGasto, e));

        }

        // Consultar registros remotos actuales
        Uri uri = ContratoPedidos.CONTENT_URI_GASTO;
        String select = ContratoPedidos.Columnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, PROJECTION, select, null, null);
        System.out.println("DATOS LOCALES "+ c);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String id;
        int monto;
        String etiqueta;
        String fecha;
        String descripcion;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_ID_REMOTA);
            monto = c.getInt(COLUMNA_MONTO);
            etiqueta = c.getString(COLUMNA_ETIQUETA);
            fecha = c.getString(COLUMNA_FECHA);
            descripcion = c.getString(COLUMNA_DESCRIPCION);
            System.out.println("id "+ id + " monto "+monto+" descripcion "+descripcion);

            Gasto match = expenseMap.get(id);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_GASTO.buildUpon()
                        .appendPath(id).build();

                // Comprobar si el gasto necesita ser actualizado
                boolean b = match.monto != monto;
                boolean b1 = match.etiqueta != null && !match.etiqueta.equals(etiqueta);
                boolean b2 = match.fecha != null && !match.fecha.equals(fecha);
                boolean b3 = match.descripcion != null && !match.descripcion.equals(descripcion);

                if (b || b1 || b2 || b3) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.Columnas.MONTO, match.monto)
                            .withValue(ContratoPedidos.Columnas.ETIQUETA, match.etiqueta)
                            .withValue(ContratoPedidos.Columnas.FECHA, match.fecha)
                            .withValue(ContratoPedidos.Columnas.DESCRIPCION, match.descripcion)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContratoPedidos.CONTENT_URI_GASTO.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (Gasto e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de: " + e.idGasto);
            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_GASTO)
                    .withValue(ContratoPedidos.Columnas.ID_REMOTA, e.idGasto)
                    .withValue(ContratoPedidos.Columnas.MONTO, e.monto)
                    .withValue(ContratoPedidos.Columnas.ETIQUETA, e.etiqueta)
                    .withValue(ContratoPedidos.Columnas.FECHA, e.fecha)
                    .withValue(ContratoPedidos.Columnas.DESCRIPCION, e.descripcion)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContratoPedidos.CONTENT_URI_GASTO,
                    null,
                    false);
            Log.i(TAG, "Sincronización finalizada.");

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }

    }

    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     * @param onlyUpload Usa true para sincronizar el servidor o false para sincronizar el cliente
     */
    public static void sincronizarAhora(Context context, boolean onlyUpload) {
        Log.i(TAG, "Realizando petición de sincronización manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);

    }

    /**
     * Crea u obtiene una cuenta existente
     *
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    public static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Crear cuenta por defecto
        Account newAccount = new Account(
                context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);

        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount)) {

            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;
        }
        Log.i(TAG, "Cuenta de usuario obtenida.");
        return newAccount;
    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE USUARIOS\\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_USUARIOS = new String[]{
            ContratoPedidos.UsuarioColumnas._ID,
            ContratoPedidos.UsuarioColumnas.ID_REMOTA,
            ContratoPedidos.UsuarioColumnas.USUARIO,
            ContratoPedidos.UsuarioColumnas.PASSWORD
    };

    public static final int COLUMNA_USR_ID = 0;
    public static final int COLUMNA_USR_ID_REMOTA = 1;
    public static final int COLUMNA_USR_USUARIO = 2;
    public static final int COLUMNA_USR_PASSWORD = 3;

    private void realizarSincronizacionLocalUsuarios(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Usuarios");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_USR,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                procesarRespuestaGetUsuarios(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetUsuarios(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGetUsuarios");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO USUARIOS  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    System.out.println("RESPONSE USUARIO PROCESAR RESPUESTA GET"+ response);
                    System.out.println("SYNCRESULT USUARIO PROCESAR RESPUESTA GET"+ syncResult);
                    actualizarDatosLocalesUsuarios(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaUsuarios(){
        Log.i(TAG, "Actualizando el servidor... Usuarios...");

        iniciarActualizacionUsuarios();

        Cursor c = obtenerRegistrosSuciosUsuarios();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_USR_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_USR,
                                Utilidades.deCursorAJSONObjectUsuarios(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertUsuarios(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Usuarios: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                                @Override
                                        public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización usuarios");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosUsuarios(){
        Uri uri = ContratoPedidos.CONTENT_URI_USUARIOS;
        String selection = ContratoPedidos.UsuarioColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.UsuarioColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_USUARIOS,selection,selectionArgs,null);

    }

    private void iniciarActualizacionUsuarios(){
        Uri uri = ContratoPedidos.CONTENT_URI_USUARIOS;
        String selection = ContratoPedidos.UsuarioColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.UsuarioColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.UsuarioColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción usuarios:" + results);
    }

    private void finalizarActualizacionUsuarios(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_USUARIOS;
        String selection = ContratoPedidos.UsuarioColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.UsuarioColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.UsuarioColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.UsuarioColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertUsuarios(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_USUARIO);

            System.out.println("ESTADO USUARIOS insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES USUARIOS "+mensaje);
                    finalizarActualizacionUsuarios(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED USUARIOS "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesUsuarios(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocalesUsuarios");
        JSONArray usuarios = null;

        try {
            usuarios = response.getJSONArray(Constantes.USUARIOS);
            System.out.println("JSON USUARIOS... " + usuarios);
        }catch (Exception e){
            e.printStackTrace();
        }
        Usuario[] res = gson.fromJson(usuarios !=null ? usuarios.toString() : null, Usuario[].class);
        List<Usuario>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Usuario>expenseMap = new HashMap<String, Usuario>();
        for (Usuario e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_USUARIOS;
        String select = ContratoPedidos.Usuarios.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_USUARIOS,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Usuarios.");

        String id;
        String nombre;
        String password;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_USR_ID_REMOTA);
            nombre = c.getString(COLUMNA_USR_USUARIO);
            password = c.getString(COLUMNA_USR_PASSWORD);

            Usuario match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_USUARIOS.buildUpon().appendPath(id).build();

                boolean b0 = match.nombre !=null && !match.nombre.equals(nombre);
                boolean b1 = match.password != null && !match.password.equals(password);

                if (b0 || b1){
                    Log.i(TAG, "Programando actualización usuarios de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.UsuarioColumnas.USUARIO,match.nombre)
                            .withValue(ContratoPedidos.UsuarioColumnas.PASSWORD,match.password)
                    .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro usuarios: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_USUARIOS.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación usuarios de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("USUARIO expenseMap "+ expenseMap.values());
        for (Usuario usr : expenseMap.values()){
            System.out.println("USUARIO DE INSERT "+usr);
            Log.i(TAG, "Programando inserción usuarios de: " + usr.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_USUARIOS)
                    .withValue(ContratoPedidos.Usuarios.ID_REMOTA,usr.id)
                    .withValue(ContratoPedidos.Usuarios.NOMBRE,usr.nombre)
                    .withValue(ContratoPedidos.Usuarios.PASSWORD,usr.password)
            .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones usuarios...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_USUARIOS,null,false);
            Log.i(TAG, "Sincronización usuarios finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de usuarios");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CIATAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_CIATAB = new String[]{
            ContratoPedidos.CiatabColumnas._ID,
            ContratoPedidos.CiatabColumnas.ID_REMOTA,
            ContratoPedidos.CiatabColumnas.CIACOD,
            ContratoPedidos.CiatabColumnas.CIANOMBRE
    };

    public static final int COLUMNA_CIA_ID = 0;
    public static final int COLUMNA_CIA_ID_REMOTA = 1;
    public static final int COLUMNA_CIA_CIACOD = 2;
    public static final int COLUMNA_CIA_CIANOMBRE = 3;

    private void realizarSincronizacionLocalCiatab(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Ciatab");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_CIA,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetCiatab(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    Log.d(TAG, "Error de red");
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetCiatab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Ciatab");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO ciatab  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesCiatab(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaCiatab(){
        Log.i(TAG, "Actualizando el servidor... Ciatab...");

        iniciarActualizacionCiatab();

        Cursor c = obtenerRegistrosSuciosCiatab();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_CIA_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_CIA,
                                Utilidades.deCursorAJSONObjectCiatab(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertCiatab(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Usuarios: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización CIATAB");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosCiatab(){
        Uri uri = ContratoPedidos.CONTENT_URI_CIA;
        String selection = ContratoPedidos.CiatabColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.CiatabColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_CIATAB,selection,selectionArgs,null);

    }

    private void iniciarActualizacionCiatab(){
        Uri uri = ContratoPedidos.CONTENT_URI_CIA;
        String selection = ContratoPedidos.CiatabColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.CiatabColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.CiatabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción usuarios:" + results);
    }

    private void finalizarActualizacionCiatab(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_CIA;
        String selection = ContratoPedidos.CiatabColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.CiatabColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.CiatabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.CiatabColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertCiatab(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_CIATAB);

            System.out.println("ESTADO USUARIOS insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES CIATAB "+mensaje);
                    finalizarActualizacionCiatab(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED CIATAB "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesCiatab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocalesUsuarios");
        JSONArray ciatab = null;

        try {
            ciatab = response.getJSONArray(Constantes.CIATAB);
            System.out.println("JSON CIATAB... " + ciatab);
        }catch (Exception e){
            e.printStackTrace();
        }
        Ciatab[] res = gson.fromJson(ciatab !=null ? ciatab.toString() : null, Ciatab[].class);
        List<Ciatab>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Ciatab>expenseMap = new HashMap<String, Ciatab>();
        for (Ciatab e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_CIA;
        String select = ContratoPedidos.Ciatab.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_CIATAB,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Ciatab.");

        String id;
        String ciacod;
        String cianombre;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_CIA_ID_REMOTA);
            ciacod = c.getString(COLUMNA_CIA_CIACOD);
            cianombre = c.getString(COLUMNA_CIA_CIANOMBRE);

            Ciatab match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_CIA.buildUpon().appendPath(id).build();

                boolean b0 = match.ciacod !=null && !match.ciacod.equals(ciacod);
                boolean b1 = match.cianombre != null && !match.cianombre.equals(cianombre);

                if (b0 || b1){
                    Log.i(TAG, "Programando actualización ciatab de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.CiatabColumnas.CIACOD,match.ciacod)
                            .withValue(ContratoPedidos.CiatabColumnas.CIANOMBRE,match.cianombre)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Ciatab: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_CIA.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación ciatab de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("USUARIO expenseMap "+ expenseMap.values());
        for (Ciatab cia : expenseMap.values()){
            System.out.println("USUARIO DE INSERT "+cia);
            Log.i(TAG, "Programando inserción ciatab de: " + cia.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_CIA)
                    .withValue(ContratoPedidos.Ciatab.ID_REMOTA,cia.id)
                    .withValue(ContratoPedidos.Ciatab.CIACOD,cia.ciacod)
                    .withValue(ContratoPedidos.Ciatab.CIANOMBRE,cia.cianombre)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones usuarios...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_CIA,null,false);
            Log.i(TAG, "Sincronización ciatab finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de ciatab");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTDTAB\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_INVPTDTAB = new String[]{
            ContratoPedidos.InvptdtabColumnas._ID,
            ContratoPedidos.InvptdtabColumnas.ID_REMOTA,
            ContratoPedidos.InvptdtabColumnas.INVPTDCIA,
            ContratoPedidos.InvptdtabColumnas.INVPTDCOD,
            ContratoPedidos.InvptdtabColumnas.INVPTDDISPI

    };

    public static final int COLUMNA_INVPTD_ID = 0;
    public static final int COLUMNA_INVPTD_ID_REMOTA = 1;
    public static final int COLUMNA_INVPTD_INVPTDCIA = 2;
    public static final int COLUMNA_INVPTD_INVPTDCOD = 3;
    public static final int COLUMNA_INVPTD_INVPTDDISPI = 4;

    private void realizarSincronizacionLocalInvptdtab(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Invptdtab");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_INVPTD,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetInvptdtab(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    Log.d(TAG, "Error de red");
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetInvptdtab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Invptdtab");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Invptdtab  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesInvptdtab(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaInvptdtab(){
        Log.i(TAG, "Actualizando el servidor... Invptdtab...");

        iniciarActualizacionInvptdtab();

        Cursor c = obtenerRegistrosSuciosInvptdtab();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_INVPTD_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_INVPTD,
                                Utilidades.deCursorAJSONObjectInvptdtab(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertInvptdtab(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Usuarios: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización CIATAB");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosInvptdtab(){
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTD;
        String selection = ContratoPedidos.InvptdtabColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.InvptdtabColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_INVPTDTAB,selection,selectionArgs,null);

    }

    private void iniciarActualizacionInvptdtab(){
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTD;
        String selection = ContratoPedidos.InvptdtabColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.InvptdtabColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.InvptdtabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Invptdtab:" + results);
    }

    private void finalizarActualizacionInvptdtab(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTD;
        String selection = ContratoPedidos.InvptdtabColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.InvptdtabColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.InvptdtabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.InvptdtabColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertInvptdtab(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_INVPTDTAB);

            System.out.println("ESTADO Invptdtab insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Invptdtab "+mensaje);
                    finalizarActualizacionInvptdtab(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Invptdtab "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesInvptdtab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Invptdtab");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.INVPTDTAB);
            System.out.println("JSON Invptdtab... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Invptdtab[] res = gson.fromJson(json !=null ? json.toString() : null, Invptdtab[].class);
        List<Invptdtab>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Invptdtab>expenseMap = new HashMap<String, Invptdtab>();
        for (Invptdtab e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTD;
        String select = ContratoPedidos.Invptdtab.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_INVPTDTAB,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Invptdtab.");

        String id;
        String invptdcia;
        String invptdcod;
        String invptddispi;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_INVPTD_ID_REMOTA);
            invptdcia = c.getString(COLUMNA_INVPTD_INVPTDCIA);
            invptdcod = c.getString(COLUMNA_INVPTD_INVPTDCOD);
            invptddispi = c.getString(COLUMNA_INVPTD_INVPTDDISPI);


            Invptdtab match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_INVPTD.buildUpon().appendPath(id).build();

                boolean b0 = match.invptdcia !=null && !match.invptdcia.equals(invptdcia);
                boolean b1 = match.invptdcod != null && !match.invptdcod.equals(invptdcod);
                boolean b2 = match.invptddispi != null && !match.invptddispi.equals(invptddispi);

                if (b0 || b1 || b2){
                    Log.i(TAG, "Programando actualización invptdtab de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.InvptdtabColumnas.INVPTDCIA,match.invptdcia)
                            .withValue(ContratoPedidos.InvptdtabColumnas.INVPTDCOD,match.invptdcod)
                            .withValue(ContratoPedidos.InvptdtabColumnas.INVPTDDISPI,match.invptddispi)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro invptdtab: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_INVPTD.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación invptdtab de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("USUARIO expenseMap "+ expenseMap.values());
        for (Invptdtab invptd : expenseMap.values()){
            System.out.println("USUARIO DE INSERT "+invptd);
            Log.i(TAG, "Programando inserción ciatab de: " + invptd.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_INVPTD)
                    .withValue(ContratoPedidos.Invptdtab.ID_REMOTA,invptd.id)
                    .withValue(ContratoPedidos.Invptdtab.INVPTDCIA,invptd.invptdcia)
                    .withValue(ContratoPedidos.Invptdtab.INVPTDCOD,invptd.invptdcod)
                    .withValue(ContratoPedidos.Invptdtab.INVPTDDISPI,invptd.invptddispi)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones invptdtab...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_INVPTD,null,false);
            Log.i(TAG, "Sincronización ciatab finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de invptdtab");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CUSTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_CUSTAB = new String[]{
            ContratoPedidos.CustabColumnas._ID,
            ContratoPedidos.CustabColumnas.ID_REMOTA,
            ContratoPedidos.CustabColumnas.CUSCIA,
            ContratoPedidos.CustabColumnas.CUSCOD,
            ContratoPedidos.CustabColumnas.CUSNAME,
            ContratoPedidos.CustabColumnas.CUSDIR,
            ContratoPedidos.CustabColumnas.CUSTEL,
            ContratoPedidos.CustabColumnas.CUSNIT,
            ContratoPedidos.CustabColumnas.CUSLIMCRE,
            ContratoPedidos.CustabColumnas.CUSDIAVEN,
            ContratoPedidos.CustabColumnas.CUSMAIL,
            ContratoPedidos.CustabColumnas.CUSLATITUD,
            ContratoPedidos.CustabColumnas.CUSLONGITUD,
            ContratoPedidos.CustabColumnas.CUSCAT,
            ContratoPedidos.CustabColumnas.CUSRUTA,
            ContratoPedidos.CustabColumnas.CUSIVA,
    };

    public static final int COLUMNA_CUS_ID = 0;
    public static final int COLUMNA_CUS_ID_REMOTA = 1;
    public static final int COLUMNA_CUS_CUSCIA = 2;
    public static final int COLUMNA_CUS_CUSCOD = 3;
    public static final int COLUMNA_CUS_CUSNAME = 4;
    public static final int COLUMNA_CUS_CUSDIR = 5;
    public static final int COLUMNA_CUS_CUSTEL = 6;
    public static final int COLUMNA_CUS_CUSNIT = 7;
    public static final int COLUMNA_CUS_CUSLIMCRE = 8;
    public static final int COLUMNA_CUS_CUSDIAVEN = 9;
    public static final int COLUMNA_CUS_CUSMAIL = 10;
    public static final int COLUMNA_CUS_CUSLATITUD = 11;
    public static final int COLUMNA_CUS_CUSLONGITUD = 12;
    public static final int COLUMNA_CUS_CUSCAT = 13;
    public static final int COLUMNA_CUS_CUSRUTA = 14;
    public static final int COLUMNA_CUS_CUSIVA = 15;

    private void realizarSincronizacionLocalCustab(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Custab");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_CUS,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetCustab(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    Log.d(TAG, "Error de red");
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetCustab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Custab");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Custab  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesCustab(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaCustab(){
        Log.i(TAG, "Actualizando el servidor... Custab...");

        iniciarActualizacionCustab();

        Cursor c = obtenerRegistrosSuciosCustab();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_CUS_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_CUS,
                                Utilidades.deCursorAJSONObjectCustab(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertCustab(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Custab: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización CIATAB");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosCustab(){
        Uri uri = ContratoPedidos.CONTENT_URI_CUS;
        String selection = ContratoPedidos.CustabColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.CustabColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_CUSTAB,selection,selectionArgs,null);

    }

    private void iniciarActualizacionCustab(){
        Uri uri = ContratoPedidos.CONTENT_URI_CUS;
        String selection = ContratoPedidos.CustabColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.CustabColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.CustabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Custab:" + results);
    }

    private void finalizarActualizacionCustab(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_CUS;
        String selection = ContratoPedidos.CustabColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.CustabColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.CustabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.CustabColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertCustab(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_CUSTAB);

            System.out.println("ESTADO Custab insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Custab "+mensaje);
                    finalizarActualizacionCustab(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Invptdtab "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesCustab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Custab");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.CUSTAB);
            System.out.println("JSON Custab... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Custab[] res = gson.fromJson(json !=null ? json.toString() : null, Custab[].class);
        List<Custab>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Custab>expenseMap = new HashMap<String, Custab>();
        for (Custab e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_CUS;
        String select = ContratoPedidos.Custab.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_CUSTAB,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Custab.");

        String id;
        String cuscia;
        String cuscod;
        String cusname;
        String cusdir;
        String custel;
        String cusnit;
        String cuslimcre;
        String cusdiaven;
        String cusmail;
        String cuslatitud;
        String cuslongitud;
        String cuscat;
        String cusruta;
        String cusiva;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_CUS_ID_REMOTA);
            cuscia = c.getString(COLUMNA_CUS_CUSCIA);
            cuscod = c.getString(COLUMNA_CUS_CUSCOD);
            cusname = c.getString(COLUMNA_CUS_CUSNAME);
            cusdir = c.getString(COLUMNA_CUS_CUSDIR);
            custel = c.getString(COLUMNA_CUS_CUSTEL);
            cusnit = c.getString(COLUMNA_CUS_CUSNIT);
            cuslimcre = c.getString(COLUMNA_CUS_CUSLIMCRE);
            cusdiaven = c.getString(COLUMNA_CUS_CUSDIAVEN);
            cusmail = c.getString(COLUMNA_CUS_CUSMAIL);
            cuslatitud = c.getString(COLUMNA_CUS_CUSLATITUD);
            cuslongitud = c.getString(COLUMNA_CUS_CUSLONGITUD);
            cuscat = c.getString(COLUMNA_CUS_CUSCAT);
            cusruta = c.getString(COLUMNA_CUS_CUSRUTA);
            cusiva = c.getString(COLUMNA_CUS_CUSIVA);



            Custab match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_CUS.buildUpon().appendPath(id).build();

                boolean b0 = match.cuscia !=null && !match.cuscia.equals(cuscia);
                boolean b1 = match.cuscod != null && !match.cuscod.equals(cuscod);
                boolean b2 = match.cusname != null && !match.cusname.equals(cusname);
                boolean b3 = match.cusdir != null && !match.cusdir.equals(cusdir);
                boolean b4 = match.custel != null && !match.custel.equals(custel);
                boolean b5 = match.cusnit != null && !match.cusnit.equals(cusnit);
                boolean b6 = match.cuslimcre != null && !match.cuslimcre.equals(cuslimcre);
                boolean b7 = match.cusdiaven != null && !match.cusdiaven.equals(cusdiaven);
                boolean b8 = match.cusmail != null && !match.cusmail.equals(cusmail);
                boolean b9 = match.cuslatitud != null && !match.cuslatitud.equals(cuslatitud);
                boolean b10 = match.cuslongitud != null && !match.cuslongitud.equals(cuslongitud);
                boolean b11 = match.cuscat != null && !match.cuscat.equals(cuscat);
                boolean b12 = match.cusruta != null && !match.cusruta.equals(cusruta);
                boolean b13 = match.cusiva != null && !match.cusiva.equals(cusiva);


                if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10 || b11 || b12 || b13){
                    Log.i(TAG, "Programando actualización Custab de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.CustabColumnas.CUSCIA,match.cuscia)
                            .withValue(ContratoPedidos.CustabColumnas.CUSCOD,match.cuscod)
                            .withValue(ContratoPedidos.CustabColumnas.CUSNAME,match.cusname)
                            .withValue(ContratoPedidos.CustabColumnas.CUSDIR,match.cusdir)
                            .withValue(ContratoPedidos.CustabColumnas.CUSTEL,match.custel)
                            .withValue(ContratoPedidos.CustabColumnas.CUSNIT,match.cusnit)
                            .withValue(ContratoPedidos.CustabColumnas.CUSLIMCRE,match.cuslimcre)
                            .withValue(ContratoPedidos.CustabColumnas.CUSDIAVEN,match.cusdiaven)
                            .withValue(ContratoPedidos.CustabColumnas.CUSMAIL,match.cusmail)
                            .withValue(ContratoPedidos.CustabColumnas.CUSLATITUD,match.cuslatitud)
                            .withValue(ContratoPedidos.CustabColumnas.CUSLONGITUD,match.cuslongitud)
                            .withValue(ContratoPedidos.CustabColumnas.CUSCAT,match.cuscat)
                            .withValue(ContratoPedidos.CustabColumnas.CUSRUTA,match.cusruta)
                            .withValue(ContratoPedidos.CustabColumnas.CUSIVA,match.cusiva)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Custab: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_CUS.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación Custab de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("Custab expenseMap "+ expenseMap.values());
        for (Custab e : expenseMap.values()){
            System.out.println("Custab DE INSERT "+e);
            Log.i(TAG, "Programando inserción Custab de: " + e.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_CUS)
                    .withValue(ContratoPedidos.Custab.ID_REMOTA,e.id)
                    .withValue(ContratoPedidos.Custab.CUSCIA,e.cuscia)
                    .withValue(ContratoPedidos.Custab.CUSCOD,e.cuscod)
                    .withValue(ContratoPedidos.Custab.CUSNAME,e.cusname)
                    .withValue(ContratoPedidos.Custab.CUSDIR,e.cusdir)
                    .withValue(ContratoPedidos.Custab.CUSTEL,e.custel)
                    .withValue(ContratoPedidos.Custab.CUSNIT,e.cusnit)
                    .withValue(ContratoPedidos.Custab.CUSLIMCRE,e.cuslimcre)
                    .withValue(ContratoPedidos.Custab.CUSDIAVEN,e.cusdiaven)
                    .withValue(ContratoPedidos.Custab.CUSMAIL,e.cusmail)
                    .withValue(ContratoPedidos.Custab.CUSLATITUD,e.cuslatitud)
                    .withValue(ContratoPedidos.Custab.CUSLONGITUD,e.cuslongitud)
                    .withValue(ContratoPedidos.Custab.CUSCAT,e.cuscat)
                    .withValue(ContratoPedidos.Custab.CUSRUTA,e.cusruta)
                    .withValue(ContratoPedidos.Custab.CUSIVA,e.cusiva)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones Custab...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_CUS,null,false);
            Log.i(TAG, "Sincronización custab finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de Custab");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTDTAB\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_TALONARIOS = new String[]{
            ContratoPedidos.TalonariosColumnas._ID,
            ContratoPedidos.TalonariosColumnas.ID_REMOTA,
            ContratoPedidos.TalonariosColumnas.TALCOD,
            ContratoPedidos.TalonariosColumnas.TALCIA,
            ContratoPedidos.TalonariosColumnas.TALDES,
            ContratoPedidos.TalonariosColumnas.TALCOR

    };

    public static final int COLUMNA_TAL_ID = 0;
    public static final int COLUMNA_TAL_ID_REMOTA = 1;
    public static final int COLUMNA_TAL_TALCOD = 2;
    public static final int COLUMNA_TAL_TALCIA = 3;
    public static final int COLUMNA_TAL_TALDES = 4;
    public static final int COLUMNA_TAL_TALCOR = 5;

    private void realizarSincronizacionLocalTalonarios(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Talonarios");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_TAL,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetTalonarios(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.networkResponse.toString());
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetTalonarios(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Talonarios");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Talonarios  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesTalonarios(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaTalonarios(){
        Log.i(TAG, "Actualizando el servidor... Invptdtab...");

        iniciarActualizacionTalonarios();

        Cursor c = obtenerRegistrosSuciosTalonarios();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_TAL_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_TAL,
                                Utilidades.deCursorAJSONObjectTalonarios(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertTalonarios(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Talonarios: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización CIATAB");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosTalonarios(){
        Uri uri = ContratoPedidos.CONTENT_URI_TAL;
        String selection = ContratoPedidos.TalonariosColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.TalonariosColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_TALONARIOS,selection,selectionArgs,null);

    }

    private void iniciarActualizacionTalonarios(){
        Uri uri = ContratoPedidos.CONTENT_URI_TAL;
        String selection = ContratoPedidos.TalonariosColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.TalonariosColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.TalonariosColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Talonarios:" + results);
    }

    private void finalizarActualizacionTalonarios(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_TAL;
        String selection = ContratoPedidos.TalonariosColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.TalonariosColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.TalonariosColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.TalonariosColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertTalonarios(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_TALONARIOS);

            System.out.println("ESTADO Talonarios insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Talonarios "+mensaje);
                    finalizarActualizacionTalonarios(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Talonarios "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesTalonarios(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Talonarios");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.TALONARIOS);
            System.out.println("JSON Talonarios... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Talonarios[] res = gson.fromJson(json !=null ? json.toString() : null, Talonarios[].class);
        List<Talonarios>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Talonarios>expenseMap = new HashMap<String, Talonarios>();
        for (Talonarios e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_TAL;
        String select = ContratoPedidos.Talonarios.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_TALONARIOS,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Talonarios.");

        String id;
        String talcod;
        String talcia;
        String taldes;
        String talcor;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_TAL_ID_REMOTA);
            talcod = c.getString(COLUMNA_TAL_TALCOD);
            talcia = c.getString(COLUMNA_TAL_TALCIA);
            taldes = c.getString(COLUMNA_TAL_TALDES);
            talcor = c.getString(COLUMNA_TAL_TALCOR);


            Talonarios match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_TAL.buildUpon().appendPath(id).build();

                boolean b0 = match.talcod !=null && !match.talcod.equals(talcod);
                boolean b1 = match.talcia != null && !match.talcia.equals(talcia);
                boolean b2 = match.taldes != null && !match.taldes.equals(taldes);
                boolean b3 = match.talcor != null && !match.talcor.equals(talcor);

                if (b0 || b1 || b2 || b3){
                    Log.i(TAG, "Programando actualización Talonarios de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.TalonariosColumnas.TALCOD,match.talcod)
                            .withValue(ContratoPedidos.TalonariosColumnas.TALCIA,match.talcia)
                            .withValue(ContratoPedidos.TalonariosColumnas.TALDES,match.taldes)
                            .withValue(ContratoPedidos.TalonariosColumnas.TALCOR,match.talcor)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Talonarios: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_TAL.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación Talonarios de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("Talonarios expenseMap "+ expenseMap.values());
        for (Talonarios e : expenseMap.values()){
            System.out.println("Talonarios DE INSERT "+e);
            Log.i(TAG, "Programando inserción Talonarios de: " + e.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_TAL)
                    .withValue(ContratoPedidos.Talonarios.ID_REMOTA,e.id)
                    .withValue(ContratoPedidos.Talonarios.TALCOD,e.talcod)
                    .withValue(ContratoPedidos.Talonarios.TALCIA,e.talcia)
                    .withValue(ContratoPedidos.Talonarios.TALDES,e.taldes)
                    .withValue(ContratoPedidos.Talonarios.TALCOR,e.talcor)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones Talonarios...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_TAL,null,false);
            Log.i(TAG, "Sincronización Talonarios finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de Talonarios");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTMTAB\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_INVPTMTAB = new String[]{
            ContratoPedidos.InvptmtabColumnas._ID,
            ContratoPedidos.InvptmtabColumnas.ID_REMOTA,
            ContratoPedidos.InvptmtabColumnas.INVPTMCIA,
            ContratoPedidos.InvptmtabColumnas.INVPTMCOD,
            ContratoPedidos.InvptmtabColumnas.INVPTMDESC,
            ContratoPedidos.InvptmtabColumnas.INVPTMMED,
            ContratoPedidos.InvptmtabColumnas.INVPTMEMP,
            ContratoPedidos.InvptmtabColumnas.INVPTMIVA

    };

    public static final int COLUMNA_INVPTM_ID = 0;
    public static final int COLUMNA_INVPTM_ID_REMOTA = 1;
    public static final int COLUMNA_INVPTM_INVPTMCIA = 2;
    public static final int COLUMNA_INVPTM_INVPTMCOD = 3;
    public static final int COLUMNA_INVPTM_INVPTMDESC = 4;
    public static final int COLUMNA_INVPTM_INVPTMMED = 5;
    public static final int COLUMNA_INVPTM_INVPTMEMP = 6;
    public static final int COLUMNA_INVPTM_INVPTMIVA = 7;

    private void realizarSincronizacionLocalInvptmtab(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Invptmtab");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_INVPTM,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetInvptmtab(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    Log.d(TAG, "Error de red");
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetInvptmtab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Invptmtab");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Invptmtab  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesInvptmtab(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaInvptmtab(){
        Log.i(TAG, "Actualizando el servidor... Invptmtab...");

        iniciarActualizacionInvptmtab();

        Cursor c = obtenerRegistrosSuciosInvptmtab();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_INVPTM_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_INVPTM,
                                Utilidades.deCursorAJSONObjectInvptmtab(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertInvptmtab(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Invptmtab: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización CIATAB");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosInvptmtab(){
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTM;
        String selection = ContratoPedidos.InvptmtabColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.InvptmtabColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_INVPTMTAB,selection,selectionArgs,null);

    }

    private void iniciarActualizacionInvptmtab(){
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTM;
        String selection = ContratoPedidos.InvptmtabColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.InvptmtabColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.InvptmtabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Invptmtab:" + results);
    }

    private void finalizarActualizacionInvptmtab(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTM;
        String selection = ContratoPedidos.InvptmtabColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.InvptmtabColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.InvptmtabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.InvptmtabColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertInvptmtab(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_INVPTMTAB);

            System.out.println("ESTADO Invptmtab insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Invptmtab "+mensaje);
                    finalizarActualizacionInvptmtab(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Invptmtab "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesInvptmtab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Invptmtab");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.INVPTMTAB);
            System.out.println("JSON Invptmtab... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Invptmtab[] res = gson.fromJson(json !=null ? json.toString() : null, Invptmtab[].class);
        List<Invptmtab>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Invptmtab>expenseMap = new HashMap<String, Invptmtab>();
        for (Invptmtab e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_INVPTM;
        String select = ContratoPedidos.Invptmtab.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_INVPTMTAB,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Invptmtab.");

        String id;
        String invptmcia;
        String invptmcod;
        String invptmdesc;
        String invptmmed;
        String invptmemp;
        String invptmiva;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_INVPTM_ID_REMOTA);
            invptmcia = c.getString(COLUMNA_INVPTM_INVPTMCIA);
            invptmcod = c.getString(COLUMNA_INVPTM_INVPTMCOD);
            invptmdesc = c.getString(COLUMNA_INVPTM_INVPTMDESC);
            invptmmed = c.getString(COLUMNA_INVPTM_INVPTMMED);
            invptmemp = c.getString(COLUMNA_INVPTM_INVPTMEMP);
            invptmiva = c.getString(COLUMNA_INVPTM_INVPTMIVA);


            Invptmtab match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_INVPTM.buildUpon().appendPath(id).build();

                boolean b0 = match.invptmcia !=null && !match.invptmcia.equals(invptmcia);
                boolean b1 = match.invptmcod != null && !match.invptmcod.equals(invptmcod);
                boolean b2 = match.invptmdesc != null && !match.invptmdesc.equals(invptmdesc);
                boolean b3 = match.invptmmed != null && !match.invptmmed.equals(invptmmed);
                boolean b4 = match.invptmemp != null && !match.invptmemp.equals(invptmemp);
                boolean b5 = match.invptmiva != null && !match.invptmiva.equals(invptmiva);

                if (b0 || b1 || b2 || b3 || b4 || b5){
                    Log.i(TAG, "Programando actualización Invptmtab de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.InvptmtabColumnas.INVPTMCIA,match.invptmcia)
                            .withValue(ContratoPedidos.InvptmtabColumnas.INVPTMCOD,match.invptmcod)
                            .withValue(ContratoPedidos.InvptmtabColumnas.INVPTMDESC,match.invptmdesc)
                            .withValue(ContratoPedidos.InvptmtabColumnas.INVPTMMED,match.invptmmed)
                            .withValue(ContratoPedidos.InvptmtabColumnas.INVPTMEMP,match.invptmemp)
                            .withValue(ContratoPedidos.InvptmtabColumnas.INVPTMIVA,match.invptmiva)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Invptmtab: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_INVPTM.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación Invptmtab de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("USUARIO expenseMap "+ expenseMap.values());
        for (Invptmtab e : expenseMap.values()){
            System.out.println("USUARIO DE INSERT "+e);
            Log.i(TAG, "Programando inserción ciatab de: " + e.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_INVPTM)
                    .withValue(ContratoPedidos.Invptmtab.ID_REMOTA,e.id)
                    .withValue(ContratoPedidos.Invptmtab.INVPTMCIA,e.invptmcia)
                    .withValue(ContratoPedidos.Invptmtab.INVPTMCOD,e.invptmcod)
                    .withValue(ContratoPedidos.Invptmtab.INVPTMDESC,e.invptmdesc)
                    .withValue(ContratoPedidos.Invptmtab.INVPTMMED,e.invptmmed)
                    .withValue(ContratoPedidos.Invptmtab.INVPTMEMP,e.invptmemp)
                    .withValue(ContratoPedidos.Invptmtab.INVPTMIVA,e.invptmiva)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones Invptmtab...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_INVPTM,null,false);
            Log.i(TAG, "Sincronización Invptmtab finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de Invptmtab");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PR1TAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_PR1TAB = new String[]{
            ContratoPedidos.Pr1tabColumnas._ID,
            ContratoPedidos.Pr1tabColumnas.ID_REMOTA,
            ContratoPedidos.Pr1tabColumnas.PR1CIA,
            ContratoPedidos.Pr1tabColumnas.PR1COD,
            ContratoPedidos.Pr1tabColumnas.PR1CAT,
            ContratoPedidos.Pr1tabColumnas.PR1FEC,
            ContratoPedidos.Pr1tabColumnas.PR1PREC,
    };

    public static final int COLUMNA_PR1_ID = 0;
    public static final int COLUMNA_PR1_ID_REMOTA = 1;
    public static final int COLUMNA_PR1_PR1CIA = 2;
    public static final int COLUMNA_PR1_PR1COD = 3;
    public static final int COLUMNA_PR1_PR1CAT = 4;
    public static final int COLUMNA_PR1_PR1FEC = 5;
    public static final int COLUMNA_PR1_PR1DIQ = 6;

    private void realizarSincronizacionLocalPr1tab(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Pr1tab");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_PR1,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("Response Json " +response);
                                procesarRespuestaGetPr1tab(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    Log.d(TAG, "Error de red");

                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetPr1tab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Pr1tab");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Pr1tab  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesPr1tab(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaPr1tab(){
        Log.i(TAG, "Actualizando el servidor... Invptmtab...");

        iniciarActualizacionPr1tab();

        Cursor c = obtenerRegistrosSuciosPr1tab();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_PR1_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_PR1,
                                Utilidades.deCursorAJSONObjectPr1tab(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertPr1tab(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Pr1tab: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización CIATAB");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosPr1tab(){
        Uri uri = ContratoPedidos.CONTENT_URI_PR1;
        String selection = ContratoPedidos.Pr1tabColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.Pr1tabColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_PR1TAB,selection,selectionArgs,null);

    }

    private void iniciarActualizacionPr1tab(){
        Uri uri = ContratoPedidos.CONTENT_URI_PR1;
        String selection = ContratoPedidos.Pr1tabColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.Pr1tabColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.Pr1tabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Pr1tab:" + results);
    }

    private void finalizarActualizacionPr1tab(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_PR1;
        String selection = ContratoPedidos.Pr1tabColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.Pr1tabColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.Pr1tabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.Pr1tabColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertPr1tab(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_PR1TAB);

            System.out.println("ESTADO Pr1tab insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Pr1tab "+mensaje);
                    finalizarActualizacionPr1tab(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Pr1tab "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesPr1tab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Pr1tab");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.PR1TAB);
            System.out.println("JSON Pr1tab... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Pr1tab[] res = gson.fromJson(json !=null ? json.toString() : null, Pr1tab[].class);
        List<Pr1tab>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Pr1tab>expenseMap = new HashMap<String, Pr1tab>();
        for (Pr1tab e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_PR1;
        String select = ContratoPedidos.Pr1tab.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_PR1TAB,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Pr1tab.");

        String id;
        String pr1cia;
        String pr1cod;
        String pr1cat;
        String pr1fec;
        String pr1diq;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_PR1_ID_REMOTA);
            pr1cia = c.getString(COLUMNA_PR1_PR1CIA);
            pr1cod = c.getString(COLUMNA_PR1_PR1COD);
            pr1cat = c.getString(COLUMNA_PR1_PR1CAT);
            pr1fec = c.getString(COLUMNA_PR1_PR1FEC);
            pr1diq = c.getString(COLUMNA_PR1_PR1DIQ);

            Pr1tab match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_INVPTM.buildUpon().appendPath(id).build();

                boolean b0 = match.pr1cia !=null && !match.pr1cia.equals(pr1cia);
                boolean b1 = match.pr1cod != null && !match.pr1cod.equals(pr1cod);
                boolean b2 = match.pr1cat != null && !match.pr1cat.equals(pr1cat);
                boolean b3 = match.pr1fec != null && !match.pr1fec.equals(pr1fec);
                boolean b4 = match.pr1diq != null && !match.pr1diq.equals(pr1diq);


                if (b0 || b1 || b2 || b3 || b4){
                    Log.i(TAG, "Programando actualización Pr1tab de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.Pr1tabColumnas.PR1CIA,match.pr1cia)
                            .withValue(ContratoPedidos.Pr1tabColumnas.PR1COD,match.pr1cod)
                            .withValue(ContratoPedidos.Pr1tabColumnas.PR1CAT,match.pr1cat)
                            .withValue(ContratoPedidos.Pr1tabColumnas.PR1FEC,match.pr1fec)
                            .withValue(ContratoPedidos.Pr1tabColumnas.PR1PREC,match.pr1diq)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Pr1tab: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_PR1.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación Pr1tab de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("Pr1tab expenseMap "+ expenseMap.values());
        for (Pr1tab e : expenseMap.values()){
            System.out.println("Pr1tab DE INSERT "+e);
            Log.i(TAG, "Programando inserción Pr1tab de: " + e.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_PR1)
                    .withValue(ContratoPedidos.Pr1tab.ID_REMOTA,e.id)
                    .withValue(ContratoPedidos.Pr1tab.PR1CIA,e.pr1cia)
                    .withValue(ContratoPedidos.Pr1tab.PR1COD,e.pr1cod)
                    .withValue(ContratoPedidos.Pr1tab.PR1CAT,e.pr1cat)
                    .withValue(ContratoPedidos.Pr1tab.PR1FEC,e.pr1fec)
                    .withValue(ContratoPedidos.Pr1tab.PR1PREC,e.pr1diq)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones Pr1tab...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_PR1,null,false);
            Log.i(TAG, "Sincronización Pr1tab finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de Pr1tab");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TIPTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_TIPTAB = new String[]{
            ContratoPedidos.TiptabColumnas._ID,
            ContratoPedidos.TiptabColumnas.ID_REMOTA,
            ContratoPedidos.TiptabColumnas.DOCCIA,
            ContratoPedidos.TiptabColumnas.DOCCOD,
            ContratoPedidos.TiptabColumnas.DOCNAME,
    };

    public static final int COLUMNA_TIP_ID = 0;
    public static final int COLUMNA_TIP_ID_REMOTA = 1;
    public static final int COLUMNA_TIP_TIPCIA = 2;
    public static final int COLUMNA_TIP_TIPCOD = 3;
    public static final int COLUMNA_TIP_TIPDES = 4;

    private void realizarSincronizacionLocalTiptab(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Tiptab");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_TIP,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetTiptab(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    Log.d(TAG, error.networkResponse.toString());
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetTiptab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Tiptab");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Tiptab  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesTiptab(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaTiptab(){
        Log.i(TAG, "Actualizando el servidor... Tiptab...");

        iniciarActualizacionTiptab();

        Cursor c = obtenerRegistrosSuciosTiptab();

        if (c.getCount()>0){
            while (c.moveToNext()){
                final int idLocal = c.getInt(COLUMNA_TIP_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_TIP,
                                Utilidades.deCursorAJSONObjectTiptab(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertTiptab(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Tiptab: " + error.getMessage());
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String>getHeaders(){
                                Map<String,String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType(){
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }

                        }
                );
            }
        } else {
            Log.i(TAG, "No se requiere sincronización Tiptab");
        }
        c.close();
    }

    private Cursor obtenerRegistrosSuciosTiptab(){
        Uri uri = ContratoPedidos.CONTENT_URI_TIP;
        String selection = ContratoPedidos.TiptabColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.TiptabColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC+""};

        return resolver.query(uri,PROJECTION_TIPTAB,selection,selectionArgs,null);

    }

    private void iniciarActualizacionTiptab(){
        Uri uri = ContratoPedidos.CONTENT_URI_PR1;
        String selection = ContratoPedidos.Pr1tabColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.Pr1tabColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.Pr1tabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Pr1tab:" + results);
    }

    private void finalizarActualizacionTiptab(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_TIP;
        String selection = ContratoPedidos.TiptabColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.TiptabColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.TiptabColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.TiptabColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertTiptab(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_TIPTAB);

            System.out.println("ESTADO Tiptab insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Tiptab "+mensaje);
                    finalizarActualizacionTiptab(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Tiptab "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesTiptab(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Tiptab");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.TIPTAB);
            System.out.println("JSON Tiptab... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Tiptab[] res = gson.fromJson(json !=null ? json.toString() : null, Tiptab[].class);
        List<Tiptab>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Tiptab>expenseMap = new HashMap<String, Tiptab>();
        for (Tiptab e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_TIP;
        String select = ContratoPedidos.Tipos.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_TIPTAB,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Tiptab.");

        String id;
        String tipcia;
        String tipcod;
        String tipdes;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_TIP_ID_REMOTA);
            tipcia = c.getString(COLUMNA_TIP_TIPCIA);
            tipcod = c.getString(COLUMNA_TIP_TIPCOD);
            tipdes = c.getString(COLUMNA_TIP_TIPDES);

            Tiptab match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_INVPTM.buildUpon().appendPath(id).build();

                boolean b0 = match.tipcia !=null && !match.tipcia.equals(tipcia);
                boolean b1 = match.tipcod != null && !match.tipcod.equals(tipcod);
                boolean b2 = match.tipdes != null && !match.tipdes.equals(tipdes);


                if (b0 || b1 || b2){
                    Log.i(TAG, "Programando actualización Tiptab de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.TiptabColumnas.DOCCIA,match.tipcia)
                            .withValue(ContratoPedidos.TiptabColumnas.DOCCOD,match.tipcod)
                            .withValue(ContratoPedidos.TiptabColumnas.DOCNAME,match.tipdes)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Tiptab: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_TIP.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación Tiptab de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("Pr1tab expenseMap "+ expenseMap.values());
        for (Tiptab e : expenseMap.values()){
            System.out.println("Tiptab DE INSERT "+e);
            Log.i(TAG, "Programando inserción Tiptab de: " + e.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_TIP)
                    .withValue(ContratoPedidos.Tipos.ID_REMOTA,e.id)
                    .withValue(ContratoPedidos.Tipos.DOCCIA,e.tipcia)
                    .withValue(ContratoPedidos.Tipos.DOCCOD,e.tipcod)
                    .withValue(ContratoPedidos.Tipos.DOCNAME,e.tipdes)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones Tiptab...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_TIP,null,false);
            Log.i(TAG, "Sincronización Tiptab finalizada.");
        }else {
            Log.i(TAG, "No se requiere sincronización de Tiptab");
        }

    }

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CUSTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private static final String[] PROJECTION_PEDIDOS = new String[]{
            ContratoPedidos.PedidosColumnas._ID,
            ContratoPedidos.PedidosColumnas.ID_REMOTA,
            ContratoPedidos.PedidosColumnas.COMPANIA,
            ContratoPedidos.PedidosColumnas.CLIENTE,
            ContratoPedidos.PedidosColumnas.RUTA,
            ContratoPedidos.PedidosColumnas.LUGAR_ENTREGA,
            ContratoPedidos.PedidosColumnas.PRODUCTO,
            ContratoPedidos.PedidosColumnas.PRECIO_PT,
            ContratoPedidos.PedidosColumnas.TIPO_PEDIDO,
            ContratoPedidos.PedidosColumnas.NO_PED_MOVIL,
            ContratoPedidos.PedidosColumnas.FECHA,
            ContratoPedidos.PedidosColumnas.UNIDADES_VTA,
            ContratoPedidos.PedidosColumnas.UNIDADES_BONI,
            ContratoPedidos.PedidosColumnas.LATITUD,
            ContratoPedidos.PedidosColumnas.LONGITUD,
            ContratoPedidos.PedidosColumnas.ESTATUS,
            ContratoPedidos.PedidosColumnas.ESTATUS_REG,
            ContratoPedidos.PedidosColumnas.QTY_ORIGINAL,
            ContratoPedidos.PedidosColumnas.ID_PEDIDO,
            ContratoPedidos.PedidosColumnas.REG_ANULADO,
            ContratoPedidos.PedidosColumnas.ESTATUS_FINAL,
            ContratoPedidos.PedidosColumnas.NO_PEDIDO_CMF,
            ContratoPedidos.PedidosColumnas.USUARIO,
            ContratoPedidos.PedidosColumnas.FECHA_SISTEMA
    };

    public static final int COLUMNA_PED_ID = 0;
    public static final int COLUMNA_PED_ID_REMOTA = 1;
    public static final int COLUMNA_PED_COMPANIA = 2;
    public static final int COLUMNA_PED_CLIENTE = 3;
    public static final int COLUMNA_PED_RUTA = 4;
    public static final int COLUMNA_PED_LUGAR_ENTREGA = 5;
    public static final int COLUMNA_PED_PRODUCTO = 6;
    public static final int COLUMNA_PED_PRECIO_PT = 7;
    public static final int COLUMNA_PED_TIPO_PEDIDO = 8;
    public static final int COLUMNA_PED_NO_PED_MOVIL = 9;
    public static final int COLUMNA_PED_FECHA = 10;
    public static final int COLUMNA_PED_UNIDADES_VTA = 11;
    public static final int COLUMNA_PED_UNIDADES_BONI = 12;
    public static final int COLUMNA_PED_LATITUD = 13;
    public static final int COLUMNA_PED_LONGITUD = 14;
    public static final int COLUMNA_PED_ESTATUS = 15;
    public static final int COLUMNA_PED_ESTATUS_REG = 16;
    public static final int COLUMNA_PED_QTY_ORIGINAL = 17;
    public static final int COLUMNA_PED_ID_PEDIDO = 18;
    public static final int COLUMNA_PED_REG_ANULADO = 19;
    public static final int COLUMNA_PED_ESTATUS_FINAL = 20;
    public static final int COLUMNA_PED_NO_PEDIDO_CMF = 21;
    public static final int COLUMNA_PED_USUARIO = 22;
    public static final int COLUMNA_PED_FECHA_SISTEMA = 23;

    private void realizarSincronizacionLocalPedidos(final SyncResult syncResult){
        Log.i(TAG,"Actualizando Cliente Pedidos");

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constantes.GET_URL_PED,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaGetPedidos(response, syncResult);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.networkResponse.toString());
                            }
                        }
                )
        );
    }

    private void procesarRespuestaGetPedidos(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"procesarRespuestaGet Pedidos");

        try {
            String estado = response.getString(Constantes.ESTADO);
            System.out.println("ESTADO Pedidos  get "+estado);

            switch (estado){
                case Constantes.SUCCESS:

                    actualizarDatosLocalesPedidos(response,syncResult);
                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Log.i(TAG, mensaje);
                    break;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void realizarSincronizacionRemotaPedidos() {
        Log.i(TAG, "Actualizando el servidor... Pedidos");

        iniciarActualizacionPedidos();

        Cursor c = obtenerRegistrosSuciosPedidos();

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros sucios Pedidos.");

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                final int idLocal = c.getInt(COLUMNA_PED_ID);

                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constantes.INSERT_URL_PED,
                                Utilidades.deCursorAJSONObjectPedidos(c),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuestaInsertPedidos(response, idLocal);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley Pedidos: " + error.getMessage());
                                        error.printStackTrace();
                                    }
                                }

                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

        } else {
            Log.i(TAG, "No se requiere sincronización");
        }
        c.close();
    }

    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     *
     * @return Cursor con el registro.
     */

    private Cursor obtenerRegistrosSuciosPedidos(){
        Uri uri = ContratoPedidos.Pedidos.URI_CONTENIDO;
        System.out.println(" Uri Pedido registros sucios "+ uri);
        String selection = ContratoPedidos.PedidosColumnas.PENDIENTE_INSERCION  + "=? AND "
                + ContratoPedidos.PedidosColumnas.ESTADO + " =?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_SYNC + ""};

        return resolver.query(uri,PROJECTION_PEDIDOS,selection,selectionArgs,null);

    }

    private void iniciarActualizacionPedidos(){
        Uri uri = ContratoPedidos.CONTENT_URI_PED;
        String selection = ContratoPedidos.PedidosColumnas.PENDIENTE_INSERCION + "=? AND "
                + ContratoPedidos.PedidosColumnas.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1",ContratoPedidos.ESTADO_OK+""};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.PedidosColumnas.ESTADO,ContratoPedidos.ESTADO_OK);

        int results = resolver.update(uri,v,selection,selectionArgs);
        Log.i(TAG, "Registros puestos en cola de inserción Pedidos:" + results);
    }

    private void finalizarActualizacionPedidos(String idRemota, int idLocal){
        Uri uri = ContratoPedidos.CONTENT_URI_PED;
        String selection = ContratoPedidos.PedidosColumnas._ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(ContratoPedidos.PedidosColumnas.PENDIENTE_INSERCION,"0");
        v.put(ContratoPedidos.PedidosColumnas.ESTADO,ContratoPedidos.ESTADO_OK);
        v.put(ContratoPedidos.PedidosColumnas.ID_REMOTA,idRemota);

        resolver.update(uri,v,selection,selectionArgs);
    }

    private void procesarRespuestaInsertPedidos(JSONObject response,int idLocal){

        try {
            String estado = response.getString(Constantes.ESTADO);
            String mensaje = response.getString(Constantes.MENSAJE);
            String idRemota = response.getString(Constantes.ID_PEDIDOS);

            System.out.println("ESTADO Pedidos insert"+estado);

            switch (estado){
                case Constantes.SUCCESS:
                    Log.i(TAG, "SUCCES Pedidos "+mensaje);
                    finalizarActualizacionPedidos(idRemota,idLocal);
                    break;
                case Constantes.FAILED:
                    Log.i(TAG, "FAILED Pedidos "+mensaje);
                    break;

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void actualizarDatosLocalesPedidos(JSONObject response,SyncResult syncResult){
        Log.i(TAG,"actualizarDatosLocales Pedidos");
        JSONArray json = null;

        try {
            json = response.getJSONArray(Constantes.PEDIDOS);
            System.out.println("JSON Pedidos... " + json);
        }catch (Exception e){
            e.printStackTrace();
        }
        Pedidos[] res = gson.fromJson(json !=null ? json.toString() : null, Pedidos[].class);
        List<Pedidos>data = Arrays.asList(res);
        ArrayList<ContentProviderOperation>ops = new ArrayList<ContentProviderOperation>();
        HashMap<String, Pedidos>expenseMap = new HashMap<String, Pedidos>();
        for (Pedidos e : data){
            System.out.println("variable e "+e.id);
            expenseMap.put(e.id, e);
            System.out.println("expenseMap "+expenseMap.values());
        }
        Uri uri = ContratoPedidos.CONTENT_URI_PED;
        String select = ContratoPedidos.PedidosColumnas.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri,PROJECTION_PEDIDOS,select,null,null);
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales Pedidos.");

        String id;
        String compania;
        String cliente;
        String ruta;
        String lugar_entrega;
        String producto;
        String precio_pt;
        String tipo_pedido;
        String no_ped_movil;
        String fecha;
        String unidades_vta;
        String unidades_boni;
        String latitud;
        String longitud;
        String estatus;
        String estatus_reg;
        String qty_original;
        String id_pedido;
        String reg_anulado;
        String estatus_final;
        String no_pedido_cmf;
        String usuario;
        String fecha_sistema;

        while (c.moveToNext()){
            syncResult.stats.numEntries++;

            id = c.getString(COLUMNA_PED_ID_REMOTA);
            compania = c.getString(COLUMNA_PED_COMPANIA);
            cliente = c.getString(COLUMNA_PED_CLIENTE);
            ruta = c.getString(COLUMNA_PED_RUTA);
            lugar_entrega = c.getString(COLUMNA_PED_LUGAR_ENTREGA);
            producto = c.getString(COLUMNA_PED_PRODUCTO);
            precio_pt = c.getString(COLUMNA_PED_PRECIO_PT);
            tipo_pedido = c.getString(COLUMNA_PED_TIPO_PEDIDO);
            no_ped_movil = c.getString(COLUMNA_PED_NO_PED_MOVIL);
            fecha = c.getString(COLUMNA_PED_FECHA);
            unidades_vta = c.getString(COLUMNA_PED_UNIDADES_VTA);
            unidades_boni = c.getString(COLUMNA_PED_UNIDADES_BONI);
            latitud = c.getString(COLUMNA_PED_LATITUD);
            longitud = c.getString(COLUMNA_PED_LONGITUD);
            estatus = c.getString(COLUMNA_PED_ESTATUS);
            estatus_reg = c.getString(COLUMNA_PED_ESTATUS_REG);
            qty_original = c.getString(COLUMNA_PED_QTY_ORIGINAL);
            id_pedido = c.getString(COLUMNA_PED_ID_PEDIDO);
            reg_anulado = c.getString(COLUMNA_PED_REG_ANULADO);
            estatus_final = c.getString(COLUMNA_PED_ESTATUS_FINAL);
            no_pedido_cmf = c.getString(COLUMNA_PED_NO_PEDIDO_CMF);
            usuario = c.getString(COLUMNA_PED_USUARIO);
            fecha_sistema = c.getString(COLUMNA_PED_FECHA_SISTEMA);

            Pedidos match = expenseMap.get(id);
            if (match!=null){

                expenseMap.remove(id);

                Uri existingUri = ContratoPedidos.CONTENT_URI_PED.buildUpon().appendPath(id).build();

                boolean b0 = match.compania !=null && !match.compania.equals(compania);
                boolean b1 = match.cliente != null && !match.cliente.equals(cliente);
                boolean b2 = match.ruta != null && !match.ruta.equals(ruta);
                boolean b3 = match.lugar_entrega != null && !match.lugar_entrega.equals(lugar_entrega);
                boolean b4 = match.producto != null && !match.producto.equals(producto);
                boolean b5 = match.precio_pt != null && !match.precio_pt.equals(precio_pt);
                boolean b6 = match.tipo_pedido != null && !match.tipo_pedido.equals(tipo_pedido);
                boolean b7 = match.no_ped_movil != null && !match.no_ped_movil.equals(no_ped_movil);
                boolean b8 = match.fecha != null && !match.fecha.equals(fecha);
                boolean b9 = match.unidades_vta != null && !match.unidades_vta.equals(unidades_vta);
                boolean b10 = match.unidades_boni != null && !match.unidades_boni.equals(unidades_boni);
                boolean b11 = match.latitud != null && !match.latitud.equals(latitud);
                boolean b12 = match.longitud != null && !match.longitud.equals(longitud);
                boolean b13 = match.estatus != null && !match.estatus.equals(estatus);
                boolean b14 = match.estatus_reg != null && !match.estatus_reg.equals(estatus_reg);
                boolean b15 = match.qty_original != null && !match.qty_original.equals(qty_original);
                boolean b16 = match.id_pedido != null && !match.id_pedido.equals(id_pedido);
                boolean b17 = match.reg_anulado != null && !match.reg_anulado.equals(reg_anulado);
                boolean b18 = match.estatus_final != null && !match.estatus_final.equals(estatus_final);
                boolean b19 = match.no_pedido_cmf != null && !match.no_pedido_cmf.equals(no_pedido_cmf);
                boolean b20 = match.usuario != null && !match.usuario.equals(usuario);
                boolean b21 = match.fecha_sistema != null && !match.fecha_sistema.equals(fecha_sistema);


                if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10 || b11 || b12 || b13 || b14 || b15 || b16 || b17 || b18 || b19 || b20 || b21){
                    Log.i(TAG, "Programando actualización Pedidos de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContratoPedidos.PedidosColumnas.COMPANIA,match.compania)
                            .withValue(ContratoPedidos.PedidosColumnas.CLIENTE,match.cliente)
                            .withValue(ContratoPedidos.PedidosColumnas.RUTA,match.ruta)
                            .withValue(ContratoPedidos.PedidosColumnas.LUGAR_ENTREGA,match.lugar_entrega)
                            .withValue(ContratoPedidos.PedidosColumnas.PRODUCTO,match.producto)
                            .withValue(ContratoPedidos.PedidosColumnas.PRECIO_PT,match.precio_pt)
                            .withValue(ContratoPedidos.PedidosColumnas.TIPO_PEDIDO,match.tipo_pedido)
                            .withValue(ContratoPedidos.PedidosColumnas.NO_PED_MOVIL,match.no_ped_movil)
                            .withValue(ContratoPedidos.PedidosColumnas.FECHA,match.fecha)
                            .withValue(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,match.unidades_vta)
                            .withValue(ContratoPedidos.PedidosColumnas.UNIDADES_BONI,match.unidades_boni)
                            .withValue(ContratoPedidos.PedidosColumnas.LATITUD,match.latitud)
                            .withValue(ContratoPedidos.PedidosColumnas.LONGITUD,match.longitud)
                            .withValue(ContratoPedidos.PedidosColumnas.ESTATUS,match.estatus)
                            .withValue(ContratoPedidos.PedidosColumnas.ESTATUS_REG,match.estatus_reg)
                            .withValue(ContratoPedidos.PedidosColumnas.QTY_ORIGINAL,match.qty_original)
                            .withValue(ContratoPedidos.PedidosColumnas.ID_PEDIDO,match.id_pedido)
                            .withValue(ContratoPedidos.PedidosColumnas.REG_ANULADO,match.reg_anulado)
                            .withValue(ContratoPedidos.PedidosColumnas.ESTATUS_FINAL,match.estatus_final)
                            .withValue(ContratoPedidos.PedidosColumnas.NO_PEDIDO_CMF,match.no_pedido_cmf)
                            .withValue(ContratoPedidos.PedidosColumnas.USUARIO,match.usuario)
                            .withValue(ContratoPedidos.PedidosColumnas.FECHA_SISTEMA,match.fecha_sistema)
                            .build());
                    syncResult.stats.numUpdates++;
                }else {
                    Log.i(TAG, "No hay acciones para este registro Pedidos: " + existingUri);
                }
            }else {
                Uri deleteUri = ContratoPedidos.CONTENT_URI_PED.buildUpon().appendPath(id).build();
                Log.i(TAG, "Programando eliminación Pedidos de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        System.out.println("Pedidos expenseMap "+ expenseMap.values());
        for (Pedidos e : expenseMap.values()){
            System.out.println("Pedidos DE INSERT "+e);
            Log.i(TAG, "Programando inserción Pedidos de: " + e.id);

            ops.add(ContentProviderOperation.newInsert(ContratoPedidos.CONTENT_URI_PED)
                    .withValue(ContratoPedidos.PedidosColumnas.ID_REMOTA,e.id)
                    .withValue(ContratoPedidos.PedidosColumnas.COMPANIA,e.compania)
                    .withValue(ContratoPedidos.PedidosColumnas.CLIENTE,e.cliente)
                    .withValue(ContratoPedidos.PedidosColumnas.RUTA,e.ruta)
                    .withValue(ContratoPedidos.PedidosColumnas.LUGAR_ENTREGA,e.lugar_entrega)
                    .withValue(ContratoPedidos.PedidosColumnas.PRODUCTO,e.producto)
                    .withValue(ContratoPedidos.PedidosColumnas.PRECIO_PT,e.precio_pt)
                    .withValue(ContratoPedidos.PedidosColumnas.TIPO_PEDIDO,e.tipo_pedido)
                    .withValue(ContratoPedidos.PedidosColumnas.NO_PED_MOVIL,e.no_ped_movil)
                    .withValue(ContratoPedidos.PedidosColumnas.FECHA,e.fecha)
                    .withValue(ContratoPedidos.PedidosColumnas.UNIDADES_VTA,e.unidades_vta)
                    .withValue(ContratoPedidos.PedidosColumnas.UNIDADES_BONI,e.unidades_boni)
                    .withValue(ContratoPedidos.PedidosColumnas.LATITUD,e.latitud)
                    .withValue(ContratoPedidos.PedidosColumnas.LONGITUD,e.longitud)
                    .withValue(ContratoPedidos.PedidosColumnas.ESTATUS,e.estatus)
                    .withValue(ContratoPedidos.PedidosColumnas.ESTATUS_REG,e.estatus_reg)
                    .withValue(ContratoPedidos.PedidosColumnas.QTY_ORIGINAL,e.qty_original)
                    .withValue(ContratoPedidos.PedidosColumnas.ID_PEDIDO,e.id_pedido)
                    .withValue(ContratoPedidos.PedidosColumnas.REG_ANULADO,e.reg_anulado)
                    .withValue(ContratoPedidos.PedidosColumnas.ESTATUS_FINAL,e.estatus_final)
                    .withValue(ContratoPedidos.PedidosColumnas.NO_PEDIDO_CMF,e.no_pedido_cmf)
                    .withValue(ContratoPedidos.PedidosColumnas.USUARIO,e.usuario)
                    .withValue(ContratoPedidos.PedidosColumnas.FECHA_SISTEMA,e.fecha_sistema)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts>0 ||
                syncResult.stats.numUpdates>0 ||
                syncResult.stats.numDeletes>0){
            Log.i(TAG, "Aplicando operaciones Pedidos...");
            try {
                resolver.applyBatch(ContratoPedidos.AUTORIDAD,ops);
            }catch (RemoteException | OperationApplicationException e){
                e.printStackTrace();
            }
            resolver.notifyChange(ContratoPedidos.CONTENT_URI_PED,null,false);

            Log.i(TAG, "Sincronización Pedidos finalizada.");
            final boolean sync = true;
        }else {
            Log.i(TAG, "No se requiere sincronización de Pedidos");
        }

    }
}