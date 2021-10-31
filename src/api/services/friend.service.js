import Friend from "../models/Friend";
import UserService from "./user.service";

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

    return friendAccepted;
  }

  static async removeFriend(friend) {
    await friend.delete();
    return friend;
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
