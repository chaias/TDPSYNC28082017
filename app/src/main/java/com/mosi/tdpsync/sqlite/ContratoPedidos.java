package com.mosi.tdpsync.sqlite;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.UUID;

/**
 * Clase que establece los nombres a usar en la base de datos
 */
public class ContratoPedidos {


    interface ColumnasCabeceraPedido {
        String ID = "id";
        String FECHA = "fecha";
        String ID_CLIENTE = "id_cliente";
        String ID_FORMA_PAGO = "id_forma_pago";
    }

    interface ColumnasDetallePedido {
        String ID_CABECERA_PEDIDO = "id_cabecera_pedido";
        String SECUENCIA = "secuencia";
        String ID_PRODUCTO = "id_producto";
        String CANTIDAD = "cantidad";
        String PRECIO = "precio";
    }

    interface ColumnasProducto {
        String ID = "id";
        String NOMBRE = "nombre";
        String PRECIO = "precio";
        String EXISTENCIAS = "existencias";
    }

    interface ColumnasCliente {
        String ID = "id";
        String NOMBRES = "nombres";
        String APELLIDOS = "apellidos";
        String TELEFONO = "telefono";
    }

    interface ColumnasFormaPago {
        String ID = "id";
        String NOMBRE = "nombre";
    }

    interface ColumnasUsuarios  {
        String ID = "id";
        String NOMBRE = "nombre";
        String PASSWORD = "password";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";
    }

    interface ColumnasCiatab {
        String ID = "id";
        String CIACOD = "ciacod";
        String CIANOMBRE = "cianombre";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";
    }

    interface ColumnasCustab {
        String ID = "id";
        String CUSCIA = "cuscia";
        String CUSCOD = "cuscod";
        String CUSNAME = "cusname";
        String CUSDIR = "cusdir";
        String CUSTEL = "custel";
        String CUSNIT = "cusnit";
        String CUSLIMCRE = "cuslimcre";
        String CUSDIAVEN = "cusdiaven";
        String CUSMAIL = "cusmail";
        String CUSLATITUD = "cuslatitud";
        String CUSLONGITUD = "cuslongitud";
        String CUSCAT = "cuscat";
        String CUSRUTA = "cusruta";
        String CUSIVA = "cusiva";
        String CUSBONI = "cusboni";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";
    }

    interface ColumnasPedidos {
        String ID = "id";
        String COMPANIA  = "compania";
        String CLIENTE = "cliente";
        String RUTA = "ruta";
        String LUGAR_ENTREGA = "lugar_entrega";
        String PRODUCTO = "producto";
        String PRECIO_PT = "precio_pt";
        String TIPO_PEDIDO = "tipo_pedido";
        String NO_PED_MOVIL = "no_ped_movil";
        String FECHA = "fecha";
        String UNIDADES_VTA = "unidades_vta";
        String UNIDADES_BONI= "unidades_boni";
        String LATITUD = "latitud";
        String LONGITUD = "longitud";
        String ESTATUS = "estatus";
        String ESTATUS_REG = "estatus_reg";
        String QTY_ORIGINAL = "qty_original";
        String ID_PEDIDO = "id_pedido";
        String REG_ANULADO = "reg_anulado";
        String ESTATUS_FINAL = "estatus_final";
        String NO_PEDIDO_CMF = "no_pedido_cmf";
        String USUARIO = "usuario";
        String FECHA_SISTEMA = "fecha_sistema";
        String ID_REMOTA = "id_Remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCION = "pendiente_insercion";
    }

    interface ColumnasInvptdtab {
        String ID = "id";
        String INVPTDCIA = "invptdcia";
        String INVPTDCOD = "invptdcod";
        String INVPTDDISPI = "invptddispi";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";
    }

    interface ColumnasPrecio {
        String ID = "id";
        String PR1CIA = "pr1cia";
        String PR1COD = "pr1cod";
        String PR1CAT = "pr1cat";
        String PR1FEC = "pr1fec";
        String PR1PREC = "pr1prec";
        String PR1DESC = "pr1desc";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";

    }

    interface ColumnasInvptmtab {
        String ID = "id";
        String INVPTMCIA = "invptmcia";
        String INVPTMCOD = "invptmcod";
        String INVPTMDESC = "invptmdesc";
        String INVPTMMED = "invptmmed";
        String INVPTMEMP = "invptmemp";
        String INVPTMIVA = "invptmiva";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";

    }

    interface ColumnasTalonarios {
        String ID = "_ID";
        String TALCIA = "talcia";
        String TALCOD = "talcod";
        String TALDES = "taldes";
        String TALCOR = "talcor";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";
    }

    interface ColumnasTipos {
        String ID = "id";
        String DOCCIA = "doccia";
        String DOCCOD = "doccod";
        String DOCNAME = "docname";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";
    }

    interface ColumnasBonificaciones {
        String ID = "id";
        String PR5CIA = "tr5cia";
        String PR5COD = "tr5cod";
        String PR5FECINICIAL = "tr5fecinicial";
        String PR5FECFINAL = "tr5fecfinal";
        String PR5RANGO1 = "tr5rango1";
        String PR5RANGO2 = "tr5rango2";
        String PR5CNT = "tr5cnt";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";

    }

    interface ColumnasGastos{
        String ID = "id";
        String MONTO = "monto";
        String ETIQUETA = "etiqueta";
        String FECHA = "fecha";
        String DESCRIPCION = "descripcion";
        String ID_REMOTA = "id_remota";
        String ESTADO = "estado";
        String PENDIENTE_INSERCCION = "pendiente_inserccion";

    }

    // [URIS]
    public static final String AUTORIDAD = "com.mosi.tdpsync";

    public static final Uri URI_BASE = Uri.parse("content://" + AUTORIDAD);

    private static final String RUTA_CABECERAS_PEDIDOS = "cabeceras_pedidos";
    private static final String RUTA_DETALLES_PEDIDO = "detalles_pedido";
    private static final String RUTA_PRODUCTOS = "productos";
    private static final String RUTA_CLIENTES = "clientes";
    private static final String RUTA_FORMAS_PAGO = "formas_pago";
    //Tablas  asignadas por Isaias A.
    public static final String RUTA_USUARIOS = "usuarios";
    private static final String RUTA_CIATAB = "ciatab";
    private static final String RUTA_CUSTAB = "custab";
    private static final String RUTA_PEDIDOS = "pedido_movil";
    private static final String RUTA_INVPTDTAB = "invptdtab";
    private static final String RUTA_INVPTMTAB = "invptmtab";
    private static final String RUTA_PRECIOS = "pr1tab";
    private static final String RUTA_BONIFICACIONES = "tr5tab";
    private static final String RUTA_TALONARIOS = "talonarios";
    private static final String RUTA_TIPOS = "tiptab";
    private static final String RUTA_GASTO = "gasto";

   //Codigo de sincronizacion


    public static final String GASTO = "gasto";


    public final static String SINGLE_MIME =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + GASTO;

    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + GASTO;

    public final static Uri CONTENT_URI_GASTO =
            Uri.parse("content://" + AUTORIDAD + "/" + GASTO);

    public static UriMatcher uriMatcher;

    public static final int ALLROWS = 1;
    public static final int SINGLE_ROW = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,GASTO,ALLROWS);
        uriMatcher.addURI(AUTORIDAD,GASTO+"/#",SINGLE_ROW);
    }

    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;

    // [TIPOS_MIME]
    public static final String BASE_CONTENIDOS = "pedidos.";

    public static final String TIPO_CONTENIDO = "vnd.android.cursor.dir/vnd."
            + BASE_CONTENIDOS;

    public static final String TIPO_CONTENIDO_ITEM = "vnd.android.cursor.item/vnd."
            + BASE_CONTENIDOS;

    public static String generarMime(String id) {
        if (id != null) {
            return TIPO_CONTENIDO + id;
        } else {
            return null;
        }
    }

    public static String generarMimeItem(String id) {
        if (id != null) {
            return TIPO_CONTENIDO_ITEM + id;
        } else {
            return null;
        }
    }
    // [/TIPOS_MIME]

    public static class CabecerasPedido implements ColumnasCabeceraPedido {

        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_CABECERAS_PEDIDOS).build();

        public static final String PARAMETRO_FILTRO = "filtro";
        public static final String FILTRO_CLIENTE = "cliente";
        public static final String FILTRO_TOTAL = "total";
        public static final String FILTRO_FECHA = "fecha";

        public static String obtenerIdCabeceraPedido(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri crearUriCabeceraPedido(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static Uri crearUriParaDetalles(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).appendPath("detalles").build();
        }

        public static boolean tieneFiltro(Uri uri) {
            return uri != null && uri.getQueryParameter(PARAMETRO_FILTRO) != null;
        }

        public static String generarIdCabeceraPedido() {
            return "CP-" + UUID.randomUUID().toString();
        }
    }

    public static class DetallesPedido implements ColumnasDetallePedido {
        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_DETALLES_PEDIDO).build();

        public static Uri crearUriDetallePedido(String id, String secuencia) {
            // Uri de la forma 'detalles_pedido/:id#:secuencia'
            return URI_CONTENIDO.buildUpon()
                    .appendPath(String.format("%s#%s", id, secuencia))
                    .build();
        }

        public static String[] obtenerIdDetalle(Uri uri) {
            return uri.getLastPathSegment().split("#");
        }
    }

    public static class Productos implements ColumnasProducto {
        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_PRODUCTOS).build();

        public static Uri crearUriProducto(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdProducto() {
            return "PRO-" + UUID.randomUUID().toString();
        }

        public static String obtenerIdProducto(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class Clientes implements ColumnasCliente {
        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_CLIENTES).build();

        public static Uri crearUriCliente(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdCliente() {
            return "CLI-" + UUID.randomUUID().toString();
        }

        public static String obtenerIdCliente(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class FormasPago implements ColumnasFormaPago {
        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_FORMAS_PAGO).build();

        public static Uri crearUriFormaPago(String id) {
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdFormaPago() {
            return "FP-" + UUID.randomUUID().toString();
        }

        public static String obtenerIdFormaPago(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Usuarios implements  ColumnasUsuarios{
        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_USUARIOS).build();


        public static Uri crearUriUsuarios(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdUsuarios(){
            return "USR-" + UUID.randomUUID().toString();
        }

        public static String ObtenerIdUsuarios(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    public static class Ciatab implements ColumnasCiatab{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_CIATAB).build();

        public static Uri crearUriCiatab(String id){
            return  URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdCiatab(){
            return "CIA-" + UUID.randomUUID().toString();
        }

        public static String ObtenerIdCiatab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Custab implements ColumnasCustab{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_CUSTAB).build();

        public static Uri crearUriCustab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdCustab(){
            return "CUS-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdCustab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Pedidos implements ColumnasPedidos{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_PEDIDOS).build();

        public static Uri crearUriPedido(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdPedido(){
            return "PED-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdPedido(Uri uri){
            return uri.getPathSegments().get(1);
        }

        private Pedidos() {
            // Sin instancias
        }
    }

    public static class Invptdtab implements ColumnasInvptdtab{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_INVPTDTAB).build();

        public static Uri crearUriInvptdtab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdInvptdtab(){
            return "INV-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdInvptdtab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Invptmtab implements ColumnasInvptmtab{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_INVPTMTAB).build();

        public static Uri crearUriInvptmtab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdInvptmtab(){
            return "INVM-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdInvptmtab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Pr1tab implements ColumnasPrecio{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_PRECIOS).build();

        public static Uri crearUriPr1tab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdPr1tab()
        {
            return "PR1-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdPr1tab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Tr5tab implements ColumnasBonificaciones{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_BONIFICACIONES).build();

        public static Uri crearUriTr5tab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdTr5tab(){
            return "TR5-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdTr5tab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Talonarios implements ColumnasTalonarios{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_TALONARIOS).build();

        public static Uri crearUriTalonarios(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdTalonarios()
        {
            return "TAL-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdTalonarios(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Tipos implements ColumnasTipos{
        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_TIPOS).build();

        public static Uri crearUriTipos(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdTipos()
        {
            return "TIP-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdTipos(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static class Columnas implements BaseColumns {

        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_GASTO).build();

        public static Uri crearUriGasto(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdGasto(){
            return "GST-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdGasto(Uri uri){
            return uri.getPathSegments().get(1);
        }

        private Columnas() {
            // Sin instancias
        }

        public final static String MONTO = "monto";
        public final static String ETIQUETA = "etiqueta";
        public final static String FECHA = "fecha";
        public final static String DESCRIPCION = "descripcion";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_Remota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

    }

    public static class Gasto implements  ColumnasGastos{
        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_GASTO).build();

        public static Uri crearUriGasto(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdGasto(){
            return "GST-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdGasto(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE USUARIOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String USUARIOS = "usuarios";

    public static final int ALLROWS_USR = 1;
    public static final int SINGLE_ROW_USR = 2;

    public final static String SINGLE_MIME_USUARIOS =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + USUARIOS;

    public final static String MULTIPLE_MIME_USUARIOS =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + USUARIOS;

    public final static Uri CONTENT_URI_USUARIOS =
            Uri.parse("content://" + AUTORIDAD + "/" + USUARIOS);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,USUARIOS,ALLROWS_USR);
        uriMatcher.addURI(AUTORIDAD,USUARIOS+"/#",SINGLE_ROW_USR);
    }

    public static class UsuarioColumnas implements BaseColumns {

        public static final Uri URI_CONTENIDO =
                URI_BASE.buildUpon().appendPath(RUTA_USUARIOS).build();

        private UsuarioColumnas() {
            // Sin instancias
        }

        public final static String USUARIO = "nombre";
        public final static String PASSWORD = "password";

        public static final String ESTADO = "estado";
        public static final String ID_REMOTA = "id_Remota";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

        public static Uri crearUriUsuarios(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdUsuarios(){
            return "USR-" + UUID.randomUUID().toString();
        }

        public static String ObtenerIdUsuarios(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CIATAB\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static final String CIATAB = "ciatab";

    public static final int ALLROWS_CIA = 1;
    public static final int SINGLE_ROW_CIA = 2;

    public final static String SINGLE_MIME_CIA =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + CIATAB;

    public final static String MULTIPLE_MIME_CIA =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + CIATAB;

    public final static Uri CONTENT_URI_CIA =
            Uri.parse("content://" + AUTORIDAD + "/" + CIATAB);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,CIATAB,ALLROWS_CIA);
        uriMatcher.addURI(AUTORIDAD,CIATAB+"/#",SINGLE_ROW_CIA);
    }

    public static class CiatabColumnas implements BaseColumns {

        private CiatabColumnas() {
            // Sin instancias
        }

        public final static String CIACOD = "ciacod";
        public final static String CIANOMBRE = "cianombre";
        public final static String ID_REMOTA = "id_remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_CIATAB).build();

        public static Uri crearUriCiatab(String id){
            return  URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdCiatab(){
            return "CIA-" + UUID.randomUUID().toString();
        }

        public static String ObtenerIdCiatab(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTDTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String INVPTDTAB = "invptdtab";

    public static final int ALLROWS_INVPTD = 1;
    public static final int SINGLE_ROW_INVPTD = 2;

    public final static String SINGLE_MIME_INVPTD =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + INVPTDTAB;

    public final static String MULTIPLE_MIME_INVPTD =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + INVPTDTAB;

    public final static Uri CONTENT_URI_INVPTD =
            Uri.parse("content://" + AUTORIDAD + "/" + INVPTDTAB);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,INVPTDTAB,ALLROWS_INVPTD);
        uriMatcher.addURI(AUTORIDAD,INVPTDTAB+"/#",SINGLE_ROW_INVPTD);
    }

    public static class InvptdtabColumnas implements BaseColumns {

        private InvptdtabColumnas() {
            // Sin instancias
        }

        public final static String INVPTDCIA = "invptdcia";
        public final static String INVPTDCOD = "invptdcod";
        public final static String INVPTDDISPI = "invptddispi";
        public final static String ID_REMOTA = "id_remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_INVPTDTAB).build();

        public static Uri crearUriInvptdtab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdInvptdtab(){
            return "INV-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdInvptdtab(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CUSCAT\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String CUSTAB = "custab";

    public static final int ALLROWS_CUS = 1;
    public static final int SINGLE_ROW_CUS= 2;

    public final static String SINGLE_MIME_CUS =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + CUSTAB;

    public final static String MULTIPLE_MIME_CUS =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + CUSTAB;

    public final static Uri CONTENT_URI_CUS =
            Uri.parse("content://" + AUTORIDAD + "/" + CUSTAB);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,CUSTAB,ALLROWS_CUS);
        uriMatcher.addURI(AUTORIDAD,CUSTAB+"/#",SINGLE_ROW_CUS);
    }

    public static class CustabColumnas implements BaseColumns {

        private CustabColumnas() {
            // Sin instancias
        }

        public final static String CUSCIA = "cuscia";
        public final static String CUSCOD = "cuscod";
        public final static String CUSNAME = "cusname";
        public final static String CUSDIR = "cusdir";
        public final static String CUSTEL = "custel";
        public final static String CUSNIT = "cusnit";
        public final static String CUSLIMCRE = "cuslimcre";
        public final static String CUSDIAVEN = "cusdiaven";
        public final static String CUSMAIL = "cusmail";
        public final static String CUSLATITUD = "cuslatitud";
        public final static String CUSLONGITUD = "cuslongitud";
        public final static String CUSCAT = "cuscat";
        public final static String CUSRUTA = "cusruta";
        public final static String CUSIVA = "cusiva";
        public final static String CUSBONI = "cusboni";
        public final static String ID_REMOTA = "id_remota";
        public final static  String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_CUSTAB).build();

        public static Uri crearUriCustab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdCustab(){
            return "CUS-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdCustab(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TALONARIOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String TALONARIOS = "talonarios";

    public static final int ALLROWS_TAL = 1;
    public static final int SINGLE_ROW_TAL = 2;

    public final static String SINGLE_MIME_TAL =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + TALONARIOS;

    public final static String MULTIPLE_MIME_TAL =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + TALONARIOS;

    public final static Uri CONTENT_URI_TAL =
            Uri.parse("content://" + AUTORIDAD + "/" + TALONARIOS);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,TALONARIOS,ALLROWS_TAL);
        uriMatcher.addURI(AUTORIDAD,TALONARIOS+"/#",SINGLE_ROW_TAL);
    }

    public static class TalonariosColumnas implements BaseColumns {

        private TalonariosColumnas() {
            // Sin instancias
        }

        public final static String TALCIA = "talcia";
        public final static String TALCOD = "talcod";
        public final static String TALDES = "taldes";
        public final static String TALCOR = "talcor";
        public final static String ID_REMOTA = "id_remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_TALONARIOS).build();

        public static Uri crearUriTalonarios(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdTalonarios(){
            return "TAL-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdTalonarios(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTMTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String INVPTMTAB = "invptmtab";

    public static final int ALLROWS_INVPTM = 1;
    public static final int SINGLE_ROW_INVPTM = 2;

    public final static String SINGLE_MIME_INVPTM =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + INVPTMTAB;

    public final static String MULTIPLE_MIME_INVPTM =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + INVPTMTAB;

    public final static Uri CONTENT_URI_INVPTM =
            Uri.parse("content://" + AUTORIDAD + "/" + INVPTMTAB);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,INVPTMTAB,ALLROWS_INVPTM);
        uriMatcher.addURI(AUTORIDAD,INVPTMTAB+"/#",SINGLE_ROW_INVPTM);
    }

    public static class InvptmtabColumnas implements BaseColumns {

        private InvptmtabColumnas() {
            // Sin instancias
        }

        public final static String INVPTMCIA = "invptmcia";
        public final static String INVPTMCOD = "invptmcod";
        public final static String INVPTMDESC = "invptmdesc";
        public final static String INVPTMMED = "invptmmed";
        public final static String INVPTMEMP = "invptmemp";
        public final static String INVPTMIVA = "invptmiva";
        public final static String ID_REMOTA = "id_remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_INVPTMTAB).build();

        public static Uri crearUriInvptmtab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdInvptmtab(){
            return "INVM-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdInvptmtab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PR1TAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String PR1TAB = "pr1tab";

    public static final int ALLROWS_PR1 = 1;
    public static final int SINGLE_ROW_PR1 = 2;

    public final static String SINGLE_MIME_PR1 =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + PR1TAB;

    public final static String MULTIPLE_MIME_PR1 =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + PR1TAB;

    public final static Uri CONTENT_URI_PR1 =
            Uri.parse("content://" + AUTORIDAD + "/" + PR1TAB);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,PR1TAB,ALLROWS_PR1);
        uriMatcher.addURI(AUTORIDAD,PR1TAB+"/#",SINGLE_ROW_PR1);
    }

    public static class Pr1tabColumnas implements BaseColumns {

        private Pr1tabColumnas() {
            // Sin instancias
        }

        public final static String PR1CIA = "pr1cia";
        public final static String PR1COD = "pr1cod";
        public final static String PR1CAT = "pr1cat";
        public final static String PR1FEC = "pr1fec";
        public final static String PR1PREC = "pr1prec";
        public final static String PR1DESC = "pr1desc";
        public final static String ID_REMOTA = "id_remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_PRECIOS).build();

        public static Uri crearUriPr1tab(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdPr1tab()
        {
            return "PR1-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdPr1tab(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TIPTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String TIPTAB = "tiptab";

    public static final int ALLROWS_TIP = 1;
    public static final int SINGLE_ROW_TIP = 2;

    public final static String SINGLE_MIME_TIP =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + TIPTAB;

    public final static String MULTIPLE_MIME_TIP =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + TIPTAB;

    public final static Uri CONTENT_URI_TIP =
            Uri.parse("content://" + AUTORIDAD + "/" + TIPTAB);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,TIPTAB,ALLROWS_TIP);
        uriMatcher.addURI(AUTORIDAD,TIPTAB+"/#",SINGLE_ROW_TIP);
    }

    public static class TiptabColumnas implements BaseColumns {

        private TiptabColumnas() {
            // Sin instancias
        }

        public final static String DOCCIA = "doccia";
        public final static String DOCCOD = "doccod";
        public final static String DOCNAME = "docname";
        public final static String ID_REMOTA = "id_remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_inserccion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_TIPOS).build();

        public static Uri crearUriTipos(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdTipos()
        {
            return "TIP-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdTipos(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    ////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PEDIDOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String PEDIDOS = "pedido_movil";

    public static final int ALLROWS_PED = 1;
    public static final int SINGLE_ROW_PED = 2;

    public final static String SINGLE_MIME_PED =
            "vnd.android.cursor.item/vnd." + AUTORIDAD + PEDIDOS;

    public final static String MULTIPLE_MIME_PED =
            "vnd.android.cursor.dir/vnd." + AUTORIDAD + PEDIDOS;

    public final static Uri CONTENT_URI_PED =
            Uri.parse("content://" + AUTORIDAD + "/" + PEDIDOS);

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTORIDAD,PEDIDOS,ALLROWS_PED);
        uriMatcher.addURI(AUTORIDAD,PEDIDOS+"/#",SINGLE_ROW_PED);
    }

    public static class PedidosColumnas implements BaseColumns {

        private PedidosColumnas() {
            // Sin instancias
        }

        public final static String COMPANIA  = "compania";
        public final static String CLIENTE = "cliente";
        public final static String RUTA = "ruta";
        public final static String LUGAR_ENTREGA = "lugar_entrega";
        public final static String PRODUCTO = "producto";
        public final static String PRECIO_PT = "precio_pt";
        public final static String TIPO_PEDIDO = "tipo_pedido";
        public final static String NO_PED_MOVIL = "no_ped_movil";
        public final static String FECHA = "fecha";
        public final static String UNIDADES_VTA = "unidades_vta";
        public final static String UNIDADES_BONI= "unidades_boni";
        public final static String LATITUD = "latitud";
        public final static String LONGITUD = "longitud";
        public final static String ESTATUS = "estatus";
        public final static String ESTATUS_REG = "estatus_reg";
        public final static String QTY_ORIGINAL = "qty_original";
        public final static String ID_PEDIDO = "id_pedido";
        public final static String REG_ANULADO = "reg_anulado";
        public final static String ESTATUS_FINAL = "estatus_final";
        public final static String NO_PEDIDO_CMF = "no_pedido_cmf";
        public final static String USUARIO = "usuario";
        public final static String FECHA_SISTEMA = "fecha_sistema";
        public final static String ID_REMOTA = "id_Remota";
        public final static String ESTADO = "estado";
        public final static String PENDIENTE_INSERCION = "pendiente_insercion";

        public static final Uri URI_CONTENIDO=
                URI_BASE.buildUpon().appendPath(RUTA_PEDIDOS).build();

        public static Uri crearUriPedido(String id){
            return URI_CONTENIDO.buildUpon().appendPath(id).build();
        }

        public static String generarIdPedido(){
            return "PED-"+ UUID.randomUUID().toString();
        }

        public static String ObtenerIdPedido(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }


}
