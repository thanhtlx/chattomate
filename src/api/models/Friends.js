import mongoose, { Schema } from "mongoose";
const schema = mongoose.Schema;

const userSchema = new schema(
  {
    nickName: {
      type: String,
      required: true,
    },
    accepted: {
      type: Boolean,
      required: true,
      default:false,
    },
    friend: {
      type: Schema.Types.ObjectId,
      ref: "users",
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const User = mongoose.model("friends", userSchema);

export default User;
