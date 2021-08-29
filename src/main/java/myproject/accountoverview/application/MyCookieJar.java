package myproject.accountoverview.application;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyCookieJar implements CookieJar {

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        cookieStore.put(httpUrl.host(), list);
    }
}
