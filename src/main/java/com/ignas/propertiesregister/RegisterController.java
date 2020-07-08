package com.ignas.propertiesregister;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class RegisterController {
    @Autowired
    BuildingRepo repo;

    @ApiOperation(value = "Returns a list of all buildings",response = Building.class)
    @GetMapping("/buildings")
    public List<Building> getAll() {

        List<Building> buildings = repo.findAll();

        //Adding links to relevant resources
        for(Building b : buildings){
            b.add(linkTo(methodOn(RegisterController.class).getBuilding(b.getId())).withSelfRel());
            b.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
        }

        return buildings;
    }

    @GetMapping("/buildings/by_owner/{owner}")
    @ApiOperation(value = "Returns a list of buildings owned by owner", notes = "Owner name is passed as a path variable. However any spaces in name must be replaced with '_'")
    public List<Building> getAllByOwner(@PathVariable("owner") String owner) {

        List<Building> buildings = repo.findByOwner(owner.replace("_", " "));
        //Adding links to each building instance
        for(Building b : buildings){
            b.add(linkTo(methodOn(RegisterController.class).getBuilding(b.getId())).withSelfRel());
            b.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
        }

        return buildings;
    }

    @PostMapping("/buildings")
    @ApiOperation(value = "Creates a new building instance")
    public Building create(@RequestBody Building building) {

        repo.save(building);
        //Adding links for return
        building.add(linkTo(methodOn(RegisterController.class).getBuilding(building.getId())).withSelfRel());
        building.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
        return building;
    }

    @GetMapping("/buildings/{id}")
    @ApiOperation(value = "Returns a building by id")
    public Building getBuilding(@PathVariable("id") int id) {

        Building building = repo.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException(id));

        building.add(linkTo(methodOn(RegisterController.class).getBuilding(id)).withSelfRel());
        building.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
        return building;
    }

    @DeleteMapping("/buildings/{id}")
    @ApiOperation(value = "Deletes a building instance")
    public ResponseEntity<String> deleteBuilding(@PathVariable("id") int id) {
        Building building = repo.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException(id));
        repo.delete(building);
        return ResponseEntity.status(HttpStatus.OK).body("DELETED " + id);
    }

    @PutMapping("/buildings/{id}")
    @ApiOperation(value = "Updates a building or creates a new one if it did not exist before")
    public Building updateBuilding(@PathVariable("id") int id, @RequestBody Building newBuilding) {

        return repo.findById(id)
                .map(building -> {
                    //moving new values
                    building.setCity(newBuilding.getCity());
                    building.setMarketValue(newBuilding.getMarketValue());
                    building.setNumber(newBuilding.getNumber());
                    building.setOwner(newBuilding.getOwner());
                    building.setPropertyType(newBuilding.getPropertyType());
                    building.setSize(newBuilding.getSize());
                    building.setStreet(newBuilding.getStreet());

                    //adding links
                    building.add(linkTo(methodOn(RegisterController.class).getBuilding(building.getId())).withSelfRel());
                    building.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
                    return repo.save(building);
                })
                .orElseGet(() -> {
                    newBuilding.setId(id);
                    //adding links
                    newBuilding.add(linkTo(methodOn(RegisterController.class).getBuilding(newBuilding.getId())).withSelfRel());
                    newBuilding.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
                    return repo.save(newBuilding);
                });
    }

    @PostMapping("/calculate/{owner}")
    @ApiOperation(value = "Calculates the total yearly tax for particular owner",
            notes = "Owner name is passed as a path variable. However any spaces in name must be replaced with '_'. \nAlso, tax rates for different property types," +
                    " that owner owns must be passed in JSON format.\n Example: {\n" +
                    "    \"pricings\":[{\"typeName\":\"House\",\"taxRate\":\"2.2\"},\n" +
                    "                {\"typeName\":\"Garage\",\"taxRate\":\"3\"}]\n" +
                    "}")
    public TotalTax calculateTax(@PathVariable("owner") String owner, @RequestBody TaxRateWrapper wrapper) {
        //Converting owner name and fetching a list from the db
        List<Building> buildings = repo.findByOwner(owner.replace("_", " "));
        double totalTax = 0;
        TotalTax total = new TotalTax(owner);

        // Putting given price rates to a hash map for quick access
        HashMap<String, Double> taxRates = new HashMap<String, Double>();
        for(TaxRate rate : wrapper.getPricings()) {
            taxRates.put(rate.getTypeName(), rate.getTaxRate());
        }

        //Summing up the total
        for(Building building : buildings) {
            String type = building.getPropertyType();
            if(taxRates.containsKey(type)) {
                totalTax += building.getMarketValue() * taxRates.get(type);
            }
            else{
                throw new TaxRateNotGivenException(type);
            }
        }
        total.setTotal(String.format("%.2f", totalTax));
        total.add(linkTo(methodOn(RegisterController.class).getAllByOwner(owner)).withRel("OwnerBuildings"));
        total.add(linkTo(methodOn(RegisterController.class).getAll()).withRel("Buildings"));
        return total;
    }

    // Exception handlers
    @ExceptionHandler(TaxRateNotGivenException.class)
    public ResponseEntity handleException(TaxRateNotGivenException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(BuildingNotFoundException.class)
    public ResponseEntity handleException(BuildingNotFoundException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
