
import tensorflow as tf

# Load the SavedModel
saved_model_dir = "efficientdet_lite2_detection_1"
model = tf.saved_model.load(saved_model_dir)

# Create a TFLite Converter
converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)

# Disable tensor list ops
converter.target_spec.supported_ops = [
    tf.lite.OpsSet.TFLITE_BUILTINS,  # Use built-in TensorFlow Lite ops
    tf.lite.OpsSet.SELECT_TF_OPS  # Use select TensorFlow ops (disable tensor list ops)
]

# Convert to TFLite format
tflite_model = converter.convert()

# Save the TFLite model to a file
with open("efficientdet_lite2_detection_1.tflite", "wb") as f:
    f.write(tflite_model)
