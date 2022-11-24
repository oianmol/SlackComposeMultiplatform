//
//  CryptoExportImportManager
//  CryptoExportImportManager
//
//  Created by Ignacio Nieto Carvajal on 6/10/15.
//  Copyright © 2015 Ignacio Nieto Carvajal. All rights reserved.
//

import Foundation

// SECP256R1 EC public key header (length + EC params (sequence) + bitstring
private let kCryptoExportImportManagerSecp256r1CurveLen = 256
private let kCryptoExportImportManagerSecp256r1header: [UInt8] = [0x30, 0x59, 0x30, 0x13, 0x06, 0x07, 0x2A, 0x86, 0x48, 0xCE, 0x3D, 0x02, 0x01, 0x06, 0x08, 0x2A, 0x86, 0x48, 0xCE, 0x3D, 0x03, 0x01, 0x07, 0x03, 0x42, 0x00]
private let kCryptoExportImportManagerSecp256r1headerLen = 26

private let kCryptoExportImportManagerSecp384r1CurveLen = 384
private let kCryptoExportImportManagerSecp384r1header: [UInt8] = [0x30, 0x76, 0x30, 0x10, 0x06, 0x07, 0x2A, 0x86, 0x48, 0xCE, 0x3D, 0x02, 0x01, 0x06, 0x05, 0x2B, 0x81, 0x04, 0x00, 0x22, 0x03, 0x62, 0x00]
private let kCryptoExportImportManagerSecp384r1headerLen = 23

private let kCryptoExportImportManagerSecp521r1CurveLen = 521
private let kCryptoExportImportManagerSecp521r1header: [UInt8] = [0x30, 0x81, 0x9B, 0x30, 0x10, 0x06, 0x07, 0x2A, 0x86, 0x48, 0xCE, 0x3D, 0x02, 0x01, 0x06, 0x05, 0x2B, 0x81, 0x04, 0x00, 0x23, 0x03, 0x81, 0x86, 0x00]
private let kCryptoExportImportManagerSecp521r1headerLen = 25

/*
RSA keys: http://www.opensource.apple.com/source/security_certtool/security_certtool-55103/src/dumpasn1.cfg
OID = 06 09 2A 86 48 86 F7 0D 01 01 01
Comment = PKCS #1
Description = rsaEncryption (1 2 840 113549 1 1 1)
NULL byte: 05 00
*/

// RSA OID header
private let kCryptoExportImportManagerRSAOIDHeader: [UInt8] = [0x30, 0x0d, 0x06, 0x09, 0x2a, 0x86, 0x48, 0x86, 0xf7, 0x0d, 0x01, 0x01, 0x01, 0x05, 0x00]
private let kCryptoExportImportManagerRSAOIDHeaderLength = 15

// ASN.1 encoding parameters.
private let kCryptoExportImportManagerASNHeaderSequenceMark: UInt8 = 48 // 0x30
private let kCryptoExportImportManagerASNHeaderIntegerMark: UInt8 = 02 // 0x32
private let kCryptoExportImportManagerASNHeaderBitstringMark: UInt8 = 03 //0x03
private let kCryptoExportImportManagerASNHeaderNullMark: UInt8 = 05 //0x05
private let kCryptoExportImportManagerASNHeaderRSAEncryptionObjectMark: UInt8 = 06 //0x06
private let kCryptoExportImportManagerExtendedLengthMark: UInt8 = 128  // 0x80
private let kCryptoExportImportManagerASNHeaderLengthForRSA = 15

// PEM encoding constants
private let kCryptoExportImportManagerPublicKeyInitialTag = "-----BEGIN PUBLIC KEY-----\n"
private let kCryptoExportImportManagerPublicKeyFinalTag = "-----END PUBLIC KEY-----"
private let kCryptoExportImportManagerPublicNumberOfCharactersInALine = 64

/**
 * This class exists due to the easy and intuitive way of using public keys generated outside iOS in
 * the Security framework and CommonCrypto tools (yes, I'm being sarcastic here).
 * CryptoCertificateImportManager is in charge of importing a certificate and obtaining a valid key
 * reference to use in any of SecKey operations (SecKeyEncrypt, SecKeyRawVerify...).
 * As far as I know, any other way of importing and using public keys from the outside is not
 * advised: https://devforums.apple.com/message/301532#301532
 */
class CryptoExportImportManager: NSObject {
    // MARK: - Import methods.
    
    /**
     * Extracts the public key from a X.509 certificate and returns a valid SecKeyRef that can be
     * used in any of SecKey operations (SecKeyEncrypt, SecKeyRawVerify...).
     * Receives the certificate data in DER format.
     */
    func importPublicKeyReferenceFromDERCertificate(_ certData: Data) -> SecKey? {
        // first we create the certificate reference
        guard let certRef = SecCertificateCreateWithData(nil, certData as CFData) else { return nil }
        print("Successfully generated a valid certificate reference from the data.")

        // now create a SecTrust structure from the certificate where to extract the key from
        var secTrust: SecTrust?
        let secTrustStatus = SecTrustCreateWithCertificates(certRef, nil, &secTrust)
        print("Generating a SecTrust reference from the certificate: \(secTrustStatus)")
        if secTrustStatus != errSecSuccess { return nil }
        
        // now evaluate the certificate.
        var resultType: SecTrustResultType = SecTrustResultType(rawValue: UInt32(0))! // result will be ignored.
        let evaluateStatus = SecTrustEvaluate(secTrust!, &resultType)
        print("Evaluating the obtained SecTrust reference: \(evaluateStatus)")
        if evaluateStatus != errSecSuccess { return nil }
        
        // lastly, once evaluated, we can export the public key from the certificate leaf.
        let publicKeyRef = SecTrustCopyPublicKey(secTrust!)
        print("Got public key reference: \(String(describing: publicKeyRef))")
        return publicKeyRef
    }
    
    // MARK: - Export methods.
    
    /**
     * Exports a key retrieved from the keychain so it can be used outside iOS (i.e: in OpenSSL).
     * Returns a DER representation of the key.
     */
    func exportPublicKeyToDER(_ rawPublicKeyBytes: Data, keyType: String, keySize: Int) -> Data? {
        if keyType == kSecAttrKeyTypeEC as String {
            return exportECPublicKeyToDER(rawPublicKeyBytes, keyType: keyType, keySize: keySize)
        } else if keyType == kSecAttrKeyTypeRSA as String {
            return exportRSAPublicKeyToDER(rawPublicKeyBytes, keyType: keyType, keySize: keySize)
        }
        // unknown key type? return nil
        return nil
    }
    
    /**
     * Exports a key retrieved from the keychain so it can be used outside iOS (i.e: in OpenSSL).
     * Returns a PEM representation of the key.
     */
    func exportPublicKeyToPEM(_ rawPublicKeyBytes: Data, keyType: String, keySize: Int) -> String? {
        if keyType == kSecAttrKeyTypeEC as String {
            return exportECPublicKeyToPEM(rawPublicKeyBytes, keyType: keyType, keySize: keySize)
        } else if keyType == kSecAttrKeyTypeRSA as String {
            return exportRSAPublicKeyToPEM(rawPublicKeyBytes, keyType: keyType, keySize: keySize)
        }
        // unknown key type? return nil
        return nil
    }
    
    /**
     * This function prepares a RSA public key generated with Apple SecKeyGeneratePair to be exported
     * and used outisde iOS, be it openSSL, PHP, Perl, whatever. By default Apple exports RSA public
     * keys in a very raw format. If we want to use it on OpenSSL, PHP or almost anywhere outside iOS, we
     * need to remove add the full PKCS#1 ASN.1 wrapping. Returns a DER representation of the key.
     */
    func exportRSAPublicKeyToDER(_ rawPublicKeyBytes: Data, keyType: String, keySize: Int) -> Data {
        // first we create the space for the ASN.1 header and decide about its length
        let bitstringEncodingLength = bytesNeededForRepresentingInteger(rawPublicKeyBytes.count)
        
        // start building the ASN.1 header
        var headerBuffer = [UInt8](repeating: 0, count: kCryptoExportImportManagerASNHeaderLengthForRSA);
        headerBuffer[0] = kCryptoExportImportManagerASNHeaderSequenceMark;
        
        // total size (OID + encoding + key size) + 2 (marks)
        let totalSize = kCryptoExportImportManagerRSAOIDHeaderLength + bitstringEncodingLength + rawPublicKeyBytes.count + 3
        let totalSizebitstringEncodingLength = encodeASN1LengthParameter(totalSize, buffer: &(headerBuffer[1]))
        
        // bitstring header
        var keyLengthBytesEncoded = 0
        var bitstringBuffer = [UInt8](repeating: 0, count: kCryptoExportImportManagerASNHeaderLengthForRSA);
        bitstringBuffer[0] = kCryptoExportImportManagerASNHeaderBitstringMark
        keyLengthBytesEncoded = encodeASN1LengthParameter(rawPublicKeyBytes.count+1, buffer: &(bitstringBuffer[1]))
        bitstringBuffer[keyLengthBytesEncoded + 1] = 0x00
        
        // build DER key.
        var derKey = Data(capacity: totalSize + totalSizebitstringEncodingLength)
        derKey.append(headerBuffer, count: totalSizebitstringEncodingLength + 1)
        derKey.append(kCryptoExportImportManagerRSAOIDHeader, count: kCryptoExportImportManagerRSAOIDHeaderLength) // Add OID header
        derKey.append(bitstringBuffer, count: keyLengthBytesEncoded + 2) // 0x03 + key bitstring length + 0x00
        derKey.append(rawPublicKeyBytes) // public key raw data.
        return derKey
    }
    

    /**
     * This function prepares a RSA public key generated with Apple SecKeyGeneratePair to be exported
     * and used outisde iOS, be it openSSL, PHP, Perl, whatever. By default Apple exports RSA public
     * keys in a very raw format. If we want to use it on OpenSSL, PHP or almost anywhere outside iOS, we
     * need to remove add the full PKCS#1 ASN.1 wrapping. Returns a DER representation of the key.
     */
    func exportRSAPublicKeyToPEM(_ rawPublicKeyBytes: Data, keyType: String, keySize: Int) -> String {
        return PEMKeyFromDERKey(exportRSAPublicKeyToDER(rawPublicKeyBytes, keyType: keyType, keySize: keySize))
    }

    
    /**
     * Returns the number of bytes needed to represent an integer.
     */
    func bytesNeededForRepresentingInteger(_ number: Int) -> Int {
        if number <= 0 { return 0 }
        var i = 1
        while (i < 8 && number >= (1 << (i * 8))) { i += 1 }
        return i
    }

    /**
     * Generates an ASN.1 length sequence for the given length. Modifies the buffer parameter by
     * writing the ASN.1 sequence. The memory of buffer must be initialized (i.e: from an NSData).
     * Returns the number of bytes used to write the sequence.
     */
    func encodeASN1LengthParameter(_ length: Int, buffer: UnsafeMutablePointer<UInt8>) -> Int {
        if length < Int(kCryptoExportImportManagerExtendedLengthMark) {
            buffer[0] = UInt8(length)
            return 1 // just one byte was used, no need for length starting mark (0x80).
        } else {
            let extraBytes = bytesNeededForRepresentingInteger(length)
            var currentLengthValue = length
            
            buffer[0] = kCryptoExportImportManagerExtendedLengthMark + UInt8(extraBytes)
            for i in 0 ..< extraBytes {
                buffer[extraBytes - i] = UInt8(currentLengthValue & 0xff)
                currentLengthValue = currentLengthValue >> 8
            }
            return extraBytes + 1 // 1 byte for the starting mark (0x80 + bytes used) + bytes used to encode length.
        }
    }

    
    /**
     * This function prepares a EC public key generated with Apple SecKeyGeneratePair to be exported
     * and used outisde iOS, be it openSSL, PHP, Perl, whatever. It basically adds the proper ASN.1
     * header and codifies the result as valid base64 string, 64 characters split.
     * Returns a DER representation of the key.
     */
    func exportECPublicKeyToDER(_ rawPublicKeyBytes: Data, keyType: String, keySize: Int) -> Data {
        print("Exporting EC raw key: \(rawPublicKeyBytes)")
        // first retrieve the header with the OID for the proper key  curve.
        let curveOIDHeader: [UInt8]
        let curveOIDHeaderLen: Int
        switch (keySize) {
        case kCryptoExportImportManagerSecp256r1CurveLen:
            curveOIDHeader = kCryptoExportImportManagerSecp256r1header
            curveOIDHeaderLen = kCryptoExportImportManagerSecp256r1headerLen
        case kCryptoExportImportManagerSecp384r1CurveLen:
            curveOIDHeader = kCryptoExportImportManagerSecp384r1header
            curveOIDHeaderLen = kCryptoExportImportManagerSecp384r1headerLen
        case kCryptoExportImportManagerSecp521r1CurveLen:
            curveOIDHeader = kCryptoExportImportManagerSecp521r1header
            curveOIDHeaderLen = kCryptoExportImportManagerSecp521r1headerLen
        default:
            curveOIDHeader = []
            curveOIDHeaderLen = 0
        }
        var data = Data(bytes: curveOIDHeader, count: curveOIDHeaderLen)
        
        // now add the raw data from the retrieved public key
        data.append(rawPublicKeyBytes)
        return data
    }
    
    /**
     * This function prepares a EC public key generated with Apple SecKeyGeneratePair to be exported
     * and used outisde iOS, be it openSSL, PHP, Perl, whatever. It basically adds the proper ASN.1
     * header and codifies the result as valid base64 string, 64 characters split.
     * Returns a DER representation of the key.
     */
    func exportECPublicKeyToPEM(_ rawPublicKeyBytes: Data, keyType: String, keySize: Int) -> String {
        return PEMKeyFromDERKey(exportECPublicKeyToDER(rawPublicKeyBytes, keyType: keyType, keySize: keySize))
    }
    
    /**
     * This method transforms a DER encoded key to PEM format. It gets a Base64 representation of
     * the key and then splits this base64 string in 64 character chunks. Then it wraps it in
     * BEGIN and END key tags.
     */
    func PEMKeyFromDERKey(_ data: Data) -> String {
        // base64 encode the result
        let base64EncodedString = data.base64EncodedString(options: [])

        // split in lines of 64 characters.
        var currentLine = ""
        var resultString = kCryptoExportImportManagerPublicKeyInitialTag
        var charCount = 0
        for character in base64EncodedString {
            charCount += 1
            currentLine.append(character)
            if charCount == kCryptoExportImportManagerPublicNumberOfCharactersInALine {
                resultString += currentLine + "\n"
                charCount = 0
                currentLine = ""
            }
        }
        // final line (if any)
        if currentLine.count > 0 { resultString += currentLine + "\n" }
        // final tag
        resultString += kCryptoExportImportManagerPublicKeyFinalTag
        return resultString
    }
    
}
