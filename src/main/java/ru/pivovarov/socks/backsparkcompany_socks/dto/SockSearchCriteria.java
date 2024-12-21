package ru.pivovarov.socks.backsparkcompany_socks.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SockSearchCriteria {

    private String color;
    private String operation;
    private Byte cotton;
    private Byte minCotton;
    private Byte maxCotton;
    private String sortBy;
    private String sortOrder = "asc";
}
