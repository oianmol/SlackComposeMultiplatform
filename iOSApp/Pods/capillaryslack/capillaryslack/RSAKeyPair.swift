//
//  RSAKeyPair.swift
//  capillaryios
//
//  Created by Anmol Verma on 17/11/22.
//

import Foundation
import Security

protocol RSAKeyPairFactoryProtocol {
    var tag: Data { get }
    func build() -> RSAKeyPair?
}

struct RSAKeyPair {
    var privateKey: SecKey
    var publicKey: SecKey
}

class RSAKeyPairFactory: RSAKeyPairFactoryProtocol {
    let tag = "\(Bundle.main.bundleIdentifier ?? "temp").\("pk")".data(using: .utf8)!
    
    func build() -> RSAKeyPair? {
        let attributes: [String: Any] = [kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
                                         kSecAttrKeySizeInBits as String: 2048,
                                         kSecPrivateKeyAttrs as String: [kSecAttrIsPermanent as String: false,
                                                                         kSecAttrApplicationTag as String: tag]]
        
        var pubKey: SecKey?
        var privKey: SecKey?
        
        let status = SecKeyGeneratePair(attributes as CFDictionary, &pubKey, &privKey)
        
        guard status == noErr else {
            print("generateKeyPair() - status != noErr")
            log(Int(status), (SecCopyErrorMessageString(status, nil) ?? "error" as CFString) as String)
            return nil
        }
        
        guard let privateKey = privKey else {
            print("generateKeyPair() - privateKey is nil")
            return nil
        }
        
        guard let publicKey = pubKey else {
            print("generateKeyPair() - publicKey is nil")
            return nil
        }
        
        print("generated with success")
        
        return .init(privateKey: privateKey, publicKey: publicKey)
    }
    
    func log(_ code: Int, _ message: String) {
        print("---------------------")
        print("code -> \(code)")
        print("description -> \(message)")
        print("---------------------")
    }
}
