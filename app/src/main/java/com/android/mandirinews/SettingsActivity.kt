package com.android.mandirinews

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.android.mandirinews.databinding.SettingsActivityBinding
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.include.toolbar

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.app_preferences, rootKey)
            val permissionStoragePreference: Preference? = findPreference("permission_storage")
            permissionStoragePreference?.onPreferenceChangeListener = this
            if (permissionStoragePreference != null) {
                onPreferenceChange(permissionStoragePreference,
                    preferenceManager.sharedPreferences?.getBoolean("permission_storage", false)
                )
            }
        }

        private fun requestStoragePermission() {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }

        companion object {
            private const val STORAGE_PERMISSION_REQUEST_CODE = 123
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            when (preference.key) {
                "permission_storage" -> {
                    val isPermissionGranted = newValue as Boolean

                    if (isPermissionGranted) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestStoragePermission()
                        } else {
                            Toast.makeText(context, "Storage permission already granted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            return true
        }

    }
}