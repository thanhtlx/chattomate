class MySocket {
  static userActice = {};
  init(io) {
    this.io = io;
    io.on("connection", function (socket) {
      // Create a new room
      let userID = socket.handshake.query.id;
      MySocket.userActice[userID] = socket.id;
      
      socket.on("typing", (data) => {
        io.to(data.rid).emmit("typing", data);
      });
      console.log(MySocket.userActice);

      socket.on("disconnect", () => {
        delete MySocket.userActice[userID];
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
