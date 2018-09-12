package com.bi.service.controller;

import com.bi.service.modelRestApi.PersonDto;
import com.bi.service.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/person")
@RestController
@Slf4j
@CrossOrigin
public class PersonController {

    private PersonService personService;


    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public PersonDto addPerson(@RequestBody PersonDto person) {


        return personService.addPerson(person);
    }

    @DeleteMapping
    public ResponseEntity deletePerson(@RequestBody PersonDto person) {
        return personService.deletePerson(person);

    }

    @PutMapping
    public PersonDto putPerson(@RequestBody PersonDto person) {


        return personService.putPerson(person);
    }

    @GetMapping(value = "/{id}")
    public PersonDto getPerson(@PathVariable Integer id) {

        return personService.getPerson(id);
    }

    @GetMapping(params = "ctry")
    public List<PersonDto> getPersonCountry(@RequestParam(required = true, value = "ctry") String country) {

        return personService.getPersonCountry(country);

    }

    @GetMapping(params = "gender")
    public List<PersonDto> getPersonGender(@RequestParam(required = true, value = "gender") String gender) {

        return personService.getPersonGender(gender);

    }

}


