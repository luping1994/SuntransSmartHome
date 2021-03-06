/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.suntrans.smarthome.model.personal;


import net.suntrans.smarthome.bean.UserInfo;
import net.suntrans.smarthome.model.BaseView;
import net.suntrans.smarthome.presenter.BasePresenter;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface PersonalContract {

    interface View extends BaseView<Presenter> {
        void getDataSuccess(UserInfo info);
        void openUserinfoUI();
        void openSuggestionsUI();
        void openUpdateUI();
        void openAboutUI();
        void openHelpUI();
        void openSettingUI();
        void onLoginOut();
        void openDeviceManagerUI();
    }

    interface Presenter extends BasePresenter {
        void openUserinfoPage();
        void openSuggestionsPage();
        void openUpdatePage();
        void openAboutPage();
        void openHelpPage();
        void openSettingPage();
        void onLoginOut();
        void openDeviceManagerPage();
    }
}
