# AndroidUniqueID

**KO** Android에서 기기/설치 단위 고유 식별자를 가져오는 방법과, Android 버전별로 막힌 항목을 정리한 샘플입니다.
**EN** A sample showing how to obtain device/installation identifiers on Android, and which ones are restricted by Android version.

> 📖 **Blog / 블로그** · [Android 고유식별자 (unique identifiers)](https://dnight.tistory.com/entry/Android-%EA%B3%A0%EC%9C%A0%EC%8B%9D%EB%B3%84%EC%9E%90) (2020.01)
> **KO** 식별자별 차이와 오류 사례를 글에서 더 자세히 설명합니다.
> **EN** The post explains each identifier and the error cases in more detail.

> ⚠️ **KO** 블로그 글은 Android 10 기준입니다. 최신 정책은 [공식 문서](https://developer.android.com/training/articles/user-data-ids)를 함께 확인하세요.
> ⚠️ **EN** The post was written for Android 10. Check the [official guide](https://developer.android.com/training/articles/user-data-ids) for the current policy.

---

## Recommendations / 공식 권장사항

**KO** (공식 문서 요약)
1. 하드웨어 식별자(SSAID/Android ID, IMEI 등) 사용을 피한다.
2. 사용자 프로필/광고 목적에는 광고 ID만 사용하고, 사용자 선택을 존중한다.
3. 결제 사기 방지·통신을 제외하면 인스턴스 ID나 앱이 비공개로 저장한 GUID를 사용한다.
4. 사용 목적에 맞는 API(DRM API, 기기 무결성 API(Play Integrity) 등)를 쓴다.

**EN** (summary of the official guide)
1. Avoid hardware identifiers (SSAID/Android ID, IMEI, etc.).
2. Use the advertising ID only for user profiles and ads, and respect the user's choice.
3. Except for payment-fraud prevention and telephony, use an instance ID or a privately stored GUID.
4. Use the API that fits your purpose (DRM API, device-integrity API (Play Integrity), etc.).

---

## What changed by version / 버전별 변화

| Identifier / 식별자 | Status / 상태 |
|---|---|
| Wi-Fi / Bluetooth MAC address | **KO** Android 6.0부터 가져올 수 없음 · **EN** No longer available since Android 6.0 |
| `TelephonyManager.getDeviceId()` / `getImei()` / `getMeid()` | **KO** Android 10부터 `SecurityException` · **EN** `SecurityException` since Android 10 |
| `Build.SERIAL` | **KO** Android 10부터 사용 불가 · **EN** Unavailable since Android 10 |
| `ANDROID_ID` | **KO** Android 10에서 대체 수단으로 사용 가능 · **EN** Usable as an alternative on Android 10 |
| Widevine ID (`MediaDrm`) | **KO** Android 10에서 대체 수단으로 사용 가능 · **EN** Usable as an alternative on Android 10 |

Android 10 error example / 오류 예:

```
java.lang.SecurityException: getIccSerialNumber: The user 10404 does not meet the requirements to access device identifiers.
```

---

## Code / 코드

### ANDROID_ID

```kotlin
val androidId = Settings.Secure.getString(
    context.contentResolver,
    Settings.Secure.ANDROID_ID
)
```

### Widevine ID

```kotlin
fun getWidevineId(): String? {
    val widevineUuid = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
    return try {
        val drm = MediaDrm(widevineUuid)
        try {
            val id = drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            Base64.encodeToString(id, Base64.NO_WRAP)
        } finally {
            drm.release()
        }
    } catch (e: UnsupportedSchemeException) {
        null // Widevine is not available
    }
}
```

> **KO** 위 코드는 레포의 [`UniqueID.kt`](app/src/main/java/dev/dnights/androiduniqueid/UniqueID.kt)를 다듬은 버전입니다. Widevine을 지원하지 않는 기기에서 `!!`로 죽지 않고 `null`을 돌려주며, `android.util.Base64`를 써서 Android 8.0 미만에서도 동작하고(`TODO(...)` 제거), 사용 후 `MediaDrm`을 해제합니다.
> **EN** This is a cleaned-up version of [`UniqueID.kt`](app/src/main/java/dev/dnights/androiduniqueid/UniqueID.kt) in this repo. It returns `null` instead of crashing on `!!` when Widevine is unavailable, uses `android.util.Base64` so it also works below Android 8.0 (no `TODO(...)`), and releases the `MediaDrm` afterwards.

---

## Notes / 참고

- **KO** `ANDROID_ID`는 공장 초기화 등으로 바뀔 수 있습니다. Android 8.0 이상에서는 앱 서명 키·사용자·기기 조합별로 값이 달라집니다.
  **EN** `ANDROID_ID` can change, e.g. after a factory reset. On Android 8.0+ it is scoped per app-signing key, user and device.
- **KO** 식별자를 개인정보와 연결해 쓰면 개인정보 이슈가 생길 수 있으니 목적에 맞게 최소한으로 사용하세요.
  **EN** Linking identifiers to personal information raises privacy issues; use the minimum needed for your purpose.
- **KO** 블로그 글이 참고한 공식 문서의 SafetyNet Attestation API는 지원이 종료되었습니다. 기기 무결성 확인에는 [Play Integrity API](https://developer.android.com/google/play/integrity)를 사용하세요.
  **EN** The SafetyNet Attestation API mentioned in the official guide at the time has been shut down. Use the [Play Integrity API](https://developer.android.com/google/play/integrity) for device-integrity checks.

---

## References / 참고 링크

- [Best practices for unique identifiers (Android Developers)](https://developer.android.com/training/articles/user-data-ids)
- [Settings.Secure.ANDROID_ID](https://developer.android.com/reference/android/provider/Settings.Secure.html#ANDROID_ID)
- [MediaDrm](https://developer.android.com/reference/android/media/MediaDrm)
- [안드로이드 Q 기기 고유 식별자(IMEI 등) 제한](https://brunch.co.kr/@huewu/9)
- [안드로이드 Q DRM 기기 식별자 활용하기](https://brunch.co.kr/@huewu/15)

---

## 📚 Related posts / 관련 글

**KO** 같은 시기에 다룬 Android 10(Q) 변경 사항입니다.
**EN** Other Android 10 (Q) changes covered around the same time.

- [MediaStore File 다루기 (Android ScopedStorage)](https://dnight.tistory.com/entry/MediaStore-File-%EB%8B%A4%EB%A3%A8%EA%B8%B0-Android-ScopedStorage) · [Storage Access Framwork 다루기](https://dnight.tistory.com/entry/Storage-Access-Framwork-%EB%8B%A4%EB%A3%A8%EA%B8%B0-Android-ScopedStorage)
- [Android 카테고리 전체 / All Android posts](https://dnight.tistory.com/category/Android)
