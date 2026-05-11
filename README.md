# Expander - System-Wide Text Expansion for Android

<p align="center">
  <img src="https://img.shields.io/badge/Android-13%2B-3DDC84?style=flat&logo=android" alt="Android 13+">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/Material%203-Dynamic-6200EE?style=flat&logo=material-design" alt="Material 3">
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat" alt="License">
</p>

## 📖 What is Expander?

Expander is a powerful text expansion app for Android that works system-wide across all your apps. Type a short trigger word followed by space, and watch it automatically expand into longer text snippets. Perfect for:

- 📧 Email addresses and signatures
- 📱 Phone numbers and addresses
- 💬 Frequently used responses
- 📅 Date and time stamps
- 🔗 URLs and usernames
- 📝 Code snippets and templates

**The best part?** It works everywhere - Messages, Chrome, WhatsApp, Gmail, Notes, and any other app with text input!

## ✨ Features

### 🚀 Core Functionality

- **System-Wide Expansion** - Works in every app on your device
- **Space-Triggered** - Expansions only happen after hitting Space
- **Smart Word Detection** - Won't expand if trigger is in the middle of a word
- **No Clipboard Interference** - Uses direct text replacement (SET_TEXT action)
- **Works with Any Keyboard** - Compatible with Gboard, SwiftKey, and all other keyboards
- **Backspace Undo** - Press backspace immediately after expansion to revert to trigger word

### 🎨 User Interface

- **Material 3 Design** - Modern, beautiful interface
- **Theme Selection** - Choose between Light, Dark, or System theme
- **Search & Filter** - Quickly find snippets with real-time search
- **Swipe to Delete** - Long-press snippets for easy deletion
- **Enable/Disable Snippets** - Toggle snippets on/off without deleting
- **Empty States** - Helpful guidance when you're getting started

### 🔄 Dynamic Snippets

Expander supports dynamic placeholders that are replaced with real-time values:

| Placeholder | Output | Description |
|------------|--------|-------------|
| `{{date}}` | 2026-01-22 | Current date (ISO format) |
| `{{time}}` | 15:45:30 | Current time (24-hour) |
| `{{datetime}}` | 2026-01-22 15:45:30 | Date and time |
| `{{day}}` | Wed | Day of week (short) |
| `{{day_long}}` | Wednesday | Day of week (full) |
| `{{month}}` | Jan | Month (short) |
| `{{month_long}}` | January | Month (full) |
| `{{year}}` | 2026 | Full year |
| `{{year_short}}` | 26 | Two-digit year |
| `{{week_num}}` | 3 | Week number |
| `{{date:format}}` | 22/01/2026 | Custom date format |
| `{{time:format}}` | 3:45 PM | Custom time format |

### 💾 Data Management

- **Persistent Database** - All snippets stored locally using Room
- **JSON Export** - Backup your snippets to a file
- **JSON Import** - Restore snippets from backup
- **CRUD Operations** - Create, Read, Update, Delete snippets
- **Multi-line Support** - Snippets can span multiple lines

### 🎯 Additional Features

- **Global Toggle** - Enable/disable the expansion service from Settings
- **Accessibility Service Integration** - Seamless system-wide text monitoring
- **Instant Updates** - No app restart needed for changes

## 📱 Screenshots

*(Screenshots would go here showing the main screens)*

## 🚀 Getting Started

### Prerequisites

- Android device running **Android 13 (API 33)** or higher
- ~10 MB of storage space

### Installation

1. Download and install the APK
2. Open the app
3. Grant accessibility permission:
   - Tap **Settings** → **Accessibility Settings**
   - Find **Expander** in the list
   - Toggle it **ON**
   - Confirm the permission prompt

### Creating Your First Snippet

1. Tap the **+** button on the home screen
2. Enter a **trigger** (e.g., `!email`)
3. Enter the **expansion** (e.g., `john.doe@example.com`)
4. Tap **Add Snippet**

### Using Snippets

1. Open any app with text input (Messages, Notes, etc.)
2. Type your trigger: `!email`
3. Press **Space**
4. Watch it expand to `john.doe@example.com`

**To undo:** Press **Backspace** immediately after expansion

## 💡 Example Snippets

### Basic Snippets

```
Trigger: !email
Expansion: john.doe@example.com

Trigger: !phone
Expansion: +1 (555) 123-4567

Trigger: !addr
Expansion: 123 Main Street
         Springfield, IL 62701

Trigger: !sig
Expansion: Best regards,
         John Doe
```

### Dynamic Snippets

```
Trigger: !today
Expansion: Today is {{day_long}}, {{month_long}} {{date:d}}, {{year}}
Output: Today is Wednesday, January 22, 2026

Trigger: !meeting
Expansion: Meeting scheduled for {{date:EEEE, MMMM d}} at {{time:h:mm a}}
Output: Meeting scheduled for Wednesday, January 22 at 3:45 PM

Trigger: !stamp
Expansion: [{{datetime}}]
Output: [2026-01-22 15:45:30]

Trigger: !header
Expansion: ## {{day}}, {{month}} {{date:d}}, {{year}}
Output: ## Wed, Jan 22, 2026

Trigger: !blog
Expansion: Published on {{month_long}} {{date:d}}, {{year}} by {{author}}
Output: Published on January 22, 2026 by {{author}}

Trigger: !version
Expansion: v{{year_short}}.{{week_num}}.{{date:dd}}
Output: v26.03.22

Trigger: !week
Expansion: Week {{week_num}} of {{year}}
Output: Week 3 of 2026
```

### Custom Date Formats

```
Trigger: !date-us
Expansion: {{date:MM/dd/yyyy}}
Output: 01/22/2026

Trigger: !date-eu
Expansion: {{date:dd/MM/yyyy}}
Output: 22/01/2026

Trigger: !date-iso
Expansion: {{date:yyyy-MM-dd}}
Output: 2026-01-22

Trigger: !date-long
Expansion: {{date:EEEE, MMMM d, yyyy}}
Output: Wednesday, January 22, 2026

Trigger: !time-12
Expansion: {{time:h:mm a}}
Output: 3:45 PM

Trigger: !time-24
Expansion: {{time:HH:mm}}
Output: 15:45
```

## 🔧 Advanced Usage

### Date Format Patterns

Use Java's SimpleDateFormat patterns for custom date/time formatting:

- `yyyy` - 4-digit year (2026)
- `yy` - 2-digit year (26)
- `MMMM` - Full month name (January)
- `MMM` - Short month name (Jan)
- `MM` - 2-digit month (01)
- `dd` - 2-digit day (22)
- `d` - Day without leading zero (22)
- `EEEE` - Full day name (Wednesday)
- `EEE` - Short day name (Wed)
- `HH` - Hour (00-23)
- `hh` - Hour (01-12)
- `mm` - Minutes (00-59)
- `ss` - Seconds (00-59)
- `a` - AM/PM marker

### Import/Export Snippets

**Export:**
1. Go to **Settings** → **Export Snippets**
2. Choose save location
3. File saved as `expander_snippets_[timestamp].json`

**Import:**
1. Go to **Settings** → **Import Snippets**
2. Select JSON file
3. Snippets are added to your collection

**JSON Format:**
```json
[
  {
    "id": 1,
    "trigger": "!email",
    "expansion": "john.doe@example.com",
    "isEnabled": true,
    "createdAt": 1234567890,
    "updatedAt": 1234567890
  }
]
```

### Theme Customization

1. Go to **Settings** → **Theme**
2. Choose:
   - **Light** - Always light theme
   - **Dark** - Always dark theme
   - **System** - Follow device settings (default)

## 🛠️ Technical Details

### Architecture

- **Language:** Kotlin 2.0.21
- **UI Framework:** Jetpack Compose with Material 3
- **Database:** Room (SQLite)
- **Navigation:** Jetpack Navigation Compose
- **Async:** Kotlin Coroutines + Flow
- **Serialization:** Gson
- **Architecture Pattern:** MVVM with Repository

### How It Works

1. **Accessibility Service** monitors text input events across all apps
2. When you type a trigger word followed by **Space**, the service:
   - Detects the trigger in the text field
   - Checks word boundaries (won't expand mid-word)
   - Processes dynamic placeholders (dates, times, etc.)
   - Replaces trigger with expanded text using `ACTION_SET_TEXT`
   - Saves expansion history for undo
3. Press **Backspace** to undo and restore the trigger word

### Permissions

- **Accessibility Service** - Required for system-wide text monitoring and replacement
  - Only monitors text change events
  - Does not collect or transmit any data
  - Processes everything locally on your device

### Privacy

- ✅ **All data stays on your device**
- ✅ **No internet connection required**
- ✅ **No analytics or tracking**
- ✅ **No ads**
- ✅ **Open source** (code can be audited)

## 🐛 Troubleshooting

### Snippets not expanding?

1. ✅ Check accessibility service is enabled (Settings → Accessibility)
2. ✅ Verify service toggle is ON in app Settings
3. ✅ Ensure snippet is enabled (toggle on snippet card)
4. ✅ Make sure you typed trigger followed by Space
5. ✅ Try restarting the accessibility service

### Accessibility service disabled after reinstall?

This is an Android security feature. Accessibility permissions don't persist across app reinstalls. You'll need to re-enable the service in Settings → Accessibility after each reinstall.

### Theme not changing?

1. Go to Settings → Theme
2. Select your preferred theme
3. Theme changes apply immediately

### Import failed?

1. Ensure JSON file is valid
2. Check file follows the correct format (see Import/Export section)
3. Try exporting snippets first to see correct format

## 🗺️ Roadmap

Potential future enhancements:

- [ ] Cursor positioning with `{{cursor}}` placeholder
- [ ] Clipboard content variable `{{clipboard}}`
- [ ] App-specific snippet filtering
- [ ] Cloud backup/sync
- [ ] Snippet categories and folders
- [ ] Usage statistics
- [ ] Snippet suggestions
- [ ] Rich text formatting
- [ ] Case transformation options

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

This app was built with the assistance of **[Claude Code](https://claude.com/claude-code)**, Anthropic's AI-powered coding assistant. Claude Code helped with:

- Architecture design and implementation
- Material 3 UI/UX design
- Room database setup and migrations
- Accessibility service integration
- Dynamic snippet processing
- Import/export functionality
- Theme selection system
- Documentation and examples

Special thanks to the open-source community and the following libraries:

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material 3](https://m3.material.io/)
- [Gson](https://github.com/google/gson)

## 📧 Contact & Support

For bug reports, feature requests, or questions:

- **Issues:** [GitHub Issues](https://github.com/yourusername/expander/issues)
- **Discussions:** [GitHub Discussions](https://github.com/yourusername/expander/discussions)

---

**Built with ❤️ and assistance from Claude Code**

*Expander - Type less, do more!*
