package com.padregiovanniciresola.padregiovanniciresola;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.venerabileciresola.appandroid.R;
import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {
    C0531bd banco;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_share);
        try {
            this.banco = new C0531bd();
            String queryParameter = getIntent().getData().getQueryParameter("id");
            new ArrayList();
            if (queryParameter == null || queryParameter.equals("")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }
            List<String> list = this.banco.get_mensagem(queryParameter);
            MainActivity.idShare = list.get(0);
            MainActivity.Textshare = list.get(1);
            service.idsite_ = list.get(0);
            service.mensagem_ = list.get(1);
            service.favorito_ = "0";
            service.cod_sequencia_ = list.get(2);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } catch (Exception unused) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
