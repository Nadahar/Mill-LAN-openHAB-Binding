# Changelog for Mill LAN Binding
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.1] - 2025-03-04

### Added

- Create dedicated decimal precision handling and improve decimal value comparison to avoid triggering events when no real change has taken place.
- Create channel for "Independent Device mode" set-temperature that combines different API endpoints to simulate a "real" channel.

### Fixed

- Minor improvements to documentation.
- Minor improvements to Norwegian translations.
- Discovery support for more recent panel heater firmware.

## [1.0.0] - 2024-12-06

### First release

[Unreleased]: https://github.com/Nadahar/Mill-LAN-openHAB-Binding/compare/v1.0.1...HEAD
[1.0.1]: https://github.com/Nadahar/Mill-LAN-openHAB-Binding/releases/tag/v1.0.1
[1.0.0]: https://github.com/Nadahar/Mill-LAN-openHAB-Binding/releases/tag/v1.0.0
