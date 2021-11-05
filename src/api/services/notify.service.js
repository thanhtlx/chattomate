import Notify from "../models/notify";
import socket from "../socket/socket";
import FriendService from "./friend.service";
import * as Config from "../socket/config";
import ConversationService from "./conversation.service";
import UserService from "./user.service";

class NotifyService {
  static async notifyFriendActiveChange(userID, message) {
    const friends = await FriendService.getAllFriends(userID);
    const friendIDs = friends.map((friend) => friend.friend._id.toString());
    for (var friend of friendIDs) {
      var clientID = socket.getClientUserId(friend);
      if (clientID) {
        socket.emit(clientID, Config.CHANNEL_FIREND_ACTICE_CHANGE, {
          message: message,
          data: await this.getAllFriendOnline(friend),
        });
        return;
      }
    }
  }

  static async notifyTyping(userID, conversationID) {
    const userTyping = UserService.findID(userID);
    const conversation = await ConversationService.findID(conversationID);
    for (var user of conversation.members) {
      var clientID = socket.getClientUserId(user.toString());
      if (clientID) {
        socket.emit(clientID, Config.CHANNEL_FIREND_ACTICE_CHANGE, {
          message: message,
          data: userTyping,
        });
        return;
      }
    }
  }

  static async getAllFriendOnline(userID) {
    const friends = await FriendService.getAllFriends(userID);
    const res = friends.filter(
      (friend) => socket.getClientUserId(friend.friend._id.toString()) != null
    );
    return res;
  }

  static async notify(channel, userID, data) {
    console.log(userID);
    const clientID = socket.getClientUserId(userID);
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
    const notifiers = await this.getAllNotifiers(userID);
    console.log(notifiers);
    for (notify of notifiers) {
      var clientID = socket.getClientUserId(userID);
      if (clientID) {
        socket.emit(clientID, notify.channel, notify.data);
        notify.delete();
        return;
      }
    }
  }
}

export default NotifyService;
