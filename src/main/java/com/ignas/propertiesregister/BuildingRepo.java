package com.ignas.propertiesregister;

import com.ignas.propertiesregister.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BuildingRepo extends JpaRepository<Building, Integer> {
    List<Building> findByOwner(String owner);
    List<Building> findAll();
}
