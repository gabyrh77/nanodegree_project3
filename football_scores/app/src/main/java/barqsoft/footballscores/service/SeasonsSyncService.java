package barqsoft.footballscores.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SeasonsSyncService extends Service {
    private final String LOG_TAG = SeasonsSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static SeasonsSyncAdapter seasonsSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate called.");
        synchronized (sSyncAdapterLock) {
            if (seasonsSyncAdapter == null) {
                seasonsSyncAdapter = new SeasonsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return seasonsSyncAdapter.getSyncAdapterBinder();
    }
}