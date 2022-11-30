//
//  capillaryios.swift
//  capillaryios
//
//  Created by Anmol Verma on 15/11/22.
//

import Foundation
import Tink



@objc public class CapillaryIOS: NSObject {
    
    @objc public class func setIsTest(isTest:Bool) {
        RSAKeyManager.shared.setIsTest(isTest: isTest)
    }
    
    @objc public class func initNow(chainId : String) {
        let tinkConfig = try! TINKAllConfig.init()
        print(tinkConfig)
        let config = try! TINKAeadConfig.init()
        print(config)
        RSAKeyManager.shared.generateKeyPair(chainId: chainId)
    }
    
    @objc public class func publicKey(chainId : String) -> Data? {
        return RSAKeyManager.shared.getMyPublicKeyData(chainId:chainId)
    }
    
    @objc public class func privateKey(chainId : String) -> Data? {
        return RSAKeyManager.shared.getMyPrivateKeyData(chainId:chainId)
    }
        
    @objc public class func encrypt(data:Data,publicKey:Data) -> EncryptedData {
        
        let symmetricKeyHandle = try! TINKKeysetHandle(keyTemplate: TINKAeadKeyTemplate(keyTemplate: TINKAeadKeyTemplates.TINKAes128Gcm))
        let symmetricKeyBytes = symmetricKeyHandle.serializedKeyset()
        let symmetricKeyCiphertext = RSAKeyManager.shared.encrypt(data: symmetricKeyBytes, publicKey: publicKey)
        let aead = try! TINKAeadFactory.primitive(with: symmetricKeyHandle)
        let payloadCiphertext = try! aead.encrypt(data, withAdditionalData: Data(count: 0))
        return EncryptedData(first: symmetricKeyCiphertext ?? nil , second: payloadCiphertext )
    }
    
    @objc public class func decrypt(symmetricKeyCiphertext:Data, payloadCiphertext:Data, privateKey:Data) -> Data? {
        let symmetricKeyBytes =  RSAKeyManager.shared.decrypt(encryptedMessage: symmetricKeyCiphertext, privateKey: privateKey)
        let symmetricKeyHandle = try! TINKKeysetHandle(cleartextKeysetHandleWith: TINKBinaryKeysetReader(serializedKeyset: symmetricKeyBytes!))
        let aead = try! TINKAeadFactory.primitive(with: symmetricKeyHandle)
        return try! aead.decrypt(payloadCiphertext, withAdditionalData: Data(count: 0))
    }
    
    @objc public class func publicKeyFromBytes(data:Data) -> Data? {
        return RSAKeyManager.shared.getPublicKey(data: data)
    }
    
    @objc public class func privateKeyFromBytes(data:Data) -> Data? {
        return RSAKeyManager.shared.getPrivateKey(data: data)
    }

}
