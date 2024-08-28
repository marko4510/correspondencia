package com.example.Proyecto.Model;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "entidadExterna")
@Getter
@Setter
public class EntidadExterna implements Serializable{
    
     private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_entidad_externa;

    private String nombre;
    private String estado;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entidadExterna", fetch = FetchType.LAZY)
    private List<HojaRuta> hojaRutas;

}
