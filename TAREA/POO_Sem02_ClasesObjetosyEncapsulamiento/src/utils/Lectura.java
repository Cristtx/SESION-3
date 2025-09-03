package utils;

import java.util.Scanner;

/**
 * Clase de utilidades para leer datos desde consola
 */
public class Lectura {
    private static Scanner teclado = new Scanner(System.in);

    public String cadena(){
        return teclado.nextLine();
    }
    public String cadenaMayuscula(){
        return cadena().toUpperCase();
    }
    public String cadenaMinuscula(){
        return cadena().toLowerCase();
    }
    public int entero(){
        return Integer.parseInt(cadena());
    }
    public double decimal(){
        return Double.parseDouble(cadena());
    }
    public char letra(){
        return cadena().charAt(0);
    }
    public void cerrar(){
        teclado.close();
    }
}
