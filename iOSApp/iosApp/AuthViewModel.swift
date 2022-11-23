//
//  AuthViewModel.swift
//  iosApp
//
//  Created by Anmol Verma on 23/11/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import common

let authComponent = AuthKoinComponents()

class AuthViewModel : ObservableObject{
    
    func login(){
        authComponent.provideUseCaseCreateWorkspace().invoke(email: "anmol.verma4@gmail.com", password: "password", domain: "mutualmobileios") { error in
            debugPrint(error ?? "no error")
        }
    }
    
}
