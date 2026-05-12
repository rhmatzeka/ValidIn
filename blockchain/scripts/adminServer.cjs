const http = require("node:http");
const path = require("node:path");
const dotenv = require("dotenv");
const { ethers } = require("ethers");

dotenv.config({ path: path.resolve(process.cwd(), "../.env") });
dotenv.config();

const port = Number(process.env.VALIDIN_ADMIN_API_PORT || 8787);
const rpcUrl = process.env.SEPOLIA_RPC_URL || "";
const privateKey = process.env.PRIVATE_KEY || "";
const contractAddress = process.env.VALIDIN_CONTRACT_ADDRESS || "";

const abi = [
  "function registerDocument(bytes32 docHash, bytes32 metadataHash, bytes32 subjectHash, string docType) external"
];

function sendJson(response, status, payload) {
  response.writeHead(status, { "Content-Type": "application/json" });
  response.end(JSON.stringify(payload));
}

function readBody(request) {
  return new Promise((resolve, reject) => {
    let body = "";
    request.on("data", chunk => {
      body += chunk;
      if (body.length > 1_000_000) request.destroy();
    });
    request.on("end", () => resolve(body));
    request.on("error", reject);
  });
}

function isBytes32(value) {
  return /^0x[0-9a-fA-F]{64}$/.test(value || "");
}

async function registerDocument(payload) {
  if (!rpcUrl || !privateKey || !contractAddress) {
    throw new Error("Server admin belum lengkap. Isi SEPOLIA_RPC_URL, PRIVATE_KEY, dan VALIDIN_CONTRACT_ADDRESS.");
  }
  if (!isBytes32(payload.docHash)) throw new Error("docHash tidak valid.");
  if (!isBytes32(payload.metadataHash)) throw new Error("metadataHash tidak valid.");
  if (!isBytes32(payload.subjectHash)) throw new Error("subjectHash tidak valid.");
  if (!payload.docType) throw new Error("docType wajib diisi.");

  const provider = new ethers.JsonRpcProvider(rpcUrl);
  const wallet = new ethers.Wallet(privateKey, provider);
  const registry = new ethers.Contract(contractAddress, abi, wallet);
  const tx = await registry.registerDocument(
    payload.docHash,
    payload.metadataHash,
    payload.subjectHash,
    payload.docType
  );
  const receipt = await tx.wait();
  return { txHash: tx.hash, blockNumber: receipt.blockNumber };
}

const server = http.createServer(async (request, response) => {
  try {
    if (request.method === "GET" && request.url === "/health") {
      sendJson(response, 200, { ok: true, contractAddress });
      return;
    }

    if (request.method === "POST" && request.url === "/register") {
      const body = await readBody(request);
      const payload = JSON.parse(body || "{}");
      const result = await registerDocument(payload);
      sendJson(response, 200, { ok: true, ...result });
      return;
    }

    sendJson(response, 404, { ok: false, error: "Not found" });
  } catch (error) {
    sendJson(response, 400, { ok: false, error: error.message || String(error) });
  }
});

server.listen(port, "0.0.0.0", () => {
  console.log(`ValidIn admin server running on http://0.0.0.0:${port}`);
});
