# ValidIn

ValidIn adalah aplikasi Android untuk verifikasi dokumen kampus berbasis AI OCR dan Ethereum Sepolia.

## Alur Pemakaian

1. Admin kampus membuat atau menerima dokumen PDF/gambar.
2. Admin menghitung SHA-256 file dokumen.
3. Issuer yang sudah diizinkan mendaftarkan hash dokumen ke `ValidInRegistry` di Sepolia.
4. Mahasiswa/verifikator membuka app Android, memilih file dokumen, lalu app:
   - menghitung hash file,
   - menjalankan AI OCR untuk membaca nama/NIM/tanggal/jenis dokumen,
   - memanggil contract Sepolia untuk mengecek apakah hash tersebut terdaftar dan aktif.

Contract hanya menyimpan hash, issuer, timestamp, dan metadata hash. Isi dokumen asli tidak disimpan di blockchain.

## Setup `.env`

File `.env` sudah dibuat di root project. Isi nilai berikut:

```env
SEPOLIA_RPC_URL=https://sepolia.infura.io/v3/YOUR_PROJECT_ID
PRIVATE_KEY=0xYOUR_DEPLOYER_PRIVATE_KEY
ETHERSCAN_API_KEY=YOUR_ETHERSCAN_API_KEY
VALIDIN_CONTRACT_ADDRESS=
VALIDIN_DEFAULT_ISSUER=0xISSUER_WALLET
```

Jangan pakai private key wallet utama. Buat wallet khusus Sepolia untuk tugas ini.

## Deploy Smart Contract

```bash
cd blockchain
npm install
npm run compile
npm run deploy:sepolia
```

Setelah deploy, copy output `VALIDIN_CONTRACT_ADDRESS=...` ke file `.env` root.

## Menambahkan Issuer Kampus

Issuer adalah wallet admin/panitia/kampus yang boleh mendaftarkan dokumen.

```bash
cd blockchain
npm run issuer:add
```

Pastikan `.env` berisi:

```env
VALIDIN_DEFAULT_ISSUER=0xALAMAT_WALLET_ISSUER
```

## Mendaftarkan Dokumen

Hitung hash file dan metadata:

```bash
cd blockchain
npm run hash -- ../sample/sertifikat.pdf "NIM 221011 Nama Rahmat" "Sertifikat Seminar 2026 ValidIn"
```

Copy output ke `.env`:

```env
VALIDIN_DOC_HASH=0x...
VALIDIN_SUBJECT_HASH=0x...
VALIDIN_METADATA_HASH=0x...
VALIDIN_DOC_TYPE=Sertifikat
```

Daftarkan ke Sepolia:

```bash
cd blockchain
npm run doc:register
```

Cek status hash dokumen:

```bash
cd blockchain
npm run doc:verify
```

## Menjalankan Android App

1. Buka project di Android Studio.
2. Pastikan `.env` root sudah berisi `SEPOLIA_RPC_URL` dan `VALIDIN_CONTRACT_ADDRESS`.
3. Sync Gradle.
4. Run app ke emulator/perangkat.
5. Pilih dokumen yang sudah didaftarkan atau scan memakai kamera.
6. Tekan `Cek status`.

Jika dokumen asli dan hash-nya terdaftar, status akan tampil sebagai `ASLI dan aktif`.

## Registrasi Dokumen dari Mobile

Jalankan admin API di laptop/server yang memegang private key issuer:

```bash
cd blockchain
npm run admin:server
```

Isi `VALIDIN_ADMIN_API_URL` di `.env` root, lalu build ulang aplikasi.
Gunakan tab `Admin` untuk memilih/scan dokumen dan mendaftarkan fingerprint-nya ke registry.

## Batasan MVP

- AI OCR membaca gambar dan halaman pertama PDF.
- Registrasi dokumen dari mobile dilakukan lewat admin API, agar private key issuer tidak ditanam di APK.
- Untuk produksi kampus, sebaiknya tambah backend admin, login SSO kampus, dan penyimpanan dokumen internal.
