//
//  capillaryios.swift
//  capillaryios
//
//  Created by Anmol Verma on 15/11/22.
//

import Foundation

@objc public class CapillaryIOS: NSObject {
    @objc public class func initNow(chainId : String) {
        RSAKeyManager.shared.generateKeyPair(chainId:chainId)
    }
    
    @objc public class func publicKey(chainId : String) -> SecKey? {
        return RSAKeyManager.shared.getMyPublicKey(chainId:chainId)?.reference
    }
    
    @objc public class func publicKeyLogs(chainId : String) -> String {
        return RSAKeyManager.shared.getMyPublicKeyLogs(chainId:chainId)
    }
    
    @objc public class func publicKeyString(chainId : String) -> String? {
        return RSAKeyManager.shared.getMyPublicKeyString(chainId:chainId)
    }
    
    @objc public class func privateKey(chainId : String) -> SecKey? {
        return RSAKeyManager.shared.getMyPrivateKey(chainId:chainId)?.reference
    }
        
    @objc public class func encrypt(data:Data,publicKey:SecKey) -> Data? {
        return try! RSAKeyManager.shared.encrypt(data: data, publicKey: PublicKey(reference: publicKey))
    }
    
    @objc public class func decrypt(data:Data,privateKey:SecKey) -> Data? {
        return try! RSAKeyManager.shared.decrypt(encryptedMessage: data, privateKey: PrivateKey(reference: privateKey))
    }
    
    @objc public class func  publicKeyFromBytes(data:Data) -> SecKey? {
        return RSAKeyManager.shared.getPublicKey(data: data)?.reference
    }
    
    @objc public class func  privateKeyFromBytes(data:Data) -> SecKey? {
        return RSAKeyManager.shared.getPrivateKey(data: data)?.reference
    }
        
    @objc public class func bytesFromSecKey(secKey:SecKey) -> Data {
        return try! PublicKey(reference: secKey).data()
    }
    
    @objc public class func bytesFromPrivateKey(secKey:SecKey) -> Data {
        return try! PrivateKey(reference: secKey).data()
    }

}
