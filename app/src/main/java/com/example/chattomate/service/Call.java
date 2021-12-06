package com.example.chattomate.service;

import android.content.Context;
import android.text.TextUtils;

import com.example.chattomate.call.CallActivity;
import com.example.chattomate.call.utils.PushNotificationSender;
import com.example.chattomate.call.utils.WebRtcSessionManager;
import com.example.chattomate.database.AppPreferenceManager;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.List;

public class Call {
    Context context;
    private AppPreferenceManager manager;

    public Call(Context context) {
        this.context = context;
        manager = new AppPreferenceManager(context);
    }

    public void startCall(boolean isVideoCall, String idApi, String _id) {

        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(Integer.valueOf(idApi));
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(context);
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(context).setCurrentSession(newQbRtcSession);
        // Make Users FullName Strings and ID's list for iOS VOIP push
        String newSessionID = newQbRtcSession.getSessionID();
        ArrayList<String> opponentsIDsList = new ArrayList<>();
        ArrayList<String> opponentsNamesList = new ArrayList<>();
        List<QBUser> usersInCall = new ArrayList<>();

        opponentsIDsList.add(idApi);
        opponentsNamesList.add(manager.getUser().name);

        String opponentsIDsString = TextUtils.join(",", opponentsIDsList);
        String opponentNamesString = TextUtils.join(",", opponentsNamesList);

        PushNotificationSender.sendPushMessage(opponentsList, manager.getUser().name, newSessionID, opponentsIDsString, opponentNamesString, isVideoCall,_id);
        CallActivity.start(context, false);
    }

}
