import jwt from "jsonwebtoken";

const guest = function (req, res, next) {
  const token = req.header("auth-token");
  if (!token) {
    return next();
  }
  try {
    const verified = jwt.verify(token, process.env.TOKEN_SECRET);
    return res.redirect("/api/test/auth");
  } catch (error) {
    return next();
  }
};

export default guest;
