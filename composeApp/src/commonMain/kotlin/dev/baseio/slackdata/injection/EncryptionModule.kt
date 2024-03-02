package dev.baseio.slackdata.injection

import dev.baseio.slackdata.datasources.IDataDecryptorImpl
import dev.baseio.slackdata.datasources.IDataEncrypterImpl
import dev.baseio.slackdomain.datasources.IDataDecryptor
import dev.baseio.slackdomain.datasources.IDataEncrypter
import org.koin.dsl.module

val encryptionModule = module {
    factory<IDataEncrypter> {
        IDataEncrypterImpl()
    }
    factory<IDataDecryptor> {
        IDataDecryptorImpl()
    }
}
