/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class McpttMbmsCallback {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected McpttMbmsCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(McpttMbmsCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tinyWRAPJNI.delete_McpttMbmsCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    tinyWRAPJNI.McpttMbmsCallback_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    tinyWRAPJNI.McpttMbmsCallback_change_ownership(this, swigCPtr, true);
  }

  public McpttMbmsCallback() {
    this(tinyWRAPJNI.new_McpttMbmsCallback(), true);
    tinyWRAPJNI.McpttMbmsCallback_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

  public int OnEvent(McpttMbmsEvent e) {
    return (getClass() == McpttMbmsCallback.class) ? tinyWRAPJNI.McpttMbmsCallback_OnEvent(swigCPtr, this, McpttMbmsEvent.getCPtr(e), e) : tinyWRAPJNI.McpttMbmsCallback_OnEventSwigExplicitMcpttMbmsCallback(swigCPtr, this, McpttMbmsEvent.getCPtr(e), e);
  }

}
