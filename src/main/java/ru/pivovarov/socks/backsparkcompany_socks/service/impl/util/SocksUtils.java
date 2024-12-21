package ru.pivovarov.socks.backsparkcompany_socks.service.impl.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;
import ru.pivovarov.socks.backsparkcompany_socks.exception.UploadBatchSocksFileException;
import ru.pivovarov.socks.backsparkcompany_socks.model.Sock;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SocksUtils {

    public static List<Sock> processFile(MultipartFile file) throws IOException {
        List<Sock> socks = new ArrayList<>();
        try (InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream());
             CSVReader reader = new CSVReaderBuilder(inputStreamReader).build()) {
            String[] sock = reader.readNext();
            while ((sock = reader.readNext()) != null) {
                String color = sock[0];
                Byte cottonPercent = Byte.parseByte(sock[1]);
                int quantity = Integer.parseInt(sock[2]);
                socks.add(Sock.builder()
                        .color(color)
                        .cottonPercent(cottonPercent)
                        .quantity(quantity)
                        .build());
            }
        } catch (NumberFormatException e) {
            throw new UploadBatchSocksFileException("Can't parse csv file. Check socks parameters");
        } catch (CsvValidationException e) {
            throw new UploadBatchSocksFileException("Invalid csv format." + e.getMessage());
        }
        return socks;
    }
}
