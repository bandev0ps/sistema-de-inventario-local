package lab.excepciones;

public class ReglaPrestamoException extends Exception {

    String mensaje;
    public ReglaPrestamoException(String mensaje){
        super(mensaje);
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return mensaje + super.toString();
    }
}
