// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;

contract ValidInRegistry {
    struct Document {
        address issuer;
        uint64 issuedAt;
        bool active;
        bytes32 metadataHash;
        bytes32 subjectHash;
        string docType;
    }

    address public owner;
    mapping(address => bool) public issuers;
    mapping(bytes32 => Document) private documents;

    event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);
    event IssuerUpdated(address indexed issuer, bool allowed);
    event DocumentRegistered(
        bytes32 indexed docHash,
        address indexed issuer,
        bytes32 metadataHash,
        bytes32 subjectHash,
        string docType
    );
    event DocumentRevoked(bytes32 indexed docHash, address indexed issuer, string reason);

    modifier onlyOwner() {
        require(msg.sender == owner, "ValidIn: only owner");
        _;
    }

    modifier onlyIssuer() {
        require(issuers[msg.sender], "ValidIn: only issuer");
        _;
    }

    constructor() {
        owner = msg.sender;
        issuers[msg.sender] = true;
        emit OwnershipTransferred(address(0), msg.sender);
        emit IssuerUpdated(msg.sender, true);
    }

    function transferOwnership(address newOwner) external onlyOwner {
        require(newOwner != address(0), "ValidIn: zero owner");
        address previousOwner = owner;
        owner = newOwner;
        issuers[newOwner] = true;
        emit OwnershipTransferred(previousOwner, newOwner);
        emit IssuerUpdated(newOwner, true);
    }

    function setIssuer(address issuer, bool allowed) external onlyOwner {
        require(issuer != address(0), "ValidIn: zero issuer");
        issuers[issuer] = allowed;
        emit IssuerUpdated(issuer, allowed);
    }

    function registerDocument(
        bytes32 docHash,
        bytes32 metadataHash,
        bytes32 subjectHash,
        string calldata docType
    ) external onlyIssuer {
        require(docHash != bytes32(0), "ValidIn: empty document hash");
        require(documents[docHash].issuedAt == 0, "ValidIn: document already registered");
        require(bytes(docType).length > 0, "ValidIn: empty document type");

        documents[docHash] = Document({
            issuer: msg.sender,
            issuedAt: uint64(block.timestamp),
            active: true,
            metadataHash: metadataHash,
            subjectHash: subjectHash,
            docType: docType
        });

        emit DocumentRegistered(docHash, msg.sender, metadataHash, subjectHash, docType);
    }

    function revokeDocument(bytes32 docHash, string calldata reason) external onlyIssuer {
        Document storage document = documents[docHash];
        require(document.issuedAt != 0, "ValidIn: document not found");
        require(document.issuer == msg.sender || msg.sender == owner, "ValidIn: not document issuer");
        require(document.active, "ValidIn: document already revoked");

        document.active = false;
        emit DocumentRevoked(docHash, msg.sender, reason);
    }

    function documentStatus(bytes32 docHash)
        external
        view
        returns (
            bool exists,
            bool active,
            address issuer,
            uint64 issuedAt,
            bytes32 metadataHash,
            bytes32 subjectHash
        )
    {
        Document storage document = documents[docHash];
        exists = document.issuedAt != 0;
        active = document.active;
        issuer = document.issuer;
        issuedAt = document.issuedAt;
        metadataHash = document.metadataHash;
        subjectHash = document.subjectHash;
    }

    function documentTypeOf(bytes32 docHash) external view returns (string memory) {
        return documents[docHash].docType;
    }
}
