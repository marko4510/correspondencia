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
        Connection con = null;

        // return stream;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Path rootPath = Paths.get("").toAbsolutePath();
        Path directorio = Paths.get(rootPath.toString(), "reportes", nombreArchivo);
        String ruta = directorio.toString();

        try (InputStream reportStream = new FileInputStream(ruta)) {
            con = dataSource.getConnection();

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
        } catch (IOException | JRException | SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        con.close();
        return stream;
    }

}
