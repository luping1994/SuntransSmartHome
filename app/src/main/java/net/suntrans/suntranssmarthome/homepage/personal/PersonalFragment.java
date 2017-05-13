package net.suntrans.suntranssmarthome.homepage.personal;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.suntrans.suntranssmarthome.App;
import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.databinding.FragmentPersonBinding;
import net.suntrans.suntranssmarthome.homepage.personal.detail.AboutActivity;
import net.suntrans.suntranssmarthome.homepage.personal.detail.HelpActivity;
import net.suntrans.suntranssmarthome.homepage.personal.detail.SettingActivity;
import net.suntrans.suntranssmarthome.homepage.personal.detail.SuggestionActivity;
import net.suntrans.suntranssmarthome.homepage.personal.detail.UpdateActivity;
import net.suntrans.suntranssmarthome.login.LoginActivity;
import net.suntrans.suntranssmarthome.utils.StatusBarCompat;

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
        intent.setClass(getActivity(), UpdateActivity.class);
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
        App.getSharedPreferences().edit().clear().commit();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom,0);
        getActivity().finish();

    }
}
