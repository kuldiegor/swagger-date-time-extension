# swagger-date-time-extension
Библиотека - дополнение для swagger библиотеки.

Библиотека позволяет автоматически добавить формат и примеры, по формату даты времени, для полей даты времени.

## Установка
Для того что бы использовать библиотеку, нужно сначала её установить.

Сначала клонируем репозиторий

`git clone https://github.com/kuldiegor/swagger-date-time-extension.git`

Затем установим библиотеку локально

`mvn install`

## Использование
Чтобы использовать библиотеку, достаточно подключить её в зависимости проекта.

```xml
<dependency>
    <groupId>com.kuldiegor</groupId>
    <artifactId>swagger-date-time-extension-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

## Пример использования
Например у нас есть поле в неком классе
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Дата время")
    private LocalDateTime dt;
}
```
Достаточно указать аннотацию ``@JsonFormat`` из библиотеки ``Jackson`` для поля `dt`, чтобы документация для класса `Request` сформировалась следующим образом

```json
{
  "com.example.demo.entity.Request": {
    "required": [
      "pet"
    ],
    "type": "object",
    "properties": {
      "dt": {
        "pattern": "yyyy-MM-dd'T'HH:mm:ss",
        "type": "string",
        "description": "Дата время",
        "format": "date-time",
        "example": "1970-01-01T01:01:01"
      }
    }
  }
}
```

Теперь для поля `dt` будет отображаться `pattern` и пример на основе `pattern`.