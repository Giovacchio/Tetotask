package com.giovacchio.tetotask;

import android.content.Context;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Riceve dall'app i prossimi task e aggiorna il widget.
 * JS: Capacitor.registerPlugin("TetoWidget").update({ title, count, items: [{ text, when, late }] })
 */
@CapacitorPlugin(name = "TetoWidget")
public class WidgetBridgePlugin extends Plugin {

    @PluginMethod
    public void update(PluginCall call) {
        String data = call.getData().toString();
        getContext()
            .getSharedPreferences(TetoWidgetProvider.PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(TetoWidgetProvider.KEY, data)
            .apply();
        TetoWidgetProvider.updateAll(getContext());
        call.resolve();
    }
}
