package lab.excepciones;

public class EquipoNotFoundException extends Exception{

    String mensaje;

    public EquipoNotFoundException(String mensaje){
        super(mensaje);
        this.mensaje = mensaje;
    }

    public EquipoNotFoundException(){
        mensaje  ="";
    }

    @Override
    public String toString() {
        return mensaje + super.toString();
    }
}
