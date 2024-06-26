# CRUD-PIX
### Introdução
O Pix é um arranjo de pagamentos e recebimentos instantâneos, disponível todos os dias do ano, com liquidação em tempo real de suas transações. Ele permite a transferência imediata de valores entre diferentes instituições, a qualquer hora e dia, entre pessoas físicas, empresas ou entes governamentais. O Pix é uma forma prática e rápida de pagar e receber valores.

### Chave Pix
Uma chave Pix é um apelido para a conta transacional que deve ser atribuído pelo titular da conta ou representante legal/operador permissionado. Ela é usada para identificar a conta corrente do cliente recebedor por meio de uma única informação. Ao registrar uma chave Pix, as mesmas devem ser armazenadas e disponibilizadas aos correntistas para consulta.

### Tipos de Chave Pix
**Número de Celular:** Inicia-se com "+", seguido do código do país, DDD, e número com nove dígitos.

**E-mail:** Contém "@" e tem um tamanho máximo de 77 caracteres.

**CPF:** Com 11 dígitos, incluindo os dígitos verificadores. Deve ser informado sem pontos ou traços.

**CNPJ:** Com 14 dígitos, incluindo os dígitos verificadores. Deve ser informado sem pontos ou traços.

**Chave Aleatória:** Um UUID. String alfanumérica com 36 posições. Deve ser informada sem pontuação.

### Limitações de Cadastro
**Pessoa Física:** Limite de até 5 chaves por conta.

**Pessoa Jurídica:** Limite de até 20 chaves por conta.

### Funcionalidades Principais
- Cadastro, busca, alteraçao e desativaçao de Usuarios.
- Cadastro, busca, alteraçao e desativaçao de Contas.
- Cadastro, busca, e desativaçao de chaves Pix.

### Requisitos de Instalação
**Linguagem de Programação:** Java
**Framework:** Spring Boot
**Banco de Dados:** MySQL
**Dependências:** Maven para gerenciamento de dependências

## Endpoints
### Usuarios
#### createUser
**Metodo**: POST  
**Endpoint**: `/api/user`

Este endpoint é utilizado para criar um usuario no sistema.

Este endpoint espera um body do tipo:
```json
{
    "personType": "fisica",
    "userName": "Rodrigo",
    "userLastName": "Nunes Santos",
    "identification": "48428781850",
    "phone": "+5511976110609",
    "email": "teste@gmail.com"
}
```

#### findAllUsers
**Metodo**: GET  
**Endpoint**: `/api/user`

Este endpoint é utilizado para buscar todos os usuarios no sistema.

#### findUserById
**Metodo**: GET  
**Endpoint**: `/api/user/{id}`

Este endpoint é utilizado para buscar um usuario especifico de acordo com o id dele. O id e um UUID.

#### updateUser
**Metodo**: PUT  
**Endpoint**: `/api/user/{id}`

Este endpoint é utilizado para atualizar as informaçoes de um usuario especifico de acordo com o id dele. O id e um UUID.

Este endpoint espera um body do tipo:
```json
{
    "personType": "fisica",
    "userName": "Rodrigo",
    "userLastName": "Nunes Santos", // opicional
    "identification": "48428781850",
    "phone": "+5511976110609",
    "email": "teste@gmail.com"
}
```

#### deleteUser
**Metodo**: DELETE  
**Endpoint**: `/api/user/{id}`

Este endpoint é utilizado para deletar um usuario especifico de acordo com o id dele. O id e um UUID.


### Contas
#### createAccount
**Metodo**: POST  
**Endpoint**: `/api/account`

Este endpoint é utilizado para criar uma conta no sistema.

Este endpoint espera um body do tipo:
```json
{
    "accountType": "corrente",
    "agencyNumber": 1234,
    "accountNumber": 12345678,
    "identification": "48428781850"
}
```

#### findAllAccounts
**Metodo**: GET  
**Endpoint**: `/api/account`

Este endpoint é utilizado para buscar todas as contas no sistema.

#### findAccountById
**Metodo**: GET  
**Endpoint**: `/api/account/{id}`

Este endpoint é utilizado para buscar uma conta especifica conforme o id dela. O id e um UUID.

#### updateAccount
**Metodo**: PUT  
**Endpoint**: `/api/account/{id}`

Este endpoint é utilizado para atualizar as informaçoes de uma conta especifica conforme o id dela. O id e um UUID.

Este endpoint espera um body do tipo:
```json
{
  "accountType": "corrente",
  "agencyNumber": 1234,
  "accountNumber": 12345678
}
```

#### deleteAccount
**Metodo**: DELETE  
**Endpoint**: `/api/account/{id}`

Este endpoint é utilizado para deletar uma conta especifica conforme o id dela. O id e um UUID.

### Chaves Pix
#### createPixKey
**Metodo**: POST  
**Endpoint**: `/api/pix`

Este endpoint é utilizado para criar uma chave Pix no sistema.

Este endpoint espera um body do tipo:
```json
{
    "keyType": "cpf",
    "keyValue": "48428781850",
    "agencyNumber": 1234,
    "accountNumber": 12345678
}
```

#### findAllPixKeys
**Metodo**: GET  
**Endpoint**: `/api/pix`

Este endpoint é utilizado para buscar todas as chaves Pixs no sistema.

#### findPixKeyById
**Metodo**: GET  
**Endpoint**: `/api/pix/{id}`

Este endpoint é utilizado para buscar uma chave Pix especifica conforme o id dela. O id e um UUID.

#### findPixKeyByType
**Metodo**: GET  
**Endpoint**: `/api/pix/by-type&keyType={keyType}`

Este endpoint é utilizado para buscar chaves Pix conforme o tipo da chave.

#### findPixKeyByAgencyAndAccount
**Metodo**: GET  
**Endpoint**: `/api/pix/by-agency-and-account&agencyNumber={agencyNumber}&accountNumber={accountNumber}`

Este endpoint é utilizado para buscar chaves Pix conforme a agência e numero de conta dela.

#### findPixKeyByUserName
**Metodo**: GET  
**Endpoint**: `/api/pix/by-user-name&userName={userName}`

Este endpoint é utilizado para buscar chaves Pix conforme o nome do usuario. O id e um UUID.

#### findPixKeyByCreatedAt
**Metodo**: GET  
**Endpoint**: `/api/pix/by-created&createdAt={createdAt}`

Este endpoint é utilizado para buscar chaves Pix conforme a data de criaçao.

#### findPixKeyByInactivatedAt
**Metodo**: GET  
**Endpoint**: `/api/pix/by-inactivated&inactivatedAt={inactivatedAt}`

Este endpoint é utilizado para buscar chaves Pix conforme a data de inativaçao

#### deletePixKey
**Metodo**: DELETE  
**Endpoint**: `/api/pix/{id}`

Este endpoint é utilizado para deletar uma chave Pix especifica conforme o id dela. O id e um UUID.
