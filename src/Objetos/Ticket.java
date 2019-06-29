package Objetos;

public class Ticket {

    //Las variables se ajustan a todas sus equivalentes a las de la base de 
    //datos.
    //DB_ID es equivalente a TicketID dentro de la base de datos.
    private int DB_ID;
    //Variable para saber a quien pertenece este ticket
    private int ClienteID;
    //Variable para saber a que ruta pertenece este ticket.
    private int RutaID;
    //Codigo del ticket. Se refiere al codigo unico que genera el sistema.
    private String Codigo;
    private String Fecha_caducidad;
    //Este boolean establece si el ticket ya ha sido utilizado o no.
    private boolean utilizado;

    //Constructor vacio
    public Ticket() {
    }

    //Constructor para establecer las variables
    //Seccion de get y set
    public int getDB_ID() {
        return DB_ID;
    }

    public void setDB_ID(int DB_ID) {
        this.DB_ID = DB_ID;
    }

    public int getRutaID() {
        return RutaID;
    }

    public void setRutaID(int RutaID) {
        this.RutaID = RutaID;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

    public String getFecha_caducidad() {
        return Fecha_caducidad;
    }

    public void setFecha_caducidad(String Fecha_caducidad) {
        this.Fecha_caducidad = Fecha_caducidad;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }

}