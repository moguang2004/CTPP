# CTPP

[![Build](https://github.com/CTNH-Team/CTPP/actions/workflows/build.yml/badge.svg?branch=dev)](https://github.com/CTNH-Team/CTPP/actions/workflows/build.yml)

Compatible mod between GregTech-Modern and Create for the modpack Create: New Horizon (CTNH).

## Building

This mod should be built under [CTNH-Team/CTNH-Modules](https://github.com/CTNH-Team/CTNH-Modules) repository using Gradle.

```shell
$ git clone --recursive https://github.com/CTNH-Team/CTNH-Modules.git 
$ cd CTNH-Modules   # And you may need to update the submodules manually
$ ./gradlew :modules:CTPP:build            # To build the mod .jar
$ ./gradlew :modules:CTPP:runData          # To generate data
$ ./gradlew :modules:CTPP:spotlessCheck    # To check code formatting
$ ...
```

Nightly builds are available on the [Actions](https://github.com/CTNH-Team/CTPP/actions/workflows/build.yml) page.

## License

All code is licensed under the [GNU LGPL v3 License](https://www.gnu.org/licenses/lgpl-3.0.en.html).
