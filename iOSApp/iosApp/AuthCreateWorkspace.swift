//
//  AuthCreateWorkspace.swift
//  iosApp
//
//  Created by Anmol Verma on 23/11/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct AuthCreateWorkspace : View{
    
    @ObservedObject var viewModel = AuthViewModel()
    
    init() {
        viewModel.login()
    }
    
    var body: some View{
        VStack{
            
        }
    }
}
