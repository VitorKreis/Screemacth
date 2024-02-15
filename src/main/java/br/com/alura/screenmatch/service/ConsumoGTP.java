package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsumoGTP {
    public static String obterTraducao(String texto) {
        OpenAiService service = new OpenAiService("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");

        CompletionRequest requisicao = CompletionRequest.builder()
                .model("babbage-002")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var resposta = service.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
    }
}
