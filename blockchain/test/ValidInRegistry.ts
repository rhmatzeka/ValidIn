import { expect } from "chai";
import { ethers } from "hardhat";

describe("ValidInRegistry", function () {
  it("registers and verifies a document", async function () {
    const [owner] = await ethers.getSigners();
    const registry = await ethers.deployContract("ValidInRegistry");
    const docHash = ethers.sha256(ethers.toUtf8Bytes("validin-demo-document"));
    const metadataHash = ethers.sha256(ethers.toUtf8Bytes("certificate-2026"));
    const subjectHash = ethers.sha256(ethers.toUtf8Bytes("nim-123"));

    await registry.registerDocument(docHash, metadataHash, subjectHash, "Sertifikat");
    const status = await registry.documentStatus(docHash);

    expect(status.exists).to.equal(true);
    expect(status.active).to.equal(true);
    expect(status.issuer).to.equal(owner.address);
    expect(status.metadataHash).to.equal(metadataHash);
    expect(status.subjectHash).to.equal(subjectHash);
  });
});
