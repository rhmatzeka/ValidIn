import { ethers } from "hardhat";

async function main() {
  const contractAddress = process.env.VALIDIN_CONTRACT_ADDRESS;
  const docHash = process.env.VALIDIN_DOC_HASH;

  if (!contractAddress) throw new Error("Set VALIDIN_CONTRACT_ADDRESS in .env");
  if (!docHash || !/^0x[0-9a-fA-F]{64}$/.test(docHash)) {
    throw new Error("Set VALIDIN_DOC_HASH as a 32-byte hex value");
  }

  const registry = await ethers.getContractAt("ValidInRegistry", contractAddress);
  const status = await registry.documentStatus(docHash);
  const docType = await registry.documentTypeOf(docHash);

  console.log(`exists=${status.exists}`);
  console.log(`active=${status.active}`);
  console.log(`issuer=${status.issuer}`);
  console.log(`issuedAt=${status.issuedAt}`);
  console.log(`metadataHash=${status.metadataHash}`);
  console.log(`subjectHash=${status.subjectHash}`);
  console.log(`docType=${docType}`);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
