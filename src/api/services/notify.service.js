import Notify from "../models/notify";
import socket from "../socket/socket";
import FriendService from "./friend.service";
import * as Config from "../socket/config";
import ConversationService from "./conversation.service";
import UserService from "./user.service";

class NotifyService {
  // chua test
  static async notifyFriendActiveChange(userID, message) {
    const friends = await FriendService.getAllFriends(userID);
    const friendIDs = friends.map((friend) => friend.friend._id.toString());
    for (var friend of friendIDs) {
      var clientID = socket.getClientID(friend);
      if (clientID) {
        socket.emit(clientID, Config.CHANNEL_FIREND_ACTICE_CHANGE, {
          message: message,
          data: await NotifyService.getAllFriendOnline(friend),
        });
        return;
      }
    }
  }

  // chua test
  static async notifyTyping(userID, conversationID) {
    const userTyping = UserService.findID(userID);
    const conversation = await ConversationService.findID(conversationID);
    for (var user of conversation.members) {
      var clientID = socket.getClientID(user.toString());
      if (clientID) {
        socket.emit(clientID, Config.CHANNEL_FIREND_ACTICE_CHANGE, {
          message: message,
          data: userTyping,
        });
        return;
      }
    }
  }

  // chua test
  static async getAllFriendOnline(userID) {
    const friends = await FriendService.getAllFriends(userID);
    const res = friends.filter(
      (friend) => friend.friend._id.toString() in socket.getUserActive()
    );
    return res;
  }

  static async notify(channel, userID, data) {
    console.log(userID);
    const clientID = await socket.getClientUserId(userID);
    if (clientID) {
      socket.emit(clientID, channel, data);
      return;
    }
    await Notify.create({
      channel: channel,
      user: userID,
      data: data,
    });
    return;
  }

  static async getAllNotifiers(userId) {
    return await Notify.find({ user: userId });
  }

  static async notifyAll(userID) {
    const notifiers = await NotifyService.getAllNotifiers(userID);
    for (notify of notifiers) {
      var clientID = await socket.getClientUserId(userID);
      if (clientID) {
        socket.emit(clientID, notify.channel, notify.data);
        notify.delete();
        return;
      }
    }
  }

  static async notifyIncomingCall(userID, caller) {
    const clientID = await socket.getClientUserId(userID);
    if (clientID) {
      socket.emit(clientID, Config.CHANNEL_IMCOMING_CALL, { caller: caller });
      return;
    }
  }
}

export default NotifyService;
