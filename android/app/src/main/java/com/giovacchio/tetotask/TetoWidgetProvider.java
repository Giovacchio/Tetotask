package com.giovacchio.tetotask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Widget TetoTask: prossimi task + pulsanti microfono e nuovo task. */
public class TetoWidgetProvider extends AppWidgetProvider {

    public static final String PREFS = "teto_widget";
    public static final String KEY = "data";

    private static final int[] ROWS = { R.id.w_row1, R.id.w_row2, R.id.w_row3, R.id.w_row4 };
    private static final int[] TEXTS = { R.id.w_text1, R.id.w_text2, R.id.w_text3, R.id.w_text4 };
    private static final int[] WHENS = { R.id.w_when1, R.id.w_when2, R.id.w_when3, R.id.w_when4 };

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            manager.updateAppWidget(id, build(context));
        }
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, TetoWidgetProvider.class));
        if (ids == null) return;
        for (int id : ids) {
            manager.updateAppWidget(id, build(context));
        }
    }

    static RemoteViews build(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_teto);
        String raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null);

        String title = "TetoTask \uD83C\uDF5E";
        String subtitle = "Apri l'app per sincronizzare";
        JSONArray items = new JSONArray();
        if (raw != null) {
            try {
                JSONObject o = new JSONObject(raw);
                title = o.optString("title", title);
                subtitle = o.optString("subtitle", "");
                JSONArray arr = o.optJSONArray("items");
                if (arr != null) items = arr;
            } catch (JSONException e) {
                // dati non validi: mostra il widget vuoto
            }
        }

        views.setTextViewText(R.id.w_title, title);
        views.setTextViewText(R.id.w_subtitle, subtitle);

        for (int i = 0; i < ROWS.length; i++) {
            JSONObject it = i < items.length() ? items.optJSONObject(i) : null;
            if (it != null) {
                views.setViewVisibility(ROWS[i], View.VISIBLE);
                views.setTextViewText(TEXTS[i], it.optString("text", ""));
                long at = it.optLong("at", 0);
                boolean late = at > 0 ? at < System.currentTimeMillis() : it.optBoolean("late", false);
                views.setTextViewText(WHENS[i], at > 0 ? when(at) : it.optString("when", ""));
                views.setTextColor(WHENS[i], late ? 0xFFE0244F : 0xFF9C7A86);
            } else {
                views.setViewVisibility(ROWS[i], View.GONE);
            }
        }
        views.setViewVisibility(R.id.w_empty, items.length() == 0 ? View.VISIBLE : View.GONE);

        views.setOnClickPendingIntent(R.id.w_root, open(context, "open", 101));
        views.setOnClickPendingIntent(R.id.w_mic, open(context, "voice", 102));
        views.setOnClickPendingIntent(R.id.w_add, open(context, "new", 103));
        return views;
    }

    /** Orario "amichevole" calcolato al momento, così non invecchia mai. */
    static String when(long at) {
        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(at);
        String hm = String.format(Locale.ITALY, "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        if (at < now.getTimeInMillis()) {
            long min = (now.getTimeInMillis() - at) / 60000;
            if (min < 60) return "scaduto " + Math.max(1, min) + "m fa";
            if (min < 1440) return "scaduto " + (min / 60) + "h fa";
            return "scaduto " + (min / 1440) + "g fa";
        }
        Calendar d0 = (Calendar) now.clone();
        d0.set(Calendar.HOUR_OF_DAY, 0); d0.set(Calendar.MINUTE, 0); d0.set(Calendar.SECOND, 0); d0.set(Calendar.MILLISECOND, 0);
        long days = (at - d0.getTimeInMillis()) / 86400000L;
        if (days == 0) return hm;
        if (days == 1) return "domani " + hm;
        if (days < 7) {
            String[] g = { "dom", "lun", "mar", "mer", "gio", "ven", "sab" };
            return g[c.get(Calendar.DAY_OF_WEEK) - 1] + " " + hm;
        }
        String[] m = { "gen", "feb", "mar", "apr", "mag", "giu", "lug", "ago", "set", "ott", "nov", "dic" };
        return c.get(Calendar.DAY_OF_MONTH) + " " + m[c.get(Calendar.MONTH)];
    }

    private static PendingIntent open(Context context, String action, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("com.giovacchio.tetotask://widget/" + action), context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
