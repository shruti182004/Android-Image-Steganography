# 🕵️‍♀️ Image Steganography Android App

An Android app that hides secret text inside images using steganography — built using **Android Studio**.

---

## 📱 Features
- Encode text into images securely  
- Decode hidden messages from encoded images  
- Save and share encoded images (`.steg` or `.doc` formats)  
- Simple UI with a rocket animation 🚀  
- WhatsApp-compatible sharing  
- Only decodes `.steg` files (prevents decoding normal images)

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

## 📂 Project Structure
