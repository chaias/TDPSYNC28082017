package com.mosi.tdpsync.utils;

/**
 * Constantes
 */
public class Constantes {

    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta característica.
     */
public String dd;

    /**
     * Dirección IP de genymotion o AVD
     * http://mosi.com.gt/isaias/Toma_De_Pedido/web/obtener_gastos.php
     */
    private static final String IP = "http://mosi.com.gt";

    /**
     * URLs del Web Service
     */
    public static  String GET_URL = IP  + "/isaias/Toma_De_Pedido/web/obtener_gastos.php";
    public static  String INSERT_URL = IP  + "/isaias/Toma_De_Pedido/web/insertar_gasto.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_GASTO = "idGasto";
    public static final String ESTADO = "estado";
    public static final String GASTOS = "gastos";
    public static final String MENSAJE = "mensaje";

    public static final String SUCCESS = "1";
    public static final String FAILED = "2";

    /**
     * Tipo de cuenta para la sincronización
     */
    public static final String ACCOUNT_TYPE = "com.mosi.tdpsync.account";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE USUARIOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_USR = IP  + "/isaias/Toma_De_Pedido/web/obtener_usuarios.php";
    public static final String INSERT_URL_USR = IP  + "/isaias/Toma_De_Pedido/web/insertar_usuarios.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_USUARIO = "id";
    public static final String USUARIOS = "usuarios";


    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CIATAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_CIA = IP  + "/isaias/Toma_De_Pedido/web/obtener_ciatab.php";
    public static final String INSERT_URL_CIA = IP  + "/isaias/Toma_De_Pedido/web/insertar_ciatab.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_CIATAB = "id";
    public static final String CIATAB = "ciatab";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTDTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_INVPTD = IP  + "/isaias/Toma_De_Pedido/web/obtener_invptdtab.php";
    public static final String INSERT_URL_INVPTD = IP  + "/isaias/Toma_De_Pedido/web/insertar_invptdtab.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_INVPTDTAB = "id";
    public static final String INVPTDTAB = "invptdtab";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE CUSTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_CUS = IP  + "/isaias/Toma_De_Pedido/web/obtener_custab.php";
    public static final String INSERT_URL_CUS = IP  + "/isaias/Toma_De_Pedido/web/insertar_custab.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_CUSTAB = "id";
    public static final String CUSTAB = "custab";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TALONARIOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_TAL = IP  + "/isaias/Toma_De_Pedido/web/obtener_talonarios.php";
    public static final String INSERT_URL_TAL = IP  + "/isaias/Toma_De_Pedido/web/insertar_talonarios.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_TALONARIOS = "id";
    public static final String TALONARIOS = "talonarios";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE INVPTMTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_INVPTM = IP  + "/isaias/Toma_De_Pedido/web/obtener_invptmtab.php";
    public static final String INSERT_URL_INVPTM = IP  + "/isaias/Toma_De_Pedido/web/insertar_invptmtab.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_INVPTMTAB = "id";
    public static final String INVPTMTAB = "invptmtab";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE TIPTAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_TIP = IP  + "/isaias/Toma_De_Pedido/web/obtener_tiptab.php";
    public static final String INSERT_URL_TIP = IP  + "/isaias/Toma_De_Pedido/web/insertar_tiptab.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_TIPTAB = "id";
    public static final String TIPTAB = "tiptab";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PR1TAB\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_PR1 = IP  + "/isaias/Toma_De_Pedido/web/obtener_pr1tab.php";
    public static final String INSERT_URL_PR1 = IP  + "/isaias/Toma_De_Pedido/web/insertar_pr1tab.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_PR1TAB = "id";
    public static final String PR1TAB = "pr1tab";

    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE PEDIDOS\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_PED = IP  + "/isaias/Toma_De_Pedido/web/obtener_pedido.php";
    public static final String INSERT_URL_PED = IP  + "/isaias/Toma_De_Pedido/web/insertar_pedido.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_PEDIDOS = "id";
    public static final String PEDIDOS = "pedido";


    /////////////////////CODIGO DE SINCRONIZACION PARA LA TABLA DE IMAGENES\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static String GET_URL_IMG = IP  + "/isaias/Toma_De_Pedido/web/obtener_imagenes.php";
    public static final String INSERT_URL_IMG = IP  + "/isaias/Toma_De_Pedido/web/insertar_imagenes.php";

    /**
     * Campos de las respuestas Json
     */
    public static final String ID_IMAGENES= "id";
    public static final String IMAGENES = "imagenes";

}
