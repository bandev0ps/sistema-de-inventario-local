package lab.prestamos;

import lab.modelo.*;

public class Prestamo {
    public long cuenta;
    public String nombre; 
    public Equipo equipoPrestado;
    private boolean activo = true;

    public Prestamo(long c, String n, Equipo e){
        cuenta = c; nombre = n; equipoPrestado = e;
    }

    public void setEstado(boolean b){
        activo = b;
    }

    public boolean getEstado(){
        return activo;
    }

    public String mostrarDatos(){
        return "CODIGO EQUIPO: %s CUENTA: %d NOMBRE: %s".formatted(
            equipoPrestado.CODIGOINV, cuenta, nombre);
    }


}
