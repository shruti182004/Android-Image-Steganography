package com.example.steganography;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;
import android.content.Intent;
import androidx.core.content.FileProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.charset.StandardCharsets;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imageView, rocketImage;
    private EditText messageInput;
    private Button downloadButton;
    private Bitmap selectedBitmap, encodedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        rocketImage = findViewById(R.id.rocketImage); // Rocket ImageView
        messageInput = findViewById(R.id.messageInput);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button encodeButton = findViewById(R.id.encodeButton);
        Button decodeButton = findViewById(R.id.decodeButton);
        Button shareButton = findViewById(R.id.shareButton);
        downloadButton = findViewById(R.id.downloadButton);

        selectImageButton.setOnClickListener(view -> selectImage());

        encodeButton.setOnClickListener(view -> {
            if (selectedBitmap != null && !messageInput.getText().toString().isEmpty()) {
                encodedBitmap = encodeMessage(selectedBitmap, messageInput.getText().toString());
                imageView.setImageBitmap(encodedBitmap);
                downloadButton.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);
                launchRocket();
                Toast.makeText(this, "Message Encoded", Toast.LENGTH_SHORT).show();
            }
        });

        downloadButton.setOnClickListener(view -> {
            if (encodedBitmap != null) {
                saveEncodedImageAsDocument(encodedBitmap);
            }
        });

        decodeButton.setOnClickListener(view -> {
            if (selectedBitmap != null) {
                String message = decodeMessage(selectedBitmap);
                messageInput.setText(message);
                launchRocket();
                Toast.makeText(this, "Message Decoded", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(view -> {
            if (encodedBitmap != null) {
                File stegFile = saveEncodedImageAsDocument(encodedBitmap);
                if (stegFile != null) {
                    shareEncodedImageAsDocument(stegFile);
                }
            }
        });

    }

    private void animateImageSelection() {
        imageView.setAlpha(0f);
        imageView.animate().alpha(1f).setDuration(500).start();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // Show all files, including .steg
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            try {
                // Read the file as a Bitmap (even if it has .steg extension)
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                selectedBitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(selectedBitmap);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void shareAsDocForWhatsApp(File file) {
        if (file == null || !file.exists()) {
            Toast.makeText(this, "Error: File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Rename file with .doc extension just for sharing
            File renamedFile = new File(file.getParent(), "encoded_image.doc");

            // Copy contents to the new file
            try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(renamedFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }

            // Get URI using FileProvider
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", renamedFile);

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/msword"); // or application/pdf
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setPackage("com.whatsapp"); // Optional: open WhatsApp directly

            startActivity(Intent.createChooser(shareIntent, "Share Encoded Message"));

        } catch (Exception e) {
            Toast.makeText(this, "Error sharing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File saveEncodedImageAsDocument(Bitmap bitmap) {
        try {
            File directory = new File(getExternalFilesDir(null), "StegoFiles");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File stegFile = new File(directory, "encoded_image.steg");
            FileOutputStream out = new FileOutputStream(stegFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return stegFile; // return the File
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }





    private Bitmap encodeMessage(Bitmap bitmap, String message) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int messageLength = messageBytes.length;

        int width = mutableBitmap.getWidth();
        int height = mutableBitmap.getHeight();
        int pixelIndex = 0;

        // Store message length in first 32 bits (4 bytes)
        for (int i = 0; i < 32; i++) {
            int pixel = mutableBitmap.getPixel(pixelIndex % width, pixelIndex / width);
            int blue = pixel & 0xFE;  // Clear LSB
            blue |= (messageLength >> (31 - i)) & 1; // Insert bit
            mutableBitmap.setPixel(pixelIndex % width, pixelIndex / width, (pixel & 0xFFFFFF00) | blue);
            pixelIndex++;
        }

        // Encode message
        for (byte b : messageBytes) {
            for (int j = 0; j < 8; j++) {
                int pixel = mutableBitmap.getPixel(pixelIndex % width, pixelIndex / width);
                int blue = pixel & 0xFE;  // Clear LSB
                blue |= (b >> (7 - j)) & 1; // Insert bit
                mutableBitmap.setPixel(pixelIndex % width, pixelIndex / width, (pixel & 0xFFFFFF00) | blue);
                pixelIndex++;
            }
        }

        return mutableBitmap;
    }

    private void shareEncodedImageAsDocument(File file) {
        if (file == null || !file.exists()) {
            Toast.makeText(this, "Error: File not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get URI using FileProvider
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            // Open document sharing interface
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/octet-stream"); // Forces it to be treated as a document
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setPackage("com.whatsapp"); // Directly open WhatsApp

            startActivity(Intent.createChooser(shareIntent, "Share Document"));

        } catch (Exception e) {
            Toast.makeText(this, "Error sharing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }





    private String decodeMessage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelIndex = 0;

        // Read message length from first 32 bits
        int messageLength = 0;
        for (int i = 0; i < 32; i++) {
            int pixel = bitmap.getPixel(pixelIndex % width, pixelIndex / width);
            messageLength = (messageLength << 1) | (pixel & 1);
            pixelIndex++;
        }

        byte[] messageBytes = new byte[messageLength];

        // Decode message
        for (int i = 0; i < messageLength; i++) {
            byte charByte = 0;
            for (int j = 0; j < 8; j++) {
                int pixel = bitmap.getPixel(pixelIndex % width, pixelIndex / width);
                charByte = (byte) ((charByte << 1) | (pixel & 1));
                pixelIndex++;
            }
            messageBytes[i] = charByte;
        }

        return new String(messageBytes, StandardCharsets.UTF_8);
    }

    private void launchRocket() {
        // Get screen height dynamically
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Move the rocket completely out of the screen
        ObjectAnimator rocketLaunch = ObjectAnimator.ofFloat(rocketImage, "translationY", -screenHeight);
        rocketLaunch.setDuration(2000);
        rocketLaunch.setInterpolator(new AccelerateInterpolator());
        rocketLaunch.start();
    }

}
