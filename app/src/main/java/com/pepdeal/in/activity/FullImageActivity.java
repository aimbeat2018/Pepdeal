package com.pepdeal.in.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pepdeal.in.R;
import com.pepdeal.in.model.productdetailsmodel.ProductImageModel;

import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageView photoView;
    ArrayList<ProductImageModel> stringArrayList = new ArrayList<>();
    String pos = "", image = "", from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        viewPager = findViewById(R.id.viewPager);
        photoView = findViewById(R.id.imgDisplay);
        from = getIntent().getStringExtra("from");
        if (from.equals("single")) {
            image = getIntent().getStringExtra("image");
            photoView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            stringArrayList = (ArrayList<ProductImageModel>) getIntent().getSerializableExtra("arraylist");
            photoView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
        pos = getIntent().getStringExtra("position");

        if (from.equals("single")) {
            Glide.with(this).load(image).into(photoView);
        } else {
            viewPager.setAdapter(new ImageSliderAdapter(FullImageActivity.this, stringArrayList));
            viewPager.setCurrentItem(Integer.parseInt(pos));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class ImageSliderAdapter extends PagerAdapter {
        Activity activity;
        ArrayList<ProductImageModel> sliderUtils;
        String image;

        public ImageSliderAdapter(Activity viewPagerActivity, ArrayList<ProductImageModel> sliderImg) {
            activity = viewPagerActivity;
            this.sliderUtils = sliderImg;
//            this.image = Image;
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fullscreenimage, null);
            String a = sliderUtils.get(position).getProductImage();
            ImageView photoView = view.findViewById(R.id.imgDisplay);
            try {
                Glide.with(activity).load(a).into(photoView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
            return view;
        }

        @Override
        public int getCount() {
            return sliderUtils.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}