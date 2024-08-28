package com.example.Proyecto.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Proyecto.Dao.CargoDao;
import com.example.Proyecto.Dao.TipoDocumentoDao;
import com.example.Proyecto.Model.Cargo;
import com.example.Proyecto.Model.TipoDocumento;
import com.example.Proyecto.Model.Unidad;

@Service
public class CargoServiceImpl implements CargoService{

    @Autowired
    private CargoDao cargoDao;

    @Override
    public List<Cargo> findAll() {
        return cargoDao.findAll();
    }

    @Override
    public Cargo findById(Long id) {
        return cargoDao.findById(id).orElse(null);
    }

    @Override
    public void save(Cargo cargo) {
        cargoDao.save(cargo);
    }

    @Override
    public void deleteById(Long id) {
        Cargo cargo = cargoDao.findById(id).orElse(null);
        cargo.setEstado("X");
        cargoDao.save(cargo);
    }

    @Override
    public Cargo obtener_cargoPorNombre(String nombreCargo) {
       return cargoDao.obtener_cargoPorNombre(nombreCargo);
    }

  
    
}
