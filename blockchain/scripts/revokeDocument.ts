import { ethers } from "hardhat";

async function main() {
  const contractAddress = process.env.VALIDIN_CONTRACT_ADDRESS;
  const docHash = process.env.VALIDIN_DOC_HASH;
  const reason = process.env.VALIDIN_REVOKE_REASON || "Revoked by issuer";

  if (!contractAddress) throw new Error("Set VALIDIN_CONTRACT_ADDRESS in .env");
  if (!docHash || !/^0x[0-9a-fA-F]{64}$/.test(docHash)) {
    throw new Error("Set VALIDIN_DOC_HASH as a 32-byte hex value");
  }

  const registry = await ethers.getContractAt("ValidInRegistry", contractAddress);
  const tx = await registry.revokeDocument(docHash, reason);
  console.log(`Revoking ${docHash}: ${tx.hash}`);
  await tx.wait();
  console.log("Document revoked.");
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
