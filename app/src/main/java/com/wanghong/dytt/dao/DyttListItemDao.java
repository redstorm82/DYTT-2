/*
 * Copyright (C) 2017 wanghong
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

package com.wanghong.dytt.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.wanghong.dytt.DyttListItem;

import java.util.List;

/**
 * Created by wanghong on 21/06/2017.
 */

@Dao
public interface DyttListItemDao {

    @Query("SELECT * FROM dyttlistitem")
    List<DyttListItem> queryAll();

    @Query("SELECT * FROM dyttlistitem WHERE type = :type")
    List<DyttListItem> queryAllByType(int type);

    @Query("SELECT * FROM dyttlistitem WHERE type = :type")
    LiveData<List<DyttListItem>> queryAllByTypeSync(int type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DyttListItem> dyttListItems);
}
