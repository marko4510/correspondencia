package com.example.Proyecto.Model;

import java.io.Serializable;
import java.util.List;

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
public class Unidad implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String codigo;
    
    @OneToMany(mappedBy = "unidad")
    private List<Usuario> usuarios;
    
    @OneToMany(mappedBy = "unidadActual")
    private List<MovimientoDocumento> movimientosActuales;
}
