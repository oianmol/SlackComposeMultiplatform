//
//  GithubTrendingVM.swift
//  iosApp
//
//  Created by Anmol Verma on 27/12/21.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import shared
import Combine
import KMPNativeCoroutinesCombine

class GithubTrendingVM : ObservableObject{
    private var trendingDataModel:GithubTrendingDataModel?
    
    @Published var repos : [UIRepo] = []
    @Published
    var loading = false
    @Published
    var error: String?
    
    func activate(){
        trendingDataModel = GithubTrendingDataModel { [weak self] dataState in
            self?.loading = dataState is GithubTrendingDataModel.LoadingState
            if(dataState is GithubTrendingDataModel.SuccessState){
                let listDataState =  dataState as! GithubTrendingDataModel.SuccessState
                self?.repos = listDataState.trendingList.map({ item in
                    return UIRepo(author:item.author, name:item.name, avatar:item.avatar, url:item.url)
                })
            }
        }
        trendingDataModel?.activate()
    }
    
    func filter(searchText:String){
        trendingDataModel?.filterRecords(search:searchText)
    }

    func destroy(){
        trendingDataModel?.destroy()
    }
    
    func refresh(){
        trendingDataModel?.refresh()
    }
    
}
