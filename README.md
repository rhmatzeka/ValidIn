# ValidIn

ValidIn adalah aplikasi Android untuk verifikasi dokumen kampus berbasis AI OCR dan blockchain registry. Aplikasi menghitung fingerprint SHA-256 dokumen, membaca isi dokumen dengan AI OCR, lalu mencocokkan fingerprint tersebut dengan registry smart contract.

## Fitur

- Login mahasiswa.
- Role admin untuk menerbitkan dokumen dari aplikasi.
- Dashboard modern dengan status pemeriksaan.
- Verifikasi dokumen PDF, gambar, dan teks.
- Scan dokumen menggunakan kamera.
- AI OCR untuk membaca nama, NIM/NPM, tanggal, penerbit, dan jenis dokumen.
- Validasi fingerprint dokumen ke smart contract.
- Riwayat pemeriksaan selama aplikasi berjalan.
- Smart contract `ValidInRegistry` untuk register, revoke, dan verifikasi dokumen.

## Login Demo

```text
NIM      : 231011402890
Password : Rahmat123
```

## Struktur Project

```text
app/          Android app
blockchain/   Smart contract, deploy script, register script
sample/       Dokumen demo untuk verifikasi
```

## Smart Contract

Contract yang dipakai:

```text
0x4eEC3e36F4F525705a1435a1CC8c6651D3cfda06
```

Dokumen demo yang sudah terdaftar:

```text
sample/validin-demo-certificate.txt
```

## Menjalankan Android App

Buka project di Android Studio, sync Gradle, lalu run aplikasi ke emulator atau device.

Build APK debug:

```bash
./gradlew :app:assembleDebug
```

APK akan tersedia di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Setup Blockchain

Buat file `.env` dari `.env.example`, lalu isi:

```env
SEPOLIA_RPC_URL=
PRIVATE_KEY=
VALIDIN_CONTRACT_ADDRESS=
VALIDIN_DEFAULT_ISSUER=
VALIDIN_ADMIN_API_URL=http://YOUR_SERVER_IP:8787
VALIDIN_ADMIN_API_PORT=8787
```

Jangan commit `.env` karena berisi private key.

Install dependency dan compile contract:

```bash
cd blockchain
npm install
npm run compile
```

Deploy contract:

```bash
npm run deploy:sepolia
```

Register dokumen:

```bash
npm run hash -- ../path/ke/dokumen.pdf "NIM Nama" "Metadata dokumen"
npm run doc:register
```

Verifikasi dari terminal:

```bash
npm run doc:verify
```

## Admin dari Mobile

Untuk mendaftarkan dokumen baru dari aplikasi mobile, jalankan admin API di laptop/server yang memegang private key issuer:

```bash
cd blockchain
npm run admin:server
```

Isi `VALIDIN_ADMIN_API_URL` di `.env` Android ke alamat server tersebut. Jika emulator Android berjalan di komputer yang sama, biasanya bisa memakai:

```env
VALIDIN_ADMIN_API_URL=http://10.0.2.2:8787
```

Jika memakai HP fisik, gunakan IP laptop dalam jaringan Wi-Fi yang sama, misalnya:

```env
VALIDIN_ADMIN_API_URL=http://192.168.1.10:8787
```

Setelah itu build ulang aplikasi. Di tab `Admin`, pilih atau scan dokumen, isi jenis dokumen dan pemilik dokumen, lalu tekan `Daftarkan dokumen`.

## Catatan

File hanya dianggap valid jika isinya sama persis dengan file yang fingerprint-nya sudah didaftarkan. Jika PDF diedit, dikompres ulang, atau dibuat ulang, hash akan berubah dan status bisa menjadi tidak ditemukan.

Private key tidak ditanam di APK. Registrasi dokumen baru dilakukan lewat admin API agar signer tetap berada di server/admin machine.
