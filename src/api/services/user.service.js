import User from "../models/User";
import bcrypt from "bcryptjs";
import fetch from "node-fetch";

class UserService {
  static async getInfoFromFriendID(id) {}

  static async getAllUsers() {
    const users = await User.find();
    return users.map((user) => UserService.getInfoUser(user));
  }

  static getInfoUser(user) {
    const data = (({ _id, name, email, avatarUrl, idApi }) => ({
      _id,
      name,
      avatarUrl,
      email,
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
      const idApi = await UserService.createQbUser(user);
      if (!idApi) {
        return "Can't create user";
      }
      user.idApi = idApi;
      const saveUser = await user.save();
      return saveUser;
    } catch (error) {
      return error;
    }
  }

  static async createQbUser(userData) {
    const body = {
      application_id: 94399,
      auth_key: "uZF6qPmTtMAyvse",
      timestamp: "1572434294",
      nonce: "33432",
      signature: "bade1b1a574313568f95d4a5151c1d52249c0350",
    };

    const response = await fetch("https://api.quickblox.com/session.json", {
      method: "POST",
      body: JSON.stringify(body),
      headers: { "Content-Type": "application/json" },
    });
    const data = await response.json();
    console.log(data);
    const token = data.session.token;

    const bodyregister = {
      user: {
        login: userData.email,
        password: process.env.QB_SECRET,
        email: userData.email,
        full_name: userData.name,
      },
    };

    const responseRegister = await fetch(
      "https://api.quickblox.com/users.json",
      {
        method: "POST",
        body: JSON.stringify(bodyregister),
        headers: { "Content-Type": "application/json", "QB-Token": token },
      }
    );

    const dataRegister = await responseRegister.json();
    console.log(dataRegister);
    return dataRegister.user.id;
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
