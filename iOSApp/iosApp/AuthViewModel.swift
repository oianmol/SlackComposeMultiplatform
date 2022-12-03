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
                 createPublisher(for: authComponent.provideUseCaseGetMessages().invokeNative(useCaseWorkspaceChannelRequest: Slack_domain_layerUseCaseWorkspaceChannelRequest(workspaceId: workspace?.uuid ?? "", channelId: channel.channelId, limit: 0, offset: 0))).receive(on: DispatchQueue.main)
                    .subscribe(on: DispatchQueue.global(qos: .default))
                    .sink { [self] completion in
                    print("Received completion: \(completion)")
                    self.isLoading = false
                } receiveValue: { [self] value in
                    print("decrypted value: \(value)")
                    self.isLoading = false
                            
                }
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
        let channel = Slack_domain_layerDomainLayerChannelsSKChannel.SkGroupChannel(uuid: UUID.init().uuidString, workId: workspace?.uuid ?? "", name: "asdfasdfsdfsdf\(UUID.init().uuidString)", createdDate: 0, modifiedDate: 0, avatarUrl: "", deleted: false, channelPublicKey: Slack_domain_layerDomainLayerUsersSKSlackKey(keyBytes: KotlinByteArray(size: 0)))
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
    
    func sendMessage(workspaceId:String,channelId:String,senderId:String,channelPublicKey:Slack_domain_layerDomainLayerUsersSKSlackKey){
        
        createPublisher(for: authComponent.provideUseCaseFetchAndSaveChannelMembers().invokeNative(useCaseWorkspaceChannelRequest: Slack_domain_layerUseCaseWorkspaceChannelRequest(workspaceId: workspaceId, channelId: channelId, limit: 0, offset: 0)))
            .receive(on: DispatchQueue.main)
            .subscribe(on: DispatchQueue.global(qos: .default))
            .sink { [self] completion in
            print("Received completion: \(completion)")
            self.isLoading = false
        } receiveValue: { [self] value in
            print("Received value: \(value)")
            self.isLoading = false
            createPublisher(for: authComponent.provideUseCaseSendMessage().invokeNative(params: Slack_domain_layerDomainLayerMessagesSKMessage(uuid:  UUID.init().uuidString, workspaceId: workspaceId, channelId: channelId, messageFirst: "",messageSecond: "", sender: senderId, createdDate: 0, modifiedDate: 0, isDeleted: false, isSynced: false, decodedMessage: "Anmol"), publicKey: channelPublicKey))
                .receive(on: DispatchQueue.main)
                .subscribe(on: DispatchQueue.global(qos: .default))
                .sink { [self] completion in
                print("Received completion: \(completion)")
                self.isLoading = false
            } receiveValue: { [self] value in
                print("message value: \(value)")
                self.isLoading = false
                        
                
                createPublisher(for: authComponent.provideIMessageDecrypter().decryptedNative(message: value)) .receive(on: DispatchQueue.main)
                    .subscribe(on: DispatchQueue.global(qos: .default))
                    .sink { [self] completion in
                    print("Received completion: \(completion)")
                    self.isLoading = false
                } receiveValue: { [self] value in
                    print("decrypted message value: \(value)")
                    self.isLoading = false
                            
                }
            }
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
