const registerValidation = (data) => {
  const schema = Joi.object({
    name: Joi.string().min(2).required(),
    email: Joi.string().min(6).required().email(),
    phone: Joi.string().min(9).required(),
    password: Joi.string().min(8).required(),
  });
  return schema.validate(data);
};
