package org.example.service;

import org.example.entity.Boleto;
import org.example.repository.BoletoRepository;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public class BoletoService {
    private BoletoRepository boletoRepository;
    private QRService qrService;

    public BoletoService() {
        this.boletoRepository = new BoletoRepository();
        this.qrService = new QRService();
    }

    public Boleto crearBoleto(Boleto boleto) {
        return boletoRepository.save(boleto);
    }

    public Boleto actualizarBoleto(Boleto boleto) {
        return boletoRepository.update(boleto);
    }

    public boolean eliminarBoleto(Long id) {
        return boletoRepository.delete(id);
    }

    public List<Boleto> obtenerTodosLosBoletos() {
        return boletoRepository.findAll();
    }

    public Optional<Boleto> obtenerBoletoPorId(Long id) {
        return boletoRepository.findById(id);
    }

    public Image generarQRCodeParaBoleto(Long boletoId, int width, int height) {
        Optional<Boleto> boleto = boletoRepository.findById(boletoId);
        if (boleto.isPresent()) {
            byte[] qrBytes = qrService.generarQRCodeBytes(boleto.get(), width, height);
            return new Image(new ByteArrayInputStream(qrBytes));
        }
        throw new RuntimeException("Boleto no encontrado");
    }
}
