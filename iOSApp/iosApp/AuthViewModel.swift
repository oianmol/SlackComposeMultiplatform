//
//  AuthViewModel.swift
//  iosApp
//
//  Created by Anmol Verma on 23/11/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import common
import Combine
import KMPNativeCoroutinesCombine

let authComponent = AuthKoinComponents()

class AuthViewModel : ObservableObject{

    @Published
    var isLoading:Bool  = false
    
 
    
    func login(){
        self.isLoading = true
        createWorkspace()
    }
    
    func fetchSaveWorkspaces(){
        createPublisher(for: authComponent.provideUseCaseFetchAndSaveWorkspaces().invokeNative())
            .receive(on: DispatchQueue.main)
                 .subscribe(on: DispatchQueue.global(qos: .default))
                 .sink { [self] completion in
                 print("Received completion: \(completion)")
             } receiveValue: { [self] value in
                 print("Received value: \(value)")
                 selectedWorkspace()
             }
        
    }
    
    func selectedWorkspace(){
        createPublisher(for: authComponent.providerUseCaseGetSelectedWorkspace()
             .invokeNative())
        .receive(on: DispatchQueue.main)
             .subscribe(on: DispatchQueue.global(qos: .default))
             .sink { [self] completion in
             print("Received completion: \(completion)")
         } receiveValue: { [self] value in
             print("Received value: \(value)")
             createChannel(workspace:value)
         
         }
    }
    
    func fetchLastMessage(workspace: Slack_domain_layerDomainLayerWorkspacesSKWorkspace?){
        createPublisher(for: authComponent.providerUseCaseFetchChannelsWithLastMessage()
           .invokeNative(workspaceId: (workspace?.uuid ?? "") as String))
        .receive(on: DispatchQueue.main)
             .subscribe(on: DispatchQueue.global(qos: .default))
             .sink { [self] completion in
             print("Received completion: \(completion)")
         } receiveValue: { [self] value in
             print("Received value: \(value)")
             value.forEach { lastMessage in
                 
             }
             sendMessage(workspaceId: workspace?.uuid ?? "", channelId: "68116941-3909-4EC1-8109-F9DFB9AE146C", senderId: "d8826aa1-4637-4ba7-a18e-e5784a2f2130", channelPublicKey: Slack_domain_layerDomainLayerUsersSKUserPublicKey(keyBytes: KotlinByteArray.testKey()))
         }
    }
    
    func fetchUser(){
        createPublisher(for: authComponent.provideUseCaseCurrentUser()
             .invokeNative())
        .receive(on: DispatchQueue.main)
             .subscribe(on: DispatchQueue.global(qos: .default))
             .sink { [self] completion in
             print("Received completion: \(completion)")
         } receiveValue: { [self] value in
             print("Received value: \(value)")
         }
    }
    
    func createWorkspace(){
        createPublisher(for: authComponent.provideUseCaseCreateWorkspace()
             .invokeNative(email: "sdfgsdfg@gmail.com", password: "password", domain: "mm"))
        .receive(on: DispatchQueue.main)
             .subscribe(on: DispatchQueue.global(qos: .default))
             .sink { [self] completion in
             print("Received completion: \(completion)")
             self.isLoading = false
         } receiveValue: { [self] value in
             print("Received value: \(value)")
             self.isLoading = false
            fetchUser()
            fetchSaveWorkspaces()
         }
    }
    
    func createChannel(workspace: Slack_domain_layerDomainLayerWorkspacesSKWorkspace?){
        let channel = Slack_domain_layerDomainLayerChannelsSKChannel.SkGroupChannel(uuid: UUID.init().uuidString, workId: workspace?.uuid ?? "", name: "asdfasdfsdfsdf\(UUID.init().uuidString)", createdDate: 0, modifiedDate: 0, avatarUrl: "", deleted: false, channelPublicKey: Slack_domain_layerDomainLayerUsersSKUserPublicKey(keyBytes: KotlinByteArray(size: 0)))
        createPublisher(for: authComponent.provideUseCaseCreateChannel().invokeNative(params: channel))
            .receive(on: DispatchQueue.main)
            .subscribe(on: DispatchQueue.global(qos: .default))
            .sink { [self] completion in
            print("Received completion: \(completion)")
            self.isLoading = false
        } receiveValue: { [self] value in
            print("Received value: \(value)")
            self.isLoading = false
            
            providerUseCaseFetchAndSaveChannels(workspace)
        }
    }
    
    func providerUseCaseFetchAndSaveChannels(_ workspace: Slack_domain_layerDomainLayerWorkspacesSKWorkspace?){
        createPublisher(for: authComponent.providerUseCaseFetchAndSaveChannels().invokeNative(workspaceId: workspace?.uuid ?? "", offset: 0, limit: 0))
            .receive(on: DispatchQueue.main)
            .subscribe(on: DispatchQueue.global(qos: .default))
            .sink { [self] completion in
            print("Received completion: \(completion)")
            self.isLoading = false
        } receiveValue: { [self] value in
            print("Received value: \(value)")
            self.isLoading = false
            fetchLastMessage(workspace:workspace)
        }
    }
    
    func sendMessage(workspaceId:String,channelId:String,senderId:String,channelPublicKey:Slack_domain_layerDomainLayerUsersSKUserPublicKey){

        createPublisher(for: authComponent.provideUseCaseSendMessage().invokeNative(params: Slack_domain_layerDomainLayerMessagesSKMessage(uuid:  UUID.init().uuidString, workspaceId: workspaceId, channelId: channelId, message: KotlinByteArray.from(data: "Anmol".data(using: .utf8) ?? Data()), sender: senderId, createdDate: 0, modifiedDate: 0, isDeleted: false, isSynced: false, decodedMessage: ""), publicKey: channelPublicKey))
            .receive(on: DispatchQueue.main)
            .subscribe(on: DispatchQueue.global(qos: .default))
            .sink { [self] completion in
            print("Received completion: \(completion)")
            self.isLoading = false
        } receiveValue: { [self] value in
            print("message value: \(value)")
            self.isLoading = false
    
        }
    }
    
}


extension KotlinByteArray {
    
    static func testKey() -> KotlinByteArray {
        let arr :[UInt32] = [48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -78, 44, -127, -57, -69, 78, 104, 48, 52, 35, -75, -63, 108, 48, 47, 49, -100, -36, 21, 111, 33, 52, -11, -49, 119, 85, 55, 120, -47, 24, 48, 8, 118, 94, -104, -46, 33, 9, 1, -18, -104, 110, 92, 88, -28, -59, 109, -94, -90, 99, 10, 62, -92, -63, 68, 32, -36, 123, -95, 119, -67, 51, 32, -88, 77, -15, 107, 86, 58, -111, -107, -7, -43, -64, 113, 70, 58, 38, -74, -20, 126, 98, -77, 31, 112, 46, 6, 23, -116, 16, 28, 113, 46, -119, -92, 74, 123, -28, -63, 94, -2, -2, 86, -96, 3, -9, 13, -1, 41, 114, -88, -125, 75, 120, -85, -91, -30, -41, 101, -37, -75, -39, 52, -12, -43, 124, -12, 55, 79, 125, 119, 108, 35, 85, 19, -104, -70, -108, 28, -51, 108, -42, -126, -1, -51, -51, 112, -33, 31, 45, -6, 3, 69, -69, 35, -58, 59, 10, -92, -78, -113, -79, 24, -19, -65, -81, -76, -28, 37, 124, 13, -128, 84, 27, 27, 62, 47, 47, 59, -59, -115, 83, -16, -70, -97, -10, 38, 48, -104, -83, 71, -70, 76, 83, -3, -23, 12, -102, 100, -3, -125, -127, -39, 41, 117, -97, -43, 68, -66, 59, 48, 35, -44, 73, -127, 82, 59, -60, 11, 123, 74, -12, 71, 124, 75, 95, -84, -107, -124, 97, 29, -47, 44, 44, -36, -106, 94, -54, -56, -90, 108, -94, 92, -52, 19, -128, -64, -125, -5, -86, 60, 69, -124, -24, -49, 105, 2, 3, 1, 0, 1]
        
       return arr.map(Int32.init(bitPattern:))
            .enumerated()
            .reduce(into: KotlinByteArray(size: Int32(arr.count))) { result, row in
                result.set(index: Int32(row.offset), value: Int8(row.element))
            }
    }
    
    static func from(data: Data) -> KotlinByteArray {
        let swiftByteArray = [UInt8](data)
        return swiftByteArray
            .map(Int8.init(bitPattern:))
            .enumerated()
            .reduce(into: KotlinByteArray(size: Int32(swiftByteArray.count))) { result, row in
                result.set(index: Int32(row.offset), value: row.element)
            }
    }
}
