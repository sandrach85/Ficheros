package es.upm.miw.ficheros;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Comprobar que el fragmento esté relacionado con la actividad
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onIsMultiPane() {
        // Determinar que siempre sera multipanel
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return ((float) metrics.densityDpi / (float) metrics.widthPixels) < 0.30;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String settings = getArguments().getString("settings");
            if ("generales".equals(settings)) {
                addPreferencesFromResource(R.xml.settings);
            } else if ("settings_almac".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_almac);
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            // Registrar escucha
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Eliminar registro de la escucha
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Actualizar el resumen de la preferencia
            if (key.equals("numArticulos")) {
                Preference preference = findPreference(key);
                preference.setSummary(sharedPreferences.getString(key, ""));
            }
        }

    }

}
