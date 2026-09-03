package com.pdf2q.dto;

/** 云端同步结果。 */
public class SyncResult {

  private int migratedSets;
  private int totalSets;
  private String message;

  public SyncResult() {
  }

  public SyncResult(int migratedSets, int totalSets, String message) {
    this.migratedSets = migratedSets;
    this.totalSets = totalSets;
    this.message = message;
  }

  public int getMigratedSets() {
    return migratedSets;
  }

  public void setMigratedSets(int migratedSets) {
    this.migratedSets = migratedSets;
  }

  public int getTotalSets() {
    return totalSets;
  }

  public void setTotalSets(int totalSets) {
    this.totalSets = totalSets;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
