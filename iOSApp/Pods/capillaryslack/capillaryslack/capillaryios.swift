//
//  capillaryios.swift
//  capillaryios
//
//  Created by Anmol Verma on 15/11/22.
//

import Foundation

@objc public class CapillaryIOS: NSObject {
    
    @objc public class func setIsTest(isTest:Bool) {
        RSAKeyManager.shared.setIsTest(isTest: isTest)
    }
    
    @objc public class func initNow(chainId : String) {
        RSAKeyManager.shared.generateKeyPair(chainId: chainId)
    }
    
    @objc public class func publicKey(chainId : String) -> Data? {
        return RSAKeyManager.shared.getMyPublicKeyData(chainId:chainId)
    }
    
    @objc public class func privateKey(chainId : String) -> Data? {
        return RSAKeyManager.shared.getMyPrivateKeyData(chainId:chainId)
    }
        
    @objc public class func encrypt(data:Data,publicKey:Data) -> Data? {
        return RSAKeyManager.shared.encrypt(data: data, publicKey: publicKey)
    }
    
    @objc public class func decrypt(data:Data,privateKey:Data) -> Data? {
        return RSAKeyManager.shared.decrypt(encryptedMessage: data, privateKey: privateKey)
    }
    
    @objc public class func publicKeyFromBytes(data:Data) -> Data? {
        return try! RSAKeyManager.shared.getPublicKey(data: data)?.data()
    }
    
    @objc public class func privateKeyFromBytes(data:Data) -> Data? {
        return try! RSAKeyManager.shared.getPrivateKey(data: data)?.data()
    }

}
