package de.shadow13041.shadowbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String HOME_URL = "https://www.google.com/";
    private static final int COLOR_BACKGROUND = Color.rgb(16, 19, 26);
    private static final int COLOR_SURFACE = Color.rgb(32, 38, 50);
    private static final int COLOR_ACCENT = Color.rgb(69, 196, 255);

    private final ArrayList<WebView> tabs = new ArrayList<>();

    private WebView webView;
    private LinearLayout webContainer;
    private EditText addressBar;
    private ProgressBar progressBar;
    private Button backButton;
    private Button forwardButton;
    private Button tabsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(COLOR_BACKGROUND);
        getWindow().setNavigationBarColor(COLOR_BACKGROUND);
        buildInterface();
        createTab(HOME_URL);
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(COLOR_BACKGROUND);
        root.setPadding(dp(10), dp(8), dp(10), dp(6));

        LinearLayout addressRow = new LinearLayout(this);
        addressRow.setGravity(Gravity.CENTER_VERTICAL);
        addressRow.setOrientation(LinearLayout.HORIZONTAL);

        addressBar = new EditText(this);
        addressBar.setOnLongClickListener(v -> {
            showBrowserDataMenu();
            return true;
        });
        addressBar.setSingleLine(true);
        addressBar.setTextColor(Color.WHITE);
        addressBar.setHintTextColor(Color.rgb(168, 178, 194));
        addressBar.setHint("Search or enter address");
        addressBar.setTextSize(14);
        addressBar.setPadding(dp(14), 0, dp(10), 0);
        addressBar.setImeOptions(EditorInfo.IME_ACTION_GO);
        addressBar.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_URI);

        GradientDrawable addressBackground = new GradientDrawable();
        addressBackground.setColor(COLOR_SURFACE);
        addressBackground.setCornerRadius(dp(24));
        addressBar.setBackground(addressBackground);
        addressRow.addView(addressBar, new LinearLayout.LayoutParams(0, dp(46), 1));

        Button goButton = createButton("Go", true);
        LinearLayout.LayoutParams goParams = new LinearLayout.LayoutParams(dp(54), dp(46));
        goParams.leftMargin = dp(7);
        addressRow.addView(goButton, goParams);
        root.addView(addressRow);

        progressBar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(
                android.content.res.ColorStateList.valueOf(COLOR_ACCENT));
        progressBar.setProgressBackgroundTintList(
                android.content.res.ColorStateList.valueOf(COLOR_SURFACE));
        root.addView(progressBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3)));

        webContainer = new LinearLayout(this);
        webContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(webContainer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        LinearLayout navigation = new LinearLayout(this);
        navigation.setGravity(Gravity.CENTER);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setPadding(0, dp(6), 0, 0);

        backButton = createButton("‹", false);
        forwardButton = createButton("›", false);
        Button homeButton = createButton("Home", false);
        Button reloadButton = createButton("↻", false);
        tabsButton = createButton("Tabs (0)", false);
        Button newTabButton = createButton("+", true);

        addNavigationButton(navigation, backButton);
        addNavigationButton(navigation, forwardButton);
        addNavigationButton(navigation, homeButton);
        addNavigationButton(navigation, reloadButton);
        addNavigationButton(navigation, tabsButton);
        addNavigationButton(navigation, newTabButton);

        root.addView(navigation);
        setContentView(root);

        goButton.setOnClickListener(v -> navigateFromAddressBar());

        addressBar.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO
                    || (event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                navigateFromAddressBar();
                return true;
            }
            return false;
        });

        backButton.setOnClickListener(v -> {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
            }
        });

        forwardButton.setOnClickListener(v -> {
            if (webView != null && webView.canGoForward()) {
                webView.goForward();
            }
        });

        homeButton.setOnClickListener(v -> {
            if (webView != null) webView.loadUrl(HOME_URL);
        });

        reloadButton.setOnClickListener(v -> {
            if (webView != null) webView.reload();
        });

        tabsButton.setOnClickListener(v -> showTabOverview());
        newTabButton.setOnClickListener(v -> createTab(HOME_URL));
    }

    private void createTab(String url) {
        WebView newWebView = new WebView(this);
        tabs.add(newWebView);
        webContainer.addView(newWebView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        if (webView != null) webView.setVisibility(View.GONE);

        webView = newWebView;
        configureWebView(newWebView);
        newWebView.setVisibility(View.VISIBLE);
        addressBar.setText(url);
        updateTabButton();
        updateNavigationButtons();
        newWebView.loadUrl(url);
    }

    private void switchTab(int index) {
        if (index < 0 || index >= tabs.size()) return;

        if (webView != null) webView.setVisibility(View.GONE);

        webView = tabs.get(index);
        webView.setVisibility(View.VISIBLE);

        String url = webView.getUrl();
        addressBar.setText(url == null ? "" : url);

        updateTabButton();
        updateNavigationButtons();
    }

    private void closeCurrentTab() {
        if (webView == null || tabs.isEmpty()) return;

        int index = tabs.indexOf(webView);
        WebView closing = webView;
        tabs.remove(closing);
        webContainer.removeView(closing);

        closing.stopLoading();
        closing.setWebChromeClient(null);
        closing.setWebViewClient(null);
        closing.destroy();

        if (tabs.isEmpty()) {
            webView = null;
            createTab(HOME_URL);
        } else {
            switchTab(Math.min(index, tabs.size() - 1));
        }

        updateTabButton();
    }

    private void showTabOverview() {
        if (tabs.isEmpty()) {
            createTab(HOME_URL);
            return;
        }

        String[] labels = new String[tabs.size()];
        int selected = tabs.indexOf(webView);

        for (int i = 0; i < tabs.size(); i++) {
            WebView tab = tabs.get(i);
            String title = tab.getTitle();
            String url = tab.getUrl();

            if (title == null || title.trim().isEmpty()) {
                title = url == null ? "New tab" : url;
            }

            labels[i] = (i == selected ? "● " : "")
                    + title + "\n"
                    + (url == null ? "" : url);
        }

        new AlertDialog.Builder(this)
                .setTitle("Open tabs (" + tabs.size() + ")")
                .setItems(labels, (dialog, which) -> switchTab(which))
                .setPositiveButton("New tab", (dialog, which) -> createTab(HOME_URL))
                .setNegativeButton("Close current tab",
                        (dialog, which) -> closeCurrentTab())
                .setNeutralButton("Done", null)
                .show();
    }

    private void updateTabButton() {
        if (tabsButton != null) {
            tabsButton.setText("Tabs (" + tabs.size() + ")");
        }
    }

    private void configureWebView(WebView target) {
        WebSettings settings = target.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

        android.webkit.CookieManager.getInstance()
                .setAcceptThirdPartyCookies(target, false);

        target.setBackgroundColor(Color.WHITE);

        target.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url != null) BrowserDataStore.addHistory(MainActivity.this, url, view.getTitle());
                if (view == webView) {
                    addressBar.setText(url);
                    updateNavigationButtons();
                }
            }
        });

        target.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (view == webView) {
                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(
                            newProgress >= 100 ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (view == webView && title != null && !title.isEmpty()) {
                    setTitle(title + " - Shadow Browser");
                }
            }
        });
    }

    private void showBrowserDataMenu() {
        String[] options = {"Aktuelle Seite speichern", "Lesezeichen anzeigen", "Verlauf anzeigen"};
        new AlertDialog.Builder(this)
                .setTitle("Browser-Daten")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (webView != null) {
                            String url = webView.getUrl();
                            String title = webView.getTitle();
                            if (url != null) {
                                BrowserDataStore.addBookmark(this, url, title);
                                Toast.makeText(this, "Lesezeichen gespeichert", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (which == 1) {
                        BrowserDataDialogs.show(this, webView, false);
                    } else {
                        BrowserDataDialogs.show(this, webView, true);
                    }
                }).show();
    }

    private void navigateFromAddressBar() {
        String input = addressBar.getText().toString().trim();
        if (input.isEmpty() || webView == null) return;

        String target;

        if (input.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            target = input;
        } else if (input.matches(
                "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(:[0-9]+)?(/.*)?$")) {
            target = "https://" + input;
        } else {
            try {
                target = "https://www.google.com/search?q="
                        + URLEncoder.encode(input, StandardCharsets.UTF_8.name());
            } catch (Exception exception) {
                Toast.makeText(this, "Search could not be started",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
        addressBar.clearFocus();
        webView.loadUrl(target);
    }

    private Button createButton(String text, boolean accent) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(12);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(dp(5), 0, dp(5), 0);

        GradientDrawable background = new GradientDrawable();
        background.setColor(accent ? COLOR_ACCENT : COLOR_SURFACE);
        background.setCornerRadius(dp(18));
        button.setBackground(background);
        return button;
    }

    private void addNavigationButton(LinearLayout parent, Button button) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, dp(42), 1);
        params.leftMargin = dp(2);
        params.rightMargin = dp(2);
        parent.addView(button, params);
    }

    private void updateNavigationButtons() {
        if (backButton != null) {
            backButton.setEnabled(webView != null && webView.canGoBack());
        }
        if (forwardButton != null) {
            forwardButton.setEnabled(
                    webView != null && webView.canGoForward());
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else if (tabs.size() > 1) {
            closeCurrentTab();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        for (WebView tab : new ArrayList<>(tabs)) {
            tab.stopLoading();
            tab.setWebChromeClient(null);
            tab.setWebViewClient(null);
            webContainer.removeView(tab);
            tab.destroy();
        }
        tabs.clear();
        webView = null;
        super.onDestroy();
    }
}
