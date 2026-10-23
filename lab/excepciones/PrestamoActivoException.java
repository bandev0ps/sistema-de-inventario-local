package lab.excepciones;

public class PrestamoActivoException extends Exception{
    String mensaje;
    public PrestamoActivoException(String mensaje){
        super(mensaje);
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return mensaje + super.toString();
    }
}
