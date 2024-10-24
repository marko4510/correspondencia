package com.example.Proyecto.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Map;
import java.sql.Connection;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import javax.sql.DataSource;

@Service
public class UtilidadServiceImpl implements UtilidadService {

    @Autowired
    private DataSource dataSource; //

    @Override
    public ByteArrayOutputStream compilarAndExportarReporte(String nombreArchivo, Map<String, Object> params)
            throws IOException, JRException, SQLException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Connection con = null;  // Inicializamos la conexión como null
        Path rootPath = Paths.get("").toAbsolutePath();
        Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
        String ruta = directorio.toString();

        try (InputStream reportStream = new FileInputStream(ruta)) {
            // Obtener la conexión de la base de datos
            con = dataSource.getConnection();

            // Compilar y llenar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);

            // Exportar el reporte a PDF
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
        } catch (IOException | JRException | SQLException e) {
            // Manejar las excepciones adecuadamente
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar la conexión si no es nula
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Error cerrando la conexión: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return stream;
    }

}
