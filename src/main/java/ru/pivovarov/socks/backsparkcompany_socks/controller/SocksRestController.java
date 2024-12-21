package ru.pivovarov.socks.backsparkcompany_socks.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockDto;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockSearchCriteria;
import ru.pivovarov.socks.backsparkcompany_socks.exception.ApiError;
import ru.pivovarov.socks.backsparkcompany_socks.service.SocksService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/socks")
public class SocksRestController {

    private final SocksService socksService;

    public SocksRestController(SocksService socksService) {
        this.socksService = socksService;
    }

    @GetMapping
    public List<SockDto> getSocks(SockSearchCriteria searchCriteria) {
        return socksService.findAll(searchCriteria);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks found"),
            @ApiResponse(responseCode = "404", description = "Socks not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping(("/{id}"))
    public SockDto getSocksById(@PathVariable Long id) {
        return socksService.findById(id);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks updated"),
            @ApiResponse(responseCode = "404", description = "Such socks id not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public SockDto updateSock(@PathVariable Long id, @RequestBody SockDto sockDto) {
        return socksService.update(id, sockDto);
    }

    @PostMapping("/income")
    public SockDto income(@RequestBody SockDto sockDto) {
        return socksService.incomeSock(sockDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks outcome"),
            @ApiResponse(responseCode = "400", description = "There are not so many socks in Warehouse",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/outcome")
    public SockDto outcome(@RequestBody SockDto sockDto) {
        return socksService.outcomeSock(sockDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Socks uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid CSV format",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/batch")
    public List<SockDto> uploadBatchSocks(@RequestParam MultipartFile file) throws IOException {
        return socksService.uploadBatchSocks(file);
    }
}