//
//  EncryptedData.swift
//  capillaryslack
//
//  Created by Anmol Verma on 02/12/22.
//

import Foundation

@objc public class EncryptedData: NSObject {
    
    @objc public private(set) var first: String?
    @objc public private(set) var second: String?
    
    private init(_ first: String?, _ second: String?) {
        super.init()
        self.first = first
        self.second = second
    }
    
    @objc public func firstItem() -> String? {
        return first
    }
    
    @objc public func secondItem() -> String? {
        return second
    }
    
    public convenience init(first: String?,second:String?) {
        self.init(first, second)
    }
}
