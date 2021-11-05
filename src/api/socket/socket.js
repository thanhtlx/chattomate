import NotifyService from "../services/notify.service";

class MySocket {
  static userActice = {};
  init(io) {
    this.io = io;
    io.on("connection", async function (socket) {
      // Create a new room
      let userID = socket.handshake.query.id;
      MySocket.userActice[userID] = socket.id;
      // notify all friend you online
      await NotifyService.notifyFriendActiveChange(userID, "online");
      await NotifyService.notifyAll();
      socket.on("typing", async (data) => {
        // data là conversation id
        await NotifyService.notifyTyping(userID, data.conversationID);
        // io.to(data.rid).emmit("typing", data);
        // notify all
      });
      console.log(MySocket.userActice);

      socket.on("disconnect", async (data) => {
        delete MySocket.userActice[userID];
        // noti all friend offline
        console.log("disconnected" + data);
        await NotifyService.notifyFriendActiveChange(userID, "offline");
      });
    });
  }

  getClientUserId(userId) {
    if (MySocket.userActice[userId] !== undefined) {
      return MySocket.userActice[userId];
    }
  }

  emit(id, channel, data) {
    this.io.to(id).emit(channel, data);
  }
}

export default new MySocket();
