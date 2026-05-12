const { createHash } = require("node:crypto");
const { readFileSync } = require("node:fs");

const filePath = process.argv[2];
const subject = process.argv[3] || "";
const metadata = process.argv[4] || "";

if (!filePath) {
  console.error("Usage: npm run hash -- ./sertifikat.pdf \"NIM/Nama\" \"Jenis/Tanggal/Issuer\"");
  process.exit(1);
}

function sha256Hex(input) {
  return `0x${createHash("sha256").update(input).digest("hex")}`;
}

const file = readFileSync(filePath);

console.log(`VALIDIN_DOC_HASH=${sha256Hex(file)}`);
console.log(`VALIDIN_SUBJECT_HASH=${sha256Hex(subject)}`);
console.log(`VALIDIN_METADATA_HASH=${sha256Hex(metadata)}`);
