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
        createPublisher(for: authComponent.provideUseCaseFetchAllChannels()
           .invokeNative(workspaceId: (workspace?.uuid ?? "") as String))
        .receive(on: DispatchQueue.main)
             .subscribe(on: DispatchQueue.global(qos: .default))
             .sink { [self] completion in
             print("Received completion: \(completion)")
         } receiveValue: { [self] value in
             print("Received value: \(value)")
             value.forEach { channel in
                 sendMessage(workspaceId: workspace?.uuid ?? "", channelId: channel.channelId, senderId: authComponent.localUser().uuid, channelPublicKey: channel.publicKey)
             }
             
             createPublisher(for: authComponent.providerUseCaseFetchChannelsWithLastMessage().invokeNative(workspaceId: workspace?.uuid ?? ""))
                 .receive(on: DispatchQueue.main)
                 .subscribe(on: DispatchQueue.global(qos: .default))
                 .sink { [self] completion in
                 print("Received completion: \(completion)")
                 self.isLoading = false
             } receiveValue: { [self] value in
                 print("decrypted value: \(value)")
                 self.isLoading = false
                         
             }
    
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
             .invokeNative(email: "xyz", password: "password", domain: "mm"))
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
