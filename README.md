# О проекте

Текущий репозиторий входит в часть проекта по backend-разработке цифровой информационной системы для работы с многоязычными текстами деловой документации.
Ниже представлены остальные репозитории этого проекта:
- [rgo-cloud-db](https://github.com/evgeny-mordyasov/rgo-cloud-db) предназначен для создания и инициализации баз данных.
- [rgo-cloud-common](https://github.com/evgeny-mordyasov/rgo-cloud-common) предназначен для общей кодовой базы двух микросервисов.
- [rgo-cloud-security](https://github.com/evgeny-mordyasov/rgo-cloud-security) предназначен для общих процессов управления регистрации и аутентификации пользователей.
- [rgo-cloud-docs](https://github.com/evgeny-mordyasov/rgo-cloud-docs) микросервис работы документов.

## Описание репозитория

Текущий репозиторий предоставляет REST API для работы с клиентскими данными: регистрация, аутентификация и авторизаций пользователей. Документация по REST API располагается по следующему URL-адресу:
```
http://localhost:8090/swagger-ui/index.html
```
Опишем модули этого репозитория:
- rgo-cloud-authentication-boot является входной точкой в приложение и содержит REST-контроллеры.
- rgo-cloud-authentication-db-api предоставляет API для работы с базой данных.
- rgo-cloud-authentication-db содержит реализацию db-api.
- rgo-cloud-authentication-service является сервисным слоем над слоем базы данных.
- rgo-cloud-authentication-mail хранит в себе логику отправки сообщений на почту.
- rgo-cloud-authentication-rest-api содержит запросы и ответы REST API.

## Использование

Для поднятия микросервиса необходимо перейти в репозиторий [rgo-cloud-common](https://github.com/evgeny-mordyasov/rgo-cloud-common), который содержит описание docker compose файла.
