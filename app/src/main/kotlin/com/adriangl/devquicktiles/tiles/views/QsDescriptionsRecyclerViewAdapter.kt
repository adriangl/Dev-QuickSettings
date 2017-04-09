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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adriangl.devquicktiles.R
import java.util.*

class QsDescriptionsRecyclerViewAdapter : RecyclerView.Adapter<QsDescriptionsRecyclerViewHolder>() {
    val items = ArrayList<QsDescriptionsItem>()

    fun setItems(list: List<QsDescriptionsItem>) {
        items.clear()
        items.addAll(list)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): QsDescriptionsRecyclerViewHolder {
        val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.qs_description_item, parent, false)
        return QsDescriptionsRecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QsDescriptionsRecyclerViewHolder?, position: Int) {
        holder?.itemTitle?.text = items[position].title
        holder?.itemDescription?.text = items[position].description
        holder?.itemIcon?.setImageResource(items[position].iconResource)
    }

}
