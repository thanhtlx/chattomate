// import
import jwt from "jsonwebtoken";
import UserService from "../services/user.service";
import bcrypt from "bcryptjs";
import {
  registerValidation,
  loginValidation,
} from "../validations/auth.validation";

class AuthController {
  static async login(req, res) {
    //validate form data
    const { error } = loginValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    //   check user exists
    const user = await UserService.checkUserExist(req.body.email);
    if (!user) {
      return res
        .status(404)
        .send({ status: "error", message: "Email or password is wrong!" });
    }
    const validatePassword = bcrypt.compareSync(
      req.body.password,
      user.password
    );

    if (!validatePassword) {
      return res
        .status(404)
        .send({ status: "error", message: "Email or password is wrong!" });
    }
    //   jwt
    const token = jwt.sign(
      { exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24, _id: user._id },
      process.env.TOKEN_SECRET
    );
    return res.header("auth-token", token).send({
      status: "success",
      data: {
        user: user,
        token: token,
      },
    });
  }

  static async register(req, res) {
    //validate form data
    const { error } = registerValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    //   check user exists
    const userExisted = await UserService.checkUserExist(req.body.email);
    if (userExisted) {
      return res.status(404).send({ status: "error", message: "User existed" });
    }

    const user = await UserService.saveUser({
      name: req.body.name,
      phone: req.body.phone,
      email: req.body.email,
      password: req.body.password,
    });
    if (user._id) {
      return res.send({ status: "success", data: user });
    } else {
      res.status(404).send(user);
    }
  }

  static logout(req, res) {
    return res.header("auth-token", "").send({
      status: "success",
    });
  }
}

export default AuthController;
