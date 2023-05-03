package dk.itu.moapd.scootersharing.jonli.viewmodels

import com.google.android.gms.maps.model.LatLng

abstract class LocationViewModel : BaseViewModel() {

    private var location: LatLng = LatLng(0.0, 0.0)

    fun getLocation(): LatLng {
        return location
    }

    fun updateLocation(location: LatLng) {
        this.location = location
    }
}
