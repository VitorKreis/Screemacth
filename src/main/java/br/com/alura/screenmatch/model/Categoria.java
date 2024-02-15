package br.com.alura.screenmatch.model;

public enum Categoria {

    CRIME("Crime", "Crime"),
    DRAMA("Drama",  "Drama"),

    COMEDIA("Comedy", "Comedia"),

    ACAO("Action", "Açao");

    private String categoriaOMDB;

    private String categoriaPortugues;
    Categoria(String categoriaOMDB, String categoriaPortugues){
        this.categoriaOMDB = categoriaOMDB;

        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOMDB.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria ToPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
