# Система управления банковскими картами

---
## Запуск

```bash
docker compose up -d
```
Или
```bash
docker compose -f docker-compose.yml -p bank_rest up -d
```

---
## Подключение
* ### Адрес приложения - http://localhost:8080
* ### Swagger - http://localhost:8080/swagger-ui/index.html
* ### Стартовый админ аккаунт
   * Логин - admin
   * Пароль - admin
   * Остальные поля в swagger authorize заполнять не нужно
