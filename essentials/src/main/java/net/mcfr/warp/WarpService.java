package net.mcfr.warp;

public interface WarpService {
  
  public void loadFromDatabase();
  
  public Warp getWarp(String name);
  
  public boolean addWarp(Warp warp);
  
  public boolean deleteWarp(Warp warp);
}
