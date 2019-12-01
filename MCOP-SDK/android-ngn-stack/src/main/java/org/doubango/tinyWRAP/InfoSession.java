/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class InfoSession extends SipSession {
  private long swigCPtr;

  protected InfoSession(long cPtr, boolean cMemoryOwn) {
    super(tinyWRAPJNI.InfoSession_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(InfoSession obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tinyWRAPJNI.delete_InfoSession(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public InfoSession(SipStack pStack) {
    this(tinyWRAPJNI.new_InfoSession(SipStack.getCPtr(pStack), pStack), true);
  }

  public boolean send(java.nio.ByteBuffer payload, long len, ActionConfig config) {
    return tinyWRAPJNI.InfoSession_send__SWIG_0(swigCPtr, this, payload, len, ActionConfig.getCPtr(config), config);
  }

  public boolean send(java.nio.ByteBuffer payload, long len) {
    return tinyWRAPJNI.InfoSession_send__SWIG_1(swigCPtr, this, payload, len);
  }

  public boolean accept(ActionConfig config) {
    return tinyWRAPJNI.InfoSession_accept__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean accept() {
    return tinyWRAPJNI.InfoSession_accept__SWIG_1(swigCPtr, this);
  }

  public boolean reject(ActionConfig config) {
    return tinyWRAPJNI.InfoSession_reject__SWIG_0(swigCPtr, this, ActionConfig.getCPtr(config), config);
  }

  public boolean reject() {
    return tinyWRAPJNI.InfoSession_reject__SWIG_1(swigCPtr, this);
  }

}
