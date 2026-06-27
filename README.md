# Mezarlink Backend

Spring Boot 3.4 + Java 21 + PostgreSQL + session tabanlı Spring Security.

## Kurulum

1. **PostgreSQL'i kur ve veritabanını oluştur:**
   ```sql
   CREATE DATABASE mezarlink;
   ```

2. **Ortam değişkenlerini ayarla** (veya `application.properties`'teki
   varsayılanları kullan — geliştirme için `postgres/postgres` zaten tanımlı):
   ```
   DB_USERNAME=postgres
   DB_PASSWORD=postgres
   FRONTEND_ORIGIN=http://localhost:5173
   ```

3. **Çalıştır:**
   ```bash
   mvn spring-boot:run
   ```

   İlk çalıştırmada `spring.jpa.hibernate.ddl-auto=update` sayesinde tablolar
   otomatik oluşur (users, memorials, media). Bu ayar sadece geliştirme için —
   production'a geçerken `validate` yapıp Flyway/Liquibase migration'a geçilmeli.

## Swagger / OpenAPI

Uygulama çalışırken şu adresten interaktif API dokümantasyonuna ulaşılır:

```
http://localhost:8080/swagger-ui.html
```

`/login` endpoint'ini Swagger UI üzerinden çalıştırırsan, dönen session
cookie'si tarayıcıda kalır ve sonraki istekleri (örn. `/api/memorials/mine`)
de aynı Swagger sekmesinden giriş yapmış gibi deneyebilirsin.

## Frontend ile bağlantı

React tarafında `axios` instance'ında **`withCredentials: true`** olmalı,
yoksa session cookie gönderilmez:

```js
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});
```

## Endpoint'ler

| Method | Path                      | Auth gerekli mi? | Açıklama |
|--------|---------------------------|-------------------|----------|
| POST   | `/api/auth/register`      | Hayır             | Kayıt ol |
| POST   | `/api/auth/login`         | Hayır             | Giriş yap (session cookie döner) |
| POST   | `/api/auth/logout`        | Evet              | Çıkış yap |
| GET    | `/api/auth/me`            | Evet              | Giriş yapmış kullanıcı bilgisi |
| GET    | `/api/memorials/{slug}`   | **Hayır**         | QR kod okutan herkes görebilir |
| GET    | `/api/memorials/mine`     | Evet              | Kendi anı sayfalarım |
| POST   | `/api/memorials`          | Evet              | Yeni anı sayfası oluştur |
| PUT    | `/api/memorials/{id}`     | Evet (sahibi)     | Güncelle |
| DELETE | `/api/memorials/{id}`     | Evet (sahibi)     | Sil |

`MemorialResponse` alan adları React component prop'larıyla (`MemorialHero`,
`Gallery`, `VideoSection`) bilerek bire bir aynı tutuldu — frontend hiçbir
dönüşüm yapmadan API yanıtını doğrudan component'lere geçirebilir.

## Henüz eklenmedi (sıradaki adımlar)

- **Medya yükleme** (`MediaController` + Cloudflare R2 entegrasyonu) — şu an
  `Media` entity'si ve `application.properties`'te R2 ayarları hazır ama
  upload endpoint'i yazılmadı.
- **Slug'a göre yetki kontrolü olmadan owner bilgisi sızdırma riski** —
  `MemorialResponse` şu an owner bilgisini döndürmüyor, bu bilerek yapıldı
  (public sayfa kimin oluşturduğunu göstermemeli).
- **Rate limiting** — login/register endpoint'leri için henüz yok.
- **Email doğrulama** — kayıt sırasında email'in gerçek olduğunu doğrulamıyoruz.
