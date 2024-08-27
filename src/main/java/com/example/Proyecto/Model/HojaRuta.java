package com.example.Proyecto.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hoja_ruta")
@Getter
@Setter
public class HojaRuta implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_hoja_ruta;
    private int nroRuta;

    @Column(name = "ref")
    private String ref;

    @Column(name = "ruta")
    private String ruta;
    
    private String estado;
    private Integer unidad_reg;
    private Date fechaCreacion;
    @OneToOne
    private Documento documento;

    @OneToMany(mappedBy = "hojaRuta", cascade = CascadeType.ALL)
    private List<MovimientoDocumento> movimientos;
    
    // @OneToMany(mappedBy = "hojaRuta", cascade = CascadeType.ALL, orphanRemoval = true)
    // @OrderBy("orden")
    // private List<Unidad> rutaProgramada;
}
