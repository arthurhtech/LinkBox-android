# LinkBox

Projeto acadêmico desenvolvido para o curso de Análise e Desenvolvimento de Sistemas (ADS). O LinkBox é um aplicativo Android voltado para salvar, organizar e acessar links da internet de maneira simples e prática, funcionando de forma totalmente offline.

## Funcionalidades

* Gerenciamento de Pastas: Criação, edição, listagem e exclusão de pastas.
* Gerenciamento de Links: Adição de links com título, URL e descrição, devidamente organizados dentro das pastas criadas.
* Acesso Rápido: Abertura direta das URLs salvas no navegador padrão do dispositivo.
* Persistência Local: Todos os dados ficam salvos no próprio aparelho do usuário, sem necessidade de cadastro ou login.

## Tecnologias e Arquitetura

* Linguagem: Kotlin
* Interface Gráfica: XML integrado via ViewBinding 
* Banco de Dados: Room Database (abstração sobre o SQLite)
* Arquitetura: MVVM (Model-View-ViewModel)
* Separação de Responsabilidades: Utilização do padrão Repository para isolar a fonte de dados da interface gráfica.
* Assincronismo e Reatividade: Kotlin Coroutines para operações em background e StateFlow para atualização reativa do estado da interface.
* Padrões Adicionais: Implementação do padrão Singleton para gerenciamento de memória da base de dados e DiffUtil para otimização de renderização das listas (RecyclerView).

## Como Executar

1. Clone este repositório em sua máquina local.
2. Abra o diretório do projeto no Android Studio.
3. Aguarde a sincronização completa das dependências pelo Gradle.
4. Execute o aplicativo em um emulador ou em um dispositivo físico conectado.
