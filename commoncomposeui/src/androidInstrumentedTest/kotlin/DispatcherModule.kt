import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module

val testDispatcherModule = module {
    single<CoroutineDispatcherProvider> { RealCoroutineDispatcherProvider() }
}