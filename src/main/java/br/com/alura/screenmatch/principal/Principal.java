package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private SerieRepository repositorio;

    List<DadosSerie> dadosSeries = new ArrayList<>();

    List<Serie> series = new ArrayList<>();

    Optional<Serie> SerieBusca;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar series buscadas
                    4 - Buscar pelo titulo
                    5 - Buscar por Ator
                    6 - Top 5 series;
                    7 - Buscar por Genero
                    8 - Buscar por Temporada
                    9 - Buscar por Episodio por titulo
                    10 - Top 5 series
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarPeloTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscaTop5Series();
                    break;
                case 7:
                    buscaPorGenero();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    filtraPorTitulEpisodio();
                    break;
                case 10:
                    buscaTop5Episodios();
                    break;
                case 11:
                    buscaEpisodioPorAno();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        //dadosSeries.add(dados);

        Serie serie = new Serie(dados);

        repositorio.save(serie);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

        /*
        Forma de encontrae pelo stream
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();
         */


        //Forma de encontrar por JPA
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }else {
            System.out.println("Serie nao encotrada!");
        }


    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    private void buscarPeloTitulo() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

         SerieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);


        if(SerieBusca.isPresent()){
            System.out.println("Serie encontrada!: " + SerieBusca);
        }else{
            System.out.println("Serie não encontrada!");
        }

    }

    private void buscarSeriePorAtor(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome do Ator que gostaria de buscar");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliaçao da serie: ");
        var avaliacao = leitura.nextDouble();


        List<Serie> serieBuscada = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        System.out.println("Serie Encontradas:");
        serieBuscada.forEach(s -> System.out.println("Titulo da serie: " + s.getTitulo()  + " e avaliação:" + s.getAvaliacao() ));

    }


    private void buscaTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();

        topSeries.forEach(
                s -> System.out.println("Titulo da serie: " + s.getTitulo()  + " e avaliação:" + s.getAvaliacao() )
        );
    }

    private void buscaPorGenero(){
        System.out.println("Digite a o genero/categoria que deseja buscar: ");
        var nomeGenero = leitura.nextLine();

        Categoria categoria = Categoria.ToPortugues(nomeGenero);

        List<Serie> seriesCat = repositorio.findByGenero(categoria);

        seriesCat.forEach(System.out::println);

    }


    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.buscarPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }


    private void filtraPorTitulEpisodio(){

        System.out.println("Digite o nome do episodio para busca");
        var nomeEP = leitura.nextLine();

        List<Episodio> ep = repositorio.buscarPorTituloEpisodio(nomeEP);

        ep.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));


    }

    private void buscaTop5Episodios(){
            buscarPeloTitulo();

        if(SerieBusca.isPresent()){

            List<Episodio> topEpisodio = repositorio.buscaTop5Episodio(SerieBusca.get());

            topEpisodio.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo()));
        }

    }

    private void buscaEpisodioPorAno(){
        buscarPeloTitulo();

        if(SerieBusca.isPresent()){
            Serie serie = SerieBusca.get();

            System.out.println("Coloque o ano que gostaria de buscar: ");
            int ano = leitura.nextInt();

            List<Episodio> ep = repositorio.buscaEpisodioPorAno(serie,ano);

            ep.forEach(System.out::println);
        }
    }

}