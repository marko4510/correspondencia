package com.example.Proyecto.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private Long id;
    
    @ManyToOne
    private Documento documento;
    
    @ManyToOne
    private Unidad unidadOrigen;
    
    @ManyToOne
    private Unidad unidadDestino;
    
    @ManyToOne
    private Usuario usuarioRegistro;
    
    private Date fechaHoraRegistro;
    private String observaciones;
}
