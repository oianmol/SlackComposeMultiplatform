//
//  StoredKey.swift
//  capillaryslack
//
//  Created by Anmol Verma on 02/12/22.
//

import Foundation

import CryptoKit

// General approach to storing symmetric keys in the keychain, and code
// snippets, are from here:
// https://developer.apple.com/documentation/cryptokit/storing_cryptokit_keys_in_the_keychain
// Declare protocol.
protocol GenericPasswordConvertible: CustomStringConvertible {
    /// Creates a key from a raw representation.
    init<D>(rawRepresentation data: D) throws where D: ContiguousBytes
    
    /// A raw representation of the key.
    var rawRepresentation: Data { get }
}

// Add extension that makes CryptoKey SymmetricKey satisfy the protocol.
extension SymmetricKey: GenericPasswordConvertible {
    public var description: String {
        return "symmetrically"
    }
    
    init<D>(rawRepresentation data: D) throws where D: ContiguousBytes {
        self.init(data: data)
    }
    
    var rawRepresentation: Data {
        return withUnsafeBytes{Data($0)}
    }
}
// End of first code to support storing CryptoKit symmetric key in the keychain.
// Handy extension to get an error message from an OSStatus.
extension OSStatus {
    var secErrorMessage: String {
        return (SecCopyErrorMessageString(self, nil) as String?) ?? "\(self)"
    }
}

// Extensions to make some pre-Swift classes conform to Encodable.
extension NSNumber: Encodable {
    public func encode(to encoder: Encoder) throws {
        try Int(exactly: self).encode(to: encoder)
    }
}

extension CFNumber: Encodable {
    public func encode(to encoder: Encoder) throws {
        try (self as NSNumber).encode(to: encoder)
    }
}
extension CFString: Encodable {
    public func encode(to encoder: Encoder) throws {
        try (self as String).encode(to: encoder)
    }
}

protocol StoredKeyBasis {
    func storedKey() -> StoredKey
}
extension SecKey: StoredKeyBasis {
    func storedKey() -> StoredKey { return StoredKey(self) }
}
extension SymmetricKey: StoredKeyBasis {
    func storedKey() -> StoredKey { return StoredKey(self) }
}

// Main class in this file.
class StoredKey {
    
    // Enumeration for different storage of keys supported by this class, either
    // as generic passwords in the keychain, or as keys in the keychain. The
    // keychain only stores private keys as keys, so symmetric keys must be
    // stored as generic passwords.
    enum Storage: String, CaseIterable {
        case generic, key

        var secClass:CFString {
            switch self {
            case .generic:
                return kSecClassGenericPassword
            case .key:
                return kSecClassKey
            }
        }
    }
    
    // Enumeration for descriptive names for types of key pair.
    //
    // The values for the key type attribute, kSecAttrKeyType, have slightly
    // strange behaviour.
    //
    // In the dictionary passed to SecKeyCreateRandomKey, the value of the
    // kSecAttrKeyType attribute is a CFString. However, the contents of the
    // CFString will be numeric. For example, kSecAttrKeyTypeRSA has the
    // value "42". See, for example:
    // https://opensource.apple.com/source/Security/Security-55471/sec/Security/SecItemConstants.c.auto.html
    //
    // In the dictionary returned from SecItemCopyMatching, the value of the
    // kSecAttrKeyType attribute will be a CFNumber instead.
    //
    private enum KeyType: String, CaseIterable {
        case RSA, EC

        var secAttrKeyType:CFString {
            switch self {
            case .RSA:
                return kSecAttrKeyTypeRSA
            case .EC:
                return kSecAttrKeyTypeECSECPrimeRandom
            }
        }
        // There is also a kSecAttrKeyTypeEC, which is deprecated. It has the
        // same value as kSecAttrKeyTypeECSECPrimeRandom. This means that
        // there's no way to tell the difference by matching.
        static func matching(_ secAttrKeyTypeValue:CFString) -> KeyType? {
            // self.allCases on the next line is enabled by CaseIterable in the
            // declaration.
            self.allCases.first(where: {
                $0.secAttrKeyType == secAttrKeyTypeValue
            })
        }

        // Nested struct for a description tuple containing:
        //
        // -   String, like "RSA" or "EC".
        // -   Raw value from a keychain query, or keychain attribute
        //     dictionary.
        //
        // In practice, the raw value will be CFNumber or CFString.
        struct Description:Encodable {
            let keyType:String
            let raw:AnyEncodable

            private init(keyType:String, raw:Encodable) {
                self.keyType = keyType
                self.raw = AnyEncodable(raw)
            }
            
            init(fromCopyAttribute specifier: Any) {
                // If the specifier is a number, compare numerically to the
                // numbers in each kSecAttrKeyType constant.
                if let typeNumber = specifier as? NSNumber,
                   let typeInt = Int(exactly: typeNumber),
                   let keyType = KeyType.allCases.first(where: {
                    Int($0.secAttrKeyType as String) == typeInt
                   })
                {
                    self.init(keyType: keyType.rawValue, raw: typeNumber)
                }
                // Otherwise, compare as a string.
                else if let typeString = specifier as? String {
                    self.init(
                        keyType:
                            KeyType.matching(typeString as CFString)?.rawValue
                            ?? typeString,
                        raw: typeString
                    )
                }
                // Otherwise, go through a catch-all.
                else {
                    self.init(keyType: "Unknown", raw: "\(specifier)")
                }
            }
        }
    }
    
    struct Deletion: Encodable {
        let deleted: [String]
        let notDeleted: [String:String]
    }

    // Clears the keychain and returns a summary of what storage types were
    // deleted or not deleted because of an error.
    static func deleteAll() -> Deletion {
        var deleted:[String] = []
        var notDeleted:[String:String] = [:]
        
        for storage in Storage.allCases {
            // Query to find all items of this security class.
            let query: [CFString: Any] = [kSecClass: storage.secClass]
            let status = SecItemDelete(query as CFDictionary)
            if status == errSecSuccess || status == errSecItemNotFound {
                deleted.append(storage.rawValue)
            }
            else {
                notDeleted[storage.rawValue] = status.secErrorMessage
            }
        }
        
        return Deletion(deleted: deleted, notDeleted: notDeleted)
    }
    
    static func keysWithName(_ alias:String) throws -> [StoredKey] {
        return try Storage.allCases.flatMap {storage -> [StoredKey] in
            var query: [CFString: Any] = [
                kSecClass: storage.secClass,
                kSecAttrLabel: alias,
            ]
            
            switch(storage) {
            case .generic:
                query[kSecReturnData] = true
            case .key:
                query[kSecReturnRef] = true
            }
            
            var itemRef: CFTypeRef?
            let status = SecItemCopyMatching(query as CFDictionary, &itemRef)
            
            guard status == errSecSuccess || status == errSecItemNotFound else {
                throw StoredKeyError(status, "Query \(query).")
            }
            
            // Set items to an NSArray of the return value, or an empty NSArray.
            let items = status == errSecSuccess
                ? (itemRef as! CFArray) as NSArray
                : NSArray()
            
            return items.map({item in
                switch(storage) {
                case .generic:
                    return StoredKey(SymmetricKey(data: item as! Data))
                case .key:
                    return StoredKey(item as! SecKey)
                }
            })
        }
    }
    
    // Dummy type to wrap any Encodable value.
    //
    // This is here because the following doesn't compile:
    //
    //     public struct Description:Encodable {
    //         let storage:String
    //         let name:String
    //         let type:String
    //         let attributes:[String:Encodable] // This line is an error.
    //     }
    //
    // It appears that there has to be an enum, struct or class wrapped around
    // the object that is Encodable.
    //
    struct AnyEncodable:Encodable {
        let encodable:Encodable
        
        init(_ encodable:Encodable) {
            self.encodable = encodable
        }

        func encode(to encoder: Encoder) throws {
            try encodable.encode(to: encoder)
        }
    }

    // Encodable representation of a key, as returned by a keychain query.
    public struct Description:Encodable {
        let storage:String
        let name:String
        let type:String
        let attributes:[String:AnyEncodable]
        
        init(_ storage:Storage, _ attributes:CFDictionary) {
            self.storage = storage.rawValue
            
            // Create a dictionary of normalised values. Some of the normalised
            // values are also used in the rest of the constructor.
            self.attributes = Description.normalise(cfAttributes: attributes)
            
            // `name` will be the kSecAttrLabel if it can be a String, or the
            // empty string otherwise.
            self.name =
                (attributes as NSDictionary)[kSecAttrLabel as String] as? String
                ?? ""

            // `type` will be a string derived by the KeyType.Description
            // constructor.
            let keyType:String
            if let element = self.attributes[kSecAttrKeyType as String] {
                if let description = element.encodable as? KeyType.Description {
                    keyType = description.keyType
                }
                else {
                    // Code reaches this point if there is somehow a value in
                    // the normalised dictionary that isn't a
                    // KeyType.Description, which shouldn't happen but just in
                    // case.
                    keyType = "\(element.encodable)"
                }
            }
            else {
                keyType = ""
            }
            self.type = keyType
        }

        static private func fallbackValue(_ rawValue:Any) -> Encodable {
            return rawValue as? NSNumber
                ?? (rawValue as? Encodable ?? "\(rawValue)")
        }
        
        static func normalise(cfAttributes:CFDictionary) -> [String:AnyEncodable] {
            // Keys in the attribute dictionary will sometimes be the short
            // names that are the underlying values of the various kSecAttr
            // constants. You can see a list of all the short names and
            // corresponding kSecAttr names in the Apple Open Source
            // SecItemConstants.c file. For example, here:
            // https://opensource.apple.com/source/Security/Security-55471/sec/Security/SecItemConstants.c.auto.html
            
            var returning: [String:AnyEncodable] = [:]
            for (rawKey, rawValue) in cfAttributes as NSDictionary {
                let value:Encodable
                    
                if let key = rawKey as? String {
                    // Check for known attributes with special handling first.
                    if key == kSecAttrApplicationTag as String {
                        if let rawData = rawValue as? Data {
                            value = String(data: rawData, encoding: .utf8)
                        }
                        else {
                            // If rawValue is a String already, or any other
                            // Encodable, the fallbackValue will return it.
                            value = fallbackValue(rawValue)
                        }
                    }
                    else if key == kSecAttrKeyType as String {
                        value = KeyType.Description(fromCopyAttribute: rawValue)
                    }
                    //
                    // Key isn't a known value with special handling.
                    else if let nsDictionary = rawValue as? NSDictionary {
                        // Recursive call to preserve hierarchy, for example if
                        // this is an attribute dictionary for a key pair.
                        value = normalise(cfAttributes: nsDictionary)
                    }
                    else {
                        value = fallbackValue(rawValue)
                    }
                    returning[key] = AnyEncodable(value)
                }
                else {
                    // Code reaches this point if the key couldn't be cast to
                    // String. This is a catch all.
                    returning[String(describing: rawKey)] =
                        AnyEncodable(fallbackValue(rawValue))
                }
            }
            return returning
        }
        
    }

    static func describeAll() throws -> [Description] {
        return try Storage.allCases.flatMap {storage -> [Description] in
            let query: [CFString: Any] = [
                kSecClass: storage.secClass,
                kSecReturnAttributes: true,
                kSecMatchLimit: kSecMatchLimitAll
            ]
            // Above query sets kSecMatchLimit: kSecMatchLimitAll so that the
            // results will be a CFArray. The type of each item in the array is
            // determined by which kSecReturn option is set.
            //
            // kSecReturnAttributes true
            // Gets a CFDictionary representation of each key.
            //
            // kSecReturnRef true
            // Would get a SecKey object for each key. A dictionary
            // representation can be generated from a SecKey by calling
            // SecKeyCopyAttributes(). However the resulting dictionary has only
            // a subset of the attributes. For example, it doesn't have these:
            //
            // -   kSecAttrLabel
            // -   kSecAttrApplicationTag
            //
            // kSecReturnData true
            // Gets a CFData instance for each key. From the reference documentation
            // it looks like the data should be a PKCS#1 representation.
            
            var itemRef: CFTypeRef?
            let status = SecItemCopyMatching(query as CFDictionary, &itemRef)
            
            // If SecItemCopyMatching failed, status will be a numeric error
            // code. To find out what a particular number means, you can look it
            // up here:
            // https://www.osstatus.com/search/results?platform=all&framework=all&search=errSec
            // That will get you the symbolic name.
            //
            // Symbolic names can be looked up in the official reference, here:
            // https://developer.apple.com/documentation/security/1542001-security_framework_result_codes
            // But it isn't searchable by number.
            //
            // This is how Jim found out that -25300 is errSecItemNotFound.
            
            guard status == errSecSuccess || status == errSecItemNotFound else {
                throw StoredKeyError(status)
            }

            // Set items to an NSArray of the return value, or an empty NSArray
            // in case of errSecItemNotFound.
            let items = status == errSecSuccess
                ? (itemRef as! CFArray) as NSArray
                : NSArray()
            
            return items.map { item -> Description in
                Description(storage, item as! CFDictionary)
            }
        }
    }
        
    private enum GenerationSentinelResult:String {
        case passed, failed, multipleKeys
    }
    
    private static func generationSentinel(
        _ basis:StoredKeyBasis, _ alias:String
    ) throws -> GenerationSentinelResult
    {
        let keys = try self.keysWithName(alias)
        if keys.count == 1 {
            let storedKey = basis.storedKey()
            let sentinel = "InMemorySentinel"
            let encrypted = try storedKey.encrypt(sentinel)
            let decrypted = try self.decrypt(
                encrypted, withFirstKeyNamed: alias)
            return sentinel == decrypted ? .passed : .failed
        }
        else {
            return .multipleKeys
        }
    }
    
    struct KeyGeneration:Encodable {
        let deletedFirst:Bool
        let sentinelCheck:String
        let summary:[String]
        let attributes:[String:AnyEncodable]
    }
    
    // Generate a symmetric key and store it in the keychain, as a generic
    // password.
    static func generateKey(withName alias:String) throws -> KeyGeneration {
        // First delete any generic key chain item with the same label. If you
        // don't, the add seems to fail as a duplicate.
        let deleteQuery:[CFString:Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrLabel: alias,
            
            // Generic passwords in the keychain use the following two items as
            // identifying attributes. If you don't set them, a first keychain
            // item will still be stored, but a second keychain item will be
            // rejected as a duplicate.
            // TOTH: https://useyourloaf.com/blog/keychain-duplicate-item-when-adding-password/
            kSecAttrAccount: "Account \(alias)",
            kSecAttrService: "Service \(alias)"
            
        ]
        let deleted = SecItemDelete(deleteQuery as CFDictionary)
        guard deleted == errSecSuccess || deleted == errSecItemNotFound else {
            throw StoredKeyError(
                deleted, "Failed SecItemDelete(\(deleteQuery)).")
        }

        // Generate the random symmetric key.
        let key = SymmetricKey(size: .bits256)
        
        // Merge in more query attributes, to create the add query.
        let addQuery = deleteQuery.merging([
            kSecReturnAttributes: true,
            kSecValueData: key.rawRepresentation,
        ]) {(_, new) in new}

        var result: CFTypeRef?
        let added = SecItemAdd(addQuery as CFDictionary, &result)
        guard added == errSecSuccess else {
            throw StoredKeyError(added, "Failed SecItemAdd(\(addQuery),)")
        }
        
        // The KeyGeneration here is a little different to the generateKeyPair
        // return value. That's because this key is created in memory and then
        // put in the keychain with a query, as two steps. Key pair generation
        // is already in the keychain as a single step.
        return KeyGeneration(
            deletedFirst: deleted == errSecSuccess,
            sentinelCheck: try generationSentinel(key, alias).rawValue,
            summary: [String(describing:key)],
            attributes:
                Description.normalise(cfAttributes: result as! CFDictionary)
        )
    }
    
    private static func tag(forAlias alias: String) -> (String, Data) {
        let tagString = "\(Bundle.main.bundleIdentifier ?? "").\(alias)"
        return (tagString, tagString.data(using: .utf8)!)
    }

    static func generateKeyPair(withName alias:String,isTest:Bool) throws
    {
        // Official code snippets are here:
        // https://developer.apple.com/documentation/security/certificate_key_and_trust_services/keys/generating_new_cryptographic_keys
        guard let tagSD = alias.data(using: .utf8) else {
                 throw SwiftyRSAError.stringToDataConversionFailed
             }
        
        let attributes: [CFString: Any] = [
            kSecAttrKeyType: kSecAttrKeyTypeRSA,
            kSecAttrKeySizeInBits: 2048,
            kSecPrivateKeyAttrs: [
                kSecAttrIsPermanent: isTest,
                kSecAttrLabel: alias,
                kSecAttrApplicationTag: tagSD
            ]
        ]
        
        var error: Unmanaged<CFError>?
        guard let secKey = SecKeyCreateRandomKey(
            attributes as CFDictionary, &error) else
        {
            throw error!.takeRetainedValue() as Error
        }
        
      
    }
    
    // Properties and methods of a StoredKey instance. It isn't necessary to use
    // StoredKey instances externally. The static methods, like
    // encypt(message withFirstKeyNamed:) for example, can be used instead.
    private let _storage:Storage
    let secKey:SecKey?
    let symmetricKey:SymmetricKey?

    var storage:String {_storage.rawValue}

    // Symmetric key constructor.
    init(_ symmetricKey:SymmetricKey) {
        _storage = .generic
        secKey = nil
        self.symmetricKey = symmetricKey
    }
    
    // Key pair constructor. The SecKey will be the private key. The
    // corresponding private key will be generated if needed in the
    // encryptBasedOnPrivateKey() method, below.
    init(_ secKey:SecKey) {
        _storage = .key
        self.secKey = secKey
        symmetricKey = nil
    }

    // Tuple for encrypted data and the algorithm. The algorithm is for
    // description only. It is nil in the symmetric key case.
    public struct Encrypted {
        let message:Data
        let algorithm:SecKeyAlgorithm?
    }

    // Instance methods for encryption and decryption.
    func encrypt(_ message:String) throws -> Encrypted
    {
        switch _storage {
        case .key:
            return try encryptBasedOnPrivateKey(message)
        case .generic:
            return try encryptWithSymmetricKey(message)
        }
    }

    // Instance methods for encryption and decryption.
    func encryptBytes(_ message:Data) throws -> Encrypted
    {
        switch _storage {
        case .key:
            return try encryptBasedOnPrivateKey(message)
        case .generic:
            return try encryptWithSymmetricKey(message)
        }
    }

    
    func decrypt(_ encrypted:Data) throws -> String {
        switch _storage {
        case .key:
            return try decryptWithPrivateKey(encrypted as CFData)
        case .generic:
            return try decryptWithSymmetricKey(encrypted)
        }
    }
    
    func decryptAsData(_ encrypted:Data) throws -> Data {
        switch _storage {
        case .key:
            return try decryptWithPrivateKeyAsData(encrypted as CFData)
        case .generic:
            return try decryptWithSymmetricKeyAsData(encrypted)
        }
    }
    
    func decrypt(_ encrypted:Encrypted) throws -> String {
        return try decrypt(encrypted.message)
    }
    
    private func encryptWithSymmetricKey(_ message:String) throws -> Encrypted {
        guard let box = try
            AES.GCM.seal(
                Data(message.utf8) as NSData, using: symmetricKey!
            ).combined else
        {
            throw StoredKeyError("Combined nil.")
        }
        return Encrypted(message:box, algorithm: nil)
    }
    
    private func encryptWithSymmetricKey(_ message:Data) throws -> Encrypted {
        guard let box = try
            AES.GCM.seal(message, using: symmetricKey!).combined else
        {
            throw StoredKeyError("Combined nil.")
        }
        return Encrypted(message:box, algorithm: nil)
    }

    private func decryptWithSymmetricKey(_ encrypted:Data) throws -> String {
        let sealed = try AES.GCM.SealedBox(combined: encrypted)
        let decryptedData = try AES.GCM.open(sealed, using: symmetricKey!)
        let message =
            String(data: decryptedData, encoding: .utf8) ?? "\(decryptedData)"
        return message
    }
    
    private func decryptWithSymmetricKeyAsData(_ encrypted:Data) throws -> Data {
        let sealed = try AES.GCM.SealedBox(combined: encrypted)
        let decryptedData = try AES.GCM.open(sealed, using: symmetricKey!)
        return decryptedData
    }

    // List of algorithms for public key encryption.
    private let algorithms:[SecKeyAlgorithm] = [
        .eciesEncryptionStandardX963SHA1AESGCM,
        .rsaEncryptionPKCS1
    ]
    
    private func encryptBasedOnPrivateKey(_ message:Data) throws -> Encrypted
    {
        guard let publicKey = SecKeyCopyPublicKey(secKey!) else {
            throw StoredKeyError("No public key.")
        }

        guard let algorithm = algorithms.first(
            where: { SecKeyIsAlgorithmSupported(publicKey, .encrypt, $0)}
            ) else
        {
            throw StoredKeyError("No algorithms supported.")
        }
        
        var error: Unmanaged<CFError>?
        guard let encryptedBytes = SecKeyCreateEncryptedData(
            publicKey, algorithm, message as CFData, &error) else {
            throw error!.takeRetainedValue() as Error
        }
        return Encrypted(message: encryptedBytes as Data, algorithm:algorithm)
    }

    private func encryptBasedOnPrivateKey(_ message:String) throws -> Encrypted
    {
        guard let publicKey = SecKeyCopyPublicKey(secKey!) else {
            throw StoredKeyError("No public key.")
        }

        guard let algorithm = algorithms.first(
            where: { SecKeyIsAlgorithmSupported(publicKey, .encrypt, $0)}
            ) else
        {
            throw StoredKeyError("No algorithms supported.")
        }
        
        var error: Unmanaged<CFError>?
        guard let encryptedBytes = SecKeyCreateEncryptedData(
            publicKey, algorithm, Data(message.utf8) as CFData, &error) else {
            throw error!.takeRetainedValue() as Error
        }
        return Encrypted(message: encryptedBytes as Data, algorithm:algorithm)
    }
    
    private func decryptWithPrivateKey(_ encrypted:CFData) throws -> String {
        guard let publicKey = SecKeyCopyPublicKey(secKey!) else {
            throw StoredKeyError("No public key.")
        }
        guard let algorithm = algorithms.first(
            where: { SecKeyIsAlgorithmSupported(publicKey, .decrypt, $0)}
            ) else
        {
            throw StoredKeyError("No algorithms supported.")
        }

        var error: Unmanaged<CFError>?
        guard let decryptedBytes = SecKeyCreateDecryptedData(
            secKey!, algorithm, encrypted, &error) else {
            throw error!.takeRetainedValue() as Error
        }
        
        let message = String(
            data: decryptedBytes as Data, encoding: .utf8)
            ?? "\(decryptedBytes)"
        return message
    }
    
    private func decryptWithPrivateKeyAsData(_ encrypted:CFData) throws -> Data {
        guard let publicKey = SecKeyCopyPublicKey(secKey!) else {
            throw StoredKeyError("No public key.")
        }
        
        
        guard let algorithm = algorithms.first(
            where: { SecKeyIsAlgorithmSupported(publicKey, .decrypt, $0)}
            ) else
        {
            throw StoredKeyError("No algorithms supported.")
        }

        var error: Unmanaged<CFError>?
        guard let decryptedBytes = SecKeyCreateDecryptedData(
            secKey!, algorithm, encrypted, &error) else {
            throw error!.takeRetainedValue() as Error
        }
        
        return decryptedBytes as Data
    }

    // Static methods that work with a key alias instead of a StoredKey
    // instance.
    static func encrypt(_ message:String, withFirstKeyNamed alias:String)
    throws -> Encrypted
    {
        guard let key = try keysWithName(alias).first else {
            throw StoredKeyError(errSecItemNotFound)
        }
        return try key.encrypt(message)
    }
    
    static func decrypt(_ encrypted:Encrypted, withFirstKeyNamed alias:String)
    throws -> String
    {
        guard let key = try keysWithName(alias).first else {
            throw StoredKeyError(errSecItemNotFound)
        }
        return try key.decrypt(encrypted)
    }
}

extension Array {
    func inserting(_ element:Element, at index:Int) -> Array<Element> {
        var inserted = self
        inserted.insert(element, at: index)
        return inserted
    }
}

// Swift seems to have made it rather difficult to create a throw-able that
// has a message that can be retrieved in the catch. So, there's a custom
// class here.
//
// Having created a custom class anyway, it seemed like a code-saver to pack
// it with convenience initialisers for an array of strings, variadic
// strings, CFString, and OSStatus.
public class StoredKeyError: Error, CustomStringConvertible {
    let _message:String
    
    public init(_ message:String) {
        self._message = message
    }
    public convenience init(_ message:[String]) {
        self.init(message.joined())
    }
    public convenience init(_ message:String...) {
        self.init(message)
    }
    public convenience init(_ message:CFString) {
        self.init(NSString(string: message) as String)
    }
    public convenience init(_ osStatus:OSStatus, _ details:String...) {
        self.init(details.inserting(osStatus.secErrorMessage, at: 0))
    }

    public var message: String {
        return self._message
    }
    
    public var localizedDescription: String {
        return self._message
    }
    
    public var description: String {
        return self._message
    }
}
