# 💳 Card Smart Track - Backend REST API (Bank Rakyat Indonesia)

Backend REST API untuk layanan **BRI Card Smart Track**, solusi cerdas perbankan digital yang membantu nasabah Bank BRI memonitor seluruh kartu kredit yang dimiliki, melacak tagihan langganan berulang (*recurring subscriptions*) yang terdaftar pada setiap kartu, serta melakukan pembatalan (*unsubscribe*) layanan kapan saja secara instan dan aman.

Referensi antarmuka & alur pengguna: [https://card-smart-track.lovable.app](https://card-smart-track.lovable.app)

---

## 📑 Daftar Isi
- [1. Latar Belakang & Masalah](#1-latar-belakang--masalah)
- [2. Tech Stack](#2-tech-stack)
- [3. Prasyarat Sistem](#3-prasyarat-sistem)
- [4. Langkah Cepat Menjalankan Backend](#4-langkah-cepat-menjalankan-backend)
- [5. Akun Demo Siap Pakai](#5-akun-demo-siap-pakai)
- [6. Panduan Step-by-Step Testing via Swagger UI](#6-panduan-step-by-step-testing-via-swagger-ui)
- [7. Panduan Step-by-Step Testing via Postman](#7-panduan-step-by-step-testing-via-postman)
- [8. Spesifikasi & Format Response API](#8-spesifikasi--format-response-api)
- [9. Daftar Endpoint Lengkap & Contoh Request/Response](#9-daftar-endpoint-lengkap--contoh-requestresponse)
- [10. Panduan Integrasi Tim Frontend (CORS)](#10-panduan-integrasi-tim-frontend-cors)
- [11. Akses H2 Database Console](#11-akses-h2-database-console)
- [12. Menjalankan Pengujian Otomatis (Unit & Integration Test)](#12-menjalankan-pengujian-otomatis-unit--integration-test)

---

## 1. Latar Belakang & Masalah
Nasabah sering kali lupa layanan digital apa saja (seperti Netflix, Spotify, iCloud, Google One, dll.) yang terhubung dengan kartu kredit mereka. Tiba-tiba tagihan bulanan muncul tanpa disadari. 

Backend ini menyediakan 3 kapabilitas utama:
1. **Visibilitas Kartu Kredit**: Menampilkan seluruh kartu kredit nasabah secara aman (nomor kartu selalu disamarkan / dimasking).
2. **Pelacakan Langganan Aktif**: Menampilkan rincian merchant/layanan, biaya, siklus tagihan, dan tanggal jatuh tempo berikutnya.
3. **Instant Unsubscribe**: Memberikan kendali penuh kepada nasabah untuk menghentikan langganan kapan saja hanya dengan satu klik.

---

## 2. Tech Stack
- **Bahasa**: Java 21 LTS (Amazon Corretto)
- **Framework**: Spring Boot 3.4.3
- **Build Tool**: Apache Maven 3.9+ dengan Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **Keamanan & Autentikasi**: Spring Security 6 & Stateless JWT (JJWT 0.12.6)
- **Basis Data**: H2 In-Memory Database (data otomatis terisi saat startup, tanpa perlu instalasi database terpisah)
- **Akses Data**: Spring Data JPA & Hibernate ORM
- **Dokumentasi Interaktif**: Springdoc OpenAPI 2.8.6 (Swagger UI 3.0)
- **Validasi Data**: Jakarta Validation / Hibernate Validator
- **Testing**: JUnit 5, Mockito, Spring Boot Test (MockMvc)

---

## 3. Prasyarat Sistem
Sebelum menjalankan aplikasi, pastikan komputer Anda telah terpasang:
- **Java Development Kit (JDK)**: Versi 21 atau lebih baru (`java -version`)
- *(Opsional)* **Apache Maven**: Jika tidak ada, Anda cukup menggunakan Maven Wrapper bawaan project (`mvnw` / `mvnw.cmd`).

---

## 4. Langkah Cepat Menjalankan Backend

### Langkah 1: Masuk ke Direktori Project
```bash
cd credit-card-be
```

### Langkah 2: Jalankan Aplikasi
Pilih perintah yang sesuai dengan terminal Anda:

**Opsi A — Menggunakan Maven Wrapper (Direkomendasikan)**
- Di Windows (CMD / PowerShell):
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- Di macOS / Linux / Git Bash:
  ```bash
  ./mvnw spring-boot:run
  ```

**Opsi B — Menggunakan Maven Global**
```bash
mvn spring-boot:run
```

### Langkah 3: Verifikasi Status
Aplikasi akan menyala dalam beberapa detik pada port **8080**:
```text
Started CardSmartTrackApplication in 3.456 seconds (process running for 4.123)
```

---

## 5. Akun Demo Siap Pakai
Database in-memory otomatis di-seed saat aplikasi pertama kali menyala:

| Akun | Email | Password | Nama Nasabah | Kepemilikan Kartu |
|---|---|---|---|---|
| **Nasabah Utama** | `nasabah@bri.co.id` | `Password123!` | Budi Pratama | 3 Kartu Kredit (Touch Visa, Wonderful Indonesia Mastercard, JCB Platinum) |
| **Nasabah Pembanding** *(Untuk uji coba proteksi 403 Forbidden)* | `siti@bri.co.id` | `Password123!` | Siti Rahma | 1 Kartu Kredit (Easy Card Mastercard) |

### Rincian Data Nasabah Utama (Budi Pratama):
- **Kartu 1: BRI Touch Visa** (`**** **** **** 4321` | Limit: Rp 25.000.000)
  - 🎬 *Netflix Premium* — Rp 186.000 / bulan (`ACTIVE`)
  - 🎵 *Spotify Family* — Rp 86.900 / bulan (`ACTIVE`)
  - ☁️ *Google One 2TB* — Rp 135.000 / bulan (`ACTIVE`)
- **Kartu 2: BRI Wonderful Indonesia Mastercard** (`**** **** **** 8765` | Limit: Rp 50.000.000)
  - 🚗 *Grab Unlimited* — Rp 9.000 / bulan (`ACTIVE`)
  - 🍿 *Disney+ Hotstar* — Rp 65.000 / bulan (`ACTIVE`)
  - 📱 *iCloud+ 200GB* — Rp 45.000 / bulan (`ACTIVE`)
- **Kartu 3: BRI JCB Platinum** (`**** **** **** 1199` | Limit: Rp 75.000.000)
  - 📺 *YouTube Premium Family* — Rp 99.000 / bulan (`ACTIVE`)
  - 🤖 *ChatGPT Plus* — Rp 330.000 / bulan (`ACTIVE`)
  - 📚 *Gramedia Digital Premium* — Rp 89.000 / bulan (`INACTIVE` — *sudah di-unsubscribe, untuk uji coba error 409*)

---

## 6. Panduan Step-by-Step Testing via Swagger UI

Swagger UI menyediakan antarmuka web interaktif sehingga Anda tidak perlu mengetik cURL manual.

### Step 1: Buka Halaman Swagger UI
Buka URL berikut pada browser Anda:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

### Step 2: Lakukan Login untuk Memperoleh Token
1. Klik accordion grup **`1. Authentication`**.
2. Klik baris **`POST /api/auth/login`**.
3. Klik tombol **`Try it out`** di sebelah kanan.
4. Pada kotak *Request body*, gunakan akun demo:
   ```json
   {
     "email": "nasabah@bri.co.id",
     "password": "Password123!"
   }
   ```
5. Klik tombol biru **`Execute`**.
6. Pada bagian *Server response*, Anda akan menerima response kode `200`:
   ```json
   {
     "success": true,
     "message": "Login berhasil",
     "data": {
       "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuYXNhYmFoQGJyaS5jby5pZC...",
       "tokenType": "Bearer",
       "expiresIn": 86400000,
       ...
     }
   }
   ```
7. **Salin (Copy)** nilai teks token tersebut (tanpa tanda kutip ganda).

---

### Step 3: Autorisasi Token di Swagger
1. Gulir ke bagian paling atas halaman Swagger UI.
2. Klik tombol hijau bertuliskan **`Authorize`** 🔓 di sebelah kanan atas.
3. Pada dialog yang muncul, tempel (**Paste**) token JWT yang tadi Anda salin ke dalam kolom **Value**.
4. Klik tombol **`Authorize`**, lalu klik tombol **`Close`**.
5. Ikon gembok akan berubah menjadi terkunci 🔒. Sekarang seluruh endpoint secured sudah dapat diakses.

---

### Step 4: Uji Coba Melihat Daftar Kartu Kredit
1. Buka grup **`2. Credit Cards`** -> klik **`GET /api/cards`**.
2. Klik **`Try it out`** -> klik **`Execute`**.
3. **Hasil**: Anda akan melihat 3 kartu kredit milik Budi.
   - Perhatikan bahwa nomor kartu **selalu disamarkan (masked)**, misalnya `**** **** **** 4321`. Nomor kartu asli tidak pernah terekspos.

---

### Step 5: Uji Coba Melihat Subscription pada Satu Kartu
1. Buka grup **`3. Subscriptions`** -> klik **`GET /api/cards/{cardId}/subscriptions`**.
2. Klik **`Try it out`**.
3. Masukkan parameter `cardId`: **`1`**.
4. *(Opsional)* Isi `status` dengan **`ACTIVE`** jika hanya ingin melihat langganan aktif.
5. Klik **`Execute`**.
6. **Hasil**: Muncul daftar langganan pada Kartu ID 1 (*Netflix Premium*, *Spotify Family*, *Google One 2TB*).

---

### Step 6: Uji Coba Membatalkan Langganan (Unsubscribe)
1. Buka grup **`3. Subscriptions`** -> klik **`POST /api/subscriptions/{id}/unsubscribe`**.
2. Klik **`Try it out`**.
3. Masukkan `id`: **`1`** (ID langganan Netflix Premium).
4. Klik **`Execute`**.
5. **Hasil (200 OK)**:
   - Field `status` berubah menjadi `"INACTIVE"`.
   - Field `unsubscribedAt` terisi dengan waktu pembatalan saat ini.

---

### Step 7: Uji Coba Skenario Keamanan & Validasi Error

1. **Uji 409 Conflict (Mencegah Double Unsubscribe)**:
   - Klik tombol **`Execute`** sekali lagi pada `POST /api/subscriptions/1/unsubscribe`.
   - **Hasil**: Muncul HTTP `409 Conflict` dengan pesan: *"Layanan 'Netflix Premium' sudah tidak aktif (sudah pernah di-unsubscribe)"*.
2. **Uji 403 Forbidden (Mencoba Akses Kartu Milik Nasabah Lain)**:
   - Pada `GET /api/cards/{cardId}`, masukkan `cardId`: **`4`** (Kartu ID 4 adalah milik nasabah Siti Rahma).
   - Klik **`Execute`**.
   - **Hasil**: Muncul HTTP `403 Forbidden` dengan pesan: *"Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login"*.
3. **Uji 404 Not Found (Data Tidak Ada)**:
   - Masukkan `cardId`: **`999`**.
   - Klik **`Execute`**.
   - **Hasil**: Muncul HTTP `404 Not Found` dengan pesan: *"Kartu kredit tidak ditemukan dengan id: '999'"*.

---

## 7. Panduan Step-by-Step Testing via Postman

Bagi Anda yang lebih nyaman menggunakan Postman, telah disediakan file collection siap pakai:
📁 [`postman_collection.json`](./postman_collection.json)

### Langkah Penggunaan di Postman:
1. Buka aplikasi **Postman**.
2. Klik tombol **Import** di sudut kiri atas.
3. Pilih file **`postman_collection.json`** yang berada di root folder project ini.
4. Buka folder **`1. Authentication`** -> pilih request **`Login Nasabah Utama (Budi)`** -> klik **Send**.
5. ✨ **Fitur Otomatis**: Script test di dalam collection akan **otomatis mengekstrak dan menyimpan JWT Token ke variabel `{{token}}`**.
6. Anda dapat langsung menjalankan request lainnya (Get Cards, Get Subscriptions, Unsubscribe) **tanpa perlu repot menyalin token manual**.

---

## 8. Spesifikasi & Format Response API

Seluruh endpoint backend menggunakan format pembungkus standar (*standard response wrapper*):

### Format Response Berhasil (Success):
```json
{
  "success": true,
  "message": "Pesan deskriptif keberhasilan",
  "data": { ... },
  "timestamp": "2026-10-16T10:15:30.123456"
}
```

### Format Response Gagal (Error):
```json
{
  "success": false,
  "message": "Pesan deskriptif error",
  "data": null,
  "timestamp": "2026-10-16T10:15:30.123456"
}
```

### Tabel Status Code HTTP:
| Status Code | Deskripsi Kasus |
|---|---|
| `200 OK` | Operasi berhasil dijalankan. |
| `400 Bad Request` | Validasi input gagal (email tidak valid, format JSON rusak). Field `data` berisi rincian field yang bermasalah. |
| `401 Unauthorized` | Belum menyertakan token Bearer, atau token tidak valid / sudah kedaluwarsa. |
| `403 Forbidden` | Upaya mengakses kartu atau subscription milik nasabah lain. |
| `404 Not Found` | Data kartu atau subscription dengan ID yang diminta tidak ditemukan. |
| `409 Conflict` | Konflik logika bisnis, misalnya mencoba unsubscribe layanan yang sudah nonaktif. |
| `500 Internal Error` | Terjadi kegagalan server yang tidak terduga. |

---

## 9. Daftar Endpoint Lengkap & Contoh Request/Response

### 1. POST /api/auth/login
Melakukan autentikasi nasabah dan mengembalikan token JWT.

- **Header**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "email": "nasabah@bri.co.id",
    "password": "Password123!"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Login berhasil",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
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

---

### 2. GET /api/cards
Mengambil semua kartu kredit yang dimiliki nasabah yang sedang login.

- **Header**: `Authorization: Bearer <TOKEN>`
- **Response (200 OK)**:
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

---

### 3. GET /api/cards/{cardId}/subscriptions
Mengambil daftar langganan aktif maupun riwayat nonaktif pada kartu kredit tertentu.

- **Header**: `Authorization: Bearer <TOKEN>`
- **Path Parameter**: `cardId` (contoh: `1`)
- **Query Parameter (Opsional)**: `status=ACTIVE`
- **Response (200 OK)**:
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
      }
    ],
    "timestamp": "2026-10-16T10:00:00"
  }
  ```

---

### 4. POST /api/subscriptions/{id}/unsubscribe
Membatalkan layanan langganan tertentu. Status subscription berubah menjadi `INACTIVE` dan dicatat waktu pembatalannya.

- **Header**: `Authorization: Bearer <TOKEN>`
- **Path Parameter**: `id` (contoh: `1`)
- **Response (200 OK)**:
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

---

## 10. Panduan Integrasi Tim Frontend (CORS)

Backend ini siap di-consume oleh aplikasi frontend (React, Vue, Vite, Next.js, atau Lovable app). Konfigurasi CORS dapat diatur langsung melalui file [`src/main/resources/application.yml`](file:///c:/Users/APP%20FS%20Dev%20SAU/Desktop/credit-card-be/src/main/resources/application.yml):

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

> **Tips untuk Developer Frontend**:
> Sertakan header `Authorization: Bearer <token_jwt>` pada setiap request ke endpoint `/api/cards` dan `/api/subscriptions`.

---

## 11. Akses H2 Database Console

Anda dapat melihat struktur tabel dan query data langsung via web console:
- **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **Driver Class**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:cardsmarttrackdb`
- **User Name**: `sa`
- **Password**: `password`

---

## 12. Menjalankan Pengujian Otomatis (Unit & Integration Test)

Project ini dilengkapi dengan 22 skenario test yang menguji service layer dan controller (termasuk skenario keamanan 401, 403, 404, dan 409).

Jalankan pengujian dengan perintah:
```bash
mvn test
```

### Hasil Test:
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
[INFO] 
[INFO] Results:
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```