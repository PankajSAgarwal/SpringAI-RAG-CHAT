# Conversational Memory
## In-memory converstaion using `MessageChatMemoryAdvisor` or `PromptChatMemoryAdvisor`

### Pre-requisite

- Qdrant vector store running in docker
- set the below properties in src/main/resources/application.properties 
```properties
spring.ai.vectorstore.qdrant.host=localhost
spring.ai.vectorstore.qdrant.port=6334

### Configuring chat memory size
```
@Bean

```
@Bean
ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(10)
            .build();
}
```
### Specifying the converstaion id
- Each conversation has an ID associated with it. The conversation ID is `default` if not specified.
- If your application has many users, you will want to keep their conversations distinct from each other. To do that you will need to assign a unique conversation ID.
- Inject a custome conversation ID header  in the controller
```java
@PostMapping(path = "/ask", produces = "application/json")
public Answer ask(
        @RequestHeader(name = "X_AI_CONVERSATION_ID", defaultValue = "default") String conversationID,
        @RequestBody @Valid Question question) {
    return boardGameService.askQuestion(question, conversationID);
}
```
- Set the conversation id as parameter in the chatclient advisor
```java
return  chatClient
            .prompt()
            .system(systemSpec -> systemSpec.text(promptTemplate).param("gameTitle", question.gameTitle()))
            .user(question.question())
            .advisors(advisorSpec ->
                    advisorSpec.param(FILTER_EXPRESSION, gameNameMatch).param(CONVERSATION_ID, conversationID))
            .call()
            .entity(Answer.class);
```

As you can see from below conversation history, in the third conversation, as soon as conversation id is changed , LLM is unable to answer the question as LLM cannot infer what `it` in the conversation refers 

```noformat
http :8080/ask question="What is the Burger Force Field Card?" gameTitle="Burger Battle" X_AI_CONVERSATION_ID:conversation_1  -b
{
    "answer": "The Burger Force Field card protects your Burger from all Battle Cards.",
    "gameTitle": "Burger Battle"
}


http :8080/ask question="Does it protect against Burgerpocalyse?" gameTitle="Burger Battle"  X_AI_CONVERSATION_ID:conversation_1 -b
{
    "answer": "No, the Burger Force Field card does not protect against the Burgerpocalypse card. When Burgerpocalypse is played, it destroys all players' ingredients regardless of protection.",
    "gameTitle": "Burger Battle"
}


http :8080/ask question="How do you remove it?" gameTitle="Burger Battle"  X_AI_CONVERSATION_ID:conversation_2 -b
{
    "answer": "I can't answer the question.",
    "gameTitle": "Burger Battle"
}

```
## Run the application
- Run the application
```shell
mvn spring-boot:run
```
- send the below request using HTTPie
```shell
 board-game-buddy http :8080/ask question="What is the Burger Force Field Card?" gameTitle="Burger Battle" -b
{
    "answer": "The Burger Force Field is a Battle Card that protects your Burger from all Battle Cards.",
    "gameTitle": "Burger Battle"
}


board-game-buddy http :8080/ask question="Does it protect against Burgerpocalyse?" gameTitle="Burger Battle" -b
{
    "answer": "No, the Burger Force Field does not protect against the Burgerpocalypse card, as it destroys all players' ingredients regardless of protection.",
    "gameTitle": "Burger Battle"
}

```




