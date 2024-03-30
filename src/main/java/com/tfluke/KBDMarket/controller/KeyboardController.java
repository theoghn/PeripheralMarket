package com.tfluke.KBDMarket.controller;

import com.tfluke.KBDMarket.model.*;
import com.tfluke.KBDMarket.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import com.tfluke.KBDMarket.service.KeyboardService;

@RestController
@RequestMapping("/api/v1/kbd")
public class KeyboardController {

    private final KeyboardService keyboardService;

    private final KeyboardModelAssembler keyboardModelAssembler;

    private final PagedResourcesAssembler<Keyboard> pagedResourcesAssembler;

    private final AuditService auditService;

    public KeyboardController(
            KeyboardService keyboardService,
            KeyboardModelAssembler keyboardModelAssembler,
            PagedResourcesAssembler<Keyboard> pagedResourcesAssembler,
            AuditService auditService) {
        this.auditService = auditService;
        this.keyboardService = keyboardService;
        this.keyboardModelAssembler = keyboardModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }
    /*@GetMapping
    public ResponseEntity<List<Keyboard>>getKeyboards(){
        //using response entity to manipulate the Status and not get error when returning empty list
        return  new ResponseEntity<List<Keyboard>>(service.getKeyboards(), HttpStatus.OK);
    }
    record NewKeyboard(String layout,
                       String color,
                       Integer price,
                       Integer imagesGroupId,
                       String description){

    }*/
    @PostMapping
    public ResponseEntity<Keyboard> addKeyboard(@RequestBody Keyboard newKeyboard){
        keyboardService.addKeyboard(newKeyboard);
        auditService.logAction("Keyboard Post");
        return new ResponseEntity<Keyboard>(newKeyboard,HttpStatus.OK);

    }
    @PutMapping("{id}")
    public ResponseEntity<String> updateKeyboard(@PathVariable Integer id,@RequestBody Keyboard kbdDetails){
        try {
            keyboardService.updateKeyboard(id,kbdDetails);
        }
        catch (ResourceAccessException e){
            return new ResponseEntity<String>("Invalid Id",HttpStatus.NO_CONTENT);
        }
        auditService.logAction("Keyboard Update");

        return new ResponseEntity<String>(kbdDetails.toString(),HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteKeyboard(@PathVariable Integer id){
        try {
            keyboardService.deleteKeyboard(id);
        }
        catch (ResourceAccessException e){
            return new ResponseEntity<String>("Invalid Id",HttpStatus.NO_CONTENT);
        }
        auditService.logAction("Keyboard Delete");

        return new ResponseEntity<String>("Keyboard with id " + id + " deleted.",HttpStatus.OK);
    }
//    @GetMapping
//    public ResponseEntity<Page<Keyboard>> getAllKeyboardsWithFilters(KeyboardFilters keyboardFilters,
//                                                                     KeyboardPage keyboardPage){
//        return new ResponseEntity<>(service.getAllKeyboardsByFilter(keyboardFilters, keyboardPage),
//                HttpStatus.OK);
//    }

    @GetMapping
   public ResponseEntity<PagedModel<KeyboardModel>> getAllKeyboardsWithFilters(
           KeyboardFilters keyboardFilters,
           KeyboardPage keyboardPage){

        auditService.logAction("Keyboard Get");

        Page<Keyboard> page = keyboardService.getAllKeyboardsByFilter(keyboardFilters, keyboardPage);

        return new ResponseEntity<>(
                pagedResourcesAssembler.toModel(page, keyboardModelAssembler),
                HttpStatus.OK);
    }


}
