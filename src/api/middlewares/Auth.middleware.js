import jwt from "jsonwebtoken";

const auth = function (req, res, next) {
  const token = req.header("auth-token");
  if (!token) {
    return res.status(401).send({
      status: "error",
      message: "Access Denied!",
    });
  }
  try {
    const verified = jwt.verify(token, process.env.TOKEN_SECRET);
    req.user = verified;
    next();
  } catch (error) {
    return res.status(400).send({
      status: "error",
      message: "Invaild Token!",
    });
  }
};

export default auth;
