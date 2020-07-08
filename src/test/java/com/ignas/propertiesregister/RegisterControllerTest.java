package com.ignas.propertiesregister;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class RegisterControllerTest {

    @InjectMocks
    private RegisterController controller;

    @Mock
    private BuildingRepo repo;

    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(Stream.of(new Building(), new Building()).collect(Collectors.toList()));

        assertEquals(2, controller.getAll().size());
    }

    @Test
    void getAllByOwner() {

        when(repo.findByOwner("Tom")).thenReturn(Stream.of(new Building(), new Building()).collect(Collectors.toList()));

        // Two buildings belong to Tom
        assertEquals(2, controller.getAllByOwner("Tom").size());
        // No buildings belong to Elen
        assertEquals(0, controller.getAllByOwner("Elen").size());
    }

    @Test
    void create() {
        Building building = new Building();
        when(repo.save(building)).thenReturn(building);

        assertEquals(building, controller.create(building));
    }

    @Test
    void getBuilding() {
        Building building = new Building();
        building.setId(1);
        building.setOwner("Tom Flask");
        building.setStreet("London st.");
        building.setCity("Manchester");
        building.setNumber(10);
        building.setSize(10000);
        building.setMarketValue(20000);
        building.setPropertyType("House");

        Optional<Building> mockBuilding = Optional.of(building);

        when(repo.findById(1)).thenReturn(mockBuilding);

        Building returnedBuilding = controller.getBuilding(1);
        assertEquals(1,returnedBuilding.getId());
        assertEquals("Tom Flask",returnedBuilding.getOwner());
        assertEquals("London st.", returnedBuilding.getStreet());
        assertEquals("Manchester", returnedBuilding.getCity());
        assertEquals(10, returnedBuilding.getNumber());
        assertEquals(10000, returnedBuilding.getSize());
        assertEquals(20000, returnedBuilding.getMarketValue());
        assertEquals("House", returnedBuilding.getPropertyType());
    }

    @Test
    void deleteBuilding() {
        Building building = new Building();
        building.setId(1);
        Optional<Building> mockBuilding = Optional.of(building);

        when(repo.findById(1)).thenReturn(mockBuilding);
        controller.deleteBuilding(1);

        verify(repo,times(1)).delete(building);
    }

    @Test
    void updateBuilding() {
        Building building = new Building();
        building.setId(1);
        building.setOwner("Tom Flask");
        building.setStreet("London st.");
        building.setCity("Manchester");
        building.setNumber(10);
        building.setSize(10000);
        building.setMarketValue(20000);
        building.setPropertyType("House");

        Building building1 = new Building();
        building1.setId(1);
        building1.setOwner("Tom");
        building1.setStreet("London st.");
        building1.setCity("Manchester");
        building1.setNumber(100);
        building1.setSize(10000);
        building1.setMarketValue(20000);
        building1.setPropertyType("Garage");


        when(repo.save(building)).thenReturn(building1);

        controller.updateBuilding(1, building1);

        assertEquals(1,building1.getId());
        assertEquals("Tom",building1.getOwner());
        assertEquals("London st.", building1.getStreet());
        assertEquals("Manchester", building1.getCity());
        assertEquals(100, building1.getNumber());
        assertEquals(10000, building1.getSize());
        assertEquals(20000, building1.getMarketValue());
        assertEquals("Garage", building1.getPropertyType());

    }

    @Test
    void calculateTax() {

        Building building = new Building();
        building.setId(1);
        building.setOwner("Tom");
        building.setStreet("London st.");
        building.setCity("Manchester");
        building.setNumber(10);
        building.setSize(10000);
        building.setMarketValue(20000);
        building.setPropertyType("House");

        Building building1 = new Building();
        building1.setId(2);
        building1.setOwner("Tom");
        building1.setStreet("London st.");
        building1.setCity("Manchester");
        building1.setNumber(100);
        building1.setSize(10000);
        building1.setMarketValue(20000);
        building1.setPropertyType("Garage");

        TaxRateWrapper taxes = new TaxRateWrapper();
        taxes.setPricings(Stream.of(new TaxRate("House", 1.2), new TaxRate("Garage", 2.4)).collect(Collectors.toList()));

        when(repo.findByOwner("Tom")).thenReturn(Stream.of(building,building1).collect(Collectors.toList()));

        TotalTax total = controller.calculateTax("Tom",taxes);
        assertEquals("Tom", total.getOwner());
        assertEquals("72000.00",total.getTotal());
    }

}