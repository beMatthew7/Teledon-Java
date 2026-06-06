package ro.mpp2024.teledon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.repository.CharityCaseRepository;

@RestController
@RequestMapping("teledon/charity-cases")
@CrossOrigin(origins = "*")
@SpringBootApplication(
        scanBasePackages = {"ro.mpp2024"}
)
public class CharityCaseRestController {

    private CharityCaseRepository charityCaseRepository;
    private SimpMessagingTemplate messagingTemplate;
    private SimpUserRegistry simpUserRegistry;

    @Autowired
    public CharityCaseRestController(CharityCaseRepository charityCaseRepository, SimpMessagingTemplate messagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.charityCaseRepository = charityCaseRepository;
        this.messagingTemplate = messagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
    }

//    @GetMapping
//    public String test(@RequestParam (value="name", defaultValue="Hello") String name) {
//        return name.toUpperCase();
//    }

    @PostMapping
    public CharityCase create(@RequestBody CharityCase charityCase){
        System.out.println("Creating charityCase");
        CharityCase saved = charityCaseRepository.save(charityCase);
        notifyConnectedVolunteers();
        return saved;
    }

    @PutMapping("/{id}")
    public CharityCase update(@PathVariable Long id, @RequestBody CharityCase charityCase){
        System.out.println("Updating charityCase with id "+id);
        charityCase.setId(id);
        CharityCase updated = charityCaseRepository.update(charityCase);
        notifyConnectedVolunteers();
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        System.out.println("Deleting charityCase with id " + id);
        CharityCase deleted = charityCaseRepository.delete(id);
        if (deleted == null) {
            return ResponseEntity.notFound().build();
        }
        notifyConnectedVolunteers();
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

    private void notifyConnectedVolunteers() {
        simpUserRegistry.getUsers()
                .forEach(user -> messagingTemplate.convertAndSendToUser(
                        user.getName(),
                        "/queue/cases-updates",
                        "updated"
                ));
    }
}
