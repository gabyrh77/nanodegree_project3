package barqsoft.footballscores.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MatchesSyncService extends Service {
    private final String LOG_TAG = MatchesSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static MatchesSyncAdapter matchesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate called.");
        synchronized (sSyncAdapterLock) {
            if (matchesSyncAdapter == null) {
                matchesSyncAdapter = new MatchesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return matchesSyncAdapter.getSyncAdapterBinder();
    }
}