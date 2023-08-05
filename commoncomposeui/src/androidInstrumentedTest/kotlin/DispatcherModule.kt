import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import org.koin.dsl.module

val testDispatcherModule = module {
    single<CoroutineDispatcherProvider> { RealCoroutineDispatcherProvider() }
}