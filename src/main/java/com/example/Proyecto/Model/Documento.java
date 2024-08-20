package com.example.Proyecto.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documento")
@Getter
@Setter
public class Documento implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_documento;
    
    private String cite;
    private String nroRuta;
    private String ruta;
    private String ref;
    private String estado;
    private Date fechaCreacion;
    private Integer unidad_origen;
    @ManyToOne
    private TipoDocumento tipoDocumento;
    
    @OneToOne(mappedBy = "documento", cascade = CascadeType.ALL)
    private HojaRuta hojaRuta;
    
    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL)
    private List<MovimientoDocumento> movimientos;
}
