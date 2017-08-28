package com.mosi.tdpsync.web;

/**
 * Created by Isaias on 23/11/2016.
 */
public class Usuario {

    public  String id;
    public  String nombre;
    public  String password;

    public Usuario(String id, String nombre, String password){
        System.out.println("id_usuario "+ id);

        this.id = id;
        this.nombre = nombre;
        this.password= password;

    }

}
