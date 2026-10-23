package lab.servicios;

import java.util.ArrayList;

import lab.excepciones.*;
import lab.modelo.*;
import lab.prestamos.*;

public class GestorLaboratorio {
    final int CANT_MAX = 10;

    int cantEquipo = 0; 
    int cantPrestamos=0;
    public int prestamosActivos =0;
    Equipo[] inventario = new Equipo[CANT_MAX];
    Prestamo[] prestamos = new Prestamo[CANT_MAX];

//METODOS DEVUELVEN LOS DATOS Q SE BUSCAN SEGUN EL PARAMETRO Y PUES ESTOS SON LOS NUEVOS

    public ArrayList<Equipo> mostrarInventario(){
        ArrayList<Equipo> devolucion = new ArrayList<>();
        for (int i = 0; i < cantEquipo; i++) {
            if (inventario[i] != null) {
                devolucion.add(inventario[i]);
            }
        }
        return devolucion;
    }

    public String getResumenLab(){
        int tot = ContarFiltro(1) + ContarFiltro(2) + ContarFiltro(3);
        int tot2 = obtenerCantPrestamosActivos();

        String DATA = """
            Por Tipo: 
                Portatil: %d
                Proyector: %d
                Multimetro: %d

            Por Estado:
                DISPONIBLE: %d
                PRESTADO: %d
                EN MANTENIMIENTO: %d
                FUERA DE SERVICIO: %d

            Total de equipos: %d
            Prestamos Activos: %d""".formatted(ContarFiltro(1),
            ContarFiltro(2), ContarFiltro(3),
            ContarFiltro(ESTADO.DISPONIBLE), ContarFiltro(ESTADO.PRESTADO), 
            ContarFiltro(ESTADO.EN_MANTENIMIENTO), ContarFiltro(ESTADO.FUERA_DE_SERVICIO),
            tot, tot2
        );

        return DATA;
    }

//METODOS DEL PROGRAMA INICIAL

    public Equipo ValidarDato(int id){
        for (int i = 0; i < cantEquipo; i++) {
            if (inventario[i].getId() == id) {
                return inventario[i];
            }
        }
        return null;
    }

    public Equipo ValidarDato(String codInv){
        for (int i = 0; i < cantEquipo; i++) {
            if ((inventario[i].CODIGOINV).equals(codInv)) {
                return inventario[i];
            }
        }
        return null; //no existe
    }

    public String getTipo(int i){
        switch (i) {
            case 1:
                return "Portatil";
            case 2:
                return "Proyector";
            case 3:
                return "Multimetro";

            default:
                System.out.println("Ingrese un numero valido");
                return "";
        }
    }

    public void FiltroTipos(int i){
        switch (i) {
            case 1:
                for (Equipo e : inventario) {
                    if(e instanceof Portatil){
                        System.out.println(e.getResumenTecnico() + " | "  + e.estado);
                    }
                }
                break;
            case 2:
                for (Equipo e : inventario) {
                    if(e instanceof Proyector){
                        System.out.println(e.getResumenTecnico() + " | "  + e.estado);
                    }
                }
                break;
            case 3: 
                for (Equipo e : inventario) {
                    if(e instanceof Multimetro){
                        System.out.println(e.getResumenTecnico() + " | "  + e.estado);
                    }
                }
                break;
            default:
                System.out.println("Ingrese un valor valido");
                break;
        }
    }
    //LOGICA DE ESTADOS 
    public void FiltroEstado(ESTADO e){
        for (int i = 0; i < cantEquipo; i++) {
            if(inventario[i] == null){continue;}
            if ((inventario[i].estado).equals(e)) {
                System.out.println(inventario[i].getResumenTecnico());  
            }
        }
    }

    public int ContarFiltro(ESTADO e){
        int cont = 0;
        for (int i = 0; i < cantEquipo; i++) {
            if(inventario[i] == null){continue;}
            if ((inventario[i].estado).equals(e)) {
                cont++; 
            }
        }

        return cont;
    }

    public int ContarFiltro(int i){
        int cont=0;
        switch (i) {
            case 1:
                for (Equipo e : inventario) {
                    if(e instanceof Portatil){
                        cont++;
                    }
                }
                break;
            case 2:
                for (Equipo e : inventario) {
                    if(e instanceof Proyector){
                        cont++;
                    }
                }
                break;
            case 3: 
                for (Equipo e : inventario) {
                    if(e instanceof Multimetro){
                        cont++;
                    }
                }
                break;
            default:
                break;
        }

        return cont;
    }

    public void CambiarEstado(int id, ESTADO aCambiar) throws EquipoNotFoundException{
        Equipo objetivo;
        objetivo = ValidarDato(id);
        if(objetivo==null){throw new EquipoNotFoundException("Id o codigo inexistente");}

        objetivo.estado = aCambiar;
    }

    public void CambiarEstado(String id, ESTADO aCambiar) throws EquipoNotFoundException{
        Equipo objetivo = null;

        objetivo = ValidarDato(id);
        if(objetivo==null){throw new EquipoNotFoundException();}

        objetivo.estado = aCambiar;
    }

    public void cambiarEquipo(String idBusqueda, ESTADO tarEstado) throws EquipoNotFoundException, EstadoOperativoInvalidoException{
        Equipo objetivo;

        objetivo = ValidarDato(idBusqueda);
        if(objetivo==null){throw new EquipoNotFoundException("Codigo no valido. Intente de nuevo");}
        if (objetivo.estado == tarEstado) {
            throw new EstadoOperativoInvalidoException("Operacion no permitida en el estado actual.");}

        switch (tarEstado) {
            case ESTADO.EN_MANTENIMIENTO:
                if (objetivo.estado != ESTADO.DISPONIBLE) {
                    throw new EstadoOperativoInvalidoException("Operacion no permitida en el estado actual.");
                }

                if (objetivo instanceof mantenible) {
                    ((mantenible)objetivo).enviarAMantenimiento("");
                }
                else if (objetivo instanceof Multimetro ) {
                    ((Multimetro)objetivo).calibrado = false;
                }

                CambiarEstado(idBusqueda, ESTADO.EN_MANTENIMIENTO);

                System.out.println("Equipo con codigo [" + idBusqueda + "] enviado a MANTENIMIENTO.");
                break;

            case ESTADO.DISPONIBLE:
                if (objetivo.estado != ESTADO.EN_MANTENIMIENTO) {
                    throw new EstadoOperativoInvalidoException("Operacion no permitida en el estado actual.");
                }

                if (objetivo instanceof mantenible) {
                    ((mantenible)objetivo).marcarOperativo();
                }
                else if (objetivo instanceof Multimetro ) {
                    ((Multimetro)objetivo).marcarCalibrado();
                }
                CambiarEstado(idBusqueda, ESTADO.DISPONIBLE);
                System.out.println("Equipo con codigo [" + idBusqueda + "] ahora está DISPONIBLE.");
                break;

            default:
                throw new EstadoOperativoInvalidoException("Operacion no permitida en el estado actual.");
        }
    }

    //LOGICA DE PRESTAMOS
    public void validarPrestamo(Equipo equipoPrestado) throws ReglaPrestamoException, PrestamoActivoException{
        if(!(equipoPrestado.estado.equals(ESTADO.DISPONIBLE))){
            throw new PrestamoActivoException("El equipo no está disponible");
        }

        if(equipoPrestado instanceof Portatil){
            return;
        }

        else if(equipoPrestado instanceof Proyector){
            if(((Proyector)equipoPrestado).horasLampara < 2000){return;}
            throw new ReglaPrestamoException("Horas Lampara exceden el limite.");
        }

        else if(equipoPrestado instanceof Multimetro){
            Multimetro m = (Multimetro) equipoPrestado;
            if(m.necesitaCalibracion()){
                throw new ReglaPrestamoException("El equipo necesita calibrarse");
            }
    }
    
}
    
    public void registrarCalibracion(String idBusqueda) throws EquipoNotFoundException{
        Equipo objetivo;

        objetivo = ValidarDato(idBusqueda);
        if(objetivo==null){throw new EquipoNotFoundException("Objeto no encontrado. Intente de nuevo.");}

        if (objetivo instanceof calibrable) {
            ((calibrable)objetivo).marcarCalibrado();
            System.out.println("Objeto ["+idBusqueda + "] calibrado con exito\n");
        }else{
            throw new EquipoNotFoundException("El equipo no puede ser calibrado.");
        }
    }

    //LOGICA DE PRESTAMOS ESTO LO CAMBIE OK
    public void CrearPrestamo(String id, long numC, String nombre) throws EquipoNotFoundException, PrestamoActivoException, ReglaPrestamoException{
        Equipo aPrestar = ValidarDato(id);

        if(aPrestar == null){
            throw new EquipoNotFoundException("Id no encontrado. Intente de nuevo.");
        }

        if (aPrestar.estado != ESTADO.DISPONIBLE) {
        throw new ReglaPrestamoException("No se puede realizar el préstamo. El equipo " 
                + id + " actualmente está en estado: " + aPrestar.estado);
    }

        try {
            validarPrestamo(aPrestar);
            prestamos[cantPrestamos] = new Prestamo(numC, nombre, aPrestar);
            prestamos[cantPrestamos].setEstado(true);
            CambiarEstado(id, ESTADO.PRESTADO);
            System.out.println("Prestamo creado exitosamente.");
            cantPrestamos++;
        } catch (PrestamoActivoException | ReglaPrestamoException e) {
            throw e;
        }
        
    }

    public ArrayList<Prestamo> MostrarPrestamosActivos(){
        ArrayList<Prestamo> DATOS = new ArrayList<>();

        prestamosActivos = 0;
        for (Prestamo e : prestamos) {
            if(e==null){continue;}

            if(e.getEstado()){
                DATOS.add(e);
                prestamosActivos++;
            }
        }

        if (prestamosActivos == 0) {
            System.out.println("No hay prestamos prestamos activos en este momento");
        }

        return DATOS;
    }
    
    public void devolverEquipo(String codInv) throws EquipoNotFoundException, EstadoOperativoInvalidoException {
        Equipo equipo = ValidarDato(codInv);

        if (equipo == null) {
            throw new EquipoNotFoundException("Error: El código de inventario " + codInv + " no existe.");
        }

        if (equipo.estado != ESTADO.PRESTADO) {
            throw new EstadoOperativoInvalidoException("Error: El equipo no se encuentra en estado PRESTADO.");
        }

        for (int i = 0; i < cantPrestamos; i++) {
            if (prestamos[i] != null && prestamos[i].equipoPrestado.CODIGOINV.equals(codInv)
                    && prestamos[i].getEstado()) {

                prestamos[i].setEstado(false);
                break;
            }
        }

        equipo.estado = ESTADO.DISPONIBLE;
        System.out.println("Equipo ["+codInv+"] devuelto exitosamente y ahora está DISPONIBLE.");
    }

    public int obtenerCantPrestamosActivos() {
        int activos = 0;
        for (int i = 0; i < cantPrestamos; i++) {
            if (prestamos[i] != null && prestamos[i].getEstado()) {
                activos++;
            }
        }
        return activos;
    }
    
    //ESTOS REGISTRAN LOS NUEVOS EQUIPOS
    //portatil
    public void registrarEquipo(int id, String codigo, String ubic, String marca, int numSerie, int almacenamiento)
    throws EquipoNotFoundException{
        inventario[cantEquipo] = new Portatil(id, codigo, ubic, marca, numSerie, almacenamiento);
        cantEquipo++;
        try{
            CambiarEstado(id, ESTADO.DISPONIBLE);
        }catch(EquipoNotFoundException e){
            throw e;
        }
        System.out.println("Laptop registrada exitosamente.");
    }

    //Proyector
    public void registrarEquipo(int id, String codigo, String ubic, int x, int y, int hLampara, int lu) throws
    EquipoNotFoundException{
        inventario[cantEquipo] = new Proyector(id, codigo, ubic, x, y, hLampara, lu);
        cantEquipo++;
        try{
            CambiarEstado(id, ESTADO.DISPONIBLE);
        }catch(EquipoNotFoundException e){
            throw e;
        }
        System.out.println("Proyector registrado exitosamente.");
    }

    //Multimetro
    public void registrarEquipo(int id, String codigo, String ubic, String tipo, boolean calibrado, 
        int rango, int precision, boolean a) throws EquipoNotFoundException{

        inventario[cantEquipo] = new Multimetro(id, codigo, ubic, tipo, calibrado, rango, precision, a);
        cantEquipo++;
        try{
            CambiarEstado(id, ESTADO.DISPONIBLE);
        }catch(EquipoNotFoundException e){
            throw e;
        }
        System.out.println("Multimetro registrado exitosamente.");
    }

}

