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

import java.util.List;

/**
 * Created by wanghong on 11/19/16.
 */

public class DyttMovieItem {

    @CreatedFromHtmlTag("div[id='Zoom'] > span > img[src]")
    @CreatedFromHtmlAttribute("src")
    @CreatedFromHtmlCollections
    private List<String> imageUrls;

    @CreatedFromHtmlTag("div[id='Zoom'] > span > table > tbody > tr > td > a[href]:lt(1)")
    @CreatedFromHtmlAttribute("href")
    @CreatedFromHtmlCollections
    private List<String> thunderUrls;

    @Override
    public String toString() {
        return "DyttMovieItem{" +
                "imageUrls=" + imageUrls +
                ", thunderUrls=" + thunderUrls +
                ", description='" + description + '\'' +
                '}';
    }

    @CreatedFromHtmlTag("div.co_content8 div[id='Zoom'] > span > p")
    @CreatedFromInnerHtml
    private String description;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public List<String> getThunderUrls() {
        return thunderUrls;
    }

    public String getDescription() {
        return description;
    }
}
