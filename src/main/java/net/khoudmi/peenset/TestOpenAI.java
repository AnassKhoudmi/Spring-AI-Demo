package net.khoudmi.peenset;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.List;

public class TestOpenAI {
    public static void main(String[] args) {
        OpenAiApi openAiApi = new OpenAiApi("sk-proj-kack7rbpulGxemIwvhzBT3BlbkFJo5H3wfudQy9KSQGFW0KR");
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi, OpenAiChatOptions.builder()
                .withMaxTokens(4000)
                .withTemperature(0F)
                .withModel("gpt-4-turbo")
                .build());
        SystemMessage systemMessage = new SystemMessage("""
                Donnes moi l'equipe qui a gagne la coupe du monde du foot de l'annee qui sera fournie
                Je veux le resultat au format json sous la forme suivante :
                - Nom de l'equipe
                - Liste des joueurs
                - Pays organisateur
                """);
        UserMessage userMessage = new UserMessage("Je veux le resultat pour l'annee 2022");
        Prompt zeroshotPrompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = openAiChatModel.call(zeroshotPrompt);
        System.out.println(chatResponse.getResult().getOutput().getContent());
    }
}
