package se.ranking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.ranking.exception.NotFoundException;
import se.ranking.model.Qualifier;
import se.ranking.service.QualifierService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "qualifier")
public class QualifierController {
    @Autowired
    QualifierService qualifierService;

    @PostMapping("/save")
    public ResponseEntity<?> saveQualifier(@RequestBody Qualifier qualifier) {
        Qualifier savedQualifier = qualifierService.save(qualifier);
        return ResponseEntity.ok(savedQualifier);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQualifier(@PathVariable("id") Long id) throws Exception {
        Qualifier qualifier = qualifierService.findById(id);
        return ResponseEntity.ok(qualifier);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllQualifiers() {
        List<Qualifier> qualifiers = qualifierService.findAll();
        return ResponseEntity.ok(qualifiers);
    }

    @PutMapping("/put")
    public ResponseEntity<?> editQualifier(@RequestBody Qualifier qualifier, @PathVariable("id") Long id) {
        Qualifier editedQualifier = qualifierService.edit(id, qualifier);
        return ResponseEntity.ok().body(qualifier);
    }

    /**
     * "Content-Type: application/json-patch+json"
     */
    @PatchMapping(value = "/patch/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchQualifier(@RequestBody JsonPatch jsonPatch, @PathVariable("id") Long id) {
        try {
            Qualifier qualifier = qualifierService.patchQualifier(jsonPatch, id);
            return ResponseEntity.ok().body(qualifier);
        } catch (JsonPatchException |JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQualifier(@RequestBody Qualifier qualifier, @PathVariable("id") Long id) {
        qualifierService.delete(qualifier);
        return ResponseEntity.ok().body(qualifier.getName() + " was deleted");
    }
}
