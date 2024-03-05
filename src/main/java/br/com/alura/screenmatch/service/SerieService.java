package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.DTO.EpisodioDTO;
import br.com.alura.screenmatch.DTO.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repository;


    private List<SerieDTO> converterDados(List<Serie> series){
        return series.stream()
                .map(se -> new SerieDTO(se.getId(), se.getTitulo(), se.getTotalTemporadas(), se.getAvaliacao(), se.getGenero() , se.getPoster(), se.getSinopse(), se.getAtores()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterDadosSerie(){
        return converterDados(repository.findAll());

    }

    public List<SerieDTO> TopSerie() {
        return converterDados(repository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converterDados(repository.BuscarPorTop5Lancamentos());
    }

    public  SerieDTO obterSeriePorID(Long id){
        Optional<Serie> serie = repository.findById(id);

        if(serie.isPresent()){
            Serie se = serie.get();
            return new SerieDTO(se.getId(), se.getTitulo(), se.getTotalTemporadas(), se.getAvaliacao(), se.getGenero() , se.getPoster(), se.getSinopse(), se.getAtores());
        }

        return null;
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);

        if(serie.isPresent()){
            Serie se = serie.get();
            return se.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getId(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio()))
                    .collect(Collectors.toList());

        }

        return null;
    }

    public List<EpisodioDTO> obterEpisodiosPorTemporada(Long id, Long temporada) {
        return repository.buscarEpisodioPorTemporada(id, temporada)
                .stream()
                .map(e -> new EpisodioDTO(e.getId(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeriePorGenero(String genero) {
        Categoria categoria = Categoria.fromPortugues(genero);
        return converterDados(repository.findByGenero(categoria));
    }

    public List<EpisodioDTO> obterTopEpisodios(Long id) {
        return repository.buscarTopEpisodios(id)
                .stream()
                .map(e -> new EpisodioDTO(e.getId(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }
}
