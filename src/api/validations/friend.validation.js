//validate
import Joi from "joi";

const friendRequestValidation = (data) => {
  const schema = Joi.object({
    userId: Joi.string().length(24).required(),
  });
  return schema.validate(data);
};

const changeNicknameValidation = (data) => {
  const schema = Joi.object({
    nickName: Joi.string().required(),
    id: Joi.string().length(24).required(),
  });
  return schema.validate(data);
};

const acceptFriendValidation = (data) => {
  const schema = Joi.object({
    id: Joi.string().length(24).required(),
  });
  return schema.validate(data);
};

export {
  friendRequestValidation,
  changeNicknameValidation,
  acceptFriendValidation,
};
