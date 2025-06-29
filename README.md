## 📦 DiscordLink - Seamless Discord Integration for Velocity and Paper

**DiscordLink** is a lightweight, modern plugin that provides seamless, real-time integration between your Velocity Minecraft server and your Discord community.

With DiscordLink, players can securely link their Minecraft accounts with Discord using OAuth2 — no need to manually enter codes.

---

### ✨ Key Features

* 🔗 **Account Linking via OAuth2**
  Players can easily and securely link their accounts through a simple Discord authorization flow — no code entry required.

* ⚙️ **Highly Configurable**
  Easily adjust settings and customize the integration to fit your server's needs.

* 🚀 **Optimized for Performance**
  Designed specifically for Velocity to ensure minimal resource usage and fast response times.

* 🧩 **PlaceholderAPI Support (only on Paper)**
  Feel free to use placeholders from PlaceholderAPI. DiscordLink itself provides placeholders for Discord account name and id

---

### 🔧 Requirements

* Velocity Proxy Server
* [LuckPerms](https://modrinth.com/plugin/luckperms) plugin on your Velocity server
* Java 17 or newer
* MySQL or MariaDB database
* Open port for web server
* A Discord bot token with the required permissions
* A registered OAuth2 Discord application

---

### 📂 Open Source & Extensible

DiscordLink is open source under the **MIT License** with a dedicated **Patent License Addendum.**
You are encouraged to contribute, build your own versions, or replace bundled libraries to suit your needs.

---

### 📜 Third-Party Libraries

This plugin uses several third-party libraries such as JDA, Gson, Kotlin Standard Library, and more. Full list and licenses can be found in the `THIRD-PARTY.txt` and `licenses/` folder included with the plugin.

---

### 💬 Get Involved

Found an issue? Have a feature request?
Feel free to open an issue or submit a pull request on the project’s GitHub page.

---

### 🎈 Future plans

* Chat synchronization
* Nickname synchronization

---

## License

This project is licensed under the **MIT License**.  
Additionally, the copyright holder grants a separate **patent license addendum**.

See the files [`LICENSE`](./LICENSE) and [`PATENT-ADDENDUM.txt`](./PATENT-ADDENDUM.txt) for full details.

---

## Building from Source

This project uses [Maven](https://maven.apache.org/) as its build system.

### Requirements

- Java 17 or newer
- Maven 3.6+  

### How to Build

To build the plugin JAR, run:

```bash
mvn clean package
````

### Customizing Dependencies

Due to the use of LGPL-licensed libraries (e.g., `trove4j`), you are **allowed and encouraged** to modify dependency versions or replace libraries as needed.

To do so:

1. Edit the `pom.xml` file to change versions or exclude/include dependencies.
2. Rebuild the project using the above Maven command.

This ensures compliance with the LGPL license terms, allowing you to replace or update third-party libraries and build your own version of this plugin.

---

## Third-Party Dependencies

This project bundles or depends on several third-party libraries, each with their own licenses. See [`THIRD-PARTY.txt`](./THIRD-PARTY.txt) and the `licenses/` folder for details.

---

## Patent License Addendum (Summary)

In addition to the MIT License, the copyright holder grants a perpetual, worldwide, non-exclusive, royalty-free patent license to use, modify, and distribute this software, to the extent patents owned by the copyright holder are necessarily infringed by the software as provided.

For full legal text, see [`PATENT-ADDENDUM.txt`](./PATENT-ADDENDUM.txt).

---

## Contact

For questions, issues, or contributions, please open an issue or contact \[your email or contact info].
