package ru.pivovarov.socks.backsparkcompany_socks.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class SockDto {

    private Long id;

    private String color;

    private Byte cottonPercent;

    private int quantity;
}
