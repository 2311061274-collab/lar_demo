package com.petcare.admin.pet;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class AdminPetRequest {

    private Long customerId;

    @NotBlank(
            message = "Tên thú cưng không được để trống"
    )
    private String name;


    @NotBlank(
            message = "Loài thú cưng không được để trống"
    )
    private String species;


    private String breed;


    private String gender;


    private LocalDate birthDate;


    public Long getCustomerId() {
        return customerId;
    }


    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getSpecies() {
        return species;
    }


    public void setSpecies(String species) {
        this.species = species;
    }


    public String getBreed() {
        return breed;
    }


    public void setBreed(String breed) {
        this.breed = breed;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public LocalDate getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(
            LocalDate birthDate
    ) {
        this.birthDate = birthDate;
    }
}
