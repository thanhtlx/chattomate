import mongoose from "mongoose";

const schema = mongoose.Schema;

const userSchema = new schema(
  {
    channel: {
      type: String,
      required: true,
    },
    data: {
      type: Object,
      required: true,
    },
    user: {
      type: schema.Types.ObjectId,
      ref: "users",
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const Notify = mongoose.model("Notifiers", userSchema);

export default Notify;
