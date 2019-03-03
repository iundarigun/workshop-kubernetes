package br.com.devcave.workshop.kubernetes.preferences.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class CharacterResponse {

    private String name;

    private String gender;

    private String born;

    private List<HouseResponse> houseList;


}
