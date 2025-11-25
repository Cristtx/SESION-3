package org.example.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.example.entity.Boleto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRService {

    public byte[] generarQRCodeBytes(Boleto boleto, int width, int height) {
        try {
            String qrContent = generarContenidoQR(boleto);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generando QR code", e);
        }
    }

    private String generarContenidoQR(Boleto boleto) {

        String url = "http://localhost:8080/boleto?codigo=" + boleto.getCodigoReserva();

        return url + "\n\n--- INFORMACION DEL BOLETO ---\n" +
                "Codigo: " + boleto.getCodigoReserva() + "\n" +
                "Pasajero: " + boleto.getPasajero() + "\n" +
                "Vuelo: " + boleto.getNumeroVuelo() + "\n" +
                "Ruta: " + boleto.getOrigen() + " - " + boleto.getDestino() + "\n" +
                "usa el codigo en el Host local ";
    }
}