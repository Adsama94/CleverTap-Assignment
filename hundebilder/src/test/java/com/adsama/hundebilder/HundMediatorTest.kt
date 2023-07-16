package com.adsama.hundebilder

import com.adsama.hundebilder.model.HundResponse
import com.adsama.hundebilder.network.HundService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class HundMediatorTest {

    private lateinit var hundMediator: HundMediator
    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher
    private lateinit var testCoroutineScope: TestScope

    @Mock
    private lateinit var hundCallbacks: HundCallbacks

    @Mock
    private val hundService: HundService = mock { HundService::class.java }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testCoroutineDispatcher = TestCoroutineDispatcher()
        testCoroutineScope = TestScope(testCoroutineDispatcher)
        hundMediator = HundMediator(hundCallbacks)
        hundMediator.mCoroutineScope = testCoroutineScope
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetImage_SuccessfulResponse() = testCoroutineScope.runTest {
        val imageUrl = "https://example.com/image.jpg"
        val hundSingleResponse = HundResponse("success", imageUrl)
        val response: Deferred<Response<HundResponse>> =
            CompletableDeferred(Response.success(hundSingleResponse))
        `when`(hundService.getImageAsync()).thenReturn(response)

        hundMediator.getImage()

        testScheduler.apply { advanceTimeBy(1000); runCurrent() } // Wait for the coroutine to complete

        verify(hundCallbacks).getImage(imageUrl)
        assertEquals(1, hundMediator.mHundImages.size)
        assertEquals(imageUrl, hundMediator.mHundImages[0])
        assertEquals(0, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetImage_UnsuccessfulResponse(): Unit = testCoroutineScope.runTest {
        val response = CompletableDeferred(Response.error<HundResponse>(404, null))
        `when`(hundService.getImageAsync()).thenReturn(response)

        hundMediator.getImage()

        testScheduler.apply { advanceTimeBy(1000); runCurrent() } // Wait for the coroutine to complete

        verify(hundCallbacks).getError("Error retrieving data!")
        assertEquals(0, hundMediator.mHundImages.size)
        assertEquals(-1, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetImage_Exception(): Unit = testCoroutineScope.runTest {
        val errorMessage = "Exception message"
        val exception = Exception(errorMessage)

        `when`(hundService.getImageAsync()).thenAnswer { throw exception }
        `when`(hundCallbacks.getError("Exception fetching data! $errorMessage")).thenReturn(Unit)

        hundMediator.getImage()

        testScheduler.apply { advanceTimeBy(1000); runCurrent() } // Wait for the coroutine to complete

        verify(hundCallbacks).getError("Exception fetching data! $errorMessage")
        assertEquals(0, hundMediator.mHundImages.size)
        assertEquals(-1, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

}