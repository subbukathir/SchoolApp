package com.daemon.oxfordschool.model;

/**
 * Created by Ravi on 29/07/15.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private boolean isSelected;

    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
