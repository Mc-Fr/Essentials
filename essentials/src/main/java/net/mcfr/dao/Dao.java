package net.mcfr.dao;

import java.util.List;

public abstract class Dao<T> {

  public Dao() {}

  public abstract List<T> getAll();

  public abstract boolean create(T o);

  public abstract boolean delete(T o);

  public abstract boolean update(T o);
}