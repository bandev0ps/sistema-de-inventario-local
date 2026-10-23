package lab.modelo;

public enum ESTADO {
    DISPONIBLE(1), PRESTADO(2), EN_MANTENIMIENTO(3), FUERA_DE_SERVICIO(4);
    private final int valor;

    ESTADO(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static ESTADO getNumEstado(int num) {
        for (ESTADO estado : ESTADO.values()) {
            if (estado.valor == num) {
                return estado;
            }
        }
        return null;
    }
}