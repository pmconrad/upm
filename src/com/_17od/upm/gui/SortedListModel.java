/*
 * Universal Password Manager
 * Copyright (C) 2005-2013 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com._17od.upm.gui;

import java.text.Collator;
import java.util.TreeSet;
import javax.swing.AbstractListModel;


public class SortedListModel<T> extends AbstractListModel<T> {

    private static final long serialVersionUID = 1L;

    private final TreeSet<T> model;


    public SortedListModel() {
        model = new TreeSet<T>((o1, o2) -> {
            String str1 = o1.toString();
            String str2 = o2.toString();
            Collator collator = Collator.getInstance();
            return collator.compare(str1, str2);
        });
    }

    
    public int getSize() {
        return model.size();
    }


    public T getElementAt(int index) {
        if (index < 0 || index >= model.size()) {
            throw new ArrayIndexOutOfBoundsException("Tried to access element " + index + " of " + model.size());
        }
        return model.stream().skip(index).findFirst().get();
    }


    public void addElement(T element) {
        if (model.add(element)) {
            fireContentsChanged(this, 0, getSize());
        }
    }


    public void clear() {
        model.clear();
        fireContentsChanged(this, 0, getSize());
    }


    public boolean contains(T element) {
        return model.contains(element);
    }


    public boolean removeElement(T element) {
        boolean removed = model.remove(element);
        if (removed) {
            fireContentsChanged(this, 0, getSize());
        }
        return removed;
    }
    
}