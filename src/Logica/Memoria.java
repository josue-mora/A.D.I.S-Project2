package Logica;

import Objetos.Usuario;
import java.awt.Image;
import java.awt.Toolkit;

//Esta clase se encarga de guardar variables estaticas en memoria para ser
//utilizadas en el proyecto.
public class Memoria {

    //Variable que contiene el nombre de la app.
    public static String app_name = "ADIS G2 VERSION 1.0";
    //Variable que contiene la contraseña para desencriptar la data.
    public static String DBKeyPassword = "ADISPROJECTULACIT2019";
    //Variable que contiene el nombre de la base de datos para ser cargada.
    public static String database_name = "ADIS_DB_V6.db";
    //Objeto de tipo Usuario que guarda la sesion del usuario logeado en ese momento.
    public static Usuario usuario_actual = new Usuario();
    //Instancia de SQLite para hacer las consultas a la base de datos
    public static SQLite sql_lite_query = new SQLite(database_name);
    
    //Retorna una imagen. La ruta default es el icono del programa.
    //Este metodo se utiliza para establecer el icono en la barra de tareas y estado.
    public static Image getIconImage() {
        return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("IMG/bus_logo_small.png"));
    }

}
