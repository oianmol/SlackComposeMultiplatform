@file:Suppress("UNUSED_PARAMETER")

import io.mockative.Invocation.Function
import io.mockative.Mockable
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

class FunctionMock<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>(private val returnsUnit: Boolean) :
    Mockable(stubsUnitByDefault = false),
    () -> R,
    (P1) -> R,
    (P1, P2) -> R,
    (P1, P2, P3) -> R,
    (P1, P2, P3, P4) -> R,
    (P1, P2, P3, P4, P5) -> R,
    (P1, P2, P3, P4, P5, P6) -> R,
    (P1, P2, P3, P4, P5, P6, P7) -> R,
    (P1, P2, P3, P4, P5, P6, P7, P8) -> R,
    (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R,
    (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {

    private fun invoked(vararg params: Any?): R =
        invoke(Function("invoke", params.toList()), returnsUnit)

    override fun invoke(): R = invoked()

    override fun invoke(p1: P1): R = invoked(p1)

    override fun invoke(p1: P1, p2: P2): R = invoked(p1, p2)

    override fun invoke(p1: P1, p2: P2, p3: P3): R = invoked(p1, p2, p3)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4): R = invoked(p1, p2, p3, p4)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): R = invoked(p1, p2, p3, p4, p5)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6): R =
        invoked(p1, p2, p3, p4, p5, p6)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7): R =
        invoked(p1, p2, p3, p4, p5, p6, p7)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8): R =
        invoked(p1, p2, p3, p4, p5, p6, p7, p8)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9): R =
        invoked(p1, p2, p3, p4, p5, p6, p7, p8, p9)

    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10): R =
        invoked(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
}

class SuspendFunctionMock<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>(private val returnsUnit: Boolean) :
    Mockable(stubsUnitByDefault = false),
    suspend () -> R,
    suspend (P1) -> R,
    suspend (P1, P2) -> R,
    suspend (P1, P2, P3) -> R,
    suspend (P1, P2, P3, P4) -> R,
    suspend (P1, P2, P3, P4, P5) -> R,
    suspend (P1, P2, P3, P4, P5, P6) -> R,
    suspend (P1, P2, P3, P4, P5, P6, P7) -> R,
    suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R,
    suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R,
    suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {

    private suspend fun invoked(vararg params: Any?): R =
        suspend(Function("invoke", params.toList()), returnsUnit)

    override suspend fun invoke(): R = invoked()

    override suspend fun invoke(p1: P1): R = invoked(p1)

    override suspend fun invoke(p1: P1, p2: P2): R = invoked(p1, p2)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3): R = invoked(p1, p2, p3)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4): R = invoked(p1, p2, p3, p4)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): R =
        invoked(p1, p2, p3, p4, p5)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6): R =
        invoked(p1, p2, p3, p4, p5, p6)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7): R =
        invoked(p1, p2, p3, p4, p5, p6, p7)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8): R =
        invoked(p1, p2, p3, p4, p5, p6, p7, p8)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9): R =
        invoked(p1, p2, p3, p4, p5, p6, p7, p8, p9)

    override suspend fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10): R =
        invoked(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
}

private typealias X = Nothing

inline fun <reified R> mock(
    f: KClass<() -> R>,
): () -> R = FunctionMock<X, X, X, X, X, X, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <reified R> mock(
    f: KClass<suspend () -> R>,
): suspend () -> R =
    SuspendFunctionMock<X, X, X, X, X, X, X, X, X, X, R>(Unit is R)

inline fun <P1, reified R> mock(
    f: KClass<(P1) -> R>,
): (P1) -> R = FunctionMock<P1, X, X, X, X, X, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, reified R> mock(
    f: KClass<suspend (P1) -> R>,
): suspend (P1) -> R =
    SuspendFunctionMock<P1, X, X, X, X, X, X, X, X, X, R>(Unit is R)

inline fun <P1, P2, reified R> mock(
    f: KClass<(P1, P2) -> R>,
): (P1, P2) -> R =
    FunctionMock<P1, P2, X, X, X, X, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, reified R> mock(
    f: KClass<suspend (P1, P2) -> R>,
): suspend (P1, P2) -> R =
    SuspendFunctionMock<P1, P2, X, X, X, X, X, X, X, X, R>(Unit is R)

inline fun <P1, P2, P3, reified R> mock(
    f: KClass<(P1, P2, P3) -> R>,
): (P1, P2, P3) -> R =
    FunctionMock<P1, P2, P3, X, X, X, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, reified R> mock(
    f: KClass<suspend (P1, P2, P3) -> R>,
): suspend (P1, P2, P3) -> R =
    SuspendFunctionMock<P1, P2, P3, X, X, X, X, X, X, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, reified R> mock(
    f: KClass<(P1, P2, P3, P4) -> R>,
): (P1, P2, P3, P4) -> R =
    FunctionMock<P1, P2, P3, P4, X, X, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4) -> R>,
): suspend (P1, P2, P3, P4) -> R =
    SuspendFunctionMock<P1, P2, P3, P4, X, X, X, X, X, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, P5, reified R> mock(
    f: KClass<(P1, P2, P3, P4, P5) -> R>,
): (P1, P2, P3, P4, P5) -> R =
    FunctionMock<P1, P2, P3, P4, P5, X, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, P5, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4, P5) -> R>,
): suspend (P1, P2, P3, P4, P5) -> R =
    SuspendFunctionMock<P1, P2, P3, P4, P5, X, X, X, X, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, P5, P6, reified R> mock(
    f: KClass<(P1, P2, P3, P4, P5, P6) -> R>,
): (P1, P2, P3, P4, P5, P6) -> R =
    FunctionMock<P1, P2, P3, P4, P5, P6, X, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, P5, P6, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4, P5, P6) -> R>,
): suspend (P1, P2, P3, P4, P5, P6) -> R =
    SuspendFunctionMock<P1, P2, P3, P4, P5, P6, X, X, X, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, P5, P6, P7, reified R> mock(
    f: KClass<(P1, P2, P3, P4, P5, P6, P7) -> R>,
): (P1, P2, P3, P4, P5, P6, P7) -> R =
    FunctionMock<P1, P2, P3, P4, P5, P6, P7, X, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, P5, P6, P7, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4, P5, P6, P7) -> R>,
): suspend (P1, P2, P3, P4, P5, P6, P7) -> R =
    SuspendFunctionMock<P1, P2, P3, P4, P5, P6, P7, X, X, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, P5, P6, P7, P8, reified R> mock(
    f: KClass<(P1, P2, P3, P4, P5, P6, P7, P8) -> R>,
): (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
    FunctionMock<P1, P2, P3, P4, P5, P6, P7, P8, X, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, P5, P6, P7, P8, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R>,
): suspend (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
    SuspendFunctionMock<P1, P2, P3, P4, P5, P6, P7, P8, X, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, reified R> mock(
    f: KClass<(P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R>,
): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
    FunctionMock<P1, P2, P3, P4, P5, P6, P7, P8, P9, X, R>(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R>,
): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
    SuspendFunctionMock<P1, P2, P3, P4, P5, P6, P7, P8, P9, X, R>(Unit is R)

inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, reified R> mock(
    f: KClass<(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R>,
): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R = FunctionMock(Unit is R)

@JvmName("suspendMock")
inline fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, reified R> mock(
    f: KClass<suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R>,
): suspend (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R = SuspendFunctionMock(Unit is R)