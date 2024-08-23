package com.example.Proyecto.Model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "unidad")
@Getter
@Setter
public class Unidad implements Serializable {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_unidad;

    private String nombre;
    private String sigla;
    private String codigo;
     @Column(name = "contador_cite")
    private Integer contadorCite;

    @Column(name = "contador_hoja_ruta")
    private Integer contadorHojaRuta;
    private String estado;

    @OneToMany(mappedBy = "unidad")
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "unidadOrigen")
    private List<MovimientoDocumento> movimientosOrigen;

    @OneToMany(mappedBy = "unidadDestino")
    private List<MovimientoDocumento> movimientosDestino;
}
