package com.bi.service.controller;

import com.bi.service.modelRestApi.PersonAggregatedByCountryDto;
import com.bi.service.modelRestApi.PersonAggregatetedByGenderDto;
import com.bi.service.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RequestMapping("/aggregateddata/ctry")
@RestController
@Slf4j
@CrossOrigin
public class PersonAgregationController {

    private PersonService personService;

    public PersonAgregationController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public Map<String, List<PersonAggregatedByCountryDto>> getByCountry() {

        return personService.getByCountry();

    }

    @GetMapping
    @RequestMapping("/gender")
    public Map<String, Map<String, List<PersonAggregatetedByGenderDto>>> getByCountryByGender() {

        return personService.getByCountryByGender();
    }

}
