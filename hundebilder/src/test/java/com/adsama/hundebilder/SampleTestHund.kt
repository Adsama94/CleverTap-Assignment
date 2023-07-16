package com.adsama.hundebilder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adsama.hundebilder.model.HundResponse
import com.adsama.hundebilder.network.HundService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class SampleTestHund {

    private lateinit var hundMediator: HundMediator

    @Mock
    private lateinit var hundCallbacks: HundCallbacks

    @Mock
    private val hundService: HundService = mock { HundService::class.java }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        hundMediator = HundMediator(hundCallbacks)
        hundMediator.mCoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    }

    @Test
    fun testGetImage_SuccessfulResponse() = runBlocking {
        val imageUrl = "https://example.com/image.jpg"
        val hundSingleResponse = HundResponse("success", imageUrl)
        val response = Response.success(hundSingleResponse)

        `when`(hundService.getImageAsync()).thenReturn(CompletableDeferred(response))
        `when`(hundCallbacks.getImage(imageUrl)).thenReturn(Unit)

        hundMediator.getImage()

        delay(1000) // Wait for the coroutine to complete

        verify(hundCallbacks).getImage(imageUrl)
        assertEquals(1, hundMediator.mHundImages.size)
        assertEquals(imageUrl, hundMediator.mHundImages[0])
        assertEquals(0, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetImage_UnsuccessfulResponse() = runBlocking {
        val response = Response.error<HundResponse>(404, null)

        `when`(hundService.getImageAsync()).thenReturn(CompletableDeferred(response))
        `when`(hundCallbacks.getError("Error retrieving data!")).thenReturn(Unit)

        hundMediator.getImage()

        delay(1000) // Wait for the coroutine to complete

        verify(hundCallbacks).getError("Error retrieving data!")
        assertEquals(0, hundMediator.mHundImages.size)
        assertEquals(-1, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetImage_Exception() = runBlocking {
        val errorMessage = "Exception message"
        val exception = Exception(errorMessage)

        `when`(hundService.getImageAsync()).thenAnswer { throw exception }
        `when`(hundCallbacks.getError("Exception fetching data! $errorMessage")).thenReturn(Unit)

        hundMediator.getImage()

        delay(1000) // Wait for the coroutine to complete

        verify(hundCallbacks).getError("Exception fetching data! $errorMessage")
        assertEquals(0, hundMediator.mHundImages.size)
        assertEquals(-1, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetMultipleImages_SuccessfulResponse() = runBlocking {
        val count = 3
        val imageUrls = arrayListOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
        )
        val hundMultipleResponse = HundResponse(imageUrls, "success")
        val response = Response.success(hundMultipleResponse)

        `when`(hundService.getImagesAsync(count)).thenReturn(CompletableDeferred(response))
        `when`(hundCallbacks.getImages(imageUrls)).thenReturn(Unit)

        hundMediator.getMultipleImages(count)

        delay(1000) // Wait for the coroutine to complete

        verify(hundCallbacks).getImages(imageUrls)
        assertEquals(3, hundMediator.mHundImages.size)
        assertEquals(imageUrls, hundMediator.mHundImages)
        assertEquals(2, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetMultipleImages_UnsuccessfulResponse() = runBlocking {
        val count = 3
        val response = Response.error<HundResponse>(404, null)

        `when`(hundService.getImagesAsync(count)).thenReturn(CompletableDeferred(response))
        `when`(hundCallbacks.getError("Error retrieving data!")).thenReturn(Unit)

        hundMediator.getMultipleImages(count)

        delay(1000) // Wait for the coroutine to complete

        verify(hundCallbacks).getError("Error retrieving data!")
        assertEquals(0, hundMediator.mHundImages.size)
        assertEquals(-1, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetMultipleImages_Exception() = runBlocking {
        val count = 3
        val errorMessage = "Exception message"
        val exception = Exception(errorMessage)

        `when`(hundService.getImagesAsync(count)).thenAnswer { throw exception }
        `when`(hundCallbacks.getError("Exception fetching data! $errorMessage")).thenReturn(Unit)

        hundMediator.getMultipleImages(count)

        delay(1000) // Wait for the coroutine to complete

        verify(hundCallbacks).getError("Exception fetching data! $errorMessage")
        assertEquals(0, hundMediator.mHundImages.size)
        assertEquals(-1, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetPreviousImage_ValidIndex() {
        val imageUrl1 = "https://example.com/image1.jpg"
        val imageUrl2 = "https://example.com/image2.jpg"
        val imageUrl3 = "https://example.com/image3.jpg"

        hundMediator.mHundImages.addAll(listOf(imageUrl1, imageUrl2, imageUrl3))
        hundMediator.currentIndex = 2

        `when`(hundCallbacks.getImage(imageUrl2)).thenReturn(Unit)

        hundMediator.getPreviousImage()

        verify(hundCallbacks).getImage(imageUrl2)
        assertEquals(2, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

    @Test
    fun testGetPreviousImage_InvalidIndex() {
        hundMediator.currentIndex = 0

        `when`(hundCallbacks.getError("No Previous Images!")).thenReturn(Unit)

        hundMediator.getPreviousImage()

        verify(hundCallbacks).getError("No Previous Images!")
        assertEquals(0, hundMediator.currentIndex)
        verifyNoMoreInteractions(hundCallbacks)
    }

}