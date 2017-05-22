package net.mcfr.dao;

import java.util.List;

public interface Dao<T> {

  /**
   * @return la collection d'objets T
   */
  public abstract List<T> getAll();

  /**
   * Enregistre un objet T.
   * 
   * @param o
   *          l'objet à enregistrer
   * @return {@code true} si l'opération a réussi, {@code false} sinon
   */
  public abstract boolean create(T o);

  /**
   * Supprime un objet T.
   * 
   * @param o
   *          l'objet à supprimer
   * @return {@code true} si l'opération a réussi, {@code false} sinon
   */
  public abstract boolean delete(T o);

  /**
   * Met à jour un objet T.
   * 
   * @param o
   *          l'objet à mettre à jour
   * @return {@code true} si l'opération a réussi, {@code false} sinon
   */
  public abstract boolean update(T o);
}