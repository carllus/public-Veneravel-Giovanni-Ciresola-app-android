package com.padregiovanniciresola.padregiovanniciresola;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.internal.view.SupportMenu;
import com.venerabileciresola.appandroid.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class service extends Service {
    static String cod_sequencia_;
    static String favorito_;
    static String idsite_;
    static String mensagem_;

    /* renamed from: db */
    SQLiteDatabase f54db;
    List<String> listafavoritos = new ArrayList();

    /* access modifiers changed from: package-private */
    public void atualizabanco() {
    }

    public void mantemsms() {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public String getdatenow() {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(5);
        int i2 = instance.get(2) + 1;
        int i3 = instance.get(1);
        "" + i2;
        "" + i3;
        instance.get(10);
        instance.get(12);
        instance.get(13);
        return i + "/" + i2 + "/" + i3;
    }

    /* access modifiers changed from: package-private */
    public void notificacao() {
        Log.d("notificacao", "inicio");
        SQLiteDatabase openOrCreateDatabase = openOrCreateDatabase(C0531bd.NomeBanco, 0, (SQLiteDatabase.CursorFactory) null);
        this.f54db = openOrCreateDatabase;
        try {
            if (!openOrCreateDatabase.rawQuery("SELECT data FROM notification WHERE data LIKE '" + getdatenow() + "'", (String[]) null).moveToFirst()) {
                this.f54db.execSQL("DROP TABLE IF EXISTS notification");
                this.f54db.execSQL("CREATE TABLE IF NOT EXISTS notification(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");
                this.f54db.execSQL("INSERT INTO notification (id, data) VALUES (NULL, '" + getdatenow() + "')");
                if (Build.VERSION.SDK_INT >= 26) {
                    notificationNovo();
                } else {
                    notificationAntigo();
                }
            }
        } catch (Exception unused) {
            this.f54db.execSQL("DROP TABLE IF EXISTS notification");
            this.f54db.execSQL("CREATE TABLE IF NOT EXISTS notification(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");
            this.f54db.execSQL("INSERT INTO notification (id, data) VALUES (NULL, '" + getdatenow() + "')");
            if (Build.VERSION.SDK_INT >= 26) {
                notificationNovo();
            } else {
                notificationAntigo();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notificationAntigo() {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        try {
            notificationManager.cancel(1337);
        } catch (Exception unused) {
        }
        PendingIntent activity = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getBaseContext(), MainActivity.class), 0);
        List<String> list = getmensagem();
        Notification build = new Notification.Builder(getBaseContext()).setContentTitle(getResources().getString(R.string.page_mensagem_do_padre)).setContentText(list.get(1)).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(activity).build();
        gravasms(list.get(0), list.get(1), list.get(2), list.get(3));
        notificationManager.notify(1337, build);
    }

    private void notificationNovo() {
        List<String> list = getmensagem();
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        String string = getResources().getString(R.string.page_mensagem_do_padre);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(string, getResources().getString(R.string.page_mensagem_do_padre), 5);
            notificationChannel.setDescription(list.get(1));
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(SupportMenu.CATEGORY_MASK);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, string);
        builder.setAutoCancel(true).setDefaults(-1).setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher).setTicker(getResources().getString(R.string.page_mensagem_do_padre)).setContentTitle(getResources().getString(R.string.page_mensagem_do_padre)).setContentText(list.get(1)).setContentInfo("...").addAction(R.drawable.ic_launcher_foreground, "OPEN APP...", PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        notificationManager.notify(1, builder.build());
    }

    public void onCreate() {
        super.onCreate();
        SQLiteDatabase openOrCreateDatabase = openOrCreateDatabase(C0531bd.NomeBanco, 0, (SQLiteDatabase.CursorFactory) null);
        this.f54db = openOrCreateDatabase;
        openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS notification(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");
        Log.d("mode", "oncreate");
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        tread();
        return 1;
    }

    /* access modifiers changed from: package-private */
    public String get_local() {
        if (Locale.getDefault().getLanguage().equals("pt") || Locale.getDefault().getLanguage().equals("es") || Locale.getDefault().getLanguage().equals("it") || Locale.getDefault().getLanguage().equals("en")) {
            return Locale.getDefault().getLanguage();
        }
        return "pt";
    }

    /* access modifiers changed from: package-private */
    public List<String> getmensagem() {
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        int i = instance.get(6);
        int conta_mensagens = conta_mensagens();
        if (i > conta_mensagens) {
            i %= conta_mensagens;
            if (i == 0) {
                i = 1;
            }
        } else if (i <= 0) {
            i += conta_mensagens;
        }
        Cursor rawQuery = this.f54db.rawQuery("SELECT * FROM mensagem WHERE idioma LIKE '" + get_local() + "' AND cod_sequencia = " + i, (String[]) null);
        ArrayList arrayList = new ArrayList();
        if (!rawQuery.moveToNext()) {
            return null;
        }
        arrayList.add(rawQuery.getString(1));
        arrayList.add(rawQuery.getString(3));
        arrayList.add(rawQuery.getString(4));
        if (rawQuery.getString(5) != null) {
            arrayList.add(rawQuery.getString(5));
        } else {
            arrayList.add("0");
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public int conta_mensagens() {
        Cursor rawQuery = this.f54db.rawQuery("SELECT Count(*) FROM mensagem WHERE idioma LIKE '" + get_local() + "'", (String[]) null);
        if (rawQuery.moveToNext()) {
            return rawQuery.getInt(0);
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void tread() {
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        service.this.notificacao();
                        Thread.sleep(10000);
                        service.this.mantemsms();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /* access modifiers changed from: package-private */
    public void gravasms(String str, String str2, String str3, String str4) {
        try {
            this.f54db.execSQL("DROP TABLE IF EXISTS mensagemnotification");
        } catch (Exception unused) {
        }
        this.f54db.execSQL("CREATE TABLE IF NOT EXISTS mensagemnotification(idsite TEXT, sms TEXT, cod_sequencia INTEGER, favorito INTEGER);");
        this.f54db.execSQL("INSERT INTO mensagemnotification (idsite, sms, cod_sequencia, favorito) VALUES ('" + str + "', '" + str2 + "', " + str3 + ", " + str4 + ")");
    }

    /* access modifiers changed from: package-private */
    public void recuperafavoritos() {
        Cursor rawQuery = this.f54db.rawQuery("SELECT * FROM mensagem WHERE idioma LIKE '" + get_local() + "' AND favorito = 1;", (String[]) null);
        while (rawQuery.moveToNext()) {
            this.listafavoritos.add(rawQuery.getString(1));
        }
        this.f54db.execSQL("DROP TABLE IF EXISTS mensagem");
    }

    /* access modifiers changed from: package-private */
    public void devolvefavorito() {
        for (String str : this.listafavoritos) {
            this.f54db.execSQL("UPDATE mensagem SET favorito = 1 WHERE idioma LIKE '" + get_local() + "' AND idsite LIKE '" + str + "'");
        }
    }

    /* access modifiers changed from: package-private */
    public String getData() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
