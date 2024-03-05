package br.com.alura.screenmatch.controller;



import br.com.alura.screenmatch.DTO.EpisodioDTO;
import br.com.alura.screenmatch.DTO.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SerieController {

    @Autowired
    private SerieService service;

    @GetMapping("/series")
    public List<SerieDTO> pegerTodasSeries(){
        return service.obterDadosSerie();
    }

    @GetMapping("/series/top5")
    public List<SerieDTO> obterTopSeries(){
        return service.TopSerie();
    }

    @GetMapping("/series/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return service.obterLancamentos();
    }

    @GetMapping("/series/{id}")
    public SerieDTO obterSeriePeloID(@PathVariable Long id){
        return service.obterSeriePorID(id);
    }


    @GetMapping("/series/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id){
        return service.obterTodasTemporadas(id);
    }


    @GetMapping("/series/{id}/temporadas/{temporada}")
    public List<EpisodioDTO> obterEpisodiosPorTemporada(@PathVariable Long id, @PathVariable Long temporada){
        return service.obterEpisodiosPorTemporada(id,temporada);
    }


    @GetMapping("/series/categoria/{genero}")
    public List<SerieDTO> obterSeriePorGenero(@PathVariable String genero){
        return service.obterSeriePorGenero(genero);
    }


    @GetMapping("/series/{id}/temporadas/top5")
    public List<EpisodioDTO> obterTopEpisodios(@PathVariable Long id){
        return service.obterTopEpisodios(id);
    }

}
