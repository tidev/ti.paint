# Ti.Paint Module - Project Documentation

## Project Overview
Ti.Paint is a cross-platform Titanium module that provides a paint surface user interface view for iOS, Android, and Windows platforms.

## Current Status (Updated August 2025)

### Version Information
- **Android Module**: v5.1.0 (Updated from 5.0.4)
- **iOS Module**: v2.1.0 (Updated from 2.0.0)
- **Minimum SDK**: 12.0.0 (Updated from 9.0.0/9.3.2)
- **Target SDK**: 12.8.0.GA

### Platform Support
- **Android**: API 21+ (Android 5.0+) targeting API 34 (Android 14)
- **iOS**: iOS 12.0+ targeting iOS 17.x
- **Architectures**: 
  - Android: arm64-v8a, armeabi-v7a, x86, x86_64
  - iOS: arm64, x86_64

## Recent Improvements (August 2025)

### Android Platform Enhancements
1. **Threading Safety**: Added proper UI thread handling in `PaintViewProxy.java`
   - New `MSG_LOAD` handler for async image loading
   - Safe `setImage()` method with thread verification

2. **Color Management**: Updated all color handling to use modern APIs
   - `TiConvert.toColor()` with activity context in all Java files
   - Improved erase mode functionality in `PathPaint.java`

3. **Performance Optimizations**: Major refactoring in `UIPaintView.java`
   - Limited touch points from 20 to 1 for stability
   - Better bitmap handling with null safety
   - Improved canvas drawing and scaling
   - Enhanced image loading with error handling

4. **Code Quality**: 
   - Removed duplicate `@Kroll.method` annotations
   - Better variable naming and code organization
   - Enhanced null safety throughout

### iOS Platform
- Minor formatting and style improvements
- Maintained compatibility with latest Xcode requirements

## SDK Requirements Justification

### Why minsdk: 12.0.0?
1. **Apple App Store Requirements**: New Xcode versions (required for App Store) don't support older Titanium SDKs
2. **Modern APIs**: The implemented improvements use APIs that require SDK 12+
3. **Platform Compatibility**: Android 14 and iOS 17 support requires modern SDK
4. **Performance**: SDK 12+ provides significant performance improvements
5. **Security**: Latest security patches and compliance requirements

## Development Commands

### Building the Module
```bash
# Android (from android/ directory)
build/gradlew -p build clean build

# iOS (from ios/ directory)
python build.py

# Using Titanium CLI (if working)
titanium build -p android -T module
titanium build -p ios -T module
```

### Testing
```bash
# Run with example app
titanium build -p android -d example/
titanium build -p ios -d example/
```

## File Structure
```
ti.paint/
├── android/                 # Android module source
│   ├── manifest             # Android module manifest (v5.1.0, minsdk: 12.0.0)
│   ├── src/ti/modules/titanium/paint/
│   │   ├── PaintViewProxy.java    # Main proxy with threading improvements
│   │   ├── PathPaint.java         # Path and paint management
│   │   └── UIPaintView.java       # Main UI view with major refactoring
│   └── build/               # Build scripts and gradle files
├── ios/                     # iOS module source
│   ├── manifest             # iOS module manifest (v2.1.0, minsdk: 12.0.0)
│   ├── Classes/             # Objective-C implementation
│   └── build.py             # iOS build script
├── example/                 # Example application
│   └── app.js              # Enhanced with save/load functionality
└── CLAUDE.md               # This documentation file
```

## Key Features
- Multi-touch painting with configurable brush properties
- Stroke width, color, and alpha control
- Erase mode functionality
- Image loading and manipulation
- Save/load paint sessions
- Undo/redo functionality
- Cross-platform compatibility

## Dependencies
- Titanium SDK 12.0.0+
- Android SDK with API 21-34 support
- Xcode with iOS 12.0-17.x support
- Java 11+ (for building Android module)

## Compilation Notes
- **Java Version**: If using Java 21, you may encounter Gradle compatibility issues. Use Java 11-17 for building.
- **Xcode**: Use latest Xcode version for iOS development and App Store compliance
- **Android SDK**: Ensure you have API 34 (Android 14) installed for target compatibility

## API Usage Examples
```javascript
// Create paint view
var paintView = TiPaint.createPaintView({
    strokeWidth: 5,
    strokeColor: 'blue',
    strokeAlpha: 255,
    eraseMode: false
});

// Set image background
paintView.setImage('path/to/image.png');

// Save painted image
var blob = paintView.toBlob();

// Clear canvas
paintView.clear();

// Undo/redo
paintView.undo();
paintView.redo();
```

## Distribution
- Module builds generate `.zip` files in respective `dist/` directories
- Install modules using `titanium module install <path-to-zip>`
- For App Store/Play Store distribution, ensure compliance with latest platform requirements

## Troubleshooting
1. **Build Errors**: Check Java version compatibility (use JDK 11-17)
2. **SDK Compatibility**: Verify Titanium SDK 12.0.0+ is installed
3. **Platform Issues**: Ensure latest Xcode and Android SDK tools are installed
4. **Thread Safety**: All UI operations are now properly handled on main thread

## Future Considerations
- Monitor Titanium SDK updates for new features and compatibility
- Consider adding support for additional image formats
- Evaluate performance improvements for large canvas sizes
- Keep up with Apple/Google platform requirement changes

---
*Last updated: August 2025*
*Module versions: Android 5.1.0, iOS 2.1.0*
*SDK requirement: 12.0.0+*