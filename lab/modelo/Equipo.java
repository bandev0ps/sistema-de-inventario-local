package lab.modelo;

public abstract class Equipo {
    public int ID;
    public String CODIGOINV;
    public String ubicacion;
    public ESTADO estado;
    public String nombre;

    public Equipo(int i, String cod, String u){
        ID = i;
        CODIGOINV = cod;
        ubicacion = u;
    }

    public int getId(){
        return ID;
    }


    public abstract String getResumenTecnico();
    public abstract String getTipo();

}
