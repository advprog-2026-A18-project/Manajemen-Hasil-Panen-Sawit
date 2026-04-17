# Management Hasil Panen Sawit

## API Spesification
Base URL: `/api/panen`

#### Global Header (Redundant)
Semua _endpoint_ wajib menerima _header_ berikut dari API Gateway:

- `X-User-Id` (UUID): ID dari user yang sedang mengakses
- `X-User-Role` (String): Role dari user.

#### Status Panen
`Enum` StatusPanen

- `REPORTED`: Laporan baru dibuat oleh Buruh, menunggu _approval_ dari Mandor.
- `APPROVED`: Laporan disetujui oleh Mandor.
- `REJECTED`: Laporan ditolak oleh Mandor. Diberikan alasan penolakan.

### Catat Laporan Panen
Role akses: `BURUH`

```http request
POST /api/panen
```

#### Request Body
| Parameter        | Type           | Keterangan                    |
|------------------|----------------|-------------------------------|
| `buruhId`        | `UUID`         | **Required** ID Buruh         |
| `kuantitasBerat` | `int`          | **Required** Berat panen (kg) |
| `berita`         | `String`       | **Required** Berita panen     |
| `buktiFoto`      | `List<String>` | **Required** Bukti foto panen |


```json
{
  "buruhId": "cd18dddc-2366-4c8d-859f-5977da818e62",
  "kuantitasBerat": 120,
  "berita": "Panen sawit blok A berjalan lancar.",
  "buktiFoto": [
    "https://storage.local/bucket/foto1.jpg",
    "https://storage.local/bucket/foto2.jpg"
  ]
}
```

### Success Response
Status Code: `201 Created`

```json
{
  "id": "4ead4206-f16d-4107-9e53-ef355d0fdee9",
  "buruhId": "cd18dddc-2366-4c8d-859f-5977da818e62",
  "mandorId": null,
  "kuantitasBerat": 120,
  "berita": "Panen sawit blok A berjalan lancar.",
  "buktiFoto": ["https://storage.local/bucket/foto1.jpg"],
  "tanggalPanen": "2026-04-17",
  "status": "PENDING",
  "pesanPenolakan": null
}
```

#### Error Responses
- `400 BAD REQUEST`\
  Format JSON salah atau validasi gagal
- `409 CONFLICT`\
  Laporan panen untuk `buruhId` pada hari ini sudah ada.

### Ambil Daftar Laporan Panen
Role akses: `BURUH | MANDOR`

```http request
GET /api/panen
```

#### Query Parameters
| Parameter       | Type              | Keterangan                                    |
|-----------------|-------------------|-----------------------------------------------|
| `buruh_id`      | `UUID`            | ID Buruh                                      |
| `tanggal_mulai` | `Date YYYY-MM-DD` | Rentang awal waktu panen                      |
| `tanggal_akhir` | `Date YYYY-MM-DD` | Rentang akhir waktu panen                     |
| `tanggal_panen` | `Date YYYY-MM-DD` | Tanggal spesifik waktu panen (untuk `MANDOR`) |
| `status`        | `String`          | Status panen                                  |
| `page`          | `int`             | Halaman data (default: `0`)                   |
| `size`          | `int`             | Jumlah data per halaman (default: `10`)       |

### Success Response
Status Code: `200 OK`

```json
{
  "content": [
    {
      "id": "a1b2c3d4-...",
      "buruhId": "550e8400-...",
      "kuantitasBerat": 150,
      "tanggalPanen": "2026-04-17",
      "status": "PENDING"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 45,
  "totalPages": 5,
  "last": false
}
```

#### Error Responses
- `400 BAD REQUEST`\
  Format JSON salah atau validasi gagal

### Ambil Detail Laporan Panen
Role akses: `BURUH | MANDOR`

```http request
GET /api/panen/{id}
```

#### Path Variable
- `id` (UUID): ID unik untuk laporan panen

### Success Response
Status Code: `200 OK`

```json
{
  "content": [
    {
      "id": "a1b2c3d4-...",
      "buruhId": "550e8400-...",
      "kuantitasBerat": 150,
      "tanggalPanen": "2026-04-17",
      "status": "PENDING"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 45,
  "totalPages": 5,
  "last": false
}
```

#### Error Responses
- `404 NOT FOUND`\
  Panen tidak ditemukan di database.

### Proses Validasi (_Approval_)
Role akses: `MANDOR`

```http request
PATCH /api/panen/{id}/approval
```

#### Path Variable
- `id` (UUID): ID dari laporan panen

#### Request Body
| Parameter        | Type     | Keterangan                          |
|------------------|----------|-------------------------------------|
| `status`         | `String` | **Required** Status validasi panen  |
| `pesanPenolakan` | `String` | Pesan/berita penolakan jika ditolak |

```json
{
  "status": "REJECTED",
  "pesanPenolakan": "Foto terlalu buram."
}
```

### Success Response
Status Code: `200 OK`\
Mengembalikan object Panen dengan nilai status dan pesan penolakan yang sudah diperbarui.

#### Error Responses
- `400 BAD REQUEST`\
  Tidak ada nilai `pesanPenolakan` dengan status `REJECTED` atau status tidak `PENDING` (sudah diproses sebelumnya)
- `404 NOT FOUND`\
  Panen tidak ditemukan di database.
