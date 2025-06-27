# DiscordLink

A Velocity plugin for Discord integration.  
*(Replace this with your actual project description)*

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
