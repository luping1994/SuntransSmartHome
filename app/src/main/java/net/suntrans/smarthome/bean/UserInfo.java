package net.suntrans.smarthome.bean;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Looney on 2017/4/20.
 */

public class UserInfo {
    public String status;

    public Result result;
    public static class Result{
        public User user;
    }
    public static class User extends BaseObservable implements Parcelable {
        public String id;
        public String username;
        public String nickname;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.username);
            dest.writeString(this.nickname);
        }

        public User() {
        }

        protected User(Parcel in) {
            this.id = in.readString();
            this.username = in.readString();
            this.nickname = in.readString();
        }

        public static final Creator<User> CREATOR = new Creator<User>() {
            @Override
            public User createFromParcel(Parcel source) {
                return new User(source);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "status='" + status + '\'' +
                ", result=" + result.user.toString() +
                '}';
    }

    public void getData(){

    }
}
