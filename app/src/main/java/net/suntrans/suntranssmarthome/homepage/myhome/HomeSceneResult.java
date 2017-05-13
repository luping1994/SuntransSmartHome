package net.suntrans.suntranssmarthome.homepage.myhome;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

/**
 * Created by Looney on 2017/4/24.
 */

public class HomeSceneResult {
    public String status;
    public Result result;

    public static class Result {
        public List<Scene> rows;
    }

    public static class Scene extends BaseObservable {
        public String id;
        public String user_id;
        public String name;
        public String img;
        public String sort;
        public String status;

    }
}
