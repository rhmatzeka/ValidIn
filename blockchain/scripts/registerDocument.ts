import { ethers } from "hardhat";

function readBytes32(name: string): string {
  const value = process.env[name];
  if (!value || !/^0x[0-9a-fA-F]{64}$/.test(value)) {
    throw new Error(`Set ${name} as a 32-byte hex value, for example 0x followed by 64 hex characters`);
  }
  return value;
}

async function main() {
  const contractAddress = process.env.VALIDIN_CONTRACT_ADDRESS;
  if (!contractAddress) throw new Error("Set VALIDIN_CONTRACT_ADDRESS in .env");

  const docHash = readBytes32("VALIDIN_DOC_HASH");
  const metadataHash = readBytes32("VALIDIN_METADATA_HASH");
  const subjectHash = readBytes32("VALIDIN_SUBJECT_HASH");
  const docType = process.env.VALIDIN_DOC_TYPE || "Dokumen Kampus";

  const registry = await ethers.getContractAt("ValidInRegistry", contractAddress);
  const tx = await registry.registerDocument(docHash, metadataHash, subjectHash, docType);
  console.log(`Registering ${docType} ${docHash}: ${tx.hash}`);
  await tx.wait();
  console.log("Document registered.");
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
