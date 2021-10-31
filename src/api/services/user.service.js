import User from "../models/User";
import bcrypt from "bcryptjs";

class UserService {
  static async getInfoFromFriendID(id) {}

  static async getInfoUser(user) {
    const data = (({ _id, name, avatarUrl, idApi }) => ({
      _id,
      name,
      avatarUrl,
      idApi,
    }))(user);
    return data;
  }

  static async checkUserExist(email) {
    return await User.findOne({ email: email });
  }

  static async createUser(data) {
    var salt = bcrypt.genSaltSync(10);
    data.password = bcrypt.hashSync(data.password, salt);
    const user = new User(data);
    try {
      const saveUser = await user.save();
      return saveUser;
    } catch (error) {
      return error;
    }
  }
  static async saveUser(user) {
    return await user.save();
  }

  static async findID(id) {
    // return await User.findById(id).populate("friends");
    return await User.findById(id);
  }
  static async findIDFriends(id) {
    return await User.findById(id).populate("friends");
  }
}

export default UserService;
