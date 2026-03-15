package com.docvault.lib.location

import com.docvault.lib.location.model.LocationResult

/**
 * Manages device location retrieval and address resolution.
 * Provides current coordinates and resolves them to a human-readable street address.
 */
interface DocVaultLocationManager {
    suspend fun getCurrentLocation(): LocationResult

    suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double,
    ): String?
}
