import Joi from "joi";

const createValidation = (data) => {
  const schema = Joi.object({
    members: Joi.array().items(Joi.string().length(24).required()).required(),
  });
  return schema.validate(data);
};

const updateValidation = (data) => {
  const schema = Joi.object({
    id: Joi.string().length(24).required(),
    name: Joi.string(),
    backgroundURI: Joi.string(),
    emoji: Joi.string(),
    ghim: Joi.string(),
    noted: Joi.string(),
  }); 
  return schema.validate(data);
};

const addMembersValidation = (data) => {
  const schema = Joi.object({
    members: Joi.array().items(Joi.string().length(24).required()).required(),
    id: Joi.string().length(24).required(),
  });
  return schema.validate(data);
};

const removeMemberValidation = (data) => {
  const schema = Joi.object({
    member: Joi.string().length(24).required(),
    id: Joi.string().length(24).required(),
  });
  return schema.validate(data);
};

export {
  createValidation,
  updateValidation,
  addMembersValidation,
  removeMemberValidation,
};
