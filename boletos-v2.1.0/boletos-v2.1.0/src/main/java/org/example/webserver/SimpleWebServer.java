package org.example.webserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.example.entity.Boleto;
import org.example.repository.BoletoRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SimpleWebServer {
    private static final int PORT = 8080;
    private static BoletoRepository boletoRepository;

    public static void startServer() {
        try {
            boletoRepository = new BoletoRepository();
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Manejar solicitudes de boletos
            server.createContext("/boleto", new BoletoHandler());

            // Página principal
            server.createContext("/", new HomeHandler());

            server.setExecutor(null);
            server.start();
            System.out.println("Servidor web iniciado en http://localhost:" + PORT);

        } catch (IOException e) {
            System.err.println("Error iniciando servidor: " + e.getMessage());
        }
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html>\n" +
                    "<head>\n" +
                    "    <title>Boletos AUREAC</title>\n" +
                    "    <style>\n" +
                    "        body { \n" +
                    "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; \n" +
                    "            margin: 0; \n" +
                    "            padding: 0;\n" +
                    "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                    "            color: white;\n" +
                    "            min-height: 100vh;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "        }\n" +
                    "        .container { \n" +
                    "            max-width: 600px; \n" +
                    "            margin: 20px; \n" +
                    "            background: rgba(255,255,255,0.15);\n" +
                    "            padding: 40px;\n" +
                    "            border-radius: 20px;\n" +
                    "            backdrop-filter: blur(15px);\n" +
                    "            box-shadow: 0 15px 35px rgba(0,0,0,0.2);\n" +
                    "            border: 1px solid rgba(255,255,255,0.2);\n" +
                    "        }\n" +
                    "        h1 { \n" +
                    "            text-align: center; \n" +
                    "            margin-bottom: 30px;\n" +
                    "            font-weight: 300;\n" +
                    "            font-size: 2.5em;\n" +
                    "        }\n" +
                    "        .info { \n" +
                    "            background: rgba(255,255,255,0.2); \n" +
                    "            padding: 25px; \n" +
                    "            border-radius: 15px; \n" +
                    "            margin: 25px 0;\n" +
                    "            border-left: 4px solid rgba(255,255,255,0.5);\n" +
                    "        }\n" +
                    "        .logo {\n" +
                    "            text-align: center;\n" +
                    "            font-size: 1.2em;\n" +
                    "            margin-bottom: 20px;\n" +
                    "            opacity: 0.9;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"logo\">✈️ AUREAC AIRLINES</div>\n" +
                    "        <h1>Sistema de Boletos AUREAC</h1>\n" +
                    "        <div class=\"info\">\n" +
                    "            <p>Usa un código de reserva para ver tu boleto virtual.</p>\n" +
                    "            <p><strong>Ejemplo:</strong> http://localhost:8080/boleto?codigo=RES123456789</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    static class BoletoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String codigoReserva = null;

            // Extraer código de reserva del query string
            if (query != null && query.startsWith("codigo=")) {
                codigoReserva = query.substring(7);
            }

            String response;
            if (codigoReserva != null && !codigoReserva.isEmpty()) {
                Optional<Boleto> boletoOpt = boletoRepository.findByCodigoReserva(codigoReserva);
                if (boletoOpt.isPresent()) {
                    response = generarPaginaBoleto(boletoOpt.get());
                } else {
                    response = generarPaginaError("Boleto no encontrado para el código: " + codigoReserva);
                }
            } else {
                response = generarPaginaError("Código de reserva no proporcionado");
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }

        private String generarPaginaBoleto(Boleto boleto) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaFormateada = boleto.getFechaVuelo().format(formatter);
            String fechaEmision = boleto.getFechaCreacion().format(formatter);

            return "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Boleto de Avión - " + boleto.getCodigoReserva() + " | AUREAC Airlines</title>\n" +
                    "    <style>\n" +
                    "        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');\n" +
                    "        \n" +
                    "        * {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "            box-sizing: border-box;\n" +
                    "        }\n" +
                    "        \n" +
                    "        body { \n" +
                    "            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; \n" +
                    "            margin: 0; \n" +
                    "            padding: 20px;\n" +
                    "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                    "            min-height: 100vh;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            color: #2c3e50;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket-container {\n" +
                    "            max-width: 420px;\n" +
                    "            width: 100%;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket {\n" +
                    "            background: linear-gradient(145deg, #ffffff, #f8f9fa);\n" +
                    "            border-radius: 24px;\n" +
                    "            padding: 0;\n" +
                    "            box-shadow: \n" +
                    "                0 25px 50px -12px rgba(0, 0, 0, 0.25),\n" +
                    "                0 0 0 1px rgba(255, 255, 255, 0.1);\n" +
                    "            position: relative;\n" +
                    "            overflow: hidden;\n" +
                    "            backdrop-filter: blur(20px);\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket-header {\n" +
                    "            background: linear-gradient(135deg, #2c3e50, #34495e);\n" +
                    "            color: white;\n" +
                    "            padding: 30px 25px 25px 25px;\n" +
                    "            position: relative;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket-header::before {\n" +
                    "            content: '';\n" +
                    "            position: absolute;\n" +
                    "            top: 0;\n" +
                    "            left: 0;\n" +
                    "            right: 0;\n" +
                    "            height: 4px;\n" +
                    "            background: linear-gradient(90deg, #e74c3c, #e67e22, #f1c40f, #2ecc71, #3498db, #9b59b6);\n" +
                    "        }\n" +
                    "        \n" +
                    "        .airline-name {\n" +
                    "            font-size: 24px;\n" +
                    "            font-weight: 700;\n" +
                    "            margin-bottom: 5px;\n" +
                    "            letter-spacing: -0.5px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket-title {\n" +
                    "            font-size: 14px;\n" +
                    "            opacity: 0.8;\n" +
                    "            font-weight: 400;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .status-badge {\n" +
                    "            background: linear-gradient(135deg, #27ae60, #2ecc71);\n" +
                    "            color: white;\n" +
                    "            padding: 8px 16px;\n" +
                    "            border-radius: 20px;\n" +
                    "            font-size: 12px;\n" +
                    "            font-weight: 600;\n" +
                    "            display: inline-block;\n" +
                    "            box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket-body {\n" +
                    "            padding: 30px 25px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .route-section {\n" +
                    "            text-align: center;\n" +
                    "            margin-bottom: 30px;\n" +
                    "            padding: 25px;\n" +
                    "            background: rgba(52, 152, 219, 0.08);\n" +
                    "            border-radius: 16px;\n" +
                    "            border: 1px solid rgba(52, 152, 219, 0.1);\n" +
                    "        }\n" +
                    "        \n" +
                    "        .cities {\n" +
                    "            font-size: 28px;\n" +
                    "            font-weight: 700;\n" +
                    "            margin: 15px 0;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            gap: 15px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .arrow {\n" +
                    "            color: #e74c3c;\n" +
                    "            font-size: 24px;\n" +
                    "            font-weight: 300;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .flight-number {\n" +
                    "            background: #34495e;\n" +
                    "            color: white;\n" +
                    "            padding: 10px 20px;\n" +
                    "            border-radius: 12px;\n" +
                    "            font-size: 14px;\n" +
                    "            font-weight: 600;\n" +
                    "            display: inline-block;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .info-grid {\n" +
                    "            display: grid;\n" +
                    "            grid-template-columns: 1fr 1fr;\n" +
                    "            gap: 20px;\n" +
                    "            margin-bottom: 25px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .info-item {\n" +
                    "            margin-bottom: 18px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .label {\n" +
                    "            font-size: 11px;\n" +
                    "            color: #7f8c8d;\n" +
                    "            text-transform: uppercase;\n" +
                    "            font-weight: 600;\n" +
                    "            margin-bottom: 6px;\n" +
                    "            letter-spacing: 0.5px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .value {\n" +
                    "            font-size: 15px;\n" +
                    "            color: #2c3e50;\n" +
                    "            font-weight: 500;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .ticket-footer {\n" +
                    "            background: #f8f9fa;\n" +
                    "            padding: 20px 25px;\n" +
                    "            text-align: center;\n" +
                    "            border-top: 2px dashed #e9ecef;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .company {\n" +
                    "            font-size: 12px;\n" +
                    "            color: #95a5a6;\n" +
                    "            font-weight: 500;\n" +
                    "            margin-bottom: 5px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .virtual-ticket {\n" +
                    "            font-size: 10px;\n" +
                    "            color: #bdc3c7;\n" +
                    "            text-transform: uppercase;\n" +
                    "            letter-spacing: 1px;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .reservation-code {\n" +
                    "            background: rgba(52, 152, 219, 0.1);\n" +
                    "            padding: 12px;\n" +
                    "            border-radius: 10px;\n" +
                    "            margin: 15px 0;\n" +
                    "            border-left: 4px solid #3498db;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .price-tag {\n" +
                    "            background: linear-gradient(135deg, #e67e22, #f39c12);\n" +
                    "            color: white;\n" +
                    "            padding: 8px 16px;\n" +
                    "            border-radius: 12px;\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: 700;\n" +
                    "            display: inline-block;\n" +
                    "            box-shadow: 0 4px 12px rgba(230, 126, 34, 0.3);\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"ticket-container\">\n" +
                    "        <div class=\"ticket\">\n" +
                    "            <div class=\"ticket-header\">\n" +
                    "                <div class=\"airline-name\">AUREAC AIRLINES</div>\n" +
                    "                <div class=\"ticket-title\">PASE DE ABORDAR ELECTRÓNICO</div>\n" +
                    "                <div class=\"status-badge\"> VERIFICADO - LISTO PARA EMBARCAR</div>\n" +
                    "            </div>\n" +
                    "            \n" +
                    "            <div class=\"ticket-body\">\n" +
                    "                <div class=\"reservation-code\">\n" +
                    "                    <div class=\"label\">CÓDIGO DE RESERVA</div>\n" +
                    "                    <div class=\"value\" style=\"font-size: 16px; font-weight: 700; color: #2c3e50;\">" + boleto.getCodigoReserva() + "</div>\n" +
                    "                </div>\n" +
                    "                \n" +
                    "                <div class=\"route-section\">\n" +
                    "                    <div class=\"label\">TRAYECTO</div>\n" +
                    "                    <div class=\"cities\">\n" +
                    "                        <span>" + boleto.getOrigen() + "</span>\n" +
                    "                        <span class=\"arrow\">→</span>\n" +
                    "                        <span>" + boleto.getDestino() + "</span>\n" +
                    "                    </div>\n" +
                    "                    <div class=\"flight-number\">Vuelo " + boleto.getNumeroVuelo() + "</div>\n" +
                    "                </div>\n" +
                    "                \n" +
                    "                <div class=\"info-grid\">\n" +
                    "                    <div class=\"info-item\">\n" +
                    "                        <div class=\"label\">Pasajero</div>\n" +
                    "                        <div class=\"value\">" + boleto.getPasajero() + "</div>\n" +
                    "                    </div>\n" +
                    "                    <div class=\"info-item\">\n" +
                    "                        <div class=\"label\">Asiento</div>\n" +
                    "                        <div class=\"value\">" + boleto.getAsiento() + "</div>\n" +
                    "                    </div>\n" +
                    "                    <div class=\"info-item\">\n" +
                    "                        <div class=\"label\">Fecha de Vuelo</div>\n" +
                    "                        <div class=\"value\">" + fechaFormateada + "</div>\n" +
                    "                    </div>\n" +
                    "                    <div class=\"info-item\">\n" +
                    "                        <div class=\"label\">Fecha de Emisión</div>\n" +
                    "                        <div class=\"value\">" + fechaEmision + "</div>\n" +
                    "                    </div>\n" +
                    "                </div>\n" +
                    "                \n" +
                    "                <div style=\"text-align: center; margin: 25px 0;\">\n" +
                    "                    <div class=\"price-tag\">$" + String.format("%.2f", boleto.getPrecio()) + "</div>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "            \n" +
                    "            <div class=\"ticket-footer\">\n" +
                    "                <div class=\"company\">AUREAC AIRLINES</div>\n" +
                    "                <div class=\"virtual-ticket\">Boleto Virtual</div>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
        }

        private String generarPaginaError(String mensaje) {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Error | AUREAC Airlines</title>\n" +
                    "    <style>\n" +
                    "        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&display=swap');\n" +
                    "        \n" +
                    "        body { \n" +
                    "            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; \n" +
                    "            margin: 0; \n" +
                    "            padding: 20px;\n" +
                    "            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);\n" +
                    "            color: white;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            min-height: 100vh;\n" +
                    "        }\n" +
                    "        .error-container { \n" +
                    "            text-align: center; \n" +
                    "            background: rgba(255,255,255,0.15);\n" +
                    "            padding: 50px 40px;\n" +
                    "            border-radius: 20px;\n" +
                    "            backdrop-filter: blur(15px);\n" +
                    "            box-shadow: 0 15px 35px rgba(0,0,0,0.2);\n" +
                    "            border: 1px solid rgba(255,255,255,0.2);\n" +
                    "            max-width: 500px;\n" +
                    "            width: 100%;\n" +
                    "        }\n" +
                    "        h1 { \n" +
                    "            font-size: 2.5em;\n" +
                    "            margin-bottom: 20px;\n" +
                    "            font-weight: 300;\n" +
                    "        }\n" +
                    "        p {\n" +
                    "            font-size: 1.1em;\n" +
                    "            margin-bottom: 25px;\n" +
                    "            line-height: 1.6;\n" +
                    "            opacity: 0.9;\n" +
                    "        }\n" +
                    "        .home-link {\n" +
                    "            color: white;\n" +
                    "            text-decoration: none;\n" +
                    "            padding: 12px 30px;\n" +
                    "            border: 2px solid rgba(255,255,255,0.3);\n" +
                    "            border-radius: 25px;\n" +
                    "            transition: all 0.3s ease;\n" +
                    "            display: inline-block;\n" +
                    "            font-weight: 500;\n" +
                    "        }\n" +
                    "        .home-link:hover {\n" +
                    "            background: rgba(255,255,255,0.2);\n" +
                    "            border-color: rgba(255,255,255,0.5);\n" +
                    "        }\n" +
                    "        .logo {\n" +
                    "            font-size: 1.2em;\n" +
                    "            margin-bottom: 20px;\n" +
                    "            opacity: 0.9;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"error-container\">\n" +
                    "        <div class=\"logo\">✈️ AUREAC AIRLINES</div>\n" +
                    "        <h1>Error en la Reserva</h1>\n" +
                    "        <p>" + mensaje + "</p>\n" +
                    "        <a href=\"/\" class=\"home-link\">Volver al Inicio</a>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }
}