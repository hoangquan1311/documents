package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class TutorialController {

    @Autowired
    TutorialRepository tutorialRepository;
    @GetMapping("/product/test")
    public ResponseEntity<List<Tutorial>> getAllTutorials() {
        return ResponseEntity.ok(tutorialRepository.findAll());
    }
    @PostMapping("/product/create")
    public ResponseEntity<Tutorial> createTutorials(@RequestBody Tutorial tutorial) {
        return ResponseEntity.ok(tutorialRepository.save(tutorial));
    }
}