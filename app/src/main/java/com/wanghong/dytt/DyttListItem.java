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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by wanghong on 11/19/16.
 */

@Entity
public class DyttListItem {

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @CreatedFromHtmlTag("a[href].ulink")
    private String title;

    @CreatedFromHtmlTag("a[href].ulink")
    @CreatedFromHtmlAttribute("href")
    @CreatedFromHtmlAbsHref
    @PrimaryKey
    @NonNull
    private String targetUrl;

    @CreatedFromHtmlTag("td > font[color='#8F8C89']")
    private String dateTime;

    @Override
    public String toString() {
        return "DyttListItem{" +
                "title='" + title + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
