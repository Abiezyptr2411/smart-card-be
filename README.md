# Card Smart Track - Backend REST API (Bank Rakyat Indonesia)

Backend REST API untuk layanan **Card Smart Track BRI**, solusi cerdas yang membantu nasabah Bank BRI memonitor seluruh kartu kredit miliknya, melacak langganan berulang (*recurring subscriptions*) yang aktif pada setiap kartu, serta melakukan pembatalan (*unsubscribe*) secara instan dan aman.

Referensi alur dan antarmuka UI: [https://card-smart-track.lovable.app](https://card-smart-track.lovable.app)

---

## Daftar Isi
1. [Tech Stack](#tech-stack)
2. [Fitur Utama](#fitur-utama)
3. [Akun Demo](#akun-demo)
4. [Cara Menjalankan](#cara-menjalankan)
5. [Dokumentasi API & Swagger UI](#dokumentasi-api--swagger-ui)
6. [H2 Database Console](#h2-database-console)
7. [Format Response Konsisten](#format-response-konsisten)
8. [Daftar Endpoint & Contoh Request / Response](#daftar-endpoint--contoh-request--response)
9. [Collection Postman](#collection-postman)
10. [Konfigurasi CORS (Untuk Frontend)](#konfigurasi-cors-untuk-frontend)
11. [Menjalankan Unit Test](#menjalankan-unit-test)

---

## Tech Stack
- **Java**: Amazon Corretto JDK 21 LTS
- **Framework**: Spring Boot 3.4.3
- **Build Tool**: Apache Maven (dengan Maven Wrapper `mvnw` & `mvnw.cmd`)
- **Keamanan**: Spring Security 6 & JWT (JSON Web Token via JJWT 0.12.6)
- **Database**: H2 In-Memory Database (untuk demo instan tanpa setup database eksternal)
- **ORM & Akses Data**: Spring Data JPA & Hibernate
- **Dokumentasi API**: Springdoc OpenAPI v2.8.6 (Swagger UI 3.0)
- **Validasi**: Hibernate Validator / Jakarta Validation
- **Boilerplate Reducer**: Project Lombok
- **Testing**: JUnit 5, Mockito, Spring Boot Starter Test (MockMvc)

---

## Fitur Utama
1. **Autentikasi JWT Stateless**: Login aman mengembalikan Bearer token.
2. **Katalog Kartu Kredit Nasabah**: Melihat daftar kartu kredit yang dimiliki beserta sisa limit dan jumlah langganan aktif.
3. **Penyensoran Nomor Kartu (Data Masking)**: Nomor kartu lengkap tidak pernah dikirim ke antarmuka klien/frontend, selalu dimasking menjadi format aman (contoh: `**** **** **** 4321`).
4. **Pelacakan Subscription per Kartu**: Melihat detail layanan (Netflix, Spotify, Google One, Grab Unlimited, iCloud, dll.) termasuk nominal tagihan, siklus tagihan, dan tanggal penagihan berikutnya.
5. **Instant Unsubscribe**: Membatalkan langganan kapan saja; status langsung berubah menjadi `INACTIVE` dengan pencatatan waktu pembatalan.
6. **Multi-Tenancy & Proteksi Cross-User**: Validasi kepemilikan ketat: nasabah A tidak dapat melihat kartu atau meng-unsubscribe layanan milik nasabah B (menghasilkan status HTTP `403 Forbidden`).
7. **Pencegahan Double Unsubscribe**: Layanan yang sudah berstatus `INACTIVE` tidak dapat di-unsubscribe ulang (menghasilkan HTTP `409 Conflict`).

---

## Akun Demo
Saat aplikasi pertama kali dinyalakan, data dummy langsung di-seed otomatis ke dalam memory database:

| Akun | Email | Password | Nama Lengkap | Jumlah Kartu |
|---|---|---|---|---|
| **Nasabah Utama** | `nasabah@bri.co.id` | `Password123!` | Budi Pratama | 3 Kartu Kredit (Touch Visa, Wonderful Indonesia Mastercard, JCB Platinum) |
| **Nasabah Sekunder** (Uji Coba 403) | `siti@bri.co.id` | `Password123!` | Siti Rahma | 1 Kartu Kredit (Easy Card Mastercard) |

### Rincian Kartu & Subscription Budi Pratama:
- **Kartu 1: BRI Touch Visa** (`**** **** **** 4321` | Limit: Rp 25.000.000)
  - Netflix Premium (Rp 186.000 / bln) - `ACTIVE`
  - Spotify Family (Rp 86.900 / bln) - `ACTIVE`
  - Google One 2TB (Rp 135.000 / bln) - `ACTIVE`
- **Kartu 2: BRI Wonderful Indonesia Mastercard** (`**** **** **** 8765` | Limit: Rp 50.000.000)
  - Grab Unlimited (Rp 9.000 / bln) - `ACTIVE`
  - Disney+ Hotstar (Rp 65.000 / bln) - `ACTIVE`
  - iCloud+ 200GB (Rp 45.000 / bln) - `ACTIVE`
- **Kartu 3: BRI JCB Platinum** (`**** **** **** 1199` | Limit: Rp 75.000.000)
  - YouTube Premium Family (Rp 99.000 / bln) - `ACTIVE`
  - ChatGPT Plus (Rp 330.000 / bln) - `ACTIVE`
  - Gramedia Digital Premium (Rp 89.000 / bln) - `INACTIVE` *(Sudah di-unsubscribe, cocok untuk test 409)*

---

## Cara Menjalankan

### Menggunakan Maven Lokal
```bash
mvn spring-boot:run
```

### Menggunakan Maven Wrapper (Windows PowerShell / CMD)
```powershell
.\mvnw.cmd spring-boot:run
```

### Menggunakan Maven Wrapper (Linux / macOS / Git Bash)
```bash
./mvnw spring-boot:run
```

Aplikasi akan berjalan pada port **8080**: `http://localhost:8080`

---

## Dokumentasi API & Swagger UI
Dokumentasi interaktif Swagger UI siap digunakan:
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) (atau `http://localhost:8080/swagger-ui.html`)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Tips di Swagger UI**: Klik tombol **Authorize** di pojok kanan atas, lalu masukkan token hasil login dengan format: `Bearer <token_jwt>`.

---

## H2 Database Console
Database in-memory H2 dapat diakses langsung melalui browser:
- **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL**: `jdbc:h2:mem:cardsmarttrackdb`
- **User Name**: `sa`
- **Password**: `password`

---

## Format Response Konsisten
Seluruh endpoint (sukses maupun error) mengembalikan struktur JSON standar:

### Sukses:
```json
{
  "success": true,
  "message": "Pesan deskriptif keberhasilan",
  "data": { ... },
  "timestamp": "2026-10-16T10:00:00.123456"
}
```

### Gagal / Error:
```json
{
  "success": false,
  "message": "Pesan kesalahan",
  "data": null,
  "timestamp": "2026-10-16T10:00:00.123456"
}
```

### Pemetaan Kode HTTP Error:
- `400 Bad Request`: Validasi payload gagal (misal email tidak valid, field kosong). Field `data` akan berisi map error per-field.
- `401 Unauthorized`: Belum menyertakan token Bearer atau token kedaluwarsa / salah.
- `403 Forbidden`: Mencoba mengakses kartu atau subscription milik nasabah lain.
- `404 Not Found`: ID kartu atau subscription tidak ada di database.
- `409 Conflict`: Mencoba meng-unsubscribe layanan yang statusnya sudah nonaktif.

---

## Daftar Endpoint & Contoh Request / Response

### 1. Login Nasabah
- **Method**: `POST`
- **Path**: `/api/auth/login`
- **Autentikasi**: Tidak perlu (Public)

**Request Body**:
```json
{
  "email": "nasabah@bri.co.id",
  "password": "Password123!"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Login berhasil",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "customer": {
      "id": 1,
      "email": "nasabah@bri.co.id",
      "fullName": "Budi Pratama",
      "phoneNumber": "+6281234567890"
    }
  },
  "timestamp": "2026-10-16T10:00:00"
}
```

**Contoh cURL**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nasabah@bri.co.id","password":"Password123!"}'
```

---

### 2. Ambil Semua Kartu Kredit Nasabah
- **Method**: `GET`
- **Path**: `/api/cards`
- **Autentikasi**: `Bearer <token>`

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Berhasil mengambil daftar kartu kredit",
  "data": [
    {
      "id": 1,
      "cardName": "BRI Touch Visa",
      "maskedCardNumber": "**** **** **** 4321",
      "lastFourDigits": "4321",
      "cardNetwork": "VISA",
      "creditLimit": 25000000.00,
      "availableLimit": 18750000.00,
      "expiryDate": "10/28",
      "status": "ACTIVE",
      "activeSubscriptionCount": 3,
      "totalActiveMonthlyAmount": 407900.00
    },
    {
      "id": 2,
      "cardName": "BRI Wonderful Indonesia Mastercard",
      "maskedCardNumber": "**** **** **** 8765",
      "lastFourDigits": "8765",
      "cardNetwork": "MASTERCARD",
      "creditLimit": 50000000.00,
      "availableLimit": 43200000.00,
      "expiryDate": "04/29",
      "status": "ACTIVE",
      "activeSubscriptionCount": 3,
      "totalActiveMonthlyAmount": 119000.00
    },
    {
      "id": 3,
      "cardName": "BRI JCB Platinum",
      "maskedCardNumber": "**** **** **** 1199",
      "lastFourDigits": "1199",
      "cardNetwork": "JCB",
      "creditLimit": 75000000.00,
      "availableLimit": 68500000.00,
      "expiryDate": "08/30",
      "status": "ACTIVE",
      "activeSubscriptionCount": 2,
      "totalActiveMonthlyAmount": 429000.00
    }
  ],
  "timestamp": "2026-10-16T10:00:00"
}
```

**Contoh cURL**:
```bash
curl -X GET http://localhost:8080/api/cards \
  -H "Authorization: Bearer <TOKEN_ANDA>"
```

---

### 3. Ambil Daftar Subscription pada Satu Kartu
- **Method**: `GET`
- **Path**: `/api/cards/{cardId}/subscriptions`
- **Query Params (Opsional)**: `status=ACTIVE` atau `status=INACTIVE`
- **Autentikasi**: `Bearer <token>`

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Berhasil mengambil daftar layanan langganan",
  "data": [
    {
      "id": 1,
      "cardId": 1,
      "serviceName": "Netflix Premium",
      "category": "ENTERTAINMENT",
      "billingAmount": 186000.00,
      "billingPeriod": "MONTHLY",
      "nextBillingDate": "2026-10-31",
      "status": "ACTIVE",
      "logoUrl": "https://assets.nflxext.com/ffe/siteui/common/icons/monogram/netflix.png",
      "description": "Paket Ultra HD 4K, 4 Perangkat",
      "unsubscribedAt": null
    },
    {
      "id": 2,
      "cardId": 1,
      "serviceName": "Spotify Family",
      "category": "MUSIC",
      "billingAmount": 86900.00,
      "billingPeriod": "MONTHLY",
      "nextBillingDate": "2026-11-07",
      "status": "ACTIVE",
      "logoUrl": "https://open.spotifycdn.com/cdn/images/favicon32.8e66b099.png",
      "description": "Paket Keluarga hingga 6 Akun Premium",
      "unsubscribedAt": null
    },
    {
      "id": 3,
      "cardId": 1,
      "serviceName": "Google One 2TB",
      "category": "CLOUD_STORAGE",
      "billingAmount": 135000.00,
      "billingPeriod": "MONTHLY",
      "nextBillingDate": "2026-11-15",
      "status": "ACTIVE",
      "logoUrl": "https://ssl.gstatic.com/images/branding/product/1x/google_one_48dp.png",
      "description": "Penyimpanan Cloud 2TB & Google Photos",
      "unsubscribedAt": null
    }
  ],
  "timestamp": "2026-10-16T10:00:00"
}
```

**Contoh cURL**:
```bash
curl -X GET http://localhost:8080/api/cards/1/subscriptions \
  -H "Authorization: Bearer <TOKEN_ANDA>"
```

---

### 4. Unsubscribe Satu Layanan Langganan
- **Method**: `POST`
- **Path**: `/api/subscriptions/{id}/unsubscribe`
- **Autentikasi**: `Bearer <token>`

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Berhasil membatalkan langganan (unsubscribe)",
  "data": {
    "id": 1,
    "cardId": 1,
    "serviceName": "Netflix Premium",
    "category": "ENTERTAINMENT",
    "billingAmount": 186000.00,
    "billingPeriod": "MONTHLY",
    "nextBillingDate": "2026-10-31",
    "status": "INACTIVE",
    "logoUrl": "https://assets.nflxext.com/ffe/siteui/common/icons/monogram/netflix.png",
    "description": "Paket Ultra HD 4K, 4 Perangkat",
    "unsubscribedAt": "2026-10-16T10:20:15.654321"
  },
  "timestamp": "2026-10-16T10:20:15"
}
```

**Response jika sudah pernah di-unsubscribe (409 Conflict)**:
```json
{
  "success": false,
  "message": "Layanan 'Netflix Premium' sudah tidak aktif (sudah pernah di-unsubscribe)",
  "data": null,
  "timestamp": "2026-10-16T10:21:00"
}
```

**Contoh cURL**:
```bash
curl -X POST http://localhost:8080/api/subscriptions/1/unsubscribe \
  -H "Authorization: Bearer <TOKEN_ANDA>"
```

---

### 5. Skenario Error & Keamanan

#### a. 401 Unauthorized (Tanpa Header Authorization)
```bash
curl -X GET http://localhost:8080/api/cards
```
**Response**:
```json
{
  "success": false,
  "message": "Akses tidak sah: Silakan login terlebih dahulu untuk mengakses layanan ini",
  "data": null,
  "timestamp": "2026-10-16T10:00:00"
}
```

#### b. 403 Forbidden (Mengakses Kartu / Subscription Milik Nasabah Lain)
Jika Budi (`nasabah@bri.co.id`) mencoba mengakses Kartu ID 4 (milik Siti):
```bash
curl -X GET http://localhost:8080/api/cards/4 \
  -H "Authorization: Bearer <TOKEN_BUDI>"
```
**Response**:
```json
{
  "success": false,
  "message": "Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login",
  "data": null,
  "timestamp": "2026-10-16T10:00:00"
}
```

#### c. 404 Not Found (Resource Tidak Ditemukan)
```bash
curl -X GET http://localhost:8080/api/cards/999 \
  -H "Authorization: Bearer <TOKEN_BUDI>"
```
**Response**:
```json
{
  "success": false,
  "message": "Kartu kredit tidak ditemukan dengan id: '999'",
  "data": null,
  "timestamp": "2026-10-16T10:00:00"
}
```

---

## Collection Postman
File Postman Collection lengkap telah disediakan pada file:
[`postman_collection.json`](./postman_collection.json)

**Fitur Collection:**
1. Import file `postman_collection.json` ke Postman Anda.
2. Jalankan request **Login Nasabah Utama (Budi)**. Script otomatis akan menyimpan token JWT ke variabel `{{token}}`.
3. Request berikutnya (Get Cards, Get Subscriptions, Unsubscribe) langsung dapat dieksekusi tanpa perlu copy-paste token manual!

---

## Konfigurasi CORS (Untuk Frontend)
CORS telah dikonfigurasi secara fleksibel melalui `src/main/resources/application.yml`:

```yaml
app:
  cors:
    allowed-origins:
      - "http://localhost:3000"
      - "http://localhost:5173"
      - "http://localhost:8080"
      - "https://card-smart-track.lovable.app"
    allowed-methods:
      - GET
      - POST
      - PUT
      - DELETE
      - OPTIONS
      - PATCH
    allowed-headers:
      - "*"
    allow-credentials: true
```

Jika teman tim frontend menjalankan dev server di port atau domain lain (misalnya Vite port 5174 atau domain staging), cukup tambahkan origin tersebut ke dalam daftar `allowed-origins` di `application.yml`.

---

## Menjalankan Unit Test
Proyek dilengkapi dengan pengujian menyeluruh (Unit Test service dan MockMvc Controller Test) yang mencakup skenario 200, 400, 401, 403, 404, dan 409:

```bash
mvn test
```

Hasil test:
```text
[INFO] Running com.bri.cardsmarttrack.controller.AuthControllerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.bri.cardsmarttrack.controller.CreditCardControllerTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.bri.cardsmarttrack.controller.SubscriptionControllerTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.bri.cardsmarttrack.service.SubscriptionServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.bri.cardsmarttrack.CardSmartTrackApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Results:
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
#   s m a r t - c a r d - b e  
 