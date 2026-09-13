package com.example

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.ui.AgriAppViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RepositoryLifecycleTest {

    @Test
    fun `AgriAppViewModel retains repository instance and state across ViewModelStore lifecycle`() = runBlocking {
        // Simulate Activity ViewModelStore retaining ViewModel across recreation
        val testStore = ViewModelStore()
        val owner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = testStore
        }

        val factory = ViewModelProvider.NewInstanceFactory()
        val providerFirst = ViewModelProvider(owner, factory)
        val vmFirst = providerFirst.get(AgriAppViewModel::class.java)

        val initialRepo = vmFirst.repository
        assertNotNull(initialRepo)

        // Add a lot in the retained repository
        val createdLot = initialRepo.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.quality_good,
            location = "Katol, Maharashtra",
            readyTiming = "Ready now",
            expectedPricePerQ = 4900,
            estimatedNetPerQ = 4750,
            customLotId = "TEST-LOT-P1-RETAINED"
        )
        assertNotNull(createdLot)

        // Verify lot is present in repository flow
        val lotsBeforeRecreation = initialRepo.getMyLots().first()
        val foundLot = lotsBeforeRecreation.find { it.lotId == createdLot.lotId }
        assertNotNull(foundLot)
        assertEquals(50, foundLot?.quantityQuintals)

        // Simulate Activity recreation by retrieving the ViewModel from the same retained ViewModelStore
        val providerSecond = ViewModelProvider(owner, factory)
        val vmSecond = providerSecond.get(AgriAppViewModel::class.java)

        // Verify it is the exact same ViewModel and exact same repository instance
        assertSame(vmFirst, vmSecond)
        assertSame(initialRepo, vmSecond.repository)

        // Verify state is completely preserved across recreation
        val lotsAfterRecreation = vmSecond.repository.getMyLots().first()
        val retainedLot = lotsAfterRecreation.find { it.lotId == createdLot.lotId }
        assertNotNull(retainedLot)
        assertEquals(50, retainedLot?.quantityQuintals)
        assertEquals("Katol, Maharashtra", retainedLot?.location)

        // Clean up
        testStore.clear()
    }
}
