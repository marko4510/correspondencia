package com.example.Proyecto.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import net.sf.jasperreports.engine.JRException;

public interface UtilidadService {
    ByteArrayOutputStream compilarAndExportarReporte(String ruta, Map<String, Object> params) throws IOException, JRException, SQLException;
}
