package com.team73.studyshare.controller;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        List<Module> modules = moduleService.getAllModules();
        return ResponseEntity.ok().body(modules);
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long moduleId) {
        Optional<Module> moduleOptional;
        try {
            moduleOptional = moduleService.getModuleById(moduleId);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return moduleOptional
                .map(module -> ResponseEntity.ok().body(module))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{moduleId}/card-sets")
    public ResponseEntity<List<CardSet>> getCardSetsFromModule(@PathVariable Long moduleId) {
        try {
            List<CardSet> cardSets = moduleService.getCardSetsFromModule(moduleId);
            return ResponseEntity.ok(cardSets);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Module> createModule(@RequestBody Module newModule) {
        try {
            Module createdModule = moduleService.createModule(newModule);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdModule);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PutMapping("/{moduleId}")
    public ResponseEntity<Module> updateModule(@PathVariable Long moduleId, @RequestBody Module updatedModule) {
        try {
            Optional<Module> updatedModuleOptional = moduleService.updateModule(moduleId, updatedModule);
            return updatedModuleOptional.map(module -> ResponseEntity.ok().body(module)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        try {
            moduleService.deleteModule(moduleId);
            return ResponseEntity.noContent().build();
        } catch (InvalidRequestException e) {
            if(e.getMessage().contains("could not be deleted")){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
    }


}
