package com.padregiovanniciresola.padregiovanniciresola;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.venerabileciresola.appandroid.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static float FonteSize = 18.0f;
    static String Textshare = "";
    static String anterior = "home";
    static String atual = "home";
    static Context esse = null;
    static String idShare = "";
    /* access modifiers changed from: private */
    public AdapterListView adapterListView;
    C0531bd banco;
    boolean botaosms = false;
    private ArrayList<ItemListView> itens;
    private ListView listView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((LinearLayout) findViewById(R.id.layout_stub)).addView(getLayoutInflater().inflate(R.layout.content_main, (ViewGroup) null, false));
        esse = this;
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.compartilhar();
            }
        });
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        try {
            C0531bd bdVar = new C0531bd();
            this.banco = bdVar;
            bdVar.bancoInicial(this);
        } catch (Exception unused) {
            C0531bd bdVar2 = new C0531bd();
            this.banco = bdVar2;
            bdVar2.excluir_mensagens(this);
            this.banco.bancoInicial(this);
        }
        mensagem();
        service();
    }

    private void permissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.INTERNET") != 0 && !ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.INTERNET")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECEIVE_BOOT_COMPLETED"}, 0);
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_NETWORK_STATE"}, 0);
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.INTERNET"}, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void openvideo(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse(str), "video/*");
        startActivity(Intent.createChooser(intent, "Complete action using"));
    }

    /* access modifiers changed from: package-private */
    public void compartilhar() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        if (Locale.getDefault().getLanguage().equals("pt")) {
            intent.putExtra("android.intent.extra.TEXT", "🙏" + Textshare + "🙏\n\nhttp://venerabileciresola.com/share.php?id=" + idShare + "&idioma=pt");
        } else if (Locale.getDefault().getLanguage().equals("es")) {
            intent.putExtra("android.intent.extra.TEXT", "🙏" + Textshare + "🙏\n\nhttp://venerabileciresola.com/share.php?id=" + idShare + "&idioma=es");
        } else if (Locale.getDefault().getLanguage().equals("it")) {
            intent.putExtra("android.intent.extra.TEXT", "🙏" + Textshare + "🙏\n\nhttp://venerabileciresola.com/share.php?id=" + idShare + "&idioma=it");
        } else if (Locale.getDefault().getLanguage().equals("en")) {
            intent.putExtra("android.intent.extra.TEXT", "🙏" + Textshare + "🙏\n\nhttp://venerabileciresola.com/share.php?id=" + idShare + "&idioma=en");
        } else {
            intent.putExtra("android.intent.extra.TEXT", "🙏" + Textshare + "🙏\n\nhttp://venerabileciresola.com/share.php?id=" + idShare + "&idioma=pt");
        }
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    public void service() {
        Intent intent = new Intent(this, service.class);
        intent.putExtra("Service", "Main");
        if (Build.VERSION.SDK_INT >= 26) {
            startService(intent);
        } else {
            startService(intent);
        }
    }

    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            drawerLayout.closeDrawer((int) GravityCompat.START);
            return;
        }
        String str = anterior;
        if (str == "home" && atual == "home") {
            finish();
        } else if (str == atual) {
            atual = "home";
            anterior = "home";
            onNavigationItemSelected((MenuItem) null);
        } else {
            onNavigationItemSelected((MenuItem) null);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_mensagem) {
            this.botaosms = true;
            mensagem();
            return true;
        } else if (itemId == R.id.menu_oracao) {
            lista_oracao();
            return true;
        } else if (itemId != R.id.menu_contato) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            contato();
            return true;
        }
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int i;
        if (menuItem == null) {
            String str = anterior;
            atual = str;
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -2056368072:
                    if (str.equals("amigoscenaculo")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1008862157:
                    if (str.equals("oracao")) {
                        c = 1;
                        break;
                    }
                    break;
                case -870024054:
                    if (str.equals("cenaculo")) {
                        c = 2;
                        break;
                    }
                    break;
                case -512220581:
                    if (str.equals("irmas_externas")) {
                        c = 3;
                        break;
                    }
                    break;
                case -27317137:
                    if (str.equals("lista_oracao")) {
                        c = 4;
                        break;
                    }
                    break;
                case 3208415:
                    if (str.equals("home")) {
                        c = 5;
                        break;
                    }
                    break;
                case 60349040:
                    if (str.equals("biografia")) {
                        c = 6;
                        break;
                    }
                    break;
                case 100473750:
                    if (str.equals("irmas")) {
                        c = 7;
                        break;
                    }
                    break;
                case 951526954:
                    if (str.equals("contato")) {
                        c = 8;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    i = R.id.amigoscenaculo;
                    break;
                case 1:
                case 4:
                    i = R.id.oracao;
                    break;
                case 2:
                    i = R.id.cenaculo;
                    break;
                case 3:
                    i = R.id.irmas_externas;
                    break;
                case 5:
                    i = R.id.mensagem;
                    break;
                case 6:
                    i = R.id.biografia;
                    break;
                case 7:
                    i = R.id.irmas;
                    break;
                case 8:
                    i = R.id.contato;
                    break;
                default:
                    i = -1;
                    break;
            }
        } else {
            i = menuItem.getItemId();
        }
        if (i == R.id.mensagem) {
            this.botaosms = true;
            mensagem();
        } else if (i == R.id.mensagens_favoritas) {
            anterior = atual;
            atual = "favoritas";
            favoritas();
        } else if (i == R.id.biografia) {
            anterior = atual;
            atual = "biografia";
            FonteSize = 16.0f;
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate = getLayoutInflater().inflate(R.layout.content_padrao, (ViewGroup) null, false);
            inflate.setBackgroundResource(R.drawable.texture_001);
            linearLayout.removeAllViews();
            linearLayout.addView(inflate);
            ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_biografia_do_padre));
            TextView textView = (TextView) findViewById(R.id.texto);
            textView.setTextSize(16.0f);
            final List<String> list = this.banco.getpagina("biografia");
            textView.setText(Html.fromHtml(list.get(2)));
            final TextView textView2 = (TextView) findViewById(R.id.texto);
            ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize += 1.0f;
                    textView2.setTextSize(MainActivity.FonteSize);
                }
            });
            ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize -= 1.0f;
                    textView2.setTextSize(MainActivity.FonteSize);
                }
            });
            ImageButton imageButton = (ImageButton) findViewById(R.id.url_img01);
            if (list.get(0).equals("---")) {
                imageButton.setBackgroundResource(R.drawable.padre_003);
            } else {
                Picasso.with(this).load(list.get(0).replace(" ", "%20")).into((ImageView) imageButton);
            }
            ImageButton imageButton2 = (ImageButton) findViewById(R.id.url_img02);
            if (list.get(1).equals("---")) {
                imageButton2.setBackgroundResource(R.drawable.padre_007);
            } else {
                Picasso.with(this).load(list.get(1).replace(" ", "%20")).into((ImageView) imageButton2);
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem01");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list.get(0)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.padre_003);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list.get(0)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
            imageButton2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem02");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list.get(1)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.padre_007);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list.get(1)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
        } else if (i == R.id.canonizacao) {
            anterior = atual;
            atual = "canonizacao";
            FonteSize = 16.0f;
            LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate2 = getLayoutInflater().inflate(R.layout.content_padrao, (ViewGroup) null, false);
            inflate2.setBackgroundResource(R.drawable.texture_003);
            linearLayout2.removeAllViews();
            linearLayout2.addView(inflate2);
            ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_canonizacao));
            TextView textView3 = (TextView) findViewById(R.id.texto);
            textView3.setTextSize(16.0f);
            final List<String> list2 = this.banco.getpagina("canonizacao");
            textView3.setText(Html.fromHtml(list2.get(2)));
            final TextView textView4 = (TextView) findViewById(R.id.texto);
            ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize += 1.0f;
                    textView4.setTextSize(MainActivity.FonteSize);
                }
            });
            ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize -= 1.0f;
                    textView4.setTextSize(MainActivity.FonteSize);
                }
            });
            ImageButton imageButton3 = (ImageButton) findViewById(R.id.url_img01);
            if (list2.get(0).equals("---")) {
                imageButton3.setBackgroundResource(R.drawable.canonizacao01);
            } else {
                Picasso.with(this).load(list2.get(0).replace(" ", "%20")).into((ImageView) imageButton3);
            }
            ImageButton imageButton4 = (ImageButton) findViewById(R.id.url_img02);
            if (list2.get(1).equals("---")) {
                imageButton4.setBackgroundResource(R.drawable.canonizacao02);
            } else {
                Picasso.with(this).load(list2.get(1).replace(" ", "%20")).into((ImageView) imageButton4);
            }
            imageButton3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem01");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list2.get(0)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.canonizacao01);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list2.get(0)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
            imageButton4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem02");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list2.get(1)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.canonizacao02);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list2.get(1)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
        } else if (i == R.id.oracao) {
            lista_oracao();
        } else if (i == R.id.irmas) {
            FonteSize = 16.0f;
            anterior = atual;
            atual = "irmas";
            LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate3 = getLayoutInflater().inflate(R.layout.content_padrao, (ViewGroup) null, false);
            inflate3.setBackgroundResource(R.drawable.texture_005);
            linearLayout3.removeAllViews();
            linearLayout3.addView(inflate3);
            ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_irmas));
            TextView textView5 = (TextView) findViewById(R.id.texto);
            textView5.setTextSize(16.0f);
            final List<String> list3 = this.banco.getpagina("irmas");
            textView5.setText(Html.fromHtml(list3.get(2)));
            final TextView textView6 = (TextView) findViewById(R.id.texto);
            ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize += 1.0f;
                    textView6.setTextSize(MainActivity.FonteSize);
                }
            });
            ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize -= 1.0f;
                    textView6.setTextSize(MainActivity.FonteSize);
                }
            });
            ImageButton imageButton5 = (ImageButton) findViewById(R.id.url_img01);
            if (list3.get(0).equals("---")) {
                imageButton5.setBackgroundResource(R.drawable.irmas01);
            } else {
                Picasso.with(this).load(list3.get(0).replace(" ", "%20")).into((ImageView) imageButton5);
            }
            ImageButton imageButton6 = (ImageButton) findViewById(R.id.url_img02);
            if (list3.get(1).equals("---")) {
                imageButton6.setBackgroundResource(R.drawable.irmas_002);
            } else {
                Picasso.with(this).load(list3.get(1).replace(" ", "%20")).into((ImageView) imageButton6);
            }
            imageButton5.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem01");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list3.get(0)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.irmas01);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list3.get(0)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
            imageButton6.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem02");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list3.get(1)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.irmas_002);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list3.get(1)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
        } else if (i == R.id.irmas_externas) {
            FonteSize = 16.0f;
            anterior = atual;
            atual = "irmas_externas";
            LinearLayout linearLayout4 = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate4 = getLayoutInflater().inflate(R.layout.content_padrao, (ViewGroup) null, false);
            inflate4.setBackgroundResource(R.drawable.texture_007);
            linearLayout4.removeAllViews();
            linearLayout4.addView(inflate4);
            ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_irmas_externas));
            TextView textView7 = (TextView) findViewById(R.id.texto);
            textView7.setTextSize(16.0f);
            final List<String> list4 = this.banco.getpagina("irmas_externas");
            textView7.setText(Html.fromHtml(list4.get(2)));
            final TextView textView8 = (TextView) findViewById(R.id.texto);
            ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize += 1.0f;
                    textView8.setTextSize(MainActivity.FonteSize);
                }
            });
            ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize -= 1.0f;
                    textView8.setTextSize(MainActivity.FonteSize);
                }
            });
            ImageButton imageButton7 = (ImageButton) findViewById(R.id.url_img01);
            if (list4.get(0).equals("---")) {
                imageButton7.setBackgroundResource(R.drawable.irmas_externas01);
            } else {
                Picasso.with(this).load(list4.get(0).replace(" ", "%20")).into((ImageView) imageButton7);
            }
            ImageButton imageButton8 = (ImageButton) findViewById(R.id.url_img02);
            if (list4.get(1).equals("---")) {
                imageButton8.setBackgroundResource(R.drawable.irmas_externas02);
            } else {
                Picasso.with(this).load(list4.get(1).replace(" ", "%20")).into((ImageView) imageButton8);
            }
            imageButton7.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem01");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list4.get(0)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.irmas_externas01);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list4.get(0)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
            imageButton8.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem02");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list4.get(1)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.irmas_externas02);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list4.get(1)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
        } else if (i == R.id.cenaculo) {
            FonteSize = 16.0f;
            anterior = atual;
            atual = "cenaculo";
            LinearLayout linearLayout5 = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate5 = getLayoutInflater().inflate(R.layout.content_padrao, (ViewGroup) null, false);
            inflate5.setBackgroundResource(R.drawable.texture_009);
            linearLayout5.removeAllViews();
            linearLayout5.addView(inflate5);
            ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_cenaculo));
            TextView textView9 = (TextView) findViewById(R.id.texto);
            textView9.setTextSize(16.0f);
            final List<String> list5 = this.banco.getpagina("cenaculo");
            textView9.setText(Html.fromHtml(list5.get(2)));
            final TextView textView10 = (TextView) findViewById(R.id.texto);
            ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize += 1.0f;
                    textView10.setTextSize(MainActivity.FonteSize);
                }
            });
            ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize -= 1.0f;
                    textView10.setTextSize(MainActivity.FonteSize);
                }
            });
            ImageButton imageButton9 = (ImageButton) findViewById(R.id.url_img01);
            if (list5.get(0).equals("---")) {
                imageButton9.setBackgroundResource(R.drawable.cenaculo_001);
            } else {
                Picasso.with(this).load(list5.get(0).replace(" ", "%20")).into((ImageView) imageButton9);
            }
            ImageButton imageButton10 = (ImageButton) findViewById(R.id.url_img02);
            if (list5.get(1).equals("---")) {
                imageButton10.setBackgroundResource(R.drawable.cenaculo_002);
            } else {
                Picasso.with(this).load(list5.get(1).replace(" ", "%20")).into((ImageView) imageButton10);
            }
            imageButton9.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem01");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list5.get(0)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.cenaculo_001);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list5.get(0)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
            imageButton10.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem02");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list5.get(1)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.cenaculo_002);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list5.get(1)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
        } else if (i == R.id.amigoscenaculo) {
            FonteSize = 16.0f;
            anterior = atual;
            atual = "amigoscenaculo";
            LinearLayout linearLayout6 = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate6 = getLayoutInflater().inflate(R.layout.content_padrao, (ViewGroup) null, false);
            inflate6.setBackgroundResource(R.drawable.texture_011);
            linearLayout6.removeAllViews();
            linearLayout6.addView(inflate6);
            ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_amigos_do_cenaculo));
            TextView textView11 = (TextView) findViewById(R.id.texto);
            textView11.setTextSize(16.0f);
            final List<String> list6 = this.banco.getpagina("amigoscenaculo");
            textView11.setText(Html.fromHtml(list6.get(2)));
            final TextView textView12 = (TextView) findViewById(R.id.texto);
            ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize += 1.0f;
                    textView12.setTextSize(MainActivity.FonteSize);
                }
            });
            ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MainActivity.FonteSize -= 1.0f;
                    textView12.setTextSize(MainActivity.FonteSize);
                }
            });
            ImageButton imageButton11 = (ImageButton) findViewById(R.id.url_img01);
            if (list6.get(0).equals("---")) {
                imageButton11.setBackgroundResource(R.drawable.amigos_cenaculo01);
            } else {
                Picasso.with(this).load(list6.get(0).replace(" ", "%20")).into((ImageView) imageButton11);
            }
            ImageButton imageButton12 = (ImageButton) findViewById(R.id.url_img02);
            if (list6.get(1).equals("---")) {
                imageButton12.setBackgroundResource(R.drawable.amigos_cenaculo02);
            } else {
                Picasso.with(this).load(list6.get(1).replace(" ", "%20")).into((ImageView) imageButton12);
            }
            imageButton11.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem01");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list6.get(0)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.amigos_cenaculo01);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list6.get(0)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
            imageButton12.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Dialog dialog = new Dialog(MainActivity.esse);
                    dialog.setContentView(R.layout.dialog_img);
                    dialog.setTitle("Imagem02");
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                    if (((String) list6.get(1)).equals("---")) {
                        imageView.setBackgroundResource(R.drawable.amigos_cenaculo02);
                    } else {
                        Picasso.with(MainActivity.esse).load(((String) list6.get(1)).replace(" ", "%20")).into(imageView);
                    }
                    dialog.show();
                }
            });
        } else if (i == R.id.contato) {
            contato();
        } else if (i == R.id.videos) {
            FonteSize = 14.0f;
            anterior = atual;
            atual = "contato";
            LinearLayout linearLayout7 = (LinearLayout) findViewById(R.id.layout_stub);
            View inflate7 = getLayoutInflater().inflate(R.layout.content_main_videos, (ViewGroup) null, false);
            inflate7.setBackgroundResource(R.drawable.bg_religioso_contato);
            linearLayout7.removeAllViews();
            linearLayout7.addView(inflate7);
            ListView listView2 = (ListView) findViewById(R.id.listView);
            this.listView = listView2;
            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    MainActivity.this.openvideo(MainActivity.this.adapterListView.getItem(i).getUrl_video());
                }
            });
            createListView();
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }

    private void createListView() {
        List<String> list = this.banco.getpagina("videos1");
        List<String> list2 = this.banco.getpagina("videos2");
        List<String> list3 = this.banco.getpagina("videos3");
        this.itens = new ArrayList<>();
        if (list.size() > 0 && !list.get(0).equals("---")) {
            this.itens.add(new ItemListView(list.get(2), list.get(0), R.mipmap.ic_launcher));
        }
        if (list2.size() > 0 && !list2.get(0).equals("---")) {
            this.itens.add(new ItemListView(list2.get(2), list2.get(0), R.mipmap.ic_launcher));
        }
        if (list3.size() > 0 && !list3.get(0).equals("---")) {
            this.itens.add(new ItemListView(list3.get(2), list3.get(0), R.mipmap.ic_launcher));
        }
        AdapterListView adapterListView2 = new AdapterListView(this, this.itens);
        this.adapterListView = adapterListView2;
        this.listView.setAdapter(adapterListView2);
        this.listView.setCacheColorHint(0);
    }

    /* access modifiers changed from: package-private */
    public void contato() {
        FonteSize = 14.0f;
        anterior = atual;
        atual = "contato";
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_stub);
        View inflate = getLayoutInflater().inflate(R.layout.content_contato, (ViewGroup) null, false);
        inflate.setBackgroundResource(R.drawable.bg_religioso_contato);
        linearLayout.removeAllViews();
        linearLayout.addView(inflate);
        final TextView textView = (TextView) findViewById(R.id.texto);
        final TextView textView2 = (TextView) findViewById(R.id.texto2);
        final TextView textView3 = (TextView) findViewById(R.id.txt);
        C050027 r8 = r0;
        final TextView textView4 = (TextView) findViewById(R.id.txt2);
        C050027 r0 = new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize += 1.0f;
                textView.setTextSize(MainActivity.FonteSize);
                textView2.setTextSize(MainActivity.FonteSize);
                textView3.setTextSize(MainActivity.FonteSize);
                textView4.setTextSize(MainActivity.FonteSize);
            }
        };
        ((Button) findViewById(R.id.btnMaior)).setOnClickListener(r8);
        ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize -= 1.0f;
                textView.setTextSize(MainActivity.FonteSize);
                textView2.setTextSize(MainActivity.FonteSize);
                textView3.setTextSize(MainActivity.FonteSize);
                textView4.setTextSize(MainActivity.FonteSize);
            }
        });
        ((Button) findViewById(R.id.btnMail)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "pgiovanniciresola@gmail.com", (String) null));
                intent.putExtra("android.intent.extra.SUBJECT", "App Giovanni Ciresola");
                intent.putExtra("android.intent.extra.TEXT", "\n\n\n\n - Send from App 'Giovanne Ciresola'");
                MainActivity.this.startActivity(Intent.createChooser(intent, "Send email..."));
            }
        });
        final List<String> list = this.banco.getpagina("contato");
        ImageButton imageButton = (ImageButton) findViewById(R.id.url_img01);
        if (list.get(0).equals("---")) {
            imageButton.setBackgroundResource(R.drawable.contato01);
        } else {
            Picasso.with(this).load(list.get(0).replace(" ", "%20")).into((ImageView) imageButton);
        }
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.url_img02);
        if (list.get(1).equals("---")) {
            imageButton2.setBackgroundResource(R.drawable.contato02);
        } else {
            Picasso.with(this).load(list.get(1).replace(" ", "%20")).into((ImageView) imageButton2);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.esse);
                dialog.setContentView(R.layout.dialog_img);
                dialog.setTitle("Imagem01");
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                if (((String) list.get(0)).equals("---")) {
                    imageView.setBackgroundResource(R.drawable.contato01);
                } else {
                    Picasso.with(MainActivity.esse).load(((String) list.get(0)).replace(" ", "%20")).into(imageView);
                }
                dialog.show();
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.esse);
                dialog.setContentView(R.layout.dialog_img);
                dialog.setTitle("Imagem02");
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                if (((String) list.get(1)).equals("---")) {
                    imageView.setBackgroundResource(R.drawable.contato02);
                } else {
                    Picasso.with(MainActivity.esse).load(((String) list.get(1)).replace(" ", "%20")).into(imageView);
                }
                dialog.show();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void lista_oracao() {
        FonteSize = 16.0f;
        anterior = atual;
        atual = "lista_oracao";
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_stub);
        View inflate = getLayoutInflater().inflate(R.layout.content_main_lista_oracao, (ViewGroup) null, false);
        inflate.setBackgroundResource(R.drawable.ceu04);
        linearLayout.removeAllViews();
        linearLayout.addView(inflate);
        ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_oracao));
        ImageButton imageButton = (ImageButton) findViewById(R.id.url_img01);
        if (imageButton != null) {
            imageButton.setVisibility(4);
        }
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.url_img02);
        if (imageButton2 != null) {
            imageButton2.setVisibility(4);
        }
        this.listView = (ListView) findViewById(R.id.listView);
        List<String> list = this.banco.get_titulos_oracao();
        this.itens = new ArrayList<>();
        for (String next : list) {
            this.itens.add(new ItemListView(next, next, R.mipmap.ic_launcher));
        }
        AdapterListView adapterListView2 = new AdapterListView(this, this.itens);
        this.adapterListView = adapterListView2;
        this.listView.setAdapter(adapterListView2);
        this.listView.setCacheColorHint(0);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                MainActivity.this.oracao(MainActivity.this.adapterListView.getItem(i).getTexto());
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void oracao(String str) {
        FonteSize = 16.0f;
        anterior = atual;
        atual = "oracao";
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_stub);
        View inflate = getLayoutInflater().inflate(R.layout.content_main_oracao, (ViewGroup) null, false);
        inflate.setBackgroundResource(R.drawable.ceu04);
        linearLayout.removeAllViews();
        linearLayout.addView(inflate);
        ((TextView) findViewById(R.id.titulo)).setText(str);
        TextView textView = (TextView) findViewById(R.id.texto);
        textView.setTextSize(16.0f);
        textView.setText(Html.fromHtml(this.banco.get_oracao(str)));
        ImageButton imageButton = (ImageButton) findViewById(R.id.url_img01);
        if (imageButton != null) {
            imageButton.setVisibility(4);
        }
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.url_img02);
        if (imageButton2 != null) {
            imageButton2.setVisibility(4);
        }
        Button button = (Button) findViewById(R.id.btnMail);
        final TextView textView2 = (TextView) findViewById(R.id.texto);
        ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize += 1.0f;
                textView2.setTextSize(MainActivity.FonteSize);
            }
        });
        ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize -= 1.0f;
                textView2.setTextSize(MainActivity.FonteSize);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void mensagem() {
        List list;
        anterior = atual;
        atual = "home";
        FonteSize = 18.0f;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_stub);
        LayoutInflater layoutInflater = getLayoutInflater();
        int nextInt = new Random().nextInt(6);
        View inflate = layoutInflater.inflate(R.layout.content_main, (ViewGroup) null, false);
        if (nextInt == 0) {
            inflate.setBackgroundResource(R.drawable.ceu01);
        } else if (nextInt == 1) {
            inflate.setBackgroundResource(R.drawable.ceu03);
        } else if (nextInt == 2) {
            inflate.setBackgroundResource(R.drawable.ceu05);
        } else if (nextInt == 3) {
            inflate.setBackgroundResource(R.drawable.ceu06);
        } else if (nextInt == 4) {
            inflate.setBackgroundResource(R.drawable.ceu07);
        } else {
            inflate.setBackgroundResource(R.drawable.ceu09);
        }
        linearLayout.removeAllViews();
        linearLayout.addView(inflate);
        new ArrayList();
        if (this.botaosms) {
            list = this.banco.getmensagem_data(0);
        } else if (service.mensagem_ == null) {
            list = this.banco.getmensagem_data(0);
        } else {
            list = new ArrayList();
            list.add(service.idsite_);
            list.add(service.mensagem_);
            list.add(service.cod_sequencia_);
            list.add(service.favorito_);
        }
        ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_mensagem_do_padre));
        final TextView textView = (TextView) findViewById(R.id.texto);
        textView.setText(Html.fromHtml(((String) list.get(1)) + "<br><b>" + getString(R.string.nome_padre) + "</b>"));
        if (((String) list.get(3)).equals("1")) {
            ((ImageButton) findViewById(R.id.btnStar)).setImageResource(getResources().getIdentifier("@android:drawable/btn_star_big_on", (String) null, (String) null));
        } else {
            ((ImageButton) findViewById(R.id.btnStar)).setImageResource(getResources().getIdentifier("@android:drawable/btn_star_big_off", (String) null, (String) null));
        }
        idShare = (String) list.get(2);
        Textshare = (String) list.get(1);
        final TextView textView2 = (TextView) findViewById(R.id.texto);
        ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize += 1.0f;
                textView2.setTextSize(MainActivity.FonteSize);
            }
        });
        ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize -= 1.0f;
                textView2.setTextSize(MainActivity.FonteSize);
            }
        });
        final int[] iArr = {0};
        ((Button) findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    int[] iArr = iArr;
                    iArr[0] = iArr[0] - 1;
                    textView.setText(Html.fromHtml(MainActivity.this.banco.getmensagem_data(iArr[0]).get(1) + "<br><b>" + MainActivity.this.getString(R.string.nome_padre) + "</b>"));
                    MainActivity.idShare = MainActivity.this.banco.getmensagem_data(iArr[0]).get(0);
                } catch (Exception unused) {
                    iArr[0] = 0;
                    textView.setText(Html.fromHtml(MainActivity.this.banco.getmensagem_data(iArr[0]).get(1) + "<br><b>" + MainActivity.this.getString(R.string.nome_padre) + "</b>"));
                    MainActivity.idShare = MainActivity.this.banco.getmensagem_data(iArr[0]).get(0);
                }
                Date date = new Date();
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                instance.add(5, iArr[0]);
                instance.getTime();
                new SimpleDateFormat("dd/MM/yyyy");
                TextView textView = (TextView) MainActivity.this.findViewById(R.id.txvData);
                if (MainActivity.this.banco.getmensagem_data(iArr[0]).get(3).equals("1")) {
                    ((ImageButton) MainActivity.this.findViewById(R.id.btnStar)).setImageResource(MainActivity.this.getResources().getIdentifier("@android:drawable/btn_star_big_on", (String) null, (String) null));
                } else {
                    ((ImageButton) MainActivity.this.findViewById(R.id.btnStar)).setImageResource(MainActivity.this.getResources().getIdentifier("@android:drawable/btn_star_big_off", (String) null, (String) null));
                }
            }
        });
        final ImageButton imageButton = (ImageButton) findViewById(R.id.btnStar);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.banco.getmensagem_data(iArr[0]).get(3).equals("1")) {
                    MainActivity.this.banco.favorito(MainActivity.this.banco.getmensagem_data(iArr[0]).get(2), 0);
                    imageButton.setImageResource(MainActivity.this.getResources().getIdentifier("@android:drawable/btn_star_big_off", (String) null, (String) null));
                    return;
                }
                MainActivity.this.banco.favorito(MainActivity.this.banco.getmensagem_data(iArr[0]).get(2), 1);
                imageButton.setImageResource(MainActivity.this.getResources().getIdentifier("@android:drawable/btn_star_big_on", (String) null, (String) null));
            }
        });
        ((Button) findViewById(R.id.btnNext)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int[] iArr = iArr;
                iArr[0] = iArr[0] + 1;
                textView.setText(Html.fromHtml(MainActivity.this.banco.getmensagem_data(iArr[0]).get(1) + "<br><b>" + MainActivity.this.getString(R.string.nome_padre) + "</b>"));
                MainActivity.Textshare = MainActivity.this.banco.getmensagem_data(iArr[0]).get(1);
                MainActivity.idShare = MainActivity.this.banco.getmensagem_data(iArr[0]).get(2);
                Date date = new Date();
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                instance.add(5, iArr[0]);
                instance.getTime();
                new SimpleDateFormat("dd/MM/yyyy");
                TextView textView = (TextView) MainActivity.this.findViewById(R.id.txvData);
                if (MainActivity.this.banco.getmensagem_data(iArr[0]).get(3).equals("1")) {
                    ((ImageButton) MainActivity.this.findViewById(R.id.btnStar)).setImageResource(MainActivity.this.getResources().getIdentifier("@android:drawable/btn_star_big_on", (String) null, (String) null));
                } else {
                    ((ImageButton) MainActivity.this.findViewById(R.id.btnStar)).setImageResource(MainActivity.this.getResources().getIdentifier("@android:drawable/btn_star_big_off", (String) null, (String) null));
                }
            }
        });
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(5, iArr[0]);
        instance.getTime();
        new SimpleDateFormat("dd/MM/yyyy");
        TextView textView3 = (TextView) findViewById(R.id.txvData);
        final List<String> list2 = this.banco.getpagina("mensagem");
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.url_img01);
        if (list2.get(0).equals("---")) {
            imageButton2.setBackgroundResource(R.drawable.padre_001);
        } else {
            Picasso.with(this).load(list2.get(0).replace(" ", "%20")).into((ImageView) imageButton2);
        }
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.url_img02);
        if (list2.get(1).equals("---")) {
            imageButton3.setBackgroundResource(R.drawable.padre_005);
        } else {
            Picasso.with(this).load(list2.get(1).replace(" ", "%20")).into((ImageView) imageButton3);
        }
        imageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.esse);
                dialog.setContentView(R.layout.dialog_img);
                dialog.setTitle("Imagem01");
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                if (((String) list2.get(0)).equals("---")) {
                    imageView.setBackgroundResource(R.drawable.padre_001);
                } else {
                    Picasso.with(MainActivity.esse).load(((String) list2.get(0)).replace(" ", "%20")).into(imageView);
                }
                dialog.show();
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.esse);
                dialog.setContentView(R.layout.dialog_img);
                dialog.setTitle("Imagem02");
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                if (((String) list2.get(1)).equals("---")) {
                    imageView.setBackgroundResource(R.drawable.padre_005);
                } else {
                    Picasso.with(MainActivity.esse).load(((String) list2.get(1)).replace(" ", "%20")).into(imageView);
                }
                dialog.show();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void favoritas() {
        anterior = atual;
        atual = "favoritas";
        FonteSize = 18.0f;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_stub);
        LayoutInflater layoutInflater = getLayoutInflater();
        int nextInt = new Random().nextInt(6);
        View inflate = layoutInflater.inflate(R.layout.content_padrao, (ViewGroup) null, false);
        if (nextInt == 0) {
            inflate.setBackgroundResource(R.drawable.ceu01);
        } else if (nextInt == 1) {
            inflate.setBackgroundResource(R.drawable.ceu03);
        } else if (nextInt == 2) {
            inflate.setBackgroundResource(R.drawable.ceu05);
        } else if (nextInt == 3) {
            inflate.setBackgroundResource(R.drawable.ceu06);
        } else if (nextInt == 4) {
            inflate.setBackgroundResource(R.drawable.ceu07);
        } else {
            inflate.setBackgroundResource(R.drawable.ceu09);
        }
        linearLayout.removeAllViews();
        linearLayout.addView(inflate);
        new ArrayList();
        List<String> list = this.banco.getmensagens_favoritas();
        ((TextView) findViewById(R.id.titulo)).setText(getResources().getString(R.string.page_mensagens_favoritas));
        TextView textView = (TextView) findViewById(R.id.texto);
        if (list != null) {
            String str = "";
            for (String str2 : list) {
                str = str + str2 + "<br><b>" + getResources().getString(R.string.app_name) + "</b><br>----------------------<br><br>";
            }
            textView.setText(Html.fromHtml(str));
        }
        final TextView textView2 = (TextView) findViewById(R.id.texto);
        ((Button) findViewById(R.id.btnMaior)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize += 1.0f;
                textView2.setTextSize(MainActivity.FonteSize);
            }
        });
        ((Button) findViewById(R.id.btnMenor)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.FonteSize -= 1.0f;
                textView2.setTextSize(MainActivity.FonteSize);
            }
        });
        final List<String> list2 = this.banco.getpagina("mensagem");
        ImageButton imageButton = (ImageButton) findViewById(R.id.url_img01);
        if (list2.get(0).equals("---")) {
            imageButton.setBackgroundResource(R.drawable.padre_001);
        } else {
            Picasso.with(this).load(list2.get(0).replace(" ", "%20")).into((ImageView) imageButton);
        }
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.url_img02);
        if (list2.get(1).equals("---")) {
            imageButton2.setBackgroundResource(R.drawable.padre_005);
        } else {
            Picasso.with(this).load(list2.get(1).replace(" ", "%20")).into((ImageView) imageButton2);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.esse);
                dialog.setContentView(R.layout.dialog_img);
                dialog.setTitle("Imagem01");
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                if (((String) list2.get(0)).equals("---")) {
                    imageView.setBackgroundResource(R.drawable.padre_001);
                } else {
                    Picasso.with(MainActivity.esse).load(((String) list2.get(0)).replace(" ", "%20")).into(imageView);
                }
                dialog.show();
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.esse);
                dialog.setContentView(R.layout.dialog_img);
                dialog.setTitle("Imagem02");
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imagem_g);
                if (((String) list2.get(1)).equals("---")) {
                    imageView.setBackgroundResource(R.drawable.padre_005);
                } else {
                    Picasso.with(MainActivity.esse).load(((String) list2.get(1)).replace(" ", "%20")).into(imageView);
                }
                dialog.show();
            }
        });
    }
}
