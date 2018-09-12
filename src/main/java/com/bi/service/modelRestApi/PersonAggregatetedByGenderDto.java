package com.bi.service.modelRestApi;

import org.codehaus.jackson.annotate.JsonProperty;

public class PersonAggregatetedByGenderDto {

    private Integer id;

    private String name;

    @JsonProperty("last_name")
    private String last_name;

    @JsonProperty("additonal_info")
    private String additonal_info;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAdditonal_info() {
        return additonal_info;
    }

    public void setAdditonal_info(String additonal_info) {
        this.additonal_info = additonal_info;
    }


}
