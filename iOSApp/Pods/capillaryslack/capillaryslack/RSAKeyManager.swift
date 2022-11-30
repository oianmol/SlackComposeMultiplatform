//
//  RSAKeyManager.swift
//  capillaryios
//
//  Created by Anmol Verma on 16/11/22.
//

import Foundation
import Security

public class RSAUtilsError: NSError {
    init(_ message: String) {
        super.init(domain: "\(Bundle.main.bundleIdentifier ?? "")", code: 500, userInfo: [
            NSLocalizedDescriptionKey: message
            ])
    }
    
    @available(*, unavailable)
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

class RSAKeyManager {
    public static let KEY_SIZE = 2048
    var isTest: Bool = false
    private var publicKey, privateKey: SecKey?
    static let shared = RSAKeyManager()
    let exportImportManager = CryptoExportImportManager()
    
    func setIsTest(isTest:Bool){
        self.isTest = isTest
    }
    
    private func tagPrivate(chainId:String) ->String {
        return "\(Bundle.main.bundleIdentifier ?? "").\(chainId).tagPrivate"
    }
    
    private func tagPublic(chainId:String) ->String {
        return "\(Bundle.main.bundleIdentifier ?? "").\(chainId).tagPrivate"
    }
    
    
    public func encrypt(data:Data,publicKey:Data) -> Data? {
        do {
            let error: UnsafeMutablePointer<Unmanaged<CFError>?>? = nil
            let publicSecKey = try PublicKey(data: stripPublicKeyHeader(publicKey)!)
            let encryptedMessageData:Data = SecKeyCreateEncryptedData(publicSecKey.reference, .rsaEncryptionOAEPSHA256, data as CFData,error)! as Data
            return encryptedMessageData
        } catch let error {
            //Log error
            debugPrint(error)
            return nil
        }
       
    }
    
    public func decrypt(encryptedMessage:Data,privateKey:Data) -> Data? {
        do {
            let error:UnsafeMutablePointer<Unmanaged<CFError>?>? = nil
            let privateKey = try PrivateKey(data:stripPrivateKeyHeader(privateKey)!)
            let decryptedMessage:Data = SecKeyCreateDecryptedData(privateKey.reference, .rsaEncryptionOAEPSHA256, encryptedMessage as CFData,error)! as Data
            return decryptedMessage
        } catch let error {
            //Log Error
            debugPrint(error)
            return nil
        }
    }
    
    private func stripPublicKeyHeader(_ pubkey: Data) throws -> Data? {
        if ( pubkey.count == 0 ) {
            return nil
        }
        
        var keyAsArray = [UInt8](repeating: 0, count: pubkey.count / MemoryLayout<UInt8>.size)
        (pubkey as NSData).getBytes(&keyAsArray, length: pubkey.count)
        
        var idx = 0
        if (keyAsArray[idx] != 0x30) {
            throw RSAUtilsError("Provided key doesn't have a valid ASN.1 structure (first byte should be 0x30).")
            //return nil
        }
        idx += 1
        
        if (keyAsArray[idx] > 0x80) {
            idx += Int(keyAsArray[idx]) - 0x80 + 1
        } else {
            idx += 1
        }
        
        /*
         * If current byte is 0x02, it means the key doesn't have a X509 header (it contains only modulo & public exponent). In this case, we can just return the provided DER data as is
         */
        if (Int(keyAsArray[idx]) == 0x02) {
            return pubkey
        }
        
        let seqiod = [UInt8](arrayLiteral: 0x30, 0x0d, 0x06, 0x09, 0x2a, 0x86, 0x48, 0x86, 0xf7, 0x0d, 0x01, 0x01, 0x01, 0x05, 0x00)
        
        for i in idx..<idx+seqiod.count {
            if ( keyAsArray[i] != seqiod[i-idx] ) {
                throw RSAUtilsError("Provided key doesn't have a valid X509 header.")
                //return nil
            }
        }
        idx += seqiod.count
        
        if (keyAsArray[idx] != 0x03) {
            throw RSAUtilsError("Invalid byte at index \(idx) (\(keyAsArray[idx])) for public key header.")
            //return nil
        }
        idx += 1
        
        if (keyAsArray[idx] > 0x80) {
            idx += Int(keyAsArray[idx]) - 0x80 + 1;
        } else {
            idx += 1
        }
        
        if (keyAsArray[idx] != 0x00) {
            throw RSAUtilsError("Invalid byte at index \(idx) (\(keyAsArray[idx])) for public key header.")
            //return nil
        }
        idx += 1
        return pubkey.subdata(in: idx..<keyAsArray.count)
        //return pubkey.subdata(in: NSMakeRange(idx, keyAsArray.count - idx).toRange()!)
    }
    
    
    private func stripPrivateKeyHeader(_ privkey: Data) throws -> Data? {
        if ( privkey.count == 0 ) {
            return nil
        }
        
        var keyAsArray = [UInt8](repeating: 0, count: privkey.count / MemoryLayout<UInt8>.size)
        (privkey as NSData).getBytes(&keyAsArray, length: privkey.count)
        
        //PKCS#8: magic byte at offset 22, check if it's actually ASN.1
        var idx = 22
        if ( keyAsArray[idx] != 0x04 ) {
            return privkey
        }
        idx += 1
        
        //now we need to find out how long the key is, so we can extract the correct hunk
        //of bytes from the buffer.
        var len = Int(keyAsArray[idx])
        idx += 1
        let det = len & 0x80 //check if the high bit set
        if (det == 0) {
            //no? then the length of the key is a number that fits in one byte, (< 128)
            len = len & 0x7f
        } else {
            //otherwise, the length of the key is a number that doesn't fit in one byte (> 127)
            var byteCount = Int(len & 0x7f)
            if (byteCount + idx > privkey.count) {
                return nil
            }
            //so we need to snip off byteCount bytes from the front, and reverse their order
            var accum: UInt = 0
            var idx2 = idx
            idx += byteCount
            while (byteCount > 0) {
                //after each byte, we shove it over, accumulating the value into accum
                accum = (accum << 8) + UInt(keyAsArray[idx2])
                idx2 += 1
                byteCount -= 1
            }
            // now we have read all the bytes of the key length, and converted them to a number,
            // which is the number of bytes in the actual key.  we use this below to extract the
            // key bytes and operate on them
            len = Int(accum)
        }
        return privkey.subdata(in: idx..<idx+len)
        //return privkey.subdata(in: NSMakeRange(idx, len).toRange()!)
    }
    
    
    public func getMyPublicKey(chainId:String) -> PublicKey? {
        do {
            if let pubKey = publicKey {
                return try PublicKey(reference: pubKey)
            } else {
                if getKeysFromKeychain(chainId:chainId), let pubKey = publicKey {
                    return try PublicKey(reference: pubKey)
                } else {
                      generateKeyPair(chainId:chainId)
                    if let pubKey = publicKey {
                        return try PublicKey(reference: pubKey)
                    }
                }
            }
        } catch let error {
            //Log Error
            return nil
        }
        return nil
    }
    
    public func getMyPrivateKey(chainId:String) -> PrivateKey? {
        do {
            if let privKey = privateKey {
                return try PrivateKey(reference: privKey)
            } else {
                if getKeysFromKeychain(chainId:chainId), let privKey = privateKey {
                    return try PrivateKey(reference: privKey)
                } else {
                     generateKeyPair(chainId:chainId)
                    if let privKey = privateKey {
                        return try PrivateKey(reference: privKey)
                    }
                }
            }
        } catch let error {
            //Log Error
            return nil
        }
        return nil
    }
    
    public func getPublicKey(data: Data) -> Data? {
        do {
            return try! PublicKey(data: stripPublicKeyHeader(data)!).data()
        } catch let error {
            debugPrint(error)
            return nil
        }
    }
    
    public func getPrivateKey(data: Data) -> Data? {
        do {
            return try! PrivateKey(data: stripPrivateKeyHeader(data)!).data()
        } catch let error {
            debugPrint(error)
            return nil
        }
    }
    
    //Check Keychain and get keys
    private func getKeysFromKeychain(chainId:String) -> Bool {
        let tagData = chainId.data(using: .utf8)
        privateKey = getKeyTypeInKeyChain(tag: tagData!,keyClass: kSecAttrKeyClassPublic as String)
        publicKey =  getKeyTypeInKeyChain(tag: tagData!,keyClass: kSecAttrKeyClassPrivate as String)
        return ((privateKey != nil)&&(publicKey != nil))
    }
    
    private func getKeyTypeInKeyChain(tag : Data,keyClass:String) -> SecKey? {
        let query: [CFString: Any] = [
            kSecClass: kSecClassKey,
            kSecAttrKeyType: kSecAttrKeyTypeRSA,
            kSecAttrKeyClass: keyClass,
            kSecAttrApplicationTag: tag,
            kSecReturnRef: true
        ]
        
        var result : AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        if status == errSecSuccess {
            return result as! SecKey?
        }
        return nil
    }
    
    //Generate private and public keys
    public func generateKeyPair(chainId:String) {
        if getKeysFromKeychain(chainId:chainId){
            return
        }
        do{
            let keyPair = try SwiftyRSA.generateRSAKeyPair(sizeInBits: RSAKeyManager.KEY_SIZE,applyUnitTestWorkaround: isTest,tagData: chainId)
            publicKey =  keyPair.publicKey.reference
            privateKey =  keyPair.privateKey.reference
        } catch let error {
            debugPrint(error)
        }
    }
    public func getMyPublicKeyData(chainId:String) -> Data? {
        guard let pubKey = self.getMyPublicKey(chainId:chainId)  else {
            return nil
        }
        return try! SwiftyRSA.prependX509KeyHeader(keyData: pubKey.data())
    }
    
    public func getMyPrivateKeyData(chainId:String) -> Data? {
        guard let privateKey = self.getMyPrivateKey(chainId:chainId)  else {
            return nil
        }
        let privateKeyFinal = try! SwiftyRSA.addPKCS8Header(privateKey.data())
        return privateKeyFinal
    }
    
    //Delete keys when required.
    public func deleteAllKeysInKeyChain() {
        let query : [CFString: Any] = [
            kSecClass: kSecClassKey
        ]
        let status = SecItemDelete(query as CFDictionary)

        switch status {
        case errSecItemNotFound: break
            //No key in keychain
        case noErr: break
            //All Keys Deleted
        default: break
            //Log Error
        }
    }
}
