# PŁ: Zaawansowane Zagadnienia Programowania w Javie - Edycja 2026

## Zaliczenie

- Projekt grupowy: od 3 do 6 osób (brak odstępstw)
- Tematyka realizowanego projektu: dowolna
- Elementy, które powinny znaleźć się końcowym projekcie i podlegają ocenie:
    - Współpraca z repozytorium Git (dowolnie wybrany darmowy hosting według upodobań, na przykład: Github, Gitlab lub
      Bitbucket)
    - Aktywność w realizacji projektu:
        - *pull requesty* wraz z *code review* wewnątrz zespołu
        - *githubowy pulse* lub inne narzędzie pokazujące ciągłość pracy podczas semestru
        - podział zadań przy implementacji funkcjonalności projektu poprzez *board projektowy* (Jira, Trello,...)

    - Praktyczna implementacja i wdrożenie rozwiązań, tematów, narzędzi prezentowanych podczas zajęć (nie muszą być
      wszystkie, 60-80% jest ok)
        - [ ] Aplikacja oparta o **Spring (Boot) Framework**
        - [ ] Integracja z zewnętrznym zasobem po **REST** z wykorzystaniem publicznego API (
          np.: https://github.com/public-apis/public-apis) wraz z wykorzystaniem (przetworzeniem) otrzymanych danych
        - [ ] Zaprojektowanie własnego API z wykorzystaniem biblioteki **OpenAPI** wraz z drugą aplikacją, która
          konsumuje udostępnione API. Klient będzie wykonywał zapytania HTTP do serwisu i analizował otrzymane dane.
        - [ ] Architektura mikroserwisowa z wykorzystaniem **Spring Cloud Eureka**, umożliwiająca rejestrację i
          wykrywanie usług. Aplikacja będzie składała się z minimum dwóch mikroserwisów, które będą się wzajemnie
          komunikowały przy użyciu Spring Eureka jako serwera rejestracji usług.
        - [ ] Wykorzystanie **Spring Cloud Config Server**, który centralizuje zarządzanie konfiguracją dla wielu
          mikroserwisów. Dzięki temu, wszystkie serwisy w systemie mogą korzystać z jednej wspólnej konfiguracji,
          przechowywanej w centralnym miejscu (np. w plikach YAML na oddzielnym repozytorium Git), a zmiany w
          konfiguracji są natychmiastowo propagowane do aplikacji.
        - [ ] Wykorzystanie **Keycloak** lub **Spring Authorization Server** (lub inny nieprezentowany podczas zajęć)
          jako systemu zarządzającego autoryzacją i autentykacją użytkowników oraz/i serwisów.
        - [ ] Aplikacja powinna być zaprojektowana w taki sposób, aby jej komponenty zostały odpowiednio przetestowane
          przy użyciu różnych typów testów, np. **jednostkowych (unit tests)**, **integracyjnych (integration tests)**
          oraz **testów BDD (Behavior Driven Development)**. Celem jest zapewnienie wysokiej jakości kodu, wykrywanie
          błędów na wczesnym etapie oraz zapewnienie, że aplikacja działa zgodnie z oczekiwaniami użytkowników
          końcowych. Użycie **Cucumber** – napisanie kilku testów z wykorzystaniem różnych konstrukcji **Gherkina** i
          dodanie testów realizowanych przez **AssertJ**
        - [ ] Statyczna analiza kodu – lokalne postawienie SonarQube, utworzenie projektu, dodanie skanera, analiza
          wyników, dbanie o utrzymanie długu technicznego na akceptowalnym poziomie
        - [ ] Aplikacja wykorzystująca ostatnie nowości w **JDK** (np. Local-Variable Type Inference, Text Blocks,
          Sealed Classes, Pattern Matching dla instanceof, Virtual Threads, Records, Structured Concurrency)
        - [ ] Obsługa **LLM / AI w Javie** – skorzystanie z API do zadania przetwarzania tekstu lub np.
          predykcja/klasyfikacja własnym modelem
    - UI oraz UX nie mają znaczenia, podczas prezentacji można użyć narzędzi typu Swagger albo Postman
    - Unikanie typowych aplikacji Create-Read-Update-Delete (CRUD)

## Propozycje tematów 2026

- IntelliJ
- JDK Updates (9~25)
- Refleksja w Javie
- Cucumber (BDD) + AssertJ
- Integracja z modelami AI przez http + biblioteki do AI w Javie
- Statyczna analiza kodu
- Spring 101: Podstawowe zagadnienia, MVC, RestController, HttpClient, JPA
- Spring 102: Security (Keycloak + Spring Authorization Server)
- Mikroserwisy 101: praktyczne podstawy budowy systemu z wykorzystaniem REST i Spring Cloud Eureka
- Mikroserwisy 102: zaawansowane tematy związane z wykorzystaniem narzędzi do zarządzania autoryzacją i autentykacją
  użytkowników i serwisów (Keycloak vs Spring Authorization Server)
- Mutation testing (pitest)
- From Java to Go(lang)
- Kafka

