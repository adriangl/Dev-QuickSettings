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
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.qs_description_item.*

/**
 * Adapter used to display [QsDescriptionsItem]s.
 */
class QsDescriptionsRecyclerViewAdapter(private val items: MutableList<QsDescriptionsItem> = mutableListOf())
    : RecyclerView.Adapter<QsDescriptionsRecyclerViewAdapter.ViewHolder>() {

    /**
     * Sets the items to display by this adapter.
     */
    fun setItems(list: List<QsDescriptionsItem>) {
        items.clear()
        items.addAll(list)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.qs_description_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.qs_title.text = items[position].title
        holder.qs_description.text = items[position].description
        holder.qs_icon.setImageResource(items[position].iconResource)
    }

    /**
     * [RecyclerView.ViewHolder] used to display [QsDescriptionsItem]s.
     */
    class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
