//validate
import Joi from "joi";


const userValidation = (data) => {
  const schema = Joi.object({
    name: Joi.string().min(2),
    email: Joi.string().min(6).email(),
    phone: Joi.string().min(9),
    password: Joi.string().min(8),
    idApi: Joi.string(),
    avatarUrl: Joi.string(),
  });
  return schema.validate(data);
};

export default userValidation;