package lab.excepciones;

public class EstadoOperativoInvalidoException extends Exception{
    String mensaje;
    public EstadoOperativoInvalidoException(String mensaje){
        super(mensaje);
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return mensaje + super.toString();
    }
}
