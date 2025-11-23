# 🧙‍♂️ LogiQuest

### Turn your coding workflow into an RPG-style productivity experience.

LogiQuest is a **Kotlin Multiplatform** desktop application that transforms your workflow into an interactive retro RPG battle scene.
It integrates deeply with **Logitech MX Master 4**, **MX Mechanical**, and the **Logi Options+ SDK** to react to your typing, gestures, and context in real time — augmented by AI-generated smart macros.

---

## ✨ Features

### 🪄 Context-Aware RPG Overlay

* Detects your **active application**
* Generates a matching “monster” based on the app identity
* Updates dynamically as you switch windows

### 🧑‍💻 Workflow Avatar

Your developer avatar reflects your activity:

* **Defense Mode** → typing on the keyboard
* **Attack Mode** → executing Logitech smart gestures
* **Idle / Focus animations** → based on system context

### 🖱 Logitech Device Integration

Through a custom **Logi Options+ plugin**, LogiQuest receives:

* Smart Actions and button gestures
* Device events from MX Master 4 / MX Mechanical
* Mapped actions through the official Logitech UI

### 🔮 AI-Generated “Spell Cards”

Uses Google Gemini to create:

* Smart macros
* Command sequences
* Workflow shortcuts
* Voice-activated actions

Generated based on natural-language instructions.

### 🧠 ML-Driven Enhancements

Optional additional modules:

* Posture checking via camera
* Voice recognition
* Contextual workflow analysis

---

## 🧰 Tech Stack

### Core Logic & UI

* **Kotlin Multiplatform (KMP)**
* **Compose Multiplatform / Compose Desktop**
* GPU-accelerated animations
* Shared logic for future Android/iOS companion app

### Logitech Integration

* **C# .NET** custom plugin
* Built for **Logi Options+ SDK**
* Local HTTP bridge to Kotlin app

### AI & ML

* **Google Gemini API** (LLM, Vision, Macro generation)
* Local posture detection module (optional)
* Kotlin-native ML interop utilities

---

## 📦 Repository Structure

```
/LogiQuestPlugin       → C# .NET plugin for Logi Options+
/hton                  → Kotlin shared core (workflow, animation engine)
```

---

## 🖥 Installation & Setup

### 1. Install Dependencies

#### Required:

* **JDK 17+**
* **.NET 8.0 SDK**
* **Logi Options+** (latest version)
* **Git**

#### Optional (for AI features):

* Google Gemini API key
* Webcam for posture module

---

### 2. Build the Kotlin Desktop App

```bash
./gradlew :app-desktop:build
```

Run:

```bash
./gradlew :app-desktop:run
```

---

### 3. Install the Logitech Plugin

The repository includes a ready-to-use plugin under:

```
LogiQuestPlugin/bin/Release/net8.0/
```

Copy the generated plugin folder into:

```
%LOCALAPPDATA%\LogiOptionsPlus\Plugins\
```

Restart **Logi Options+**
→ You will see **LogiQuest Plugin** appear in the Smart Actions UI.

---

### 4. Configure Smart Actions

Open **Logi Options+** → Select your device → Smart Actions

Map any button or gesture to:

* **LogiQuest Attack**
* **LogiQuest Special Action**
* **LogiQuest Macro Trigger**

These will be sent to the Kotlin app via the local HTTP bridge.

---

## 🔧 Configuration

### LogiQuest App Settings

Located at:

```
~/.logiquest/config.json
```

Configurable values include:

* Animation speed
* AI integration toggle
* Spell card generation rules
* HTTP bridge port
* Logging options
---

Optional modules connect to:

* Google Gemini (LLM + Vision)
* Webcam (Posture detection)

---

## 📚 Use Cases

### 🚀 Productivity Gamification

Turn your coding session into an interactive RPG battle — maintain flow and reduce burnout.

### ⚔️ Gesture-Based Automation

Trigger AI-generated macros using hardware gestures.

### 🧠 AI Workflow Assistance

Ask for an action naturally:

> “Create a macro that opens my current GitHub repo and runs tests.”

LogiQuest generates a spell card automatically.

### 👁️ Posture & Health Awareness

Stay healthy while coding — slouching gradually lowers your character’s HP.

### 📱 Companion Device Integration (future)

Use your phone as:

* Spell inventory
* Dashboard
* Alerts console

---

## 🤝 Contributing

Pull requests, feature requests, and bug reports are welcome.

---

## 📜 License

This project is licensed under the MIT License.
