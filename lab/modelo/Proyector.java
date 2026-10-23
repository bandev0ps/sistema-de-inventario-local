package lab.modelo;

public class Proyector extends Equipo implements mantenible {
    public int resX, resY, lumens; 
    public double horasLampara;

    public Proyector(int id, String cu, String ub, int x, int y, int l, int lu){
        super(id, cu, ub);
        resX = x; resY = y;
        horasLampara = l;
        lumens = lu;

        super.nombre = "Proyector";
    }

    public void marcarOperativo(){
        estado = ESTADO.DISPONIBLE;
        horasLampara = 0;
    }

    public void enviarAMantenimiento(String motivo){
        System.out.println(motivo);
        estado = ESTADO.EN_MANTENIMIENTO;
    }

    public String getResumenTecnico(){
        return  resX + "x" + resY +" | "+lumens+ "lm | " +horasLampara 
        +" h lámpara | " + super.ubicacion;
    }

    public String getTipo(){
        return "Proyector";
    }

}
