import Friend from "../models/Friend";
import UserService from "./user.service";
import NotifyService from "./notify.service";
import * as Config from "../socket/config";

class FriendService {
  static async getAllFriends(id, status = 0) {
    const user = await UserService.findIDFriends(id);
    const friends = await user.friends;
    const data = [];
    await Promise.all(
      await friends.map(async (friend) => {
        if (friend.status == status) {
          data.push(await this.getFriendInfo(friend));
        }
      })
    );

    return data;
  }

  static async getAllFriendAccepted(id) {
    return this.getAllFriends(id);
  }

  static async getAllFriendPendding(id) {
    return this.getAllFriends(id, -1);
  }

  static async getAllFriendRequest(id) {
    return this.getAllFriends(id, 1);
  }

  static async createFriend(receiverID, senderID) {
    const receiver = await UserService.findIDFriends(receiverID);
    const sender = await UserService.findID(senderID);
    // check da lam ban chua da gui chua ?
    // nho la socket io
    var isExits = false;
    await Promise.all(
      await receiver.friends.map(async (friend) => {
        if (friend.friend == senderID) {
          isExits = true;
        }
      })
    );
    if (isExits) {
      return;
    }

    const friendSender = await Friend.create({
      friend: receiverID,
      status: 1,
    });
    const friendReceiver = await Friend.create({
      friend: senderID,
      status: -1,
    });
    receiver.friends.push(friendReceiver._id);
    sender.friends.push(friendSender._id);
    await receiver.save();
    await sender.save();
    friendReceiver.friend = await UserService.findID(friendReceiver.friend);
    await NotifyService.notify(
      Config.CHANNEL_NEW_FRIEND_REQUEST,
      receiver._id,
      {
        message: sender.name + " muốn kết bạn với bạn",
        data: friendReceiver,
      }
    );
    return friendSender;
  }

  static async acceptFriend(acceptedID, id) {
    var friendAccepted = await this.getFriendByID(acceptedID, id);
    // check null hoa
    if (!friendAccepted || friendAccepted.status == 0) {
      return;
    }
    friendAccepted.status = 0; // cho -1 -> 0
    // create friend phia ben nguoi kia ben sender thi 1 -> 0
    // friend cua ben sender
    const friendSender = await this.getFriendByUserID(
      friendAccepted.friend,
      acceptedID
    );
    if (!friendSender || friendSender.status == 0) {
      return;
    }
    friendSender.status = 0;
    await friendSender.save();
    await friendAccepted.save();
    friendSender.friend = await UserService.findID(friendSender.friend);
    await NotifyService.notify(
      Config.CHANNEL_NEW_FRIEND_REQUEST,
      friendAccepted.friend,
      {
        message: friendSender.friend.name + " đã chấp nhận lời mời kết bạn",
        data: friendSender,
      }
    );
    return friendAccepted;
  }

  static async removeFriend(userID, friendID) {
    const user = await UserService.findID(userID);
    const friend1 = await FriendService.getFriendByID(userID, friendID);
    if (!friend1) {
      return;
    }
    user.friends.splice(user.friends.indexOf(friend1._id), 1);

    const user2 = await UserService.findID(friend1.friend);
    const friend2 = await FriendService.getFriendByUserID(user2._id, userID);
    if (!friend2) {
      return res
        .status(400)
        .send({ status: "error", message: "Permission denied!" });
    }
    await friend1.delete();
    await friend2.delete();
    user2.friends.splice(user2.friends.indexOf(friend2._id), 2);
    await UserService.saveUser(user);
    await UserService.saveUser(user2);
    await NotifyService.notify(Config.CHANNEL_DELETE_FRIEND, user2._id, {
      message: "xoa ban",
      data: friend2,
    });
    return friend1;
  }

  static async findID(id) {
    return await Friend.findById(id);
  }

  static async getFriendByID(userID, id) {
    const user = await UserService.findIDFriends(userID);
    const friends = user.friends.filter((obj) => {
      return obj._id == id;
    });
    if (friends.length <= 0) {
      return;
    }
    return friends[0];
  }

  static async getFriendByUserID(userID, friendID) {
    const user = await UserService.findIDFriends(userID);
    const friends = user.friends.filter((obj) => {
      return obj.friend.toString() == friendID.toString();
    });
    if (friends.length <= 0) {
      return;
    }
    return friends[0];
  }

  static async getFriendInfo(friend) {
    const data = (({ _id, nickName, createdAt }) => ({
      _id,
      nickName,
      createdAt,
    }))(friend);
    const user = await UserService.findID(friend.friend);
    data.friend = await UserService.getInfoUser(user);
    return data;
  }

  static async save(friend) {
    await friend.save();
    return friend;
  }
}

export default FriendService;
