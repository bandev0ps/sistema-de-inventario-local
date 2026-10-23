package lab.modelo;

public class Multimetro extends Equipo implements calibrable{
    public int rango, precision;
    public String tipo; 
    public boolean calibrado, an;


    public Multimetro(int id, String cu, String ub, String t, boolean c, int r, int p, boolean a){
        super(id, cu, ub);
        tipo = t; calibrado = c; 
        rango = r; precision = p;
        an = a;

        super.nombre = "Multimetro";
    }

    public String getResumenTecnico(){
        return tipo +" | "+(an ? "Analogo":"Digital")+" | " + rango +" V | calibrado: " + (calibrado ? "si": "no");
    }

    public boolean necesitaCalibracion() {
        return !calibrado;
    }

    public void marcarCalibrado(){
       
        estado = ESTADO.DISPONIBLE;
        calibrado = true;
    }

    public String getTipo(){
        return "Multimetro";
    }
}
