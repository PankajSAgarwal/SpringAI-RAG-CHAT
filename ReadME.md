# Conversational Memory

## Pre-requisite

- Qdrant vector store running in docker
- set the below properties in src/main/resources/application.properties 
```properties
spring.ai.vectorstore.qdrant.host=localhost
spring.ai.vectorstore.qdrant.port=6334

## Configuring chat memory size
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


(base) ➜  board-game-buddy http :8080/ask question="Does it protect against Burgerpocalyse?" gameTitle="Burger Battle" -b
{
    "answer": "No, the Burger Force Field does not protect against the Burgerpocalypse card, as it destroys all players' ingredients regardless of protection.",
    "gameTitle": "Burger Battle"
}

```




