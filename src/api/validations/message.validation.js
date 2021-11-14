import Joi from "joi";

const sendMessageValidation = (data) => {
  const schema = Joi.object({
    conversation: Joi.string().required(),
    type: Joi.string().required(),
    content: Joi.string(),
    contentUrl: Joi.string(),
    duration: Joi.string(),
  });
  return schema.validate(data);
};
export default sendMessageValidation;
