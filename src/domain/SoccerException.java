package domain;

/**
 * Excepcion propia del dominio DOPOSoccer.
 * @author Mary Alejandra Guanga y Samuel Esteban Cruz
 * @version 2.0
 */
public class SoccerException extends Exception {
    public static final String INVALID_STAT_LEVEL = "La estadistica ya alcanzo el nivel maximo (10).";
    public static final String INSUFFICIENT_POINTS = "No tienes puntos suficientes para mejorar esta estadistica.";
    public static final String INVALID_CONFIG = "La configuracion del partido no es valida.";
    public static final String POWER_NOT_READY = "La barra de poder aun no esta llena o ya tienes un aura activa.";
    public static final String NULL_FILE = "El archivo seleccionado es nulo.";
    public static final String INVALID_EXTENSION_DAT = "El archivo debe tener extension .dat: ";
    public static final String FILE_NOT_FOUND = "El archivo no existe o no se pudo encontrar: ";
    public static final String CLASS_NOT_FOUND = "No se encontro la definicion de una clase al leer el archivo: ";
    public static final String CORRUPTED_FILE = "El archivo esta corrupto o no contiene una partida valida de DOPOSoccer: ";
    public static final String IO_ERROR = "Error de entrada/salida al procesar el archivo: ";

    public SoccerException(String message) {
        super(message);
    }
}