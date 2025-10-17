# API e Front de Simulação de Empréstimos

## 📌 Visão Geral
Este projeto consiste em consumir uma api terceiro de sócios e trazer suas informações para o front:


## 🛠️ Tecnologias Utilizadas
- Java 23
- Spring Boot 3.x
- Gradle
- Lombok


## 📋 Requisitos
- JDK 23
- Angular
- Spring Boot

<img width="1129" height="426" alt="image" src="https://github.com/user-attachments/assets/81144436-43e6-4e4a-a0a5-b4ad37b75401" />

<img width="1056" height="516" alt="image" src="https://github.com/user-attachments/assets/3eb790d1-4412-46c5-a849-3dcd685192f6" />

<img width="1034" height="1109" alt="image" src="https://github.com/user-attachments/assets/32282843-5dea-4909-9073-329164c1a6da" />

<img width="932" height="1314" alt="image" src="https://github.com/user-attachments/assets/c3b04104-a902-430c-8f09-4e795eb48e8a" />



## 🚀 Como Executar
```bash
mvn spring-boot:run

## Como usar

2. **Build**:  
   ```
   gradle clean install
   ```
3. **Executar**:  
   ```
   gradle spring-boot:run
   ```

   **Front**:  
   ```
   clonar o projeto e dar os seguintes comando:
   npm install
   npm start
   ```


4. ## Exemplo de Requisição

```json
GET http://localhost:8080/socios?participacaoMin=20
Content-Type: application/json

```

## Exemplo de Resposta

```json
[
    {
        "cnpj": "45990181000189",
        "nome": "ROBERT BOSCH LIMITADA",
        "participacao": 28.0,
        "cep": ""
    },
    {
        "cnpj": "33372251006278",
        "nome": "IBM BRASIL - INDUSTRIA MAQUINA E SERVIÇO LTDA",
        "participacao": 20.0,
        "cep": ""
    }
]
```
