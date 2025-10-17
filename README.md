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

```json
GET http://localhost:8080/socios/33372251006278
Content-Type: application/json

```

## Exemplo de Resposta

```json
{
    "cnpj": "33372251006278",
    "nome": "IBM BRASIL - INDUSTRIA MAQUINA E SERVIÇO LTDA",
    "participacao": 20.0,
    "cep": "13186525",
    "razaoSocial": "IBM BRASIL-INDUSTRIA MAQUINAS E SERVICOS LIMITADA",
    "nomeFantasia": "",
    "naturezaJuridica": "Sociedade Empresária Limitada",
    "situacaoCadastral": "Ativa",
    "mapaEmbedUrl": "https://www.google.com/maps?q=RODOVIA+JORNALISTA+FRANCISCO+AGUIRRE+PROENCA+S%2FN+KM+09%2C+CHACARAS+ASSAY%2C+Hortol%C3%A2ndia+-+SP%2C+13186-525%2C+Brasil&output=embed",
    "estabelecimentos": [
        {
            "cnpj": "33372251006278",
            "tipo": "Filial",
            "nomeFantasia": "",
            "situacaoCadastral": "Ativa",
            "dataSituacaoCadastral": "2005-11-03",
            "dataInicioAtividade": "1971-03-26",
            "tipoLogradouro": "RODOVIA",
            "logradouro": "JORNALISTA FRANCISCO AGUIRRE PROENCA",
            "numero": "S/N",
            "complemento": "KM    09",
            "bairro": "CHACARAS ASSAY",
            "cep": "13186525",
            "cidade": "Hortolândia",
            "cidadeIbgeId": 3519071,
            "estadoSigla": "SP",
            "estadoNome": "São Paulo",
            "telefone1": "1121323053",
            "telefone2": "",
            "email": "ibmevoce@br.ibm.com",
            "atividadePrincipal": {
                "id": "4751201",
                "secao": "G",
                "divisao": "47",
                "grupo": "47.5",
                "classe": "47.51-2",
                "subclasse": "4751-2/01",
                "descricao": "Comércio varejista especializado de equipamentos e suprimentos de informática"
            },
            "atividadesSecundarias": [
                {
                    "id": "6203100",
                    "secao": "J",
                    "divisao": "62",
                    "grupo": "62.0",
                    "classe": "62.03-1",
                    "subclasse": "6203-1/00",
                    "descricao": "Desenvolvimento e licenciamento de programas de computador não customizáveis"
                },
                {
                    "id": "6311900",
                    "secao": "J",
                    "divisao": "63",
                    "grupo": "63.1",
                    "classe": "63.11-9",
                    "subclasse": "6311-9/00",
                    "descricao": "Tratamento de dados, provedores de serviços de aplicação e serviços de hospedagem na Internet"
                },
                {
                    "id": "6319400",
                    "secao": "J",
                    "divisao": "63",
                    "grupo": "63.1",
                    "classe": "63.19-4",
                    "subclasse": "6319-4/00",
                    "descricao": "Portais, provedores de conteúdo e outros serviços de informação na Internet"
                },
                {
                    "id": "8220200",
                    "secao": "N",
                    "divisao": "82",
                    "grupo": "82.2",
                    "classe": "82.20-2",
                    "subclasse": "8220-2/00",
                    "descricao": "Atividades de teleatendimento"
                },
                {
                    "id": "8630503",
                    "secao": "Q",
                    "divisao": "86",
                    "grupo": "86.3",
                    "classe": "86.30-5",
                    "subclasse": "8630-5/03",
                    "descricao": "Atividade médica ambulatorial restrita a consultas"
                },
                {
                    "id": "8630599",
                    "secao": "Q",
                    "divisao": "86",
                    "grupo": "86.3",
                    "classe": "86.30-5",
                    "subclasse": "8630-5/99",
                    "descricao": "Atividades de atenção ambulatorial não especificadas anteriormente"
                }
            ]
        }
    ]
}
```
