package com.example.chattomate.call.utils;

import android.content.Context;

import com.example.chattomate.App;
import com.example.chattomate.database.AppPreferenceManager;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;


public class UsersUtils {

    private static SharedPrefsHelper sharedPrefsHelper;

    public static ArrayList<QBUser> getListAllUsersFromIds(ArrayList<QBUser> existedUsers, List<Integer> allIds) {
        ArrayList<QBUser> qbUsers = new ArrayList<>();
        for (Integer userId : allIds) {
            QBUser stubUser = createStubUserById(userId);
            if (!existedUsers.contains(stubUser)) {
                qbUsers.add(stubUser);
            }
        }
        qbUsers.addAll(existedUsers);

        return qbUsers;
    }

    private static QBUser createStubUserById(Integer userId) {
        AppPreferenceManager manager = new AppPreferenceManager(App.getInstance());
        QBUser stubUser = new QBUser(userId);
        stubUser.setFullName(manager.getNameFromIdApi(userId));
        return stubUser;
    }

    public static ArrayList<Integer> getIdsNotLoadedUsers(ArrayList<QBUser> existedUsers, List<Integer> allIds) {
        ArrayList<Integer> idsNotLoadedUsers = new ArrayList<>();
        for (Integer userId : allIds) {
            QBUser stubUser = createStubUserById(userId);
            if (!existedUsers.contains(stubUser)) {
                idsNotLoadedUsers.add(userId);
            }
        }

        return idsNotLoadedUsers;
    }
}