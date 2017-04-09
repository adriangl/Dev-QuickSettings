/*
 * Copyright (C) 2017 Adrián García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adriangl.devquicktiles.tiles.views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.adriangl.devquicktiles.R

class QsDescriptionsRecyclerViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val itemTitle: TextView = itemView?.findViewById(R.id.qs_title) as TextView
    val itemDescription: TextView = itemView?.findViewById(R.id.qs_description) as TextView
    val itemIcon: ImageView = itemView?.findViewById(R.id.qs_icon) as ImageView
}