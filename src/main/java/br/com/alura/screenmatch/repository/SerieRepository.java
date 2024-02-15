package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);


    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporada AND s.avaliacao >= :avaliacao")
    List<Serie> buscarPorTemporadaEAvaliacao(int totalTemporada, double avaliacao);

    @Query("SELECT ep FROM Serie s JOIN s.episodios ep WHERE ep.titulo ILIKE %:nomeEP%")
    List<Episodio> buscarPorTituloEpisodio(String nomeEP);


    @Query("SELECT ep FROM Serie s JOIN s.episodios ep WHERE s = :serie ORDER BY ep.avaliacao DESC LIMIT 5")
    List<Episodio> buscaTop5Episodio(Serie serie);

    @Query("SELECT ep FROM Serie s JOIN s.episodios ep WHERE s = :serie AND YEAR(ep.dataLancamento) >= :ano")
    List<Episodio> buscaEpisodioPorAno(Serie serie, int ano);
}
