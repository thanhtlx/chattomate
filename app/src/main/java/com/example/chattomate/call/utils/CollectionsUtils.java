package com.example.chattomate.call.utils;

import com.example.chattomate.App;
import com.example.chattomate.database.AppPreferenceManager;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;


public class CollectionsUtils {

    public static String makeStringFromUsersFullNames(ArrayList<QBUser> allUsers) {
        StringifyArrayList<String> usersNames = new StringifyArrayList<>();
        AppPreferenceManager manager = new AppPreferenceManager(App.getInstance());
        for (QBUser usr : allUsers) {
//            usr.getId() != null;
            String name = manager.getNameFromIdApi(usr.getId());
            usersNames.add(name);
        }
        return usersNames.getItemsAsString().replace(",", ", ");
    }

    public static ArrayList<Integer> getIdsSelectedOpponents(Collection<QBUser> selectedUsers) {
        ArrayList<Integer> opponentsIds = new ArrayList<>();
        if (!selectedUsers.isEmpty()) {
            for (QBUser qbUser : selectedUsers) {
                opponentsIds.add(qbUser.getId());
            }
        }

        return opponentsIds;
    }
}