package lab.modelo;

public class Portatil extends Equipo implements mantenible{
    public String marca;
    public int numSerie;
    public int almacenamiento;

    public Portatil(int id, String cu, String ub, String m, int n, int a){
        super(id, cu, ub);
        marca = m; numSerie=n; almacenamiento=a;

        super.nombre = "Portatil";
    }

    public String getResumenTecnico(){
        return marca+" | "+almacenamiento+" GB | " + super.ubicacion;
    }

    public void enviarAMantenimiento(String motivo){
        System.out.println(motivo);
        estado = ESTADO.EN_MANTENIMIENTO;
    }

    public void marcarOperativo(){
        estado = ESTADO.DISPONIBLE;
    }
    
    public String getTipo(){
        return "Portatil";
    }

}
