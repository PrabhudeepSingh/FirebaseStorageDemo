package com.psb13.dineshadmin.model;

public class ButtonModel {

    public String name;
    public String urlToIcon;
    public String onClickUrl;
    public Boolean isEnabled;

    public ButtonModel() {
    }

    public ButtonModel(String name, String urlToIcon, String onClickUrl, Boolean isEnabled) {
        this.name = name;
        this.urlToIcon = urlToIcon;
        this.onClickUrl = onClickUrl;
        this.isEnabled = isEnabled;
    }
}
