# Vstack

## Description
Lorem Ipsum

<br>

This app it's just tested on Android 5.0 (API 21), Android 9 (API 28) and Android 11 ( API 30 ) physical devices.

- Standard library dependencies used:
  - Thread
  - android.graphics:
    - Bitmap, BitmapFactory, Canvas, Paint, Rect, RectF
    - Matrix, ColorMatrix, ColorMatrixColorFilter
    
  - android.animation ( Animator )
  - android.view.animation ( Animation )
  - org.json (JSONObject, JSONArray, ... )
  - android.media ( SoundPool )
  - android.content:
    - AssetsManager
    - SharedPreferences
  
  - androidx.fragment.app:
    - DialogFragment
    - FragmentManager
    - FragmentResultListener

  - androidx.databinding.DataBindingUtil

  - androidx.recyclerview:recyclerview:1.3.0
	- androidx.viewpager2:viewpager2:1.0.0


- Third party library dependencies used:

  - com.google.android.gms.ads.MobileAds ( Google AdMob )
    - AdView ( AdBanner )
    - RewardedAd
  
  - com.google.firebase:
    - firebase-crashlytics:18.3.5
    - firebase-analytics:21.2.0


## What I learned
- View sub-class:
  To draw every level applying color filters to reduce the amount of image files needed to represent different ambients.

- Global access to SharedPreferences using Singleton class ( GamePreferences )

- Game content loaded from assets as json file.

- Player progress tracking with SharedPreferences

- RecyclerView with two ViewHolders

- DialogFragment sub-class:
  - Overlay customization to prevent AdMob Banners from being covered by default DialogFragments.

- Proper use of the FragmenResultListener to mimic the callback since listeners can't be passed through the DialogFragment's Bundle.

- Use of Module to better organization of the project
- Use of Gradle files to keep sensitive data out of public access.
- Use of BuildTypes in gradle to automatically select AdMob real or test ads when changing the build type to prevent from showing real ads on several release types on Google Play Console.

- Activity sub-class:
  Custom Activity sub-class as library used as Splash Screen to show the brand of the company.

- Constants class:
  - Grouping constant values in different static classes to better organization.
  - Defining the current device's dimensions to fit every widget to the proper size.


## Future Work
- Lorem
- Lorem

## Screenshots
### Home

### World Selector