/*
 * Copyright (C) 2016 wanghong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wanghong.dytt;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghong on 11/19/16.
 */

public class JsoupEngine<T> {

    private static final String TAG = JsoupEngine.class.getSimpleName();
    private static final boolean DEBUG = false;
    private Class<T> tClass;
    private String pageUrl;
    private JsoupEngineCallback<T> jsoupEngineCallback;

    private static class HtmlTagAttribute {

        private String htmlTag;
        private String htmlAttribute;
        private boolean collections;
        private boolean absHref;

        public HtmlTagAttribute(String htmlTag, String htmlAttribute, boolean collections, boolean absHref) {
            this.htmlTag = htmlTag;
            this.htmlAttribute = htmlAttribute;
            this.collections = collections;
            this.absHref = absHref;
        }

        public boolean isAbsHref() {
            return absHref;
        }

        public boolean isCollections() {
            return collections;
        }

        public String getHtmlTag() {
            return htmlTag;
        }

        public String getHtmlAttribute() {
            return htmlAttribute;
        }

        @Override
        public String toString() {
            return "HtmlTagAttribute{" +
                    "htmlTag='" + htmlTag + '\'' +
                    ", htmlAttribute='" + htmlAttribute + '\'' +
                    '}';
        }
    }

    private Map<Field, HtmlTagAttribute> fieldHtmlTagAttributeMap = new HashMap<>();

    public interface JsoupEngineCallback<U> {
        void onJsoupParsed(List<U> results);
    }

    public JsoupEngine<T> setResultClass(Class<T> tClass) {
        this.tClass = tClass;
        fieldHtmlTagAttributeMap.clear();
        for (Field field : tClass.getDeclaredFields()) {
            String tag = null;
            String attribute = null;
            boolean collections = false;
            boolean absHref = false;
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType().isAssignableFrom(CreatedFromHtmlTag.class)) {
                    CreatedFromHtmlTag createdFromHtmlTag = (CreatedFromHtmlTag) annotation;
                    tag = createdFromHtmlTag.value();
                }
                if (annotation.annotationType().isAssignableFrom(CreatedFromHtmlAttribute.class)) {
                    CreatedFromHtmlAttribute createdFromHtmlAttribute = ((CreatedFromHtmlAttribute) annotation);
                    attribute = createdFromHtmlAttribute.value();
                }
                if (annotation.annotationType().isAssignableFrom(CreatedFromHtmlCollections.class)) {
                    collections = true;
                }
                if (annotation.annotationType().isAssignableFrom(CreatedFromHtmlAbsHref.class)) {
                    absHref = true;
                }
            }
            if (tag != null) {
                fieldHtmlTagAttributeMap.put(field, new HtmlTagAttribute(tag, attribute, collections, absHref));
            }
        }

        if (DEBUG) {
            for (Map.Entry<Field, HtmlTagAttribute> fieldHtmlTagAttributeEntry : fieldHtmlTagAttributeMap.entrySet()) {
                System.out.println(fieldHtmlTagAttributeEntry.getKey() + " ---> " + fieldHtmlTagAttributeEntry.getValue());
            }
        }

        return this;
    }

    public JsoupEngine<T> setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public JsoupEngine<T> setJsoupEngineCallback(JsoupEngineCallback<T> jsoupEngineCallback) {
        this.jsoupEngineCallback = jsoupEngineCallback;
        return this;
    }

    public void parseAsync() {
        new Thread() {

            @Override
            public void run() {
                super.run();
                parse();
            }
        }.start();
    }

    public void parse() {
        try {
            Document document = Jsoup.connect(pageUrl).userAgent(ActivityConstants.USER_AGENT).get();
            final List<T> resultList = new ArrayList<>();
            List<Integer> elementSizes = new ArrayList<>();

            for (Map.Entry<Field, HtmlTagAttribute> fieldHtmlTagAttributeEntry : fieldHtmlTagAttributeMap.entrySet()) {
                Elements elements = document.select(fieldHtmlTagAttributeEntry.getValue().getHtmlTag());
                if (fieldHtmlTagAttributeEntry.getValue().isCollections()) {
                    elementSizes.add(1);
                } else {
                    elementSizes.add(elements.size());
                }
            }
            if (Collections.max(elementSizes).intValue() != Collections.min(elementSizes).intValue()) {
                Log.e(TAG, "parse: element sizes are not equal, something might missing");
            }
            for (Map.Entry<Field, HtmlTagAttribute> fieldHtmlTagAttributeEntry : fieldHtmlTagAttributeMap.entrySet()) {
                Elements elements = document.select(fieldHtmlTagAttributeEntry.getValue().getHtmlTag());
                if (fieldHtmlTagAttributeEntry.getValue().isCollections()) {
                    ArrayList<String> collections = new ArrayList<>();

                    T result;
                    try {
                        result = resultList.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        result = tClass.newInstance();
                        resultList.add(result);
                    }

                    for (Element element : elements) {
                        if (fieldHtmlTagAttributeEntry.getValue().getHtmlAttribute() != null) {
                            collections.add(element.attributes().get(fieldHtmlTagAttributeEntry.getValue().getHtmlAttribute()));
                        } else {
                            collections.add(element.text());
                        }
                    }

                    fieldHtmlTagAttributeEntry.getKey().setAccessible(true);
                    fieldHtmlTagAttributeEntry.getKey().set(result, collections);

                } else {
                    for (int i = 0; i < elements.size(); i++) {
                        T result;
                        try {
                            result = resultList.get(i);
                        } catch (IndexOutOfBoundsException e) {
                            result = tClass.newInstance();
                            resultList.add(result);
                        }
                        fieldHtmlTagAttributeEntry.getKey().setAccessible(true);

                        if (fieldHtmlTagAttributeEntry.getValue().getHtmlAttribute() != null) {
                            fieldHtmlTagAttributeEntry.getKey().set(result, elements.get(i).attributes().get(
                                    fieldHtmlTagAttributeEntry.getValue().getHtmlAttribute()
                            ));
                            if (fieldHtmlTagAttributeEntry.getValue().isAbsHref()) {
                                fieldHtmlTagAttributeEntry.getKey().set(result,
                                        elements.get(i).absUrl(fieldHtmlTagAttributeEntry.getValue().getHtmlAttribute()));
                            }
                        } else {
                            fieldHtmlTagAttributeEntry.getKey().set(result, elements.get(i).text());
                        }
                    }
                }
            }

            if (jsoupEngineCallback != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jsoupEngineCallback.onJsoupParsed(resultList);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
