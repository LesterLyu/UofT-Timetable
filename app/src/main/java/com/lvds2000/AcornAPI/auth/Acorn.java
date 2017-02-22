package com.lvds2000.AcornAPI.auth;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.lvds2000.AcornAPI.course.CourseManager;
import com.lvds2000.AcornAPI.plan.PlannedCourse;
import com.lvds2000.AcornAPI.exception.LoginFailedException;
import com.lvds2000.AcornAPI.grade.GradeManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Lester Lyu
 */
public class Acorn {
    private OkHttpClient client;
    private String acornUsername;
    private String acornPassword;
    private AcornCookieJar acoreCookieJar;
    private CourseManager courseManager;
    private RegistrationManager registrationManager;
    private GradeManager gradeManager;
    private Boolean isLoggedIn = null;

    public Acorn(String acornUsername, String acornPassword) {
        this.acornUsername = acornUsername;
        this.acornPassword = acornPassword;
        acoreCookieJar = new AcornCookieJar();
        this.client = new OkHttpClient.Builder()
                .cookieJar(acoreCookieJar)
                .build();
        registrationManager = new RegistrationManager(client);
        courseManager = new CourseManager(client, registrationManager);
        gradeManager = new GradeManager(client);

    }

    public boolean isSameUserPass(String user, String pass){
        return acornUsername.equals(user) && acornPassword.equals(pass);
    }

    public void refresh() {
        acoreCookieJar = new AcornCookieJar();
        this.client = new OkHttpClient.Builder()
                .cookieJar(acoreCookieJar)
                .build();
        registrationManager = new RegistrationManager(client);
        courseManager = new CourseManager(client, registrationManager);
        gradeManager = new GradeManager(client);
        //this.doLogin();
    }

    public OkHttpClient getHttpClient() {
        return client;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public RegistrationManager getRegistrationManager() {
        return registrationManager;
    }

    public GradeManager getGradeManager() {
        return gradeManager;
    }



    public boolean isLoggedIn(){
        final Boolean isLoggedIn[] = new Boolean[1];

        final Request request = new Request.Builder()
                .url("https://acorn.utoronto.ca/sws/rest/profile/studentBasicInfo")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLoggedIn[0] = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.body().string().contains("<html>")){
                    refresh();
                    isLoggedIn[0] = false;
                }
                else
                    isLoggedIn[0] = true;
                System.out.println("usLoggedIn=" + isLoggedIn[0]);
            }
        });
        long prev = System.currentTimeMillis();
        while (System.currentTimeMillis() - prev < 15000 && isLoggedIn[0] == null) {

        }
        return (isLoggedIn[0] == null) ? false : (isLoggedIn[0]);
    }

    /**
     * First step,
     * get pubcookie_g_req, post_stuff, relay_url from https://idp.utorauth.utoronto.ca/idp/Authn/RemoteUserForceAuth
     *
     * @return the Map of them
     */
    public void doLogin() throws LoginFailedException {
        if(isLoggedIn()){
            Log.i("Acorn Login", "Already Logged in...");
            return;
        }
        Log.i("Acorn Login", "Loging in...");
        final Map<String, String> map = new HashMap<String, String>();
        String url = "https://acorn.utoronto.ca/sws";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    String formInnerHtml = doc.select("[name=relay]").html();
                    Document formInnerDoc = Jsoup.parse(formInnerHtml);
                    map.put("pubcookie_g_req", formInnerDoc.select("[name=pubcookie_g_req]").attr("value"));
                    map.put("post_stuff", formInnerDoc.select("[name=post_stuff]").attr("value"));
                    map.put("relay_url", formInnerDoc.select("[name=relay_url]").attr("value"));
                    getLoginParam2(map);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        long prev = System.currentTimeMillis();
        while (System.currentTimeMillis() - prev < 15000 && isLoggedIn == null) {

        }
        Log.i("Login", "consume " + (System.currentTimeMillis() - prev) + "ms");
        if (isLoggedIn == null) {
            throw new LoginFailedException("Connection timeout");
        } else if (!isLoggedIn) {
            isLoggedIn = null;
            throw new LoginFailedException("Username/password mismatch or Acorn went down.");
        }

    }

    /**
     * Second step
     * get 25 params include password and username
     *
     * @return
     */
    private Map<String, String> getLoginParam2(Map<String, String> loginParam1) {
        //Map<String, String> loginParam1 = getLoginParam1();
        //System.out.println(loginParam1);
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("pubcookie_g_req", loginParam1.get("pubcookie_g_req"))
                .add("post_stuff", loginParam1.get("post_stuff"))
                .add("relay_url", loginParam1.get("relay_url"));
        RequestBody formBody = formBuilder.build();

        Request request = new Request.Builder()
                .url("https://weblogin.utoronto.ca/")
                .post(formBody)
                .build();
        //System.out.println("trying to open https//weblogin.utoronto.ca/ with parameter: " + loginParam1);

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    String formInnerHtml = doc.select("#query").html();
                    Document formInnerDoc = Jsoup.parse(formInnerHtml);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("user", acornUsername);
                    map.put("pass", acornPassword);
                    map.put("one", formInnerDoc.select("[name=one]").attr("value"));
                    map.put("two", formInnerDoc.select("[name=two]").attr("value"));
                    map.put("creds_from_greq", formInnerDoc.select("[name=creds_from_greq]").attr("value"));
                    map.put("three", formInnerDoc.select("[name=three]").attr("value"));
                    map.put("four", formInnerDoc.select("[name=four]").attr("value"));
                    map.put("five", formInnerDoc.select("[name=five]").attr("value"));
                    map.put("six", formInnerDoc.select("[name=six]").attr("value"));
                    map.put("seven", formInnerDoc.select("[name=seven]").attr("value"));
                    map.put("relay_url", formInnerDoc.select("[name=relay_url]").attr("value"));
                    map.put("eight", formInnerDoc.select("[name=eight]").attr("value"));
                    map.put("fr", formInnerDoc.select("[name=fr]").attr("value"));
                    map.put("hostname", formInnerDoc.select("[name=hostname]").attr("value"));
                    map.put("nine", formInnerDoc.select("[name=nine]").attr("value"));
                    map.put("file", formInnerDoc.select("[name=file]").attr("value"));
                    map.put("flag", formInnerDoc.select("[name=flag]").attr("value"));
                    map.put("referer", formInnerDoc.select("[name=referer]").attr("value"));
                    map.put("post_stuff", formInnerDoc.select("[name=post_stuff]").attr("value"));
                    map.put("sess_re", formInnerDoc.select("[name=sess_re]").attr("value"));
                    map.put("pre_sess_tok", formInnerDoc.select("[name=pre_sess_tok]").attr("value"));
                    map.put("first_kiss", formInnerDoc.select("[name=first_kiss]").attr("value"));
                    map.put("pinit", formInnerDoc.select("[name=pinit]").attr("value"));
                    map.put("reply", formInnerDoc.select("[name=reply]").attr("value"));
                    map.put("create_ts", formInnerDoc.select("[name=create_ts]").attr("value"));
                    getLoginParam3(map);
                }
            });
            //Cookie cookie = new Cookie(acornPassword, acornPassword, 0, acornPassword, acornPassword, false, false, false, false);
            //client.cookieJar().saveFromResponse("https://weblogin.utoronto.ca/", response.headers().);
            //response.headers("Set-Cookie")
            //System.out.println("getLoginParam2, cookies:\n" + acoreCookieJar.getAllCookie());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<String, String>();
    }

    /**
     * Third step
     *
     * @return
     */
    public Map<String, String> getLoginParam3(Map<String, String> loginParam2) {
        //Map<String, String> loginParam2 = getLoginParam2();
        //System.out.println(loginParam2);
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("weblogin.utoronto.ca")
                .addQueryParameter("user", loginParam2.get("user"))
                .addQueryParameter("pass", loginParam2.get("pass"))
                .addQueryParameter("one", loginParam2.get("one"))
                .addQueryParameter("two", loginParam2.get("two"))
                .addQueryParameter("creds_from_greq", loginParam2.get("creds_from_greq"))
                .addQueryParameter("three", loginParam2.get("three"))
                .addQueryParameter("four", loginParam2.get("four"))
                .addQueryParameter("five", loginParam2.get("five"))
                .addQueryParameter("six", loginParam2.get("six"))
                .addQueryParameter("seven", loginParam2.get("seven"))
                .addQueryParameter("relay_url", loginParam2.get("relay_url"))
                .addQueryParameter("eight", loginParam2.get("eight"))
                .addQueryParameter("fr", loginParam2.get("fr"))
                .addQueryParameter("hostname", loginParam2.get("hostname"))
                .addQueryParameter("nine", loginParam2.get("nine"))
                .addQueryParameter("file", loginParam2.get("file"))
                .addQueryParameter("flag", loginParam2.get("flag"))
                .addQueryParameter("referer", loginParam2.get("referer"))
                .addQueryParameter("post_stuff", loginParam2.get("post_stuff"))
                .addQueryParameter("sess_re", loginParam2.get("sess_re"))
                .addQueryParameter("pre_sess_tok", loginParam2.get("pre_sess_tok"))
                .addQueryParameter("first_kiss", loginParam2.get("first_kiss"))
                .addQueryParameter("pinit", loginParam2.get("pinit"))
                .addQueryParameter("reply", loginParam2.get("reply"))
                .addQueryParameter("create_ts", loginParam2.get("create_ts"))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    //System.out.println(doc.html());
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("post_stuff", doc.select("[name=post_stuff]").attr("value"));
                    map.put("get_args", doc.select("[name=get_args]").attr("value"));
                    map.put("redirect_url", doc.select("[name=redirect_url]").attr("value"));
                    map.put("pubcookie_g", doc.select("[name=pubcookie_g]").attr("value"));
                    getLoginParam4(map);
                }
            });

            //System.out.println("getLoginParam3, cookies:\n" + acoreCookieJar.getAllCookie());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<String, String>();
    }

    public String getLoginParam4(Map<String, String> loginParam3) {
        //Map<String, String> loginParam3 = getLoginParam3();
        //System.out.println(loginParam3);

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("post_stuff", loginParam3.get("post_stuff"))
                .add("get_args", loginParam3.get("get_args"))
                .add("redirect_url", loginParam3.get("redirect_url"))
                .add("pubcookie_g", loginParam3.get("pubcookie_g"));
        RequestBody formBody = formBuilder.build();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("idp.utorauth.utoronto.ca")
                .addPathSegment("PubCookie.reply")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        //System.out.println(url.url().toString());

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Document doc = Jsoup.parse(response.body().string());
                    // SAMLResponse
                    String res = doc.select("[name=SAMLResponse]").attr("value");
                    doLoginFinalStep(res);
                }
            });
            //System.out.println("getLoginParam4, cookies:\n" + acoreCookieJar.getAllCookie());
            //System.out.println(response.body().string());

            //return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    public String doLoginFinalStep(String loginParam4) {

        //String loginParam4 = getLoginParam4();
        //System.out.println(loginParam4);

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("SAMLResponse", loginParam4);
        RequestBody formBody = formBuilder.build();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("acorn.utoronto.ca")
                .addPathSegment("spACS")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        //System.out.println(url.url().toString());

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    //System.out.println(body);
                    if (body.contains("<title>ACORN</title>")) {
                        Log.i("Login", "Login Success");
                        isLoggedIn = true;
                    } else {
                        isLoggedIn = false;
                        //throw new LoginFailedException();
                    }
                }
            });
            //System.out.println("doLoginFinalStep, cookies:\n" + acoreCookieJar.getAllCookie());
            //System.out.println(response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed to login";
    }


    public static void main(String[] args) throws IOException, LoginFailedException {
        Acorn example = new Acorn("username", "password");
        example.doLogin();

        example.getCourseManager().loadCourses();
        for (PlannedCourse c : example.getCourseManager().getPlannedCourses()) {
            System.out.println(c);
        }
        //String gradeHtml = example.getGradeManager().getGradeHtml();
        //System.out.println(gradeHtml);
    }
}
