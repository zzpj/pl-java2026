## Członkowie grupy 
251558, 251554, 251598, 251620, 251606

## Polecenia

Uruchomienie wszystkich modułów
```docker
docker compose up -d
```

## SonarQube
### Default credentials:
login: admin \
password: admin

Po pierwszym logowaniu należy zmienić hasło oraz utworzyć nowy User Token i wrzucić go do .env

Analiza problemów we wszystkich modułach
```docker
docker compose --profile tools run --rm sonar-scan
```

## Linki
Eureka - http://localhost:8761 \
Config Server - http://localhost:8888/application/default \
SonarQube - http://localhost:9000 \
Authentication Server - http://localhost:8081 \
Watermark Service - http://localhost:8082 \
Gateway - http://localhost:8083

## Repozytorium z configami
https://github.com/bkolacinski/pl-java2026-config.git
