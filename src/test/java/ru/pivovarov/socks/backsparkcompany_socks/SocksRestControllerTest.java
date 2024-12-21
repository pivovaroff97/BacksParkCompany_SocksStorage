package ru.pivovarov.socks.backsparkcompany_socks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.pivovarov.socks.backsparkcompany_socks.controller.SocksRestController;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockDto;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockSearchCriteria;
import ru.pivovarov.socks.backsparkcompany_socks.service.SocksService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocksRestController.class)
public class SocksRestControllerTest {

    @MockBean
    private SocksService socksService;

    @Autowired
    private MockMvc mockMvc;

    private final static String URL = "/api/socks";

    private SockDto socksDto;

    private final List<SockDto> socksDtos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        socksDto = SockDto.builder()
                .id(1L)
                .color("black")
                .cottonPercent((byte) 70)
                .quantity(200)
                .build();
        socksDtos.add(socksDto);
        socksDtos.add(SockDto.builder()
                .id(2L)
                .color("white")
                .cottonPercent((byte) 90)
                .quantity(400).build());
    }

    @Test
    public void shouldReturnAllSocks() throws Exception {
        Mockito.when(socksService.findAll(ArgumentMatchers.any(SockSearchCriteria.class))).thenReturn(socksDtos);
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
        Mockito.verify(socksService, Mockito.times(1))
                .findAll(ArgumentMatchers.any(SockSearchCriteria.class));
    }

    @Test
    public void shouldReturnSocksByParameters() throws Exception {
        Mockito.when(socksService.findAll(ArgumentMatchers.any(SockSearchCriteria.class)))
                .thenReturn(List.of(socksDto));
        mockMvc.perform(get(URL)
                        .param("color", "black")
                        .param("operation", "between")
                        .param("cotton", String.valueOf(socksDto.getCottonPercent())))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(socksDto.getId()))
                .andExpect(jsonPath("$[0].color").value(socksDto.getColor()))
                .andExpect(jsonPath("$[0].cottonPercent").value(String.valueOf(socksDto.getCottonPercent())));
        Mockito.verify(socksService, Mockito.times(1))
                .findAll(ArgumentMatchers.any(SockSearchCriteria.class));
    }

    @Test
    public void shouldReturnSocksByParametersBetween() throws Exception {
        Mockito.when(socksService.findAll(ArgumentMatchers.any(SockSearchCriteria.class)))
                .thenReturn(List.of(socksDto));
        mockMvc.perform(get(URL)
                        .param("color", "black")
                        .param("operation", "between")
                        .param("minCotton", String.valueOf(socksDto.getCottonPercent()))
                        .param("maxCotton", String.valueOf(socksDto.getCottonPercent() + 10)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(socksDto.getId()))
                .andExpect(jsonPath("$[0].color").value(socksDto.getColor()))
                .andExpect(jsonPath("$[0].cottonPercent").value(String.valueOf(socksDto.getCottonPercent())));
        Mockito.verify(socksService, Mockito.times(1))
                .findAll(ArgumentMatchers.any(SockSearchCriteria.class));
    }
}
