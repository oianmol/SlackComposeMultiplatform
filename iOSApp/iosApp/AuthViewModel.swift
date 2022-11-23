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
       createPublisher(for: authComponent.provideUseCaseCreateWorkspace()
            .invokeNative(email: "anmol.verma4@gmail.com", password: "password", domain: "mutualmobileios"))
       .receive(on: DispatchQueue.main)
            .subscribe(on: DispatchQueue.global(qos: .default))
            .sink { [self] completion in
            print("Received completion: \(completion)")
            self.isLoading = false
        } receiveValue: { [self] value in
            print("Received value: \(value)")
            self.isLoading = false
            
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
        
     
       
    }
    
}
