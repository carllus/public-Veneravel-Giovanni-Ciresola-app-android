package com.padregiovanniciresola.padregiovanniciresola;

public class ItemListView {
    private String texto;
    private int url_ico;
    private String url_video;

    public ItemListView() {
    }

    public ItemListView(String str, String str2, int i) {
        this.texto = str;
        this.url_video = str2;
        this.url_ico = i;
    }

    public String getTexto() {
        return this.texto;
    }

    public void setTexto(String str) {
        this.texto = str;
    }

    public String getUrl_video() {
        return this.url_video;
    }

    public void setUrl_video(String str) {
        this.url_video = str;
    }

    public int getUrl_ico() {
        return this.url_ico;
    }

    public void setUrl_ico(int i) {
        this.url_ico = i;
    }
}
