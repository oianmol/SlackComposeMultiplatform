//
//  UiRepo.swift
//  iosApp
//
//  Created by Anmol Verma on 27/12/21.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation

class UIRepo: Identifiable {
    let author, name: String?
    let avatar: String?
    let url: String?


    init(author: String?, name: String?, avatar: String?, url: String?) {
        self.author = author
        self.name = name
        self.avatar = avatar
        self.url = url
    }
}
