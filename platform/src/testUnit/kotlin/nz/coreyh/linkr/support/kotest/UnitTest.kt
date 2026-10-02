package nz.coreyh.linkr.support.kotest

import io.mockk.junit5.MockKExtension

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MockKExtension.CheckUnnecessaryStub
annotation class UnitTest
