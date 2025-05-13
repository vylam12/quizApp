<h1 align="center">🗣️ Bilingual Chat Application</h1>
<p align="center">
 A bilingual chat app built with Kotlin and Node.js.
 <br />
 <em>Kotlin + Node.js + MongoDB + Firebase | Auth | Chat | Translate | Add Friend | Vocabulary Practice</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-developing-blue" alt="status" />
  <img src="https://img.shields.io/badge/client-kotlin-orange" alt="client" />
  <img src="https://img.shields.io/badge/backend-node.js-yellowgreen" alt="backend" />
  <img src="https://img.shields.io/badge/database-mongodb_&_firebase-lightgrey" alt="database" />
</p>

---

## ✨ Introduction

**Talk Bilingually** is a bilingual chat app that helps users improve their vocabulary while chatting. It supports real-time conversations, message translation, vocabulary saving, and interactive learning tools like flashcards and quizzes.

### 🌟 Main Features

- User authentication (Sign up, Login, Password reset)
- Real-time messaging with friends
- Auto-translate messages between languages
- Search chats and users
- Add or remove friends
- Save vocabulary from messages
- Learn with flashcards
- Review with vocabulary quizzes

---

## 🔧 Technologies Used

| Component       | Technology Stack                               |
|-----------------|--------------------------------------------------|
| Android Client  | Kotlin, MVVM, Retrofit, ViewModel, Firebase      |
| Backend API     | Node.js, Express.js, JWT, Bcrypt, Cloudinary     |
| Database        | MongoDB (via Mongoose), Firebase Realtime DB     |
| Image Hosting   | Cloudinary                                       |

---

## 🚀 Setup & Run the App

### 1. Open Project in Android Studio
- Open **Android Studio**
- Select **Open an existing project**
- Choose the folder: `MyApplication`
- Android Studio will auto-download required dependencies.

### 2. Configure Firebase
1. Go to [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Add your Android app to Firebase (use your app's package name).
3. Download the `google-services.json` file.
4. Place the file inside the `app/` directory of your Android project.

### 3. Set API URL
Open `RetrofitClient.kt` and update the server URL:

```kotlin
const val BASE_URL = "http://your-server-ip:9000/"
```

### 4. Run the App
Click Run in Android Studio to launch the app on your device or emulator.

## 📄 License

This project is released under the MIT License.
Feel free to use it for learning and development purposes.

---

## 📬 Contact

**Author:** Trần Lê Thảo Vy
**Email:** lethaovytran7@gmail.com  
**GitHub:** [@vylam12](https://github.com/vylam12))
