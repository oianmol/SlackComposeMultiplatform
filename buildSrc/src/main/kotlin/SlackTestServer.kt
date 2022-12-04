import dev.baseio.slackdata.protos.*
import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder


object SlackTestServer {

    private var server: Server? = null

    fun start() {
        if (server != null) return

        server = NettyServerBuilder
            .forPort(8083)
            .addService(object : AuthServiceGrpcKt.AuthServiceCoroutineImplBase() {})
            .addService(object : WorkspaceServiceGrpcKt.WorkspaceServiceCoroutineImplBase() {})
            .addService(object : ChannelsServiceGrpcKt.ChannelsServiceCoroutineImplBase() {})
            .addService(object : MessagesServiceGrpcKt.MessagesServiceCoroutineImplBase() {})
            .addService(object : QrCodeServiceGrpcKt.QrCodeServiceCoroutineImplBase() {})
            .addService(object : SecurePushServiceGrpcKt.SecurePushServiceCoroutineImplBase() {})
            .addService(object : UsersServiceGrpcKt.UsersServiceCoroutineImplBase() {})
            .build()
            .start()
    }

    fun stop() {
        server?.shutdownNow()
        server = null
    }
}