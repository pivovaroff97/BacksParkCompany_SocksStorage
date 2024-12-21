package ru.pivovarov.socks.backsparkcompany_socks.mapper;

import org.mapstruct.Mapper;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockDto;
import ru.pivovarov.socks.backsparkcompany_socks.model.Sock;

@Mapper(componentModel = "spring")
public interface SocksMapper {

    Sock toEntity(SockDto sockDto);
    SockDto toDto(Sock sock);
}
