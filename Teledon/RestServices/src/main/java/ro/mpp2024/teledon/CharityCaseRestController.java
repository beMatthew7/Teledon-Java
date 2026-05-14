package ro.mpp2024.teledon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.repository.CharityCaseRepository;

@RestController
@RequestMapping("teledon/charity-cases")
public class CharityCaseRestController {

    @Autowired
    private CharityCaseRepository charityCaseRepository;

//    @GetMapping
//    public String test(@RequestParam (value="name", defaultValue="Hello") String name) {
//        return name.toUpperCase();
//    }

    @PostMapping
    public CharityCase create(@RequestBody CharityCase charityCase){
        System.out.println("Creating charityCase");
        return charityCaseRepository.save(charityCase);
    }

    @PutMapping("/{id}")
    public CharityCase update(@PathVariable Long id, @RequestBody CharityCase charityCase){
        System.out.println("Updating charityCase with id "+id);
        charityCase.setId(id);
        return charityCaseRepository.update(charityCase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        System.out.println("Deleting charityCase with id " + id);
        CharityCase deleted = charityCaseRepository.delete(id);
        if (deleted == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        System.out.println("Getting charityCase by id "+id);
        CharityCase charityCase = charityCaseRepository.findOne(id);
        if (charityCase == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(charityCase);

    }

    @GetMapping
    public Iterable<CharityCase> getAll(@RequestParam(value = "maxAmount", required = false) Double maxAmount) {
        if (maxAmount != null) {
            System.out.println("Filtering cases with totalAmount < " + maxAmount);
            return charityCaseRepository.findByAmountLessThan(maxAmount);
        }
        System.out.println("Retrieving all charity cases");
        return charityCaseRepository.findAll();
    }
}
