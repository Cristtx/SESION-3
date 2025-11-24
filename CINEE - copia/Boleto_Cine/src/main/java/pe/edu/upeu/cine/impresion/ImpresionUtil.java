package pe.edu.upeu.cine.impresion;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.Copies;
import java.io.*;

public class ImpresionUtil {

    public static void imprimirEnPOS(String rutaPDF) {
        try {
            FileInputStream fis = new FileInputStream(rutaPDF);
            Doc documento = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);

            AttributeSet atributos = new HashPrintServiceAttributeSet(
                    new PrinterName("POS-80-Series", null)
            );

            PrintService[] servicios = PrintServiceLookup.lookupPrintServices(null, atributos);

            if (servicios.length == 0) {
                System.out.println(" No se encontró la impresora POS-80-Series");
                return;
            }

            PrintService impresora = servicios[0];

            PrintRequestAttributeSet params = new HashPrintRequestAttributeSet();
            params.add(new Copies(1));

            DocPrintJob job = impresora.createPrintJob();
            job.print(documento, params);

            System.out.println(" Ticket enviado a la impresora POS-80-Series");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al imprimir el ticket: " + e.getMessage());
        }
    }
}
