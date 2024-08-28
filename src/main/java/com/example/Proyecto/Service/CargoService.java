package com.example.Proyecto.Service;
import java.util.List;

import com.example.Proyecto.Model.Cargo;
import com.example.Proyecto.Model.Unidad;

public interface CargoService {

    List<Cargo> findAll();
    Cargo findById(Long id);
    void save(Cargo cargo);
    void deleteById(Long id);
    public Cargo obtener_cargoPorNombre(String nombreCargo);
}
