import { ethers } from "hardhat";

async function main() {
  const [deployer] = await ethers.getSigners();
  console.log(`Deploying ValidInRegistry from ${deployer.address}`);

  const registry = await ethers.deployContract("ValidInRegistry");
  await registry.waitForDeployment();

  const address = await registry.getAddress();
  console.log(`VALIDIN_CONTRACT_ADDRESS=${address}`);
  console.log("Copy this address into the root .env file.");
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
