package net.suntrans.smarthome.fragment.perc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.bean.UserInfo;
import net.suntrans.smarthome.databinding.FragmentPersonBinding;
import net.suntrans.smarthome.activity.perc.AboutActivity;
import net.suntrans.smarthome.model.personal.PersonalContract;
import net.suntrans.smarthome.activity.perc.DeviceManagerActivity;
import net.suntrans.smarthome.activity.perc.HelpActivity;
import net.suntrans.smarthome.activity.perc.SettingActivity;
import net.suntrans.smarthome.activity.perc.SuggestionActivity;
import net.suntrans.smarthome.activity.perc.ModifyPassActivity;
import net.suntrans.smarthome.login.LoginActivity;
import net.suntrans.smarthome.utils.StatusBarCompat;

/**
 * Created by Looney on 2017/4/19.
 */

public class PersonalFragment extends Fragment implements PersonalContract.View {


    private PersonalContract.Presenter presenter;
    private FragmentPersonBinding binding;

    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        binding = FragmentPersonBinding.bind(view);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((RxAppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar ab = ((RxAppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowHomeEnabled(false);

        ab.setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statebarHeight = StatusBarCompat.getStatusBarHeight(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statebarHeight);
            binding.statusBarFix.setLayoutParams(layoutParams);

        }
        return view;
    }

    @Override
    public void setPresenter(PersonalContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.setActionHandler(presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void getDataSuccess(UserInfo info) {
        binding.setUser(info.result.user);
    }

    @Override
    public void openUserinfoUI() {

    }

    @Override
    public void openSuggestionsUI() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SuggestionActivity.class);
        startActivity(intent);
    }

    @Override
    public void openUpdateUI() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ModifyPassActivity.class);
        startActivity(intent);
    }

    @Override
    public void openAboutUI() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void openHelpUI() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void openSettingUI() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginOut() {
        new AlertDialog.Builder(getContext())
                .setMessage("是否注销")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getSharedPreferences().edit().clear().commit();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom,0);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("取消", null).create().show();


    }

    @Override
    public void openDeviceManagerUI() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), DeviceManagerActivity.class);
        startActivity(intent);
    }
}
