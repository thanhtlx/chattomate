import express from "express";
import User from "../models/User";
import {
  registerValidation,
  loginValidation,
} from "../validations/auth.validation";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";


const router = express.Router();

router.post("/register", async (req, res) => {
  //validate form data
  const { error } = registerValidation(req.body);
  if (error) {
    return res
      .status(404)
      .send({ status: "error", message: error.details[0].message });
  }
  //   check user exists
  const emailExist = await User.findOne({ email: req.body.email });
  if (emailExist) {
    return res.status(404).send({ status: "error", message: "User existed" });
  }

  var salt = bcrypt.genSaltSync(10);
  const user = new User({
    name: req.body.name,
    phone: req.body.phone,
    email: req.body.email,
    password: bcrypt.hashSync(req.body.password, salt),
  });

  try {
    const saveUser = await user.save();
    return res.send({ status: "success", data: saveUser });
  } catch (error) {
    res.status(404).send(error);
  }
});

router.post("/login", async (req, res) => {
  //validate form data
  const { error } = loginValidation(req.body);
  if (error) {
    return res
      .status(404)
      .send({ status: "error", message: error.details[0].message });
  }
  //   check user exists
  const user = await User.findOne({ email: req.body.email });
  if (!user) {
    return res
      .status(404)
      .send({ status: "error", message: "Email or password is wrong!" });
  }
  const validatePassword = bcrypt.compareSync(req.body.password, user.password);

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
    data: token,
  });
});

export default router;
