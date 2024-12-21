package ru.pivovarov.socks.backsparkcompany_socks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockDto;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockSearchCriteria;
import ru.pivovarov.socks.backsparkcompany_socks.exception.NoSocksInWarehouseException;
import ru.pivovarov.socks.backsparkcompany_socks.exception.UploadBatchSocksFileException;
import ru.pivovarov.socks.backsparkcompany_socks.mapper.SocksMapper;
import ru.pivovarov.socks.backsparkcompany_socks.mapper.SocksMapperImpl;
import ru.pivovarov.socks.backsparkcompany_socks.model.Sock;
import ru.pivovarov.socks.backsparkcompany_socks.repository.SocksRepository;
import ru.pivovarov.socks.backsparkcompany_socks.repository.specification.OperationType;
import ru.pivovarov.socks.backsparkcompany_socks.service.SocksService;
import ru.pivovarov.socks.backsparkcompany_socks.service.impl.SocksServiceImpl;
import ru.pivovarov.socks.backsparkcompany_socks.service.impl.util.SocksUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;

public class SocksServiceTest {

    private final SocksRepository repository;

    private final SocksMapper mapper;

    private final SocksService socksService;

    private SockDto socksDto;
    private final List<Sock> socks = new ArrayList<>();

    public SocksServiceTest() {
        this.repository = Mockito.mock(SocksRepository.class);
        this.mapper = new SocksMapperImpl();
        this.socksService = new SocksServiceImpl(repository, mapper);
    }

    @BeforeEach
    public void setUp() {
        socksDto = SockDto.builder()
                .id(1L)
                .color("black")
                .cottonPercent((byte) 80)
                .quantity(200).build();
        socks.add(Sock.builder()
                .id(2L)
                .color("white")
                .cottonPercent((byte) 90)
                .quantity(400).build());
        socks.add(mapper.toEntity(socksDto));
    }

    @Test
    public void shouldReturnAllSocks() {
        Mockito.when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(socks);
        List<SockDto> curSocks = socksService.findAll(new SockSearchCriteria());

        Assertions.assertEquals(socks.size(), curSocks.size());

        Mockito.verify(repository, Mockito.times(1))
                .findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    public void shouldReturnSocksByColorAndCottonPercentLessThan() {
        SockSearchCriteria searchCriteria = new SockSearchCriteria();
        searchCriteria.setColor("black");
        searchCriteria.setOperation(OperationType.LESS_THAN.getValue());
        searchCriteria.setCotton((byte) 90);

        List<Sock> sockList = List.of(mapper.toEntity(socksDto));
        Mockito.when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(sockList);
        List<SockDto> dtos = socksService.findAll(searchCriteria);

        Assertions.assertEquals(sockList.size(), dtos.size());
        Assertions.assertEquals(1, sockList.size());
        Assertions.assertEquals(socksDto.getColor(), dtos.get(0).getColor());
        Assertions.assertTrue(socksDto.getCottonPercent() < 90);

        Mockito.verify(repository, Mockito.times(1))
                .findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    public void shouldReturnSocksCottonPercentEqual() {
        SockSearchCriteria searchCriteria = new SockSearchCriteria();
        searchCriteria.setColor("black");
        searchCriteria.setOperation(OperationType.EQUAL.getValue());
        searchCriteria.setCotton((byte) 80);

        List<Sock> sockList = List.of(mapper.toEntity(socksDto));
        Mockito.when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(sockList);
        List<SockDto> dtos = socksService.findAll(searchCriteria);

        Assertions.assertEquals(sockList.size(), dtos.size());
        Assertions.assertEquals(1, sockList.size());
        Assertions.assertEquals(socksDto.getColor(), dtos.get(0).getColor());
        Assertions.assertEquals(80, (byte) socksDto.getCottonPercent());

        Mockito.verify(repository, Mockito.times(1))
                .findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    public void shouldReturnSocksCottonPercentBetween() {
        SockSearchCriteria searchCriteria = new SockSearchCriteria();
        searchCriteria.setColor("black");
        searchCriteria.setOperation(OperationType.BETWEEN.getValue());
        searchCriteria.setMinCotton(socksDto.getCottonPercent());
        searchCriteria.setMaxCotton((byte) 90);

        List<Sock> sockList = List.of(mapper.toEntity(socksDto));
        Mockito.when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(sockList);
        List<SockDto> dtos = socksService.findAll(searchCriteria);

        Assertions.assertEquals(sockList.size(), dtos.size());
        Assertions.assertEquals(1, sockList.size());
        Assertions.assertEquals(socksDto.getColor(), dtos.get(0).getColor());
        Assertions.assertEquals(80, (byte) socksDto.getCottonPercent());

        Mockito.verify(repository, Mockito.times(1))
                .findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    public void shouldReturnSocksById() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(mapper.toEntity(socksDto)));
        SockDto dto = socksService.findById(1L);

        Assertions.assertEquals(socksDto.getColor(), dto.getColor());
        Assertions.assertEquals(socksDto.getCottonPercent(), dto.getCottonPercent());

        Mockito.verify(repository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void shouldThrowNoSuchElementException_WhenFindSocksById() {
        Mockito.when(repository.findById(1L)).thenThrow(NoSuchElementException.class);
        Assertions.assertThrows(NoSuchElementException.class, () -> socksService.findById(1L));
        Mockito.verify(repository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void shouldIncomeSocks() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(mapper.toEntity(socksDto)));
        SockDto dto = socksService.incomeSock(SockDto.builder()
                .id(1L)
                .quantity(50)
                .build());
        Assertions.assertEquals(socksDto.getQuantity() + 50, dto.getQuantity());
    }

    @Test
    public void shouldOutcomeSocks() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(mapper.toEntity(socksDto)));
        SockDto dto = socksService.outcomeSock(SockDto.builder()
                .id(1L)
                .quantity(socksDto.getQuantity() / 2)
                .build());
        Assertions.assertEquals(socksDto.getQuantity() - socksDto.getQuantity() / 2, dto.getQuantity());
    }

    @Test
    public void shouldThrowNoSocksInWarehouseException_WhenOutcomeSocks() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(mapper.toEntity(socksDto)));
        Assertions.assertThrows(NoSocksInWarehouseException.class, () -> socksService.outcomeSock(
                SockDto.builder()
                .id(1L)
                .quantity(socksDto.getQuantity() + 1)
                .build())
        );
    }

    @Test
    public void shouldUpdateSocksById() {
        Sock socksToUpdate = Sock.builder()
                .id(1L)
                .color("gray")
                .quantity(100)
                .cottonPercent((byte) 50).build();
        Mockito.when(repository.existsById(1L)).thenReturn(true);
        Mockito.when(repository.save(any(Sock.class))).thenReturn(socksToUpdate);
        SockDto dto = socksService.update(1L, mapper.toDto(socksToUpdate));

        Assertions.assertEquals(socksToUpdate.getColor(), dto.getColor());
        Assertions.assertEquals(socksToUpdate.getQuantity(), dto.getQuantity());

        Mockito.verify(repository, Mockito.times(1)).existsById(1L);
        Mockito.verify(repository, Mockito.times(1)).save(any(Sock.class));
    }

    @Test
    public void shouldThrowNoSuchElementException_WhenUpdateSocksById() {
        Mockito.when(repository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(NoSuchElementException.class, () -> socksService.update(1L, socksDto));
        Mockito.verify(repository, Mockito.times(1)).existsById(1L);
        Mockito.verify(repository, Mockito.times(0)).save(any(Sock.class));
    }

    @Test
    public void shouldReturnSocksFromCsvFile() throws IOException {
        String testCsv = """
                color,cottonPercent,quantity
                green,40,100
                red,50,200
                yellow,60,300
                blue,70,400
                """;
        MultipartFile file = new MockMultipartFile("test.csv", testCsv.getBytes());
        List<Sock> socksFromCsv = SocksUtils.processFile(file);
        Mockito.when(repository.saveAll(anyCollection())).thenReturn(socksFromCsv);
        List<SockDto> savedSocks = socksService.uploadBatchSocks(file);
        Assertions.assertEquals(socksFromCsv.size(), savedSocks.size());
        Mockito.verify(repository, Mockito.times(1)).saveAll(anyCollection());
    }

    @Test
    public void shouldThrowUploadBatchSocksFileException_whenUploadCsvFile() throws IOException {
        String testCsv = """
                    color,cottonPercent,quantity
                    green,40,100
                    red,50,200
                    yellow,60,3 hundred
                    blue,70,400
                """;
        MultipartFile multipartFile = new MockMultipartFile("test.csv", testCsv.getBytes());
        Mockito.when(repository.saveAll(anyCollection()))
                .thenThrow(UploadBatchSocksFileException.class);
        Assertions.assertThrows(UploadBatchSocksFileException.class, () -> socksService.uploadBatchSocks(multipartFile));
        Mockito.verify(repository, Mockito.times(0)).saveAll(anyCollection());
    }
}
