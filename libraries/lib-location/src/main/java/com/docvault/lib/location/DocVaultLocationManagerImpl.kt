package com.docvault.lib.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.docvault.lib.location.model.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DocVaultLocationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DocVaultLocationManager {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationResult =
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token,
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        LocationResult(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        ),
                    )
                } else {
                    continuation.resumeWithException(
                        Exception("No se pudo obtener la ubicación"),
                    )
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }

    override suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double,
    ): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull()?.toFullAddress())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(latitude, longitude, 1)
                ?.firstOrNull()
                ?.toFullAddress()
        }
    }

    private fun android.location.Address.toFullAddress(): String {
        return listOfNotNull(
            thoroughfare,
            subThoroughfare,
            subLocality,
            locality,
            adminArea,
            countryName,
        ).joinToString(", ")
    }
}
