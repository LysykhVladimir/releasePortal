# Release Portal

**Release Portal** — веб-приложение на базе [JHipster 8.1.0](https://www.jhipster.tech/) (монолит: Spring Boot + Vue.js SPA).
Пакет приложения — `ru.homebank`, Maven-артефакт — `ru.homebank:release-portal:0.0.1-SNAPSHOT`.

На текущем этапе проект содержит сгенерированный JHipster-каркас: аутентификацию по JWT, управление пользователями,
административные разделы (метрики, health, логи, конфигурация, Swagger) и инфраструктуру для развёртывания.
Бизнес-сущности пока не добавлены — в разделе `entities` есть только встроенная сущность `User`.

## Технологический стек

| Слой | Технологии |
|------|------------|
| Язык / платформа | Java 17 |
| Backend | Spring Boot 3.2.0, Spring Security (OAuth2 Resource Server, JWT), Spring Data JPA, Hibernate 6.3, Undertow |
| БД и миграции | PostgreSQL 16 (prod), H2 file-based (dev), Liquibase 4.24 |
| Маппинг / валидация | MapStruct 1.5, Hibernate Validator |
| Шаблоны писем | Thymeleaf, Spring Mail |
| API-документация | springdoc-openapi (профиль `api-docs`), Swagger UI |
| Мониторинг | Spring Boot Actuator, Micrometer + Prometheus, Grafana |
| Frontend | Vue 3 + TypeScript, Bootstrap-Vue, Pinia, i18n (en, ru) |
| Тестирование | JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL), ArchUnit, Vitest для фронтенда |
| Качество кода | Checkstyle (nohttp), Spotless, Modernizer, JaCoCo, SonarQube |
| Контейнеризация | Jib (`eclipse-temurin:17-jre-focal`), Docker Compose |

## Структура проекта

```
.
├── pom.xml                          # Maven-сборка, профили dev / prod / api-docs / tls / no-liquibase
└── src
    ├── main
    │   ├── java/ru/homebank
    │   │   ├── ReleasePortalApp.java    # точка входа Spring Boot
    │   │   ├── aop/logging              # аспект логирования
    │   │   ├── config                   # Security, JWT, CORS, Jackson, Liquibase, Async, Locale и т.д.
    │   │   ├── domain                   # JPA-сущности: User, Authority, AbstractAuditingEntity
    │   │   ├── repository               # Spring Data репозитории
    │   │   ├── security                 # UserDetailsService, роли, утилиты, JWT
    │   │   ├── service                  # UserService, MailService, DTO и MapStruct-мапперы
    │   │   ├── management               # метрики безопасности
    │   │   └── web
    │   │       ├── filter               # SpaWebFilter (форвард маршрутов SPA на index.html)
    │   │       └── rest                 # REST-контроллеры и обработка ошибок (Problem Details)
    │   ├── resources
    │   │   ├── config                   # application*.yml, Liquibase changelog, TLS keystore
    │   │   ├── i18n                     # сообщения сервера (en, ru)
    │   │   └── templates                # Thymeleaf-шаблоны писем и страницы ошибки
    │   ├── webapp                       # Vue SPA: account, admin, entities, router, shared, i18n
    │   └── docker                       # docker-compose файлы, Prometheus, Grafana
    └── test
        ├── java/ru/homebank             # unit- и integration-тесты (*IT.java)
        └── resources                    # тестовые конфиги (application-testdev / testprod)
```

## Функциональность

### REST API

| Endpoint | Назначение |
|----------|-----------|
| `POST /api/authenticate` | Вход, выдача JWT |
| `POST /api/register` | Регистрация пользователя |
| `GET  /api/activate?key=` | Активация учётной записи |
| `GET  /api/account`, `POST /api/account` | Получение / изменение профиля текущего пользователя |
| `POST /api/account/change-password` | Смена пароля |
| `POST /api/account/reset-password/init`, `/finish` | Сброс пароля по e-mail |
| `GET  /api/users`, `GET /api/authorities` | Публичный список пользователей и ролей |
| `/api/admin/users/**` | CRUD пользователей (роль `ROLE_ADMIN`) |
| `/management/**` | Actuator: health, info, metrics, prometheus, loggers, env, configprops |
| `/v3/api-docs` | OpenAPI-спецификация (профиль `api-docs`) |

### Web-интерфейс

- **Учётная запись:** вход, регистрация, активация, настройки профиля, смена и сброс пароля.
- **Администрирование:** управление пользователями, метрики JVM/HTTP, состояние сервисов (health), управление уровнями логирования, просмотр конфигурации, Swagger UI.
- **Локализация:** английский и русский.

### Пользователи по умолчанию

Создаются Liquibase-миграцией (`config/liquibase/data/user.csv`):

| Логин | Пароль | Роли |
|-------|--------|------|
| `admin` | `admin` | `ROLE_ADMIN`, `ROLE_USER` |
| `user`  | `user`  | `ROLE_USER` |

> Обязательно смените пароли и `jhipster.security.authentication.jwt.base64-secret` перед развёртыванием в любом не-локальном окружении.

## Профили Spring / Maven

| Профиль | Описание |
|---------|----------|
| `dev` (по умолчанию) | H2 в файле `target/h2db`, DevTools, DEBUG-логи, Liquibase-контексты `dev, faker`, CORS для `localhost:9000` |
| `prod` | PostgreSQL (`jdbc:postgresql://localhost:5432/releasePortal`), сборка фронтенда через `frontend-maven-plugin`, кэширование статики |
| `api-docs` | Включает генерацию OpenAPI / Swagger UI |
| `tls` | HTTPS с self-signed сертификатом из `config/tls/keystore.p12` |
| `no-liquibase` | Отключает Liquibase при старте |

## Требования

- JDK 17
- Maven 3.2.5+
- Node.js 18.18.2 / npm 10.2.4 (для сборки фронтенда)
- Docker и Docker Compose (для PostgreSQL, мониторинга, SonarQube и запуска в контейнере)

## Запуск

### Режим разработки (H2)

```bash
mvn                        # эквивалент mvn spring-boot:run с профилем dev
```

Приложение будет доступно на http://localhost:8080.

### Запуск с PostgreSQL

```bash
docker compose -f src/main/docker/services.yml up -d   # поднимает PostgreSQL 16 на 127.0.0.1:5432
mvn -Pprod
```

### Сборка production-артефакта

```bash
mvn -Pprod clean verify
java -jar target/*.jar
```

### Docker-образ (Jib) и запуск всего стека

```bash
mvn -Pprod verify jib:dockerBuild                      # собирает образ releaseportal:latest
docker compose -f src/main/docker/app.yml up -d        # приложение + PostgreSQL
```

Контейнер приложения стартует с профилями `prod,api-docs`, health-check — `GET /management/health`.

## Тестирование

```bash
mvn test                   # unit-тесты (surefire)
mvn verify                 # + интеграционные тесты *IT (failsafe), PostgreSQL через Testcontainers
```

Интеграционные тесты помечены аннотацией `@IntegrationTest`; БД для них поднимается автоматически через
`SqlTestContainersSpringContextCustomizerFactory` (нужен запущенный Docker). Архитектурные ограничения
проверяются в `TechnicalStructureTest` (ArchUnit).

## Мониторинг и качество кода

```bash
docker compose -f src/main/docker/monitoring.yml up -d              # Prometheus :9090, Grafana :3000 (admin/admin)
docker compose -f src/main/docker/jhipster-control-center.yml up -d # JHipster Control Center
docker compose -f src/main/docker/sonar.yml up -d                   # SonarQube :9001
mvn -Pprod clean verify sonar:sonar
```

В Grafana заранее подключён источник Prometheus и дашборд JVM.

## Известные ограничения текущего состояния репозитория

В репозиторий закоммичены не все файлы, которые генерирует JHipster. Отсутствуют:

- `package.json`, `package-lock.json`, конфигурация сборщика фронтенда (`vite`/`webpack`, `tsconfig*.json`) — без них не соберётся Vue-клиент и профиль `prod`;
- Maven Wrapper (`mvnw`, `.mvn/`) — используйте локально установленный Maven;
- `checkstyle.xml` — требуется `maven-checkstyle-plugin`;
- `src/main/docker/jib/entrypoint.sh` — требуется для сборки образа через Jib;
- `.yo-rc.json`, `.gitignore`, `sonar-project.properties`.

Для полноценной сборки эти файлы нужно восстановить (например, повторной генерацией `jhipster` с тем же `baseName=releasePortal` и пакетом `ru.homebank`).
