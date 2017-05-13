package net.suntrans.suntranssmarthome.homepage.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.suntrans.suntranssmarthome.R;

/**
 * Created by Looney on 2017/4/20.
 */

public class ShopFragment extends Fragment{
    public static ShopFragment newInstance(){
        return new ShopFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop,container,false);
        return view;
    }
}
