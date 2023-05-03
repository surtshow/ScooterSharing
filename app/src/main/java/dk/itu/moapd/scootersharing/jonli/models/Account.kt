package dk.itu.moapd.scootersharing.jonli.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Account(
    val balance: Double = 0.0,
)
