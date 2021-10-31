import Notify from "../models/notify";
import socket from "../socket/socket";

class NotifyService {
  static async notify(channel, userID, data) {
    console.log(userID);
    const clientID = socket.getClientUserId(userID);
    if (clientID) {
      socket.emit(clientID, channel, data);
      return;
    }
    // create notify
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
}

export default NotifyService;
