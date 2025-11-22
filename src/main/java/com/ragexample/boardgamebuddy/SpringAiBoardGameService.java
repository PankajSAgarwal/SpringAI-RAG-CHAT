package com.ragexample.boardgamebuddy;

import static org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor.FILTER_EXPRESSION;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {
    private final ChatClient chatClient;

    public SpringAiBoardGameService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource promptTemplate;

    @Override
    public Answer askQuestion(Question question) {
        String gameNameMatch = String.format("gameTitle == '%s'", normalizeGameTitle(question.gameTitle()));
        var answer = chatClient
                .prompt()
                .system(systemSpec -> systemSpec.text(promptTemplate).param("gameTitle", question.gameTitle()))
                .user(question.question())
                .advisors(advisorSpec -> advisorSpec.param(FILTER_EXPRESSION, gameNameMatch))
                .call()
                .entity(Answer.class);
        return answer;
    }

    private String normalizeGameTitle(String in) {
        return in.toLowerCase().replace(' ', '_');
    }
}
