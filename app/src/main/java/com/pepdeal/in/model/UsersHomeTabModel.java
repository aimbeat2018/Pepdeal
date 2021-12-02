package com.pepdeal.in.model;

import android.content.Intent;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class UsersHomeTabModel {
    Integer color;
    Integer icon;
    String title;

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
