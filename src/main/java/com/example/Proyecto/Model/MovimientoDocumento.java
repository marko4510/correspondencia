package com.example.Proyecto.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movimiento_documento")
@Getter
@Setter
public class MovimientoDocumento implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_movimiento_documento;
    
    private String ruta_movimiento;
    private String instruccion;
    private Integer usuarioRegistro;
    private Integer usuarioDestino;

    @ManyToOne
    @JoinColumn(name = "id_hoja_ruta")
    private HojaRuta hojaRuta;
    
    @ManyToOne
    @JoinColumn(name = "id_unidad_origen")
    private Unidad unidadOrigen;
    
    @ManyToOne
    @JoinColumn(name = "id_unidad_destino")
    private Unidad unidadDestino;
    
   
    
    private Date fechaHoraRegistro;
    private String observaciones;
}
