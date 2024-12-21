package ru.pivovarov.socks.backsparkcompany_socks.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockDto;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockSearchCriteria;
import ru.pivovarov.socks.backsparkcompany_socks.exception.NoSocksInWarehouseException;
import ru.pivovarov.socks.backsparkcompany_socks.mapper.SocksMapper;
import ru.pivovarov.socks.backsparkcompany_socks.model.Sock;
import ru.pivovarov.socks.backsparkcompany_socks.repository.SocksRepository;
import ru.pivovarov.socks.backsparkcompany_socks.repository.specification.SockSpecification;
import ru.pivovarov.socks.backsparkcompany_socks.service.SocksService;
import ru.pivovarov.socks.backsparkcompany_socks.service.impl.util.SocksUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SocksServiceImpl implements SocksService {

    private final SocksRepository repository;
    private final SocksMapper socksMapper;

    @Autowired
    public SocksServiceImpl(SocksRepository repository, SocksMapper socksMapper) {
        this.repository = repository;
        this.socksMapper = socksMapper;
    }

    @Override
    public List<SockDto> findAll(SockSearchCriteria criteria) {
        log.info("Find all socks with filter: specification = {}", criteria);
        return repository.findAll(SockSpecification.filterByParams(criteria), getSort(criteria))
                .stream()
                .map(socksMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SockDto findById(Long id) {
        log.info("Find socks by id: id = {}", id);
        return repository.findById(id)
                .map(socksMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("No such socks present"));
    }

    @Transactional
    @Override
    public SockDto update(Long id, SockDto sockDto) {
        log.info("Update socks: id = {}, SockDto = {}", id, sockDto);
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("No such socks present");
        }
        Sock sock = socksMapper.toEntity(sockDto);
        sock.setId(id);
        return socksMapper.toDto(repository.save(sock));
    }

    @Transactional
    @Override
    public SockDto incomeSock(SockDto sockDto) {
        log.info("Income socks: SockDto = {}", sockDto);
        Sock sock = repository.findById(sockDto.getId())
                .orElseThrow(() -> new NoSuchElementException("No such socks present"));
        sock.setQuantity(sock.getQuantity() + sockDto.getQuantity());
        return socksMapper.toDto(sock);
    }

    @Transactional
    @Override
    public SockDto outcomeSock(SockDto sockDto) {
        log.info("Outcome socks: SockDto = {}", sockDto);
        Sock sock = repository.findById(sockDto.getId())
                .orElseThrow(() -> new NoSuchElementException("No such socks present"));
        if (sock.getQuantity() < sockDto.getQuantity()) {
            throw new NoSocksInWarehouseException(
                    String.format("There are not %d socks in warehouse. %d socks left",
                            sockDto.getQuantity(), sock.getQuantity()));
        }
        sock.setQuantity(sock.getQuantity() - sockDto.getQuantity());
        return socksMapper.toDto(sock);
    }

    @Transactional
    @Override
    public List<SockDto> uploadBatchSocks(MultipartFile file) throws IOException {
        List<Sock> socks = SocksUtils.processFile(file);
        log.info("Upload batch socks: {}", socks);
        return repository.saveAll(socks)
                .stream()
                .map(socksMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Delete socks by id: id = {}", id);
        repository.deleteById(id);
    }

    private Sort getSort(SockSearchCriteria criteria) {
        if (criteria == null || criteria.getSortBy() == null) {
            return Sort.unsorted();
        }
        return Sort.by(Sort.Direction.fromString(criteria.getSortOrder()), criteria.getSortBy());
    }
}
