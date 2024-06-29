package net.khoudmi.peenset;

import net.khoudmi.peenset.model.Team;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
public class ChatController {
    private ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(String question){
        String systemMessage = """
                Donnes moi l'equipe qui a gagne la coupe du monde du foot de l'annee qui sera fournie
                Je veux le resultat au format json sous la forme suivante :
                - Nom de l'equipe
                - Liste des joueurs
                - Pays organisateur
                """;
        return chatClient.prompt()
                .system(systemMessage)
                .user(question)
                .call()
                .content();
    }

    @GetMapping(value = "/chat2")
    public Team chat2(String question){
        String systemMessage = """
                Donnes moi l'equipe qui a gagne la coupe du monde du foot de l'annee qui sera fournie.
                """;
        return chatClient.prompt()
                .system(systemMessage)
                .user(question)
                .call()
                .entity(Team.class);
    }

    @GetMapping(value = "/chat3")
    public List<Team> chat3(){
        String systemMessage = """
                Donnes moi l'equipe qui a gagne les 4 dernieres 
                coupes du monde du foot.
                """;
        return chatClient.prompt()
                .system(systemMessage)
                .call()
                .entity(new ParameterizedTypeReference<List<Team>>() {
                });
    }

    @GetMapping("/ocr")
    public String ocr() throws IOException {
        Resource resource = new ClassPathResource("depenses.png");
        byte[] data = resource.getContentAsByteArray();
        String userMessageText = """
                Analyses l'image qui contient du texte manuscrit et donnes moi,
                au format json comment les depenses du salaire sont reparties.
                """;
        UserMessage userMessage = new UserMessage(userMessageText, List.of(
                new Media(MimeTypeUtils.IMAGE_PNG, data)
        ));

        Prompt prompt = new Prompt(userMessage);
        return chatClient.prompt(prompt).call().content();
    }

    @GetMapping(path="/generateImage", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateImageDALLE() throws IOException {
        OpenAiImageApi openAiImageApi = new OpenAiImageApi("sk-proj-kack7rbpulGxemIwvhzBT3BlbkFJo5H3wfudQy9KSQGFW0KR");
        OpenAiImageModel openAiImageModel = new OpenAiImageModel(openAiImageApi);
        ImageResponse response = openAiImageModel.call(
                new ImagePrompt("un chat avec un costume dans une fete avec un cafe dans sa main",
                        OpenAiImageOptions.builder()
                                .withModel("dall-e-3")
                                .withQuality("hd")
                                .withN(1)
                                .withResponseFormat("b64_json")
                                .withHeight(1024)
                                .withWidth(1024)
                                .build())
        );

        String image = response.getResult().getOutput().getB64Json();
        byte[] decode = Base64.getDecoder().decode(image);
        return decode;
    }
}
