package com.example.demo;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;

@DataMongoTest
public class CSVUtilTest {

    @Autowired
    public PlayerRepository playerRepository;

    private CsvUtilFile csvUtilFile;

    @BeforeEach
    void before(){
        this.csvUtilFile= new CsvUtilFile(playerRepository);
    }

    @Test
    void converterData(){
        List<Player> list = csvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void stream_filtrarJugadoresMayoresA35(){
        List<Player> list = csvUtilFile.getPlayers();
        Map<String, List<Player>> listFilter = list.parallelStream()
                .filter(player -> player.age >= 35)
                .map(player -> {
                    player.name = player.name.toUpperCase(Locale.ROOT);
                    return player;
                })
                .flatMap(playerA -> list.parallelStream()
                        .filter(playerB -> playerA.club.equals(playerB.club))
                )
                .distinct()
                .collect(Collectors.groupingBy(Player::getClub));

        assert listFilter.size() == 322;
    }

    @Test
    void reactive_filtrarJugadoresMayoresA35(){
        List<Player> list = csvUtilFile.getPlayers();
        Flux<Player> listFlux = Flux.fromStream(list.parallelStream()).cache();
        Mono<Map<String, Collection<Player>>> listFilter = listFlux
                .filter(player -> player.age >= 35)
                .map(player -> {
                    player.name = player.name.toUpperCase(Locale.ROOT);
                    return player;
                })
                .buffer(100)
                .flatMap(playerA -> listFlux
                         .filter(playerB -> playerA.stream()
                                 .anyMatch(a ->  a.club.equals(playerB.club)))
                )
                .distinct()
                .collectMultimap(Player::getClub);

        assert listFilter.block().size() == 322;
    }
    @Test
    void reactive_filtrarJugadoresMayoresA35PorUnEquipoEspecifico(){
        List<Player> list = csvUtilFile.getPlayers();
        Flux<Player> listFlux = Flux.fromStream(list.parallelStream()).cache();
        Mono<Map<String, Collection<Player>>> listFilter = listFlux
                .filter(player -> player.age >= 35 && player.club.equals("Perth Glory"))
                .map(player -> {
                    player.name = player.name.toUpperCase(Locale.ROOT);
                    return player;
                })
                .collectMultimap(Player::getClub);
        listFilter.block().forEach((equipo,players)->{
                        System.out.println(equipo);
                players.stream().forEach(p-> System.out.println(p.name+"-"+p.age));
            assert players.size()==4;
        });
    }

    @Test
    void reactive_filtrarNacionalidad() {
        List<Player> list = csvUtilFile.getPlayers();
        Flux<Player> listFlux = Flux.fromStream(list.parallelStream()).cache();
        Mono<Map<String, Collection<Player>>> listFilter = listFlux
                .filter(player -> player.age == 27)
                .map(player -> {
                    player.name = player.name.toUpperCase(Locale.ROOT);
                    return player;
                })
                .collectMultimap(Player::getNational);
        listFilter.block().forEach((national, players) -> {
            System.out.println(national);
            players.stream().sorted((p,w)->w.winners-p.winners).forEach(p -> System.out.println(p.name + "- Partidos ganados: " +p.winners));
        });
    }

    private double converDouble(int num,int num2){
        Double.valueOf(num);

        return num;
    }

}
