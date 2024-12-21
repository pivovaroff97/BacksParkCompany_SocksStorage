package ru.pivovarov.socks.backsparkcompany_socks.service;

import org.springframework.web.multipart.MultipartFile;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockDto;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockSearchCriteria;

import java.io.IOException;
import java.util.List;

public interface SocksService {

    List<SockDto> findAll(SockSearchCriteria specification);
    SockDto findById(Long id);
    SockDto update(Long id, SockDto sockDto);
    SockDto incomeSock(SockDto sockDto);
    SockDto outcomeSock(SockDto sockDto);
    List<SockDto> uploadBatchSocks(MultipartFile file) throws IOException;
    void deleteById(Long id);
}
