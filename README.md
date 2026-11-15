# Shadow Browser

Native Android browser starter project using Android WebView and Java.

## Current baseline

- App: Shadow Browser
- Application ID: `de.shadow13041.shadowbrowser`
- Language: Java 17
- Android Gradle Plugin: 8.6.1
- Gradle distribution: 8.7
- Compile SDK: 35
- Minimum Android version: Android 6.0 (API 23)
- Target SDK: 35

## Open in AndroidIDE

1. Extract this ZIP.
2. In AndroidIDE, open the extracted `Shadow-Browser` folder as an existing Gradle project.
3. Allow Gradle sync and install any SDK components AndroidIDE requests.
4. Build and run the `app` configuration.

The ZIP contains Gradle wrapper configuration, but not the wrapper JAR. If AndroidIDE asks for it, generate/download the wrapper from AndroidIDE's Gradle tooling or use its configured Gradle installation. Do not copy a random wrapper JAR from another Gradle version.

## GitHub setup

Repository: `https://github.com/Shadow13041/Shadow-Browser`

After extracting the project, open the AndroidIDE terminal in the project root and run:

```bash
git init
git branch -M main
git remote add origin https://github.com/Shadow13041/Shadow-Browser.git
git add -A
git commit -m "Initial Shadow Browser project"
git push -u origin main
```

GitHub authentication is handled by Git when pushing. Use a fine-grained token with access only to this repository and Contents: Read and write. Enter it only into Git's password prompt; never paste it into a project file, shell script, or chat. Do not enable plaintext credential storage.

## Synchronization

Before pushing local changes:

```bash
./tools/sync-to-github.sh
```

To pull GitHub changes when the local working tree is clean:

```bash
./tools/sync-from-github.sh
```

This is deliberate/manual synchronization, not continuous background synchronization. Always sync before starting a new patch and again after committing local changes. Avoid editing the same files on both sides before syncing.

## Safe patch workflow

Save a patch as a `.diff` file, then run:

```bash
./tools/apply-patch.sh /path/to/change.diff
```

The script first runs `git apply --check`, then applies the patch and runs structural/XML checks. A patch that does not match the current source is rejected. These checks do not replace a full Android Gradle compilation or runtime test.

## Browser baseline

The first version provides an address/search bar, WebView, progress indicator, back/forward, home, and reload controls. JavaScript and DOM storage are enabled for modern websites. Mixed content, file access, content access, and third-party cookies are restricted. This is a starter browser, not yet a complete privacy-hardened browser.
