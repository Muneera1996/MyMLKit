package com.example.mymlkit.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymlkit.R
import com.example.mymlkit.databinding.MenuListItemBinding


class MenuAdapter(private var menus: ArrayList<Menu>?,
                  private val listener: RecyclerViewEvent
) :
    RecyclerView.Adapter<MenuAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        // Creating new View holders for items in recyclerView
        val menuItemListBinding: MenuListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.menu_list_item,
            parent,
            false
        )
        return ContactViewHolder(listener,menuItemListBinding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        // Called by recyclerView when it needs to display or update an item
        // at a specific position in the list or grid.
        val currentContact = menus!![position]
        holder.menuItemListBinding.menu = currentContact
    }

    override fun getItemCount(): Int {
        // Determines the total number of items in the dataset that will
        // be displayed in the recyclerview
        return if (menus != null) {
            menus!!.size
        } else {
            0
        }
    }


     class ContactViewHolder(private val listener: RecyclerViewEvent, val menuItemListBinding: MenuListItemBinding) :
        RecyclerView.ViewHolder(menuItemListBinding.root), View.OnClickListener{
         init {
             menuItemListBinding.root.setOnClickListener(this)
            // view.setOnClickListener(this)
         }
         override fun onClick(p0: View?) {
             val position = adapterPosition
             if (position != RecyclerView.NO_POSITION){
                 listener.onItemClick(position)
             }
         }

     }

    interface RecyclerViewEvent {
        fun onItemClick(position: Int)
    }
}



