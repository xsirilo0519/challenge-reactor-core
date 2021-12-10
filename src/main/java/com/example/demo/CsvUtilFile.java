package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class CsvUtilFile {

    public PlayerRepository playerRepository;

    public CsvUtilFile(PlayerRepository playerRepository) {
    this.playerRepository=playerRepository;
    }

    public List<Player> getPlayers(){
        List<Player> list=playerRepository.findAll();
           return list;
    }

//    public static List<Player> getPlayers(){
//        var uri =  CsvUtilFile.class.getClassLoader().getResource("data.csv");
//        List<Player> list = new ArrayList<>();
//        try (CSVReader reader = new CSVReader(new FileReader(uri.getFile()))) {
//            List<String[]> registers = reader.readAll();
//            registers.forEach(strings -> list.add(new Player(
//                    Integer.parseInt(strings[0].trim()),
//                    strings[1],
//                    Integer.parseInt(Optional.of(strings[2].trim()).filter(h -> !h.isBlank()).orElse("0")),
//                    strings[3],
//                    strings[4],
//                    Integer.parseInt(strings[5].trim()),
//                    Integer.parseInt(strings[6].trim()),
//                    strings[7]
//            )));
//
//            return list;
//
//        } catch (IOException | CsvException e) {
//            throw new IllegalArgumentException(e.getMessage());
//        }
//    }
}
