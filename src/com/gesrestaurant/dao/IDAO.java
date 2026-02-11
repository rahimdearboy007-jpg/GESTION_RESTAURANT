/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gesrestaurant.dao;

import java.util.List;

public interface IDAO<T> {
    // CRUD operations
    boolean create(T obj);
    T read(int id);
    boolean update(T obj);
    boolean delete(int id);
    List<T> findAll();
}
