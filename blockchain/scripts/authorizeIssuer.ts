import { ethers } from "hardhat";

async function main() {
  const contractAddress = process.env.VALIDIN_CONTRACT_ADDRESS;
  const issuer = process.env.VALIDIN_DEFAULT_ISSUER;

  if (!contractAddress) throw new Error("Set VALIDIN_CONTRACT_ADDRESS in .env");
  if (!issuer) throw new Error("Set VALIDIN_DEFAULT_ISSUER in .env");

  const registry = await ethers.getContractAt("ValidInRegistry", contractAddress);
  const tx = await registry.setIssuer(issuer, true);
  console.log(`Authorizing issuer ${issuer}: ${tx.hash}`);
  await tx.wait();
  console.log("Issuer authorized.");
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
