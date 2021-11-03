package com.example.geofen

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.geofen.databinding.ActivityMainBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var geofencingClient: GeofencingClient
    val GEOFENCE_RADIUS_IN_METERS = 80
    private val TAG = "..Read"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.enable.setOnClickListener(View.OnClickListener {
            geofencingClient = LocationServices.getGeofencingClient(this)
            addGeoFence()
        })

        binding.disable.setOnClickListener(View.OnClickListener {
            stop()
        })

        registerReceiver(broadcastReceiver, IntentFilter("GeofenceBroadcastReceiver"));
    }


    private fun stop() {
        super.onStop()
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Toast.makeText(applicationContext,"geofencing Removed",Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(applicationContext, "geofencing Removing failure $exception",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addGeoFence() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
             Log.i(TAG, "addGeoFence: ")
             geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.i(TAG, "addGeoFence: Success")
                    Toast.makeText(applicationContext,"onSuccess()",Toast.LENGTH_SHORT).show()
                }
                addOnFailureListener {
                    Log.i(TAG, "addGeoFence:  ...$exception")
                    Toast.makeText(applicationContext, "onFailure() : $exception",Toast.LENGTH_SHORT).show()
                    binding.alert.setTextColor(Color.parseColor( "#ED2E2E"))
                    binding.alert.setText(" on Faliure : $exception")
                }
            }
        }
        else{
            Log.i(TAG, "addGeoFence: ask permission")
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0)
        }

    }


    private val geofencePendingIntent: PendingIntent by lazy {
        Log.i(TAG, "pending Intent: ")
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        Log.i(TAG,"get broadcast.. ")
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun  addGeoFenceList(): ArrayList<Geofence> {
        Log.i(TAG, "addGeoFenceList: ..")
        val geofenceList :ArrayList<Geofence> = ArrayList()
        geofenceList.add(Geofence.Builder()
            .setRequestId("Geofencing")
            .setCircularRegion(
                28.474308,
                77.041803,
                GEOFENCE_RADIUS_IN_METERS.toFloat()
            )
            .setLoiteringDelay(1000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build())
        Log.i(TAG, "addGeoFenceList: returning List")
        return geofenceList
    }


    private fun getGeofencingRequest(): GeofencingRequest {
        Log.i(TAG, "getGeofencingRequest: ...")
        return GeofencingRequest.Builder().apply {
        setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
        addGeofences(addGeoFenceList())
        }.build()
    }

    private var broadcastReceiver: GeofenceBroadcastReceiver = object : GeofenceBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val b = intent.extras
            val message = b!!.getString("sendData")
            val lat = b.getDouble("lat")
            val lon = b.getDouble("lon")
            binding.alert.setText("$message" + "\n lat : " +lat +"\n lon : "+ lon)
            Log.i("dataReceived", " $message")
        }
    }
}