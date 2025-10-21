# 🕵️‍♀️ Image Steganography Android App

An Android app that hides secret text inside images using steganography — built using **Android Studio**.

---

## 📱 Features
- Encode text into images securely  
- Decode hidden messages from encoded images  
- Save and share encoded images  
- Simple UI with a rocket animation 🚀  
- WhatsApp-compatible sharing  
  
---

## 👥 Contributors
- A special thanks to the following people who contributed to this project:
- **Jitin Nair** 
---

## 🧠 Tech Stack
- **Language:** Java  
- **IDE:** Android Studio  
- **Framework:** Android SDK  
- **Storage:** Local device storage  
- **File handling:** Bitmap & Byte Array conversion  

---

## ⚙️ How It Works
1. **Encoding:**  
   The app embeds secret text into the pixel data of an image using **Least Significant Bit (LSB)** manipulation.  
2. **Decoding:**  
   The app reads those pixel bits to extract and reconstruct the hidden text.  

---

## ▶️ How to Run the Project
🧩 Prerequisites

Before running this project, make sure you have:

Android Studio (latest version recommended)

Java JDK 8 or above

Android SDK properly configured

A physical or virtual Android device (emulator) running Android 8.0 (Oreo) or higher

⚙️ Steps to Run

## 1) Clone the repository

```git clone https://github.com/shruti182004/Image-Steganography-Android.git```

## 2)Open the project in Android Studio

- Go to File → Open → select the cloned project folder.

- Let Gradle finish syncing.

## 3)Build the project

- Android Studio will automatically download required dependencies.

- If prompted, click “Sync Now” to resolve Gradle files.

## 4)Run the app

- Click on the green Run ▶️ button.

- Choose an emulator or connect a physical Android device via USB.

The app will install and launch automatically.

## 📱 Usage

- Tap Encode Image → select an image → enter secret text → click Encode.

- Save or share the encoded image.

- Tap Decode Image → select a .steg file → view the hidden message.

- Enjoy the rocket animation 🚀 and smooth UI!

## 🐞 Troubleshooting

- If Gradle fails to sync, go to File → Invalidate Caches / Restart.

- Ensure local.properties file has your Android SDK path:

``` sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk ```

- Use “Clean Project” and “Rebuild Project” if you face build errors.


